package limpieza;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import modelo.LineaPedido;

/**
 * Filtro que elimina líneas duplicadas provenientes de pedidos registrados más de una vez.
 * Conserva únicamente la primera aparición de cada linea descartando las apariciones repetidas
 */
public class SinDuplicados implements Filtro {

    @Override
    public String nombre() {
        return "SinDuplicados";
    }

    @Override
    public List<LineaPedido> aplicar(List<LineaPedido> lineas) {
        List<LineaPedido> resultado = new ArrayList<>();
        if (lineas == null) {
            return resultado;
        }

        Set<String> clavesVistas = new HashSet<>();
        for (LineaPedido linea : lineas) {
            // Clave única por línea dentro de un pedido
            String clave = linea.numeroPedido() + "#" + linea.platoId();
            if (clavesVistas.add(clave)) {
                resultado.add(linea);
            }
        }
        return resultado;
    }
}

