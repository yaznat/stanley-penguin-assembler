package assembler;

import java.util.ArrayList;
import java.util.List;

/**
 * Interprets raw file content into tokens.
 */
public class Lexer {
    public Lexer() {}

    /**
     * Parses raw file content into a sequence of structured tokens. Lines are split by newline tokens.
     * @param fileContent The raw file content. 
     * @return The list of parsed tokens.
     * @throws SyntaxException If disallowed chars are present or if key chars are used improperly.
     */
    public List<Token> lex(String fileContent) throws SyntaxException {
        List<Token> tokens = new ArrayList<>();

        String[] lines = fileContent.split("\n", -1);

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].isEmpty()) continue;

            // Truncate comments
            String cleaned = lines[i];
            int semicolonIndex = cleaned.indexOf(";");
            if (semicolonIndex == 0) continue;
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
                // Handle identifier (:)
                else if (token.endsWith(":")) {
                    validateIdentifier(token, i + 1, lines[i]);
                    tokens.add(new Token(TokenType.IDENTIFIER, token.substring(0, token.length() -  1), i + 1));
                }
                // Fallback to value
                else {
                    tokens.add(new Token(TokenType.VALUE, token, i + 1));
                }
            }

            tokens.add(new Token(TokenType.NEWLINE, "\n", i + 1));
        }

        return tokens;
    }

    /**
     * Validates a token that contains bracket dereferencing characters,
     * ensuring that brackets are matched and properly placed. 
     * 
     * <p><b>Assumption</b>: {@code token} contains {@code '['} or {@code ']'}.
     *
     * @param token The raw token to validate.
     * @param lineNumber The line number (for error reporting).
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
     * Validates a token that ends with {@code ':'}, ensuring it contains no other colons.
     * Also catches the edge case in which a reserved keyword is used as an identifier.
     * 
     * <p><b>Assumption</b>: {@code token} ends with {@code ':'}.
     * 
     * @param token The raw token to validate.
     * @param lineNumber The line number (for error reporting).
     * @param lineContent The raw content of the entire line (for error reporting).
     * @throws SyntaxException If the token is an invalid identifier.
     */
    private void validateIdentifier(String token, int lineNumber, String lineContent) throws SyntaxException {
        if (token.indexOf(':') != token.length() - 1) {
            throw new SyntaxException("Invalid placement of character ':'.", lineNumber, lineContent);
        }

        // Edge case
        String identifier = token.substring(0, token.length() - 1);
        if (Format.parseInstruction(identifier) != null) {
            throw new SyntaxException(
                "Reserved keyword '" + identifier + "' cannot be used as an identifier.",
                lineNumber, lineContent
            );
        }
    }
}
