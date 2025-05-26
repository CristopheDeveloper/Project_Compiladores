package umg.edu.gt.compilador.semantic;

import umg.edu.gt.compilador.model.*;

public class Evaluator {

    private String ultimaAsignacion = "";

    public String getUltimaAsignacion() { return ultimaAsignacion; }

    /**  Devuelve  [a , b]  tal que  a·v + b  representa el AST  */
    private double[] coef(Expression e, String var) {
        if (e instanceof NumberExpression n)          return new double[]{0, Double.parseDouble(n.value)};
        if (e instanceof IdentifierExpression id)     return new double[]{ id.name.equals(var) ? 1 : 0 , 0};

        BinaryExpression b = (BinaryExpression) e;
        double[] l = coef(b.left , var);
        double[] r = coef(b.right, var);

        return switch (b.operator) {
            case "+" -> new double[]{ l[0] + r[0],  l[1] + r[1] };
            case "-" -> new double[]{ l[0] - r[0],  l[1] - r[1] };
            case "*" -> {           // sólo permitimos  número * variable   ó  variable * número
                if      (l[0]==0 && l[1]!=0 && r[0]==1) yield new double[]{ l[1]*r[0], l[1]*r[1] }; // num * var
                else if (r[0]==0 && r[1]!=0 && l[0]==1) yield new double[]{ r[1]*l[0], r[1]*l[1] }; // var * num
                else throw new RuntimeException("Sólo se permite coeficiente*variable.");
            }
            default -> throw new RuntimeException("Operador "+b.operator+" no soportado.");
        };
    }

    /**  Calcula  v = -b/a  si la función es lineal y a≠0  */
    public void ejecutar(String var, Expression expr) {
        double[] ab = coef(expr, var);   // a , b
        double a = ab[0], b = ab[1];
        if (a==0) throw new RuntimeException("La ecuación no es de primer grado.");
        double valor = -b / a;
        ultimaAsignacion = var + "=" + valor;
    }
}
