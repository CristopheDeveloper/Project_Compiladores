package umg.edu.gt.compilador.model;

public class PrintInstruction extends Instruction {
    public final Expression expression;

    public PrintInstruction(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "Print(" + expression + ")";
    }
}
