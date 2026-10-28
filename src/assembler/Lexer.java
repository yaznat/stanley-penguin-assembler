package assembler;

import java.util.ArrayList;
import java.util.List;

import exception.SyntaxException;

/**
 * Interprets raw file content into tokens.
 */
public class Lexer {
    public Lexer() {
    }

    /**
     * Splits raw file content into a sequence of structured tokens. Lines are
     * delimited by newline tokens.
     * 
     * @param lines The raw file content, split by lines (empty lines should be
     *              included as {@code ""}).
     * @return The list of parsed tokens.
     * @throws SyntaxException If disallowed chars are present or if key chars are
     *                         used improperly.
     */
    public List<Token> lex(String[] lines) throws SyntaxException {
        List<Token> tokens = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].isEmpty())
                continue;

            // Truncate comments
            String cleaned = lines[i];
            int semicolonIndex = cleaned.indexOf(";");
            if (semicolonIndex == 0)
                continue;
            if (semicolonIndex != -1) {
                cleaned = cleaned.substring(0, semicolonIndex);
            }

            String[] split = cleaned.trim().split("\\s+");

            for (String token : split) {
                // Enforce allowed chars
                for (int k = 0; k < token.length(); k++) {
                    char c = token.charAt(k);
                    if (!Format.isAllowed(c)) {
                        throw new SyntaxException(
                                "Invalid character '" + c + "'.",
                                i + 1, // line number
                                lines[i] // line content
                        );
                    }
                }
                // Handle dereference tokens ([])
                if (token.contains("[") || token.contains("]")) {
                    validateDereference(token, i + 1, lines[i]);
                    tokens.add(new Token(TokenType.DEREFERENCE, token, i + 1));
                }
                // Handle instruction
                else if (Format.parseInstruction(token) != null) {
                    tokens.add(new Token(TokenType.INSTRUCTION, token, i + 1));
                }
                // Handle label (:)
                else if (token.contains(":")) {
                    validateLabel(token, i + 1, lines[i]);
                    tokens.add(new Token(TokenType.LABEL, token.substring(0, token.length() - 1), i + 1));
                }
                // Handle numeric value
                else if (Format.isNumericInteger(token)) {
                    tokens.add(new Token(TokenType.INTEGER, token, i + 1));
                }
                // Fallback to identifier
                else {
                    tokens.add(new Token(TokenType.IDENTIFIER, token, i + 1));
                }
            }

            // Delimiter
            tokens.add(new Token(TokenType.NEWLINE, "\n", i + 1));
        }

        return tokens;
    }

    /**
     * Validates a token that contains bracket dereferencing characters,
     * ensuring that brackets are matched and properly placed.
     * 
     * <p>
     * <b>Assumption</b>: {@code token} contains {@code '['} or {@code ']'}.
     *
     * @param token       The raw token to validate.
     * @param lineNumber  The line number (for error reporting).
     * @param lineContent The raw content of the entire line (for error reporting).
     * @throws SyntaxException If brackets are mismatched or improperly placed.
     */
    private void validateDereference(String token, int lineNumber, String lineContent) throws SyntaxException {
        // Ensure brackets open and close the sequence
        boolean textFound = false;
        boolean closingBracketFound = false;

        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if (c == '[') {
                if (textFound) {
                    throw new SyntaxException("Invalid placement of character '['.", lineNumber, lineContent);
                }
            } else if (c == ']') {
                closingBracketFound = true;
            } else {
                textFound = true;
                if (closingBracketFound) {
                    throw new SyntaxException("Invalid placement of character ']'.", lineNumber, lineContent);
                }
            }
        }

        // Ensure brackets are matched
        for (int i = 0; i < token.length() / 2; i++) {
            if ((token.charAt(i) == '[') != (token.charAt(token.length() - i - 1) == ']')) {
                throw new SyntaxException("Unmatched brackets.", lineNumber, lineContent);
            }
        }
    }

    /**
     * Validates a token that contains {@code ':'}, ensuring the colon is used properly.
     * 
     * Also catches the edge case in which a reserved keyword is used as an
     * label.
     * 
     * <p>
     * <b>Assumption</b>: {@code token} contains {@code ':'}.
     * 
     * @param token       The raw token to validate.
     * @param lineNumber  The line number (for error reporting).
     * @param lineContent The raw content of the entire line (for error reporting).
     * @throws SyntaxException If the token is an invalid label.
     */
    private void validateLabel(String token, int lineNumber, String lineContent) throws SyntaxException {
        if (token.indexOf(':') != token.length() - 1) {
            throw new SyntaxException("Misplaced ':' in token '" + token + "'.", lineNumber, lineContent);
        }

        // Edge case
        String label = token.substring(0, token.length() - 1);
        if (Format.parseInstruction(label) != null) {
            throw new SyntaxException(
                    "Reserved keyword '" + label + "' cannot be used as a label.",
                    lineNumber, lineContent);
        }
    }
}
