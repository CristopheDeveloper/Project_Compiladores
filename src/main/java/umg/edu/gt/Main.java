package umg.edu.gt;

import umg.edu.gt.compilador.lexer.Lexer;
import umg.edu.gt.compilador.model.Instruction;
import umg.edu.gt.compilador.parser.Parser;
import umg.edu.gt.compilador.semantic.Evaluator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {
        String code = new String(Files.readAllBytes(Paths.get("src/main/resources/test.txt")));

        Lexer lexer = new Lexer(code);
        List<Lexer.Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        List<Instruction> instructions = parser.parseProgram();

        Evaluator evaluator = new Evaluator();
        evaluator.execute(instructions);
    }
}