// Paquete donde se encuentra la clase Evaluator
package umg.edu.gt.compilador.semantic;

// Importación de las clases del modelo del AST (Árbol de Sintaxis Abstracta)
import umg.edu.gt.compilador.model.*;

// Clase Evaluator: se encarga de evaluar expresiones para hallar soluciones de ecuaciones lineales
public class Evaluator {

    // Almacena la última asignación realizada en formato "variable=valor"
    private String ultimaAsignacion = "";

    // Método público que retorna la última asignación calculada
    public String getUltimaAsignacion() { return ultimaAsignacion; }

    /**
     * Método privado que calcula los coeficientes [a, b] de una expresión.
     * Interpreta que cualquier expresión se puede reducir a la forma a·v + b
     * donde 'v' es la variable de interés.
     */
    private double[] coef(Expression e, String var) {
        // Si la expresión es un número (constante), devuelve a = 0 y b = valor
        if (e instanceof NumberExpression n)
            return new double[]{0, Double.parseDouble(n.value)};

        // Si la expresión es una variable (identificador)
        if (e instanceof IdentifierExpression id)
            return new double[]{ id.name.equals(var) ? 1 : 0 , 0};  // a = 1 si coincide con var, si no a = 0

        // Si es una expresión binaria (con operador)
        BinaryExpression b = (BinaryExpression) e;

        // Se obtienen los coeficientes del lado izquierdo y derecho
        double[] l = coef(b.left,  var);
        double[] r = coef(b.right, var);

        // Se combinan los coeficientes según el operador
        return switch (b.operator) {
            // Suma: se suman los coeficientes a y b por separado
            case "+" -> new double[]{ l[0] + r[0],  l[1] + r[1] };

            // Resta: se restan los coeficientes a y b por separado
            case "-" -> new double[]{ l[0] - r[0],  l[1] - r[1] };

            // Multiplicación: solo se permite número * variable o variable * número
            case "*" -> {
                // Caso válido: número * variable (lado izquierdo es constante, lado derecho es variable)
                if      (l[0]==0 && l[1]!=0 && r[0]==1) yield new double[]{ l[1]*r[0], l[1]*r[1] };

                    // Caso válido: variable * número (lado derecho es constante, lado izquierdo es variable)
                else if (r[0]==0 && r[1]!=0 && l[0]==1) yield new double[]{ r[1]*l[0], r[1]*l[1] };

                    // Otros casos no permitidos
                else throw new RuntimeException("Sólo se permite coeficiente*variable.");
            }

            // División: solo se permite dividir entre una constante
            case "/" -> {
                // Validación: el divisor debe ser constante (a = 0, b ≠ 0)
                if (r[0]==0 && r[1]!=0) {
                    // (a·x + b) / c = (a/c)·x + (b/c)
                    yield new double[]{ l[0]/r[1], l[1]/r[1] };
                } else {
                    throw new RuntimeException("Sólo se permite división por constante.");
                }
            }

            // Cualquier otro operador no es soportado
            default -> throw new RuntimeException("Operador "+b.operator+" no soportado.");
        };
    }

    /**
     * Método público que ejecuta el cálculo de la variable.
     * A partir de una expresión en forma a·v + b, resuelve v = -b/a
     */
    public void ejecutar(String var, Expression expr) {
        // Obtiene los coeficientes a y b de la expresión
        double[] ab = coef(expr, var);   // ab[0] = a, ab[1] = b
        double a = ab[0], b = ab[1];

        // Verifica que la expresión sea de primer grado (a ≠ 0)
        if (a==0) throw new RuntimeException("La ecuación no es de primer grado.");

        // Calcula el valor de la variable usando v = -b/a
        double valor = -b / a;

        // Guarda la asignación como cadena en el atributo
        ultimaAsignacion = var + "=" + valor;
    }
}
