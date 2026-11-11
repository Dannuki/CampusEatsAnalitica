package metricas;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import modelo.LineaPedido;

//Calcula la cantidad de pedidos realizados por cada hora del día,
//agrupando por la hora en que el pedido fue efectivamente pagado

public class PedidosPorHora implements Metrica<Map<Integer, Integer>> {

    @Override
    public String nombre() {
        return "Pedidos por hora";
    }

    @Override
    public Map<Integer, Integer> calcular(List<LineaPedido> datos) {
        if (datos == null || datos.isEmpty()) {
            return Collections.emptyMap();
        }

        // Agrupamos por numeroPedido único para conservar la fecha y hora de pago
        // (fechaPagado) de cada pedido
        Map<String, LocalDateTime> pedidosUnicos = new HashMap<>();
        for (LineaPedido linea : datos) {
            if (linea == null || linea.numeroPedido() == null || linea.fechaPagado() == null) {
                continue;
            }
            pedidosUnicos.putIfAbsent(linea.numeroPedido(), linea.fechaPagado());
        }

        // Contamos la cantidad de pedidos por hora cronológica (TreeMap asegura orden
        // por hora)
        Map<Integer, Integer> pedidosPorHora = new TreeMap<>();
        for (LocalDateTime fechaHora : pedidosUnicos.values()) {
            int hora = fechaHora.getHour();
            pedidosPorHora.merge(hora, 1, Integer::sum);
        }

        return Collections.unmodifiableMap(pedidosPorHora);
    }
}
