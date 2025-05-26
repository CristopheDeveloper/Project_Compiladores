package umg.edu.gt.compilador.model;


public class LetInstruction  extends  Instruction {
    public final String identifier;
    public final Expression expression;

    public LetInstruction(String identifier, Expression expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "Let(" + identifier + " = " + expression + ")";
    }
}
