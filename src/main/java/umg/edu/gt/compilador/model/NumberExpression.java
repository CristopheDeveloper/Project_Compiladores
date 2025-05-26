package umg.edu.gt.compilador.model;

public class NumberExpression extends Expression {
    public final String value;

    public NumberExpression(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
