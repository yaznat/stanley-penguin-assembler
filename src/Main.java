import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import assembler.Lexer;
import assembler.Parser;
import assembler.Token;
import exception.CyclicDereferenceException;
import exception.OverflowException;
import exception.SyntaxException;

/**
 * A demonstration of the Stanley/Penguin compiler.
 * 
 * <p>
 * Compiles Stanley/Penguin assembly into machine code and prints the results.
 */
public class Main {
    private static final String SOURCE_PATH = "./scripts/demo.peng";

    public static void main(String[] args) {
        System.out.println("Stanley/Penguin Compilation Demo");

        Lexer l = new Lexer();
        Parser p = new Parser();

        try {
            Path file = Path.of(SOURCE_PATH);
            String fileContent = Files.readString(file);
            String[] lines = fileContent.split("\n", -1);

            try {
                List<Token> tokens = l.lex(lines);
                List<Integer> words = p.parse(tokens, lines);

                System.out.println("===== MACHINE CODE: =====");
                for (Integer w : words) {
                    String hex = Integer.toHexString(w).toUpperCase();
                    System.out.println("0".repeat(Math.max(0, 8 - hex.length())) + hex);
                }
            } catch (SyntaxException e) {
                System.out.println(e.getMessage());
            } catch (OverflowException e) {
                System.out.println(e.getMessage());
            } catch (CyclicDereferenceException e) {
                System.out.println(e.getMessage());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
