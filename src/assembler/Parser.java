package assembler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
     * @throws OverflowException If integer values exceed the capacity of
     *                           the operand.
     * @throws SyntaxException   If tokens are used improperly.
     */
    public List<Integer> parse(List<Token> tokens, String[] lines)
            throws OverflowException, SyntaxException {
        List<Integer> programWords = new ArrayList<>();

        // First pass to build symbol table
        HashMap<String, Integer> symbolTable = new HashMap<>();
        buildSymbolTable(tokens, lines, symbolTable);

        // States tracked for syntax checks
        Integer opcode = null;
        Integer operand = null;
        boolean dereference = false;

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.type() == TokenType.INSTRUCTION) {
                if (opcode != null) {
                    throw new SyntaxException(
                            "Duplicate instruction '" + t.lexeme() + "'.",
                            t.line(), lines[t.line() - 1]);
                }
                if (operand != null) {
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
                if (operand != null) {
                    throw new SyntaxException(
                            "Extraneous identifier '" + t.lexeme() + "'.",
                            t.line(), lines[t.line() - 1]);
                }
                operand = location;
            } else if (t.type() == TokenType.INTEGER) {
                if (operand != null) {
                    throw new SyntaxException(
                            "Extraneous integer '" + t.lexeme() + "'.",
                            t.line(), lines[t.line() - 1]);
                }
                boolean signed = opcode == null || !(opcode < 0x4 || opcode > 0xA);
                operand = parseInteger(t.lexeme(), signed, t.line(), lines[t.line() - 1]);
            } else if (t.type() == TokenType.DEREFERENCE) {
                if (operand != null) {
                    throw new SyntaxException(
                            "Extraneous token '" + t.lexeme() + "'.",
                            t.line(), lines[t.line() - 1]);
                }

                String cleaned = t.lexeme().replace("[", "").replace("]", "");
                Integer location = symbolTable.getOrDefault(cleaned, null);
                if (location == null) {
                    throw new SyntaxException(
                            "Invalid identifier '" + cleaned + "'.",
                            t.line(), lines[t.line() - 1]);
                }

                operand = location;
                dereference = true;
            } else if (t.type() == TokenType.NEWLINE && (opcode != null || operand != null)) {
                if (opcode != null && operand == null) {
                    throw new SyntaxException(
                            "Missing operand for instruction.",
                            t.line(), lines[t.line() - 1]);
                }

                if (opcode == null && dereference) {
                    throw new SyntaxException(
                            "Unintended behavior - storing a dereference promise as an integer value.",
                            t.line(), lines[t.line() - 1]);
                }

                if (opcode == null) {
                    // 32-bit signed integer value
                    programWords.add(operand);
                } else {
                    boolean signed = false;
                    overflowCheck(operand, signed, Format.OPERAND_SIZE_BITS, t.line(), lines[t.line() - 1]);
                    programWords.add(Format.encodeWord(opcode, operand, signed, dereference));
                }

                // Reset state
                opcode = null;
                operand = null;
                dereference = false;
            }
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
                int startIdx = lexeme.startsWith("-") ? 1 : 0;
                String hexDigits = lexeme.substring(0, startIdx) +
                        lexeme.substring(startIdx + 2);

                value = Long.parseUnsignedLong(hexDigits, 16);

                if (signed && startIdx == 0 && value <= 0xFFFFFFFFL) {
                    value = (int) value;
                }

                if (startIdx == 1) {
                    value = -value;
                }
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
}
