package umg.edu.gt.compilador.lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private final String input;
    private int position = 0;

    public Lexer(String input) {
        this.input = input;
    }

    public enum TokenType {
        KEYWORD, IDENTIFIER, NUMBER, OPERATOR, SYMBOL, EOF
    }

    public static class Token {
        public final TokenType type;
        public final String value;

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return "[" + type + ": " + value + "]";
        }
    }

    private char peek() {
        return position < input.length() ? input.charAt(position) : '\0';
    }

    private char advance() {
        return input.charAt(position++);
    }

    private void skipWhitespace() {
        while (position < input.length()) {
            char c = peek();
            // Ignorar cualquier tipo de espacio (incluye tabs, \r, \n, etc.)
            if (Character.isWhitespace(c)) {
                advance();
            } else {
                break;
            }
        }
    }
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (position < input.length()) {
            skipWhitespace();
            char current = peek();

            if (Character.isLetter(current)) {
                String word = readWord();
                if (word.equals("let") || word.equals("print")) {
                    tokens.add(new Token(TokenType.KEYWORD, word));
                } else {
                    tokens.add(new Token(TokenType.IDENTIFIER, word));
                }
            } else if (Character.isDigit(current)) {
                String number = readNumber();
                // Si después viene una letra (como en "2x"), insertar multiplicación
                if (Character.isLetter(peek())) {
                    tokens.add(new Token(TokenType.NUMBER, number));
                    tokens.add(new Token(TokenType.OPERATOR, "*"));
                    tokens.add(new Token(TokenType.IDENTIFIER, String.valueOf(advance())));
                } else {
                    tokens.add(new Token(TokenType.NUMBER, number));
                }
            } else if ("=+-*/".indexOf(current) >= 0) {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(advance())));
            } else if ("();{}".indexOf(current) >= 0) {
                tokens.add(new Token(TokenType.SYMBOL, String.valueOf(advance())));
            } else {
                // Solo imprimir si realmente es un carácter no válido y visible
                if (!Character.isWhitespace(current) && current != '\0') {
                    System.err.println("Unexpected character: " + current);
                }
                advance(); // seguir avanzando para evitar bloqueo
            }
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }


    private String readWord() {
        StringBuilder sb = new StringBuilder();
        while (Character.isLetterOrDigit(peek())) {
            sb.append(advance());
        }
        return sb.toString();
    }

    private String readNumber() {
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(peek())) {
            sb.append(advance());
        }
        return sb.toString();
    }
}
