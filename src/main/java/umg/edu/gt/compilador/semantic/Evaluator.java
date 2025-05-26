package umg.edu.gt.compilador.semantic;

import umg.edu.gt.compilador.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluator {
    private final Map<String, Integer> environment = new HashMap<>();

    public void execute(List<Instruction> instructions) {
        for (Instruction instr : instructions) {
            if (instr instanceof LetInstruction letInstr) {
                int value = evaluateExpression(letInstr.expression);
                environment.put(letInstr.identifier, value);
            } else if (instr instanceof PrintInstruction printInstr) {
                int value = evaluateExpression(printInstr.expression);
                System.out.println(value);
            } else {
                throw new RuntimeException("Instrucción desconocida: " + instr);
            }
        }
    }

    private int evaluateExpression(Expression expr) {
        if (expr instanceof NumberExpression numExpr) {
            return Integer.parseInt(numExpr.value);
        } else if (expr instanceof IdentifierExpression idExpr) {
            if (!environment.containsKey(idExpr.name)) {
                throw new RuntimeException("Variable no definida: " + idExpr.name);
            }
            return environment.get(idExpr.name);
        } else if (expr instanceof BinaryExpression binExpr) {
            int left = evaluateExpression(binExpr.left);
            int right = evaluateExpression(binExpr.right);

            switch (binExpr.operator) {
                case "+":
                    return left + right;
                case "-":
                    return left - right;
                case "*":
                    return left * right;
                case "/":
                    if (right != 0) {
                        return left / right;
                    } else {
                        throw new ArithmeticException("División por cero");
                    }
                default:
                    throw new RuntimeException("Operador desconocido: " + binExpr.operator);
            }
        } else {
            throw new RuntimeException("Expresión no reconocida: " + expr);
        }
    }

}
