package assembler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;

import exception.CyclicDereferenceException;
import exception.OverflowException;
import exception.SyntaxException;

/**
 * Parses tokens into Stanley/Penguin machine code.
 */
public class Parser {
    public Parser() {
    }

    public record AsyncReference(
            int sourceLineOpcode,
            int sourceLine,
            int destLine) {
    }

    /**
     * Parses tokens into Stanley/Penguin machine code.
     * 
     * @param tokens A sequence of tokens produced by a {@code Lexer}.
     * @param lines  The raw file lines (for error reporting).
     * @return A list of program words, compiled into Stanley/Penguin machine code.
     * @throws OverflowException          If integer values exceed the capacity of
     *                                    the operand.
     * @throws SyntaxException            If tokens are used improperly.
     * @throws CyclicDereferenceException If a cyclic dereference is detected.
     */
    public List<Integer> parse(List<Token> tokens, String[] lines)
            throws OverflowException, SyntaxException, CyclicDereferenceException {
        List<Integer> programWords = new ArrayList<>();

        // First pass to build symbol table
        HashMap<String, Integer> symbolTable = new HashMap<>();
        buildSymbolTable(tokens, lines, symbolTable);

        // Blueprints for asynchronous compilation of forward references
        Deque<AsyncReference> referenceStack = new ArrayDeque<>();

        // States tracked for syntax checks
        Integer opcode = null;
        Integer operand = null;
        boolean async = false;

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.type() == TokenType.INSTRUCTION) {
                if (opcode != null) {
                    throw new SyntaxException(
                            "Duplicate instruction '" + t.lexeme() + "'.",
                            t.line(), lines[t.line() - 1]);
                }
                if (async || operand != null) {
                    throw new SyntaxException(
                            "Misplaced instruction '" + t.lexeme() + "'.",
                            t.line(), lines[t.line() - 1]);
                }
                opcode = Format.parseInstruction(t.lexeme()).getOpcode();
            } else if (t.type() == TokenType.IDENTIFIER) {
                Integer location = symbolTable.getOrDefault(t.lexeme(), null);
                if (location == null) {
                    throw new SyntaxException(
                            "Invalid identifier '" + t.lexeme() + "'.",
                            t.line(), lines[t.line() - 1]);
                }
                if (operand != null || async) {
                    throw new SyntaxException(
                            "Extraneous identifier '" + t.lexeme() + "'.",
                            t.line(), lines[t.line() - 1]);
                }
                operand = location;
            } else if (t.type() == TokenType.INTEGER) {
                if (operand != null || async) {
                    throw new SyntaxException(
                            "Extraneous integer '" + t.lexeme() + "'.",
                            t.line(), lines[t.line() - 1]);
                }
                boolean signed = opcode == null || !(opcode < 0x4 || opcode > 0xA);
                operand = parseInteger(t.lexeme(), signed, t.line(), lines[t.line() - 1]);
            } else if (t.type() == TokenType.DEREFERENCE) {
                if (operand != null || async) {
                    throw new SyntaxException(
                            "Extraneous token '" + t.lexeme() + "'.",
                            t.line(), lines[t.line() - 1]);
                }

                // TEMPORARY: assume no nested references
                String cleaned = t.lexeme().replace("[", "").replace("]", "");
                Integer location = symbolTable.getOrDefault(cleaned, null);
                if (location == null) {
                    throw new SyntaxException(
                            "Invalid identifier '" + cleaned + "'.",
                            t.line(), lines[t.line() - 1]);
                }

                int sourceLine = programWords.size();

                referenceStack.add(new AsyncReference(opcode, sourceLine, location));

                async = true;
            } else if (t.type() == TokenType.NEWLINE && (opcode != null || operand != null || async)) {
                if (async) {
                    // Placeholder
                    programWords.add(-1);
                } else {
                    if (opcode != null && operand == null) {
                        throw new SyntaxException(
                                "Missing operand for instruction.",
                                t.line(), lines[t.line() - 1]);
                    }

                    if (opcode == null) {
                        // 32-bit signed integer value
                        programWords.add(operand);
                    } else {
                        boolean signed = false;
                        overflowCheck(operand, signed, Format.OPERAND_SIZE_BITS, t.line(), lines[t.line() - 1]);
                        programWords.add(Format.encodeWord(opcode, operand, signed, async));
                    }
                }

                // Reset state
                opcode = null;
                operand = null;
                async = false;
            }
        }

        // Compile lines with dereferences
        HashMap<Integer, AsyncReference> references = new HashMap<>();

        while (!referenceStack.isEmpty()) {
            AsyncReference ref = referenceStack.pop();
            references.put(ref.sourceLine(), ref);
        }

        byte[] states = new byte[programWords.size()];

        for (int i = 0; i < programWords.size(); i++) {
            resolveWord(i, programWords, references, states, lines);
        }

        return programWords;
    }

    private void buildSymbolTable(
            List<Token> tokens,
            String[] lines,
            HashMap<String, Integer> symbolTable) throws SyntaxException {

        int wordIndex = 0;
        int lastLabelLine = -1;
        boolean statementHasContent = false;

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);

            if (t.type() == TokenType.LABEL) {
                if (lastLabelLine == t.line()) {
                    throw new SyntaxException(
                            "Duplicate label '" + t.lexeme() + "'.",
                            t.line(), lines[t.line() - 1]);
                }

                if (statementHasContent) {
                    throw new SyntaxException(
                            "Misplaced label '" + t.lexeme() + "'.",
                            t.line(), lines[t.line() - 1]);
                }

                if (i == tokens.size() - 1 ||
                        tokens.get(i + 1).type() == TokenType.NEWLINE) {
                    throw new SyntaxException(
                            "Empty label '" + t.lexeme() + "'.",
                            t.line(), lines[t.line() - 1]);
                }

                if (symbolTable.putIfAbsent(t.lexeme(), wordIndex) != null) {
                    throw new SyntaxException(
                            "Duplicate label '" + t.lexeme() + "'.",
                            t.line(), lines[t.line() - 1]);
                }

                lastLabelLine = t.line();
                statementHasContent = true;

            } else if (t.type() == TokenType.NEWLINE) {
                if (statementHasContent) {
                    wordIndex++;
                    statementHasContent = false;
                }
            } else {
                statementHasContent = true;
            }
        }
    }

    private int parseInteger(String lexeme, boolean signed, int lineNumber, String line)
            throws OverflowException, SyntaxException {

        long value;
        if (lexeme.startsWith("0x") || lexeme.startsWith("0X") ||
                lexeme.startsWith("-0x") || lexeme.startsWith("-0X")) {
            try {
                int startIdx = (lexeme.startsWith("-")) ? 1 : 0;
                String hexDigits = lexeme.substring(0, startIdx) + lexeme.substring(startIdx + 2);
                value = Long.parseLong(hexDigits, 16);
            } catch (NumberFormatException e) {
                throw new RuntimeException(
                        "Unexpected error: token marked INTEGER could not be " +
                                "parsed into an integer - this should never occur.");
            }
        } else {
            try {
                value = Long.parseLong(lexeme);
            } catch (NumberFormatException e) {
                throw new RuntimeException(
                        "Unexpected error: token marked INTEGER could not be " +
                                "parsed into an integer - this should never occur.");
            }
        }

        overflowCheck(value, signed, 32, lineNumber, line);

        return (int) value;
    }

    private void overflowCheck(long value, boolean signed, int bitSize,
            int lineNumber, String line) {

        if (signed) {
            long min = -(1L << (bitSize - 1));
            long max = (1L << (bitSize - 1)) - 1;

            if (value < min || value > max) {
                throw new OverflowException(
                        String.valueOf(value), signed, bitSize, lineNumber, line);
            }
        } else {
            long max = (1L << bitSize) - 1;

            if (value > max) {
                throw new OverflowException(
                        String.valueOf(value), signed, bitSize, lineNumber, line);
            }

            if (value < 0) {
                throw new SyntaxException(
                        "Expected unsigned integer but received '" + value + "'.",
                        lineNumber, line);
            }
        }
    }

    /**
     * Recursive helper to resolve references and detect cycles.
     * 
     * @param wordIndex    Index of the word to resolve.
     * @param programWords List of program words in which unresolved lines have
     *                     placeholder values.
     * @param references   Table defining words that need to be resolved by
     *                     dereferencing another word.
     * @param states       A state for each program word determining if it's 0:
     *                     unresolved, 1: currently resolving, or 2: resolved.
     * @param lines        Provided for helpful error reporting.
     */
    private void resolveWord(
            int wordIndex,
            List<Integer> programWords,
            HashMap<Integer, AsyncReference> references,
            byte[] states,
            String[] lines) throws CyclicDereferenceException {
        if (states[wordIndex] == 2) {
            return;
        }

        if (states[wordIndex] == 1) {
            throw new CyclicDereferenceException(wordIndex + 1, lines[wordIndex]);
        }

        states[wordIndex] = 1;

        AsyncReference ref = references.get(wordIndex);

        if (ref != null) {
            // Resolve the target before reading its value
            resolveWord(ref.destLine(), programWords, references, states, lines);

            int resolvedWord = Format.encodeWord(ref.sourceLineOpcode(), ref.destLine(), false, true);

            programWords.set(wordIndex, resolvedWord);
        }

        states[wordIndex] = 2;
    }
}
