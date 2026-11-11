package limpieza;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lector.FabricaDeFuentes;
import lector.FuenteDeDatos;
import modelo.LineaPedido;
import modelo.Pago;
import modelo.Pedido;
import modelo.Plato;

/**
 * Clase ejecutable para demostrar y verificar el funcionamiento del Pipeline de limpieza.
 */
public class DemoLimpieza {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        System.out.println("       Demostracion de la tarea 3 - PIPELINE DE LIMPIEZA          ");

        Path configPath = Path.of("config/fuentes.json");
        FabricaDeFuentes fabrica = new FabricaDeFuentes();
        ObjectMapper mapper = new ObjectMapper();

        List<Pedido> pedidos = new ArrayList<>();
        List<Pago> pagos = new ArrayList<>();
        List<Plato> platos = new ArrayList<>();

        try {
            JsonNode rootNode = mapper.readTree(configPath.toFile());
            JsonNode fuentesNode = rootNode.path("fuentes");

            for (JsonNode fuenteNode : fuentesNode) {
                String tipo = fuenteNode.path("tipo").asText();
                Path ruta = Path.of(fuenteNode.path("ruta").asText());

                FuenteDeDatos<?> fuente = fabrica.crear(tipo, ruta);
                switch (tipo) {
                    case "app_json" -> {
                        pedidos = (List<Pedido>) fuente.leer();
                        System.out.println("-> Cargados " + pedidos.size() + " pedidos desde " + ruta);
                    }
                    case "pasarela_csv" -> {
                        pagos = (List<Pago>) fuente.leer();
                        System.out.println("-> Cargadas " + pagos.size() + " transacciones desde " + ruta);
                    }
                    case "menu_csv" -> {
                        platos = (List<Plato>) fuente.leer();
                        System.out.println("-> Cargados " + platos.size() + " platos del menu desde " + ruta);
                    }
                    default -> System.err.println("Tipo de fuente desconocido: " + tipo);
                }
            }
        } catch (Exception e) {
            System.err.println("Error leyendo configuracion de fuentes: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 1. Aplanar pedidos a lista de líneas
        List<LineaPedido> lineasIniciales = Pedido.aplanar(pedidos);
        long pedidosInicialesDistintos = lineasIniciales.stream()
                .map(LineaPedido::numeroPedido)
                .distinct()
                .count();

        System.out.println("\n Datos de entrada ");
        System.out.println(
                "Total pedidos leidos: " + pedidos.size() + " (" + pedidosInicialesDistintos + " pedidos unicos)");
        System.out.println("Total lineas aplanadas iniciales: " + lineasIniciales.size());
        System.out.println();

        // 2. Construir la secuencia de filtros para el Pipeline
        List<Filtro> filtros = List.of(
                new SinDuplicados(),
                new CantidadValida(),
                new SoloPagados(),
                new PagoAprobado(pagos),
                new EnriquecerNombrePlato(platos));

        Pipeline pipeline = new Pipeline(filtros);

        // 3. Ejecutar Pipeline
        List<LineaPedido> lineasFinales = pipeline.ejecutar(lineasIniciales);

        // 4. Calcular métricas finales
        long pedidosFinalesDistintos = lineasFinales.stream()
                .map(LineaPedido::numeroPedido)
                .distinct()
                .count();

        System.out.println("\n Resumen final ");
        System.out.println("Lineas iniciales:            " + lineasIniciales.size());
        for (Pipeline.EstadisticaFiltro est : pipeline.getUltimasEstadisticas()) {
            System.out.printf("  Descartadas por %-20s: %d%n", est.nombreFiltro(), est.lineasDescartadas());
        }
        System.out.println("Total final de lineas limpias: " + lineasFinales.size());
        System.out.println("Total final pedidos distintos: " + pedidosFinalesDistintos);


    }
}
