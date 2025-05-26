package umg.edu.gt.compilador.parser;

import umg.edu.gt.compilador.lexer.Lexer;
import umg.edu.gt.compilador.lexer.Lexer.Token;
import umg.edu.gt.compilador.lexer.Lexer.TokenType;
import umg.edu.gt.compilador.model.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int position = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token peek() {
        return tokens.get(position);
    }

    private Token advance() {
        return tokens.get(position++);
    }

    private boolean match(TokenType type, String value) {
        if (position < tokens.size()) {
            Token token = peek();
            return token.type == type && token.value.equals(value);
        }
        return false;
    }

    private boolean match(TokenType type) {
        if (position < tokens.size() && peek().type == type) {
            return true;
        }
        return false;
    }

    public List<Instruction> parseProgram() {
        List<Instruction> instructions = new ArrayList<>();

        while (!match(TokenType.EOF)) {
            instructions.add(parseStatement());
        }

        return instructions;
    }

    private Instruction parseStatement() {
        if (match(TokenType.KEYWORD, "let")) {
            advance(); // consume "let"
            String identifier = advance().value; // consume IDENTIFIER
            advance(); // consume '='
            Expression expr = parseExpression();
            advance(); // consume ';'
            return new LetInstruction(identifier, expr);
        } else if (match(TokenType.KEYWORD, "print")) {
            advance(); // consume "print"
            advance(); // consume '('
            Expression expr = parseExpression();
            advance(); // consume ')'
            advance(); // consume ';'
            return new PrintInstruction(expr);
        } else {
            throw new RuntimeException("Unknown statement: " + peek());
        }
    }

    private Expression parseExpression() {
        Expression left = parseTerm();

        while (match(TokenType.OPERATOR)) {
            String op = advance().value;
            Expression right = parseTerm();
            left = new BinaryExpression(left, op, right);
        }

        return left;
    }

    private Expression parseTerm() {
        Token token = advance();

        if (token.type == TokenType.NUMBER) {
            return new NumberExpression(token.value);
        } else if (token.type == TokenType.IDENTIFIER) {
            return new IdentifierExpression(token.value);
        } else {
            throw new RuntimeException("Expected term, got: " + token);
        }
    }
}
