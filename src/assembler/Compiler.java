package assembler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import exception.CyclicDereferenceException;
import exception.OverflowException;
import exception.SyntaxException;
import io.EggWriter;

/**
 * Top-level component that compiles a raw file into binary.
 */
public class Compiler {
    public Compiler() {
    }

    public void compile(String filepath, String outpath) throws IOException {
        Path file = Path.of(filepath);
        String fileContent = Files.readString(file);
        String[] lines = fileContent.split("\n", -1);

        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        EggWriter writer = new EggWriter();

        try {
            List<Token> tokens = lexer.lex(lines);
            List<Integer> words = parser.parse(tokens, lines);
            writer.saveBinary(words, outpath);
        } catch (SyntaxException e) {
            System.out.println(e.getMessage());
        } catch (OverflowException e) {
            System.out.println(e.getMessage());
        } catch (CyclicDereferenceException e) {
            System.out.println(e.getMessage());
        }
    }
}
