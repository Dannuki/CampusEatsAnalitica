package limpieza;

import java.util.ArrayList;
import java.util.List;
import modelo.LineaPedido;

/**
 * Filtro que descarta aquellas líneas cuya cantidad de platos sea menor o igual a cero.
 */
public class CantidadValida implements Filtro {

    @Override
    public String nombre() {
        return "CantidadValida";
    }

    @Override
    public List<LineaPedido> aplicar(List<LineaPedido> lineas) {
        List<LineaPedido> resultado = new ArrayList<>();
        if (lineas == null) {
            return resultado;
        }

        for (LineaPedido linea : lineas) {
            if (linea.cantidad() > 0) {
                resultado.add(linea);
            }
        }
        return resultado;
    }
}

