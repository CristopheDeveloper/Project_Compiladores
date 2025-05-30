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
import java.util.regex.*;

public class Main {


    public static void main(String[] args) throws IOException {


        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese la ruta del archivo .c25: ");
        Path input = Path.of(sc.nextLine().trim());   // ej. src/main/resources/test.c25

        List<String> lines = Files.readAllLines(input);

        /* 2. Inicializar gestores -------------------------------------- */
        ErrorManager err = new ErrorManager();
        List<String> res = new ArrayList<>();

        Pattern fxPattern = Pattern.compile("(?i)f\\s*\\(\\s*([a-z])\\s*\\)");

        /* 3. Procesar línea a línea ------------------------------------ */
        int n = 1;
        for (String raw : lines) {

            if (raw == null || raw.isBlank()) { n++; continue; }      // saltar vacías
            String t = raw.trim();

            /* Encabezados o comentarios (no empiezan con f(…) ) */
            if (!t.toLowerCase().startsWith("f(")) { n++; continue; }

            /* Error 1: falta ‘;’ */
            if (!t.endsWith(";")) { err.agregarError(n, 1); n++; continue; }

            /* Extraer LHS y RHS */
            int eq = t.indexOf('=');
            if (eq < 0) { err.agregarError(n, 5); n++; continue; }    // Error 5

            String lhs = t.substring(0, eq).trim();
            String rhs = t.substring(eq + 1, t.length() - 1).trim(); // sin ';'

            /* Error 6: mal formado f(x) (pero acepta espacios y mayúsculas) */
            Matcher m = fxPattern.matcher(lhs);
            if (!m.matches()) { err.agregarError(n, 6); n++; continue; }

            /* Variable declarada (minúscula) */
            String declared = m.group(1).toLowerCase();

            /* Tokenizar RHS */
            Lexer lexer = new Lexer(rhs);
            List<Token> tokens = lexer.tokenize();

            /* Error 7: operador no soportado */
            boolean opInvalid = tokens.stream()
                    .anyMatch(tk -> tk.type == Lexer.TokenType.OPERATOR &&
                            !List.of("+", "-", "*", "/").contains(tk.value));
            if (opInvalid) { err.agregarError(n, 7); n++; continue; }

            /* Error 3 y 2: variables presentes */
            Set<String> vars = new HashSet<>();
            for (Token tk : tokens)
                if (tk.type == Lexer.TokenType.IDENTIFIER)
                    vars.add(tk.value.toLowerCase());

            if (vars.size() > 1)           { err.agregarError(n, 3); n++; continue; } // Error 3
            if (!vars.contains(declared))  { err.agregarError(n, 2); n++; continue; } // Error 2

            /* Parsear expresión */
            Parser parser = new Parser(tokens);
            Expression expr;
            try {
                expr = parser.parseExpression();
            } catch (RuntimeException ex) {
                err.agregarError(n, 5); n++; continue; // Error 5
            }

            /* Evaluar (ax + b = 0) */
            Evaluator ev = new Evaluator();
            ev.ejecutar(declared, expr);

            res.add(n + ".- " + ev.getUltimaAsignacion());
            n++;
        }

        /* 4. Escribir salidas ----------------------------------------- */
        FileUtils.writeFile("salida.res", String.join("\n", res));
        FileUtils.writeFile("pruebas2.err", String.join("\n", err.getErrores()));

        System.out.println("Compilación finalizada.");
    }
}
