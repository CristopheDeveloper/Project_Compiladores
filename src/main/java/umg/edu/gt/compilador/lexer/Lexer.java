// Paquete donde se encuentra la clase Lexer
package umg.edu.gt.compilador.lexer;

// Importación de clases necesarias
import java.util.ArrayList;
import java.util.List;

// Clase Lexer: se encarga de dividir una cadena de entrada en tokens léxicos
public class Lexer {

    // Cadena de entrada a analizar
    private final String input;

    // Posición actual dentro de la cadena
    private int position = 0;

    // Constructor: recibe la cadena de entrada a tokenizar
    public Lexer(String input) {
        this.input = input;
    }

    // Tipos de tokens que puede reconocer el lexer
    public enum TokenType {
        KEYWORD,     // Palabras clave como 'let', 'print'
        IDENTIFIER,  // Identificadores (nombres de variables)
        NUMBER,      // Números
        OPERATOR,    // Operadores como +, -, *, /
        SYMBOL,      // Símbolos como paréntesis, punto y coma, llaves
        EOF          // Fin del archivo (fin de la entrada)
    }

    // Clase Token: representa un token con su tipo y valor
    public static class Token {
        public final TokenType type;
        public final String value;

        // Constructor del token
        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        // Representación legible del token
        @Override
        public String toString() {
            return "[" + type + ": " + value + "]";
        }
    }

    // Método para ver el carácter actual sin avanzar la posición
    private char peek() {
        return position < input.length() ? input.charAt(position) : '\0';  // '\0' representa fin
    }

    // Método que devuelve el carácter actual y avanza la posición
    private char advance() {
        return input.charAt(position++);
    }

    // Método que omite cualquier carácter de espacio, tabulación o salto de línea
    private void skipWhitespace() {
        while (position < input.length()) {
            char c = peek();
            if (Character.isWhitespace(c)) {
                advance();
            } else {
                break;
            }
        }
    }

    // Método principal que divide la entrada en una lista de tokens
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        // Recorre toda la entrada
        while (position < input.length()) {
            skipWhitespace(); // Omite espacios
            char current = peek(); // Mira el carácter actual

            // Si comienza con letra, puede ser una palabra clave o identificador
            if (Character.isLetter(current)) {
                String word = readWord();
                // Si es palabra clave conocida
                if (word.equals("let") || word.equals("print")) {
                    tokens.add(new Token(TokenType.KEYWORD, word));
                } else {
                    tokens.add(new Token(TokenType.IDENTIFIER, word));
                }

                // Si comienza con número, puede ser un número o multiplicación implícita (como 2x)
            } else if (Character.isDigit(current)) {
                String number = readNumber();

                // Si justo después hay una letra (como en "2x"), se interpreta como 2 * x
                if (Character.isLetter(peek())) {
                    tokens.add(new Token(TokenType.NUMBER, number));
                    tokens.add(new Token(TokenType.OPERATOR, "*")); // inserta operador multiplicación
                    tokens.add(new Token(TokenType.IDENTIFIER, String.valueOf(advance()))); // inserta identificador
                } else {
                    tokens.add(new Token(TokenType.NUMBER, number));
                }

                // Si es un operador (+ - * / = ^)
            } else if ("=+-*/^".indexOf(current) >= 0)  {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(advance())));

                // Si es un símbolo especial (paréntesis, punto y coma, llaves)
            } else if ("();{}".indexOf(current) >= 0) {
                tokens.add(new Token(TokenType.SYMBOL, String.valueOf(advance())));

                // Si es un carácter inesperado o inválido
            } else {
                // Solo muestra error si es visible y no espacio
                if (!Character.isWhitespace(current) && current != '\0') {
                    System.err.println("Unexpected character: " + current);
                }
                advance(); // Se salta para evitar bucles infinitos
            }
        }

        // Agrega token de fin de entrada
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    // Método auxiliar para leer una palabra compuesta de letras o dígitos
    private String readWord() {
        StringBuilder sb = new StringBuilder();
        while (Character.isLetterOrDigit(peek())) {
            sb.append(advance());
        }
        return sb.toString();
    }

    // Método auxiliar para leer una secuencia numérica
    private String readNumber() {
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(peek())) {
            sb.append(advance());
        }
        return sb.toString();
    }
}
