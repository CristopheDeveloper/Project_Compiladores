package umg.edu.gt;

import umg.edu.gt.compilador.lexer.Lexer;
import umg.edu.gt.compilador.lexer.Lexer.Token;
import umg.edu.gt.compilador.parser.Parser;
import umg.edu.gt.compilador.model.Expression;
import umg.edu.gt.compilador.semantic.Evaluator;
import umg.edu.gt.compilador.util.ErrorManager;
import umg.edu.gt.compilador.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // 1. Lee todas las líneas del .c25
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese el archivo que desea cargar ruta: ");
        String archivo = scanner.nextLine();
        Path input = Path.of(archivo);
        List<String> lines = Files.readAllLines(input);

        ErrorManager err = new ErrorManager();
        List<String> results = new ArrayList<>();

        int lineNumber = 1;
        for (String raw : lines) {
            String t = raw.trim();

            // Error 1: falta ';'
            if (!t.endsWith(";")) {
                err.agregarError(lineNumber, 1);
                lineNumber++;
                continue;
            }
            // Error 4: no inicia con f(
            if (!t.startsWith("f(")) {
                err.agregarError(lineNumber, 4);
                lineNumber++;
                continue;
            }


            int eqPos = t.indexOf('=');
            if (eqPos < 0) {
                err.agregarError(lineNumber, 5);
                lineNumber++;
                continue;
            }
            String lhs = t.substring(0, eqPos).trim();                   // p.ej. "f(x)"
            String rhs = t.substring(eqPos + 1, t.length() - 1).trim();  // p.ej. "2x + 1"


            if (!lhs.matches("f\\([A-Za-z]\\)")) {
                err.agregarError(lineNumber, 6);
                lineNumber++;
                continue;
            }


            String declaredVar = lhs.substring(2, 3);


            Lexer lexer = new Lexer(rhs);
            List<Token> tokens = lexer.tokenize();


            Set<String> vars = new HashSet<>();
            for (Token token : tokens) {
                if (token.type == Lexer.TokenType.IDENTIFIER) {
                    vars.add(token.value);
                }
            }
            if (vars.size() > 1) {
                err.agregarError(lineNumber, 3);
                lineNumber++;
                continue;
            }


            if (!vars.contains(declaredVar)) {
                err.agregarError(lineNumber, 2);
                lineNumber++;
                continue;
            }


            Parser parser = new Parser(tokens);
            Expression expr;
            try {
                expr = parser.parseExpression();
            } catch (RuntimeException ex) {
                err.agregarError(lineNumber, 5);
                lineNumber++;
                continue;
            }


            Evaluator evaluator = new Evaluator();
            evaluator.ejecutar(declaredVar, expr);


            results.add(lineNumber + ".- " + evaluator.getUltimaAsignacion());

            lineNumber++;
        }


        FileUtils.writeFile("salida.res",   String.join("\n", results));
        FileUtils.writeFile("35723025.err", String.join("\n", err.getErrores()));

        System.out.println("Compilación finalizada.");
    }
}
