package umg.edu.gt;

import org.junit.jupiter.api.Test;
import umg.edu.gt.compilador.lexer.Lexer;
import umg.edu.gt.compilador.parser.Parser;
import umg.edu.gt.compilador.model.Expression;
import umg.edu.gt.compilador.semantic.Evaluator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EvaluatorTest {
    @Test
    public void solveSimpleEquation() {
        Lexer lx = new Lexer("x+1");
        Parser p = new Parser(lx.tokenize());
        Expression expr = p.parseExpression();
        Evaluator ev = new Evaluator();
        ev.ejecutar("x", expr);
        assertEquals("x=-1.0", ev.getUltimaAsignacion());
    }

    @Test
    public void handlesUppercaseVariable() {
        Lexer lx = new Lexer("X+1");
        Parser p = new Parser(lx.tokenize());
        Expression expr = p.parseExpression();
        Evaluator ev = new Evaluator();
        ev.ejecutar("x", expr);
        assertEquals("x=-1.0", ev.getUltimaAsignacion());
    }
}
