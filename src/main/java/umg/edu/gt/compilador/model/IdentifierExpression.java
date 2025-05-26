package umg.edu.gt.compilador.model;

public class IdentifierExpression extends Expression {
    public final String name;

    public IdentifierExpression(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
