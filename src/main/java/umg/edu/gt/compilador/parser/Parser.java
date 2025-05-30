// Paquete donde se encuentra el parser (analizador sintáctico)
package umg.edu.gt.compilador.parser;

// Importación de clases necesarias del lexer y del modelo del AST
import umg.edu.gt.compilador.lexer.Lexer;
import umg.edu.gt.compilador.lexer.Lexer.Token;
import umg.edu.gt.compilador.lexer.Lexer.TokenType;
import umg.edu.gt.compilador.model.*;

import java.util.ArrayList;
import java.util.List;

// Clase Parser: interpreta una lista de tokens y genera una representación estructurada (AST)
public class Parser {

    // Lista de tokens a procesar
    private final List<Token> tokens;

    // Índice de posición actual en la lista de tokens
    private int position = 0;

    // Constructor: recibe los tokens a interpretar
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Método que devuelve el token actual sin avanzar la posición
    private Token peek() {
        return tokens.get(position);
    }

    // Método que consume y devuelve el token actual, avanzando a la siguiente posición
    private Token advance() {
        return tokens.get(position++);
    }

    // Método que verifica si el token actual coincide con un tipo y un valor específicos
    private boolean match(TokenType type, String value) {
        if (position < tokens.size()) {
            Token token = peek();
            return token.type == type && token.value.equals(value);
        }
        return false;
    }

    // Método que verifica si el token actual coincide con un tipo (sin importar su valor)
    private boolean match(TokenType type) {
        if (position < tokens.size() && peek().type == type) {
            return true;
        }
        return false;
    }

    // Método principal que analiza todo el programa y devuelve una lista de instrucciones
    public List<Instruction> parseProgram() {
        List<Instruction> instructions = new ArrayList<>();

        // Mientras no se llegue al final del archivo, seguir parseando instrucciones
        while (!match(TokenType.EOF)) {
            instructions.add(parseStatement());  // Analiza cada statement (let, print, etc.)
        }

        return instructions;
    }

    // Método que analiza una instrucción individual
    private Instruction parseStatement() {
        // Si encuentra palabra clave "let", es una asignación
        if (match(TokenType.KEYWORD, "let")) {
            advance(); // consume "let"
            String identifier = advance().value; // consume identificador (nombre de variable)
            advance(); // consume '='
            Expression expr = parseExpression(); // analiza la expresión a la derecha del '='
            advance(); // consume ';'
            return new LetInstruction(identifier, expr); // construye instrucción LET

            // Si encuentra palabra clave "print", es una instrucción de impresión
        } else if (match(TokenType.KEYWORD, "print")) {
            advance(); // consume "print"
            advance(); // consume '('
            Expression expr = parseExpression(); // analiza la expresión dentro del print
            advance(); // consume ')'
            advance(); // consume ';'
            return new PrintInstruction(expr); // construye instrucción PRINT

            // Si no reconoce el tipo de instrucción, lanza error
        } else {
            throw new RuntimeException("Unknown statement: " + peek());
        }
    }

    // Método que analiza expresiones (soporta operadores binarios: +, -, *, etc.)
    public Expression parseExpression() {
        Expression left = parseTerm(); // lee el primer operando

        // Mientras haya operadores, seguir combinando con nuevos operandos
        while (match(TokenType.OPERATOR)) {
            String op = advance().value;     // consume operador
            Expression right = parseTerm();  // consume siguiente operando
            left = new BinaryExpression(left, op, right); // crea nodo binario en el AST
        }

        return left; // devuelve la expresión completa
    }

    // Método que analiza un término simple (número o variable)
    private Expression parseTerm() {
        Token token = advance(); // consume siguiente token

        // Si es número, crea nodo de tipo NumberExpression
        if (token.type == TokenType.NUMBER) {
            return new NumberExpression(token.value);

            // Si es identificador, crea nodo de tipo IdentifierExpression
        } else if (token.type == TokenType.IDENTIFIER) {
            return new IdentifierExpression(token.value);

            // Si no es válido, lanza error
        } else {
            throw new RuntimeException("Expected term, got: " + token);
        }
    }
}
