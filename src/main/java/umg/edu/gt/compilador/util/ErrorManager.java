package umg.edu.gt.compilador.util;

import java.util.ArrayList;
import java.util.List;

public class ErrorManager {
    private final List<String> errores = new ArrayList<>();

    public void agregarError(int linea, int codigo) {
        errores.add(linea + ".- Error " + codigo);
    }

    public boolean tieneErrores() {
        return !errores.isEmpty();
    }

    public List<String> getErrores() {
        return errores;
    }
}
