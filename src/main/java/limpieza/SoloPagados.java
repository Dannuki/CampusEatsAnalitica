package limpieza;

import java.util.ArrayList;
import java.util.List;
import modelo.LineaPedido;

/**
 * Filtro que conserva únicamente las líneas de pedidos que alguna vez pasaron por el estado PAGADO según el historial de estados
 */
public class SoloPagados implements Filtro {

    @Override
    public String nombre() {
        return "SoloPagados";
    }

    @Override
    public List<LineaPedido> aplicar(List<LineaPedido> lineas) {
        List<LineaPedido> resultado = new ArrayList<>();
        if (lineas == null) {
            return resultado;
        }

        for (LineaPedido linea : lineas) {
            if (linea.fechaPagado() != null) {
                resultado.add(linea);
            }
        }
        return resultado;
    }
}

