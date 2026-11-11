package metricas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.LineaPedido;

//Calcula los cinco platos más vendidos en la cafetería,
//sumando la cantidad demandada en cada línea de pedido por nombre de plato.

public class PlatosTop implements Metrica<List<PlatosTop.PlatoVendido>> {

    private static final int LIMITE_TOP = 5;

    // Registro que representa un plato y el total acumulado de venta.
    public record PlatoVendido(String nombrePlato, int cantidad) {
        @Override
        public String toString() {
            return nombrePlato + " (" + cantidad + " unidades)";
        }
    }

    @Override
    public String nombre() {
        return "Platos más vendidos";
    }

    @Override
    public List<PlatoVendido> calcular(List<LineaPedido> datos) {
        if (datos == null || datos.isEmpty()) {
            return Collections.emptyList();
        }

        // Acumulamos las cantidades vendidas por cada nombre de plato
        Map<String, Integer> cantidadesPorPlato = new HashMap<>();
        for (LineaPedido linea : datos) {
            if (linea == null) {
                continue;
            }
            String nombre = linea.nombrePlato();
            if (nombre == null || nombre.isBlank()) {
                nombre = "Plato #" + linea.platoId();
            }
            cantidadesPorPlato.merge(nombre, linea.cantidad(), Integer::sum);
        }

        // Convertimos a lista de registros
        List<PlatoVendido> lista = new ArrayList<>();
        for (Map.Entry<String, Integer> entrada : cantidadesPorPlato.entrySet()) {
            lista.add(new PlatoVendido(entrada.getKey(), entrada.getValue()));
        }

        // Ordenamos en forma descendente por cantidad; en caso de empate,
        // alfabéticamente por nombre
        lista.sort(Comparator
                .comparingInt(PlatoVendido::cantidad).reversed()
                .thenComparing(PlatoVendido::nombrePlato));

        int limite = Math.min(LIMITE_TOP, lista.size());
        return List.copyOf(lista.subList(0, limite));
    }
}
