package metricas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.LineaPedido;

//Calcula el ticket promedio (dinero gastado promedio por pedido).

public class TicketPromedio implements Metrica<Double> {

    @Override
    public String nombre() {
        return "Ticket promedio";
    }

    @Override
    public Double calcular(List<LineaPedido> datos) {
        if (datos == null || datos.isEmpty()) {
            return 0.0;
        }

        // Mapeamos numeroPedido al total del pedido, garantizando cada pedido una sola
        // vez
        Map<String, Double> totalPorPedido = new HashMap<>();
        for (LineaPedido linea : datos) {
            if (linea == null || linea.numeroPedido() == null) {
                continue;
            }
            totalPorPedido.putIfAbsent(linea.numeroPedido(), linea.totalPedido());
        }

        if (totalPorPedido.isEmpty()) {
            return 0.0;
        }

        double sumaTotales = 0.0;
        for (double total : totalPorPedido.values()) {
            sumaTotales += total;
        }

        return sumaTotales / totalPorPedido.size();
    }
}
