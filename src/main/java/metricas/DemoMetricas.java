package metricas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lector.FabricaDeFuentes;
import lector.FuenteDeDatos;
import limpieza.CantidadValida;
import limpieza.EnriquecerNombrePlato;
import limpieza.Filtro;
import limpieza.PagoAprobado;
import limpieza.Pipeline;
import limpieza.SinDuplicados;
import limpieza.SoloPagados;
import modelo.LineaPedido;
import modelo.Pago;
import modelo.Pedido;
import modelo.Plato;

//Clase ejecutable para la demostración de la Tarea 4 (Patrón Strategy).

public class DemoMetricas {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        System.out.println(" ---- CAMPUS EATS ANALITICA - DEMOSTRACION TAREA 4 (STRATEGY) ---- ");

        Path configPath = Path.of("config/fuentes.json");
        if (!java.nio.file.Files.exists(configPath)) {
            Path configAlt = Path.of("CampusEatsAnalitica/config/fuentes.json");
            if (java.nio.file.Files.exists(configAlt)) {
                configPath = configAlt;
            }
        }
        FabricaDeFuentes fabrica = new FabricaDeFuentes();
        ObjectMapper mapper = new ObjectMapper();

        List<Pedido> pedidos = new ArrayList<>();
        List<Pago> pagos = new ArrayList<>();
        List<Plato> platos = new ArrayList<>();

        // 1. Carga de datos configurados en fuentes.json
        try {
            JsonNode rootNode = mapper.readTree(configPath.toFile());
            JsonNode fuentesNode = rootNode.path("fuentes");

            for (JsonNode fuenteNode : fuentesNode) {
                String tipo = fuenteNode.path("tipo").asText();
                Path ruta = Path.of(fuenteNode.path("ruta").asText());
                if (!java.nio.file.Files.exists(ruta)) {
                    Path rutaAlt = Path.of("CampusEatsAnalitica").resolve(ruta);
                    if (java.nio.file.Files.exists(rutaAlt)) {
                        ruta = rutaAlt;
                    }
                }

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

        // 2. Aplanar pedidos a líneas
        List<LineaPedido> lineasIniciales = Pedido.aplanar(pedidos);

        // 3. Aplicar Pipeline con los 5 filtros en el mismo orden que DemoLimpieza
        List<Filtro> filtros = List.of(
                new SinDuplicados(),
                new CantidadValida(),
                new SoloPagados(),
                new PagoAprobado(pagos),
                new EnriquecerNombrePlato(platos));
        Pipeline pipeline = new Pipeline(filtros);
        List<LineaPedido> lineasLimpias = pipeline.ejecutar(lineasIniciales);

        // 4. Filtrar por rango de fechas solicitado en la Demo
        LocalDate fechaInicio = LocalDate.of(2026, 10, 19);
        LocalDate fechaFin = LocalDate.of(2026, 10, 30);
        List<LineaPedido> datosAnalitica = lineasLimpias.stream()
                .filter(l -> l.registradoEn() != null)
                .filter(l -> {
                    LocalDate fechaReg = l.registradoEn().toLocalDate();
                    return !fechaReg.isBefore(fechaInicio) && !fechaReg.isAfter(fechaFin);
                })
                .toList();

        long pedidosUnicos = datosAnalitica.stream()
                .map(LineaPedido::numeroPedido)
                .distinct()
                .count();

        System.out.println();
        System.out.println("Datos listos para analitica (del 19 al 30 de octubre):");
        System.out.printf("  Total lineas evaluadas : %d%n", datosAnalitica.size());
        System.out.printf("  Total pedidos unicos   : %d%n", pedidosUnicos);
        System.out.println();

        // 5. Instanciar y ejecutar las estrategias de cálculo mediante List<Metrica<?>>
        List<Metrica<?>> metricas = List.of(
                new PlatosTop(),
                new PedidosPorHora(),
                new TicketPromedio());

        System.out.println(" EJECUCION DE METRICAS (PATRON STRATEGY) ");

        for (int i = 0; i < metricas.size(); i++) {
            Metrica<?> metrica = metricas.get(i);
            System.out.printf("[%d/%d] Metrica: %s%n", (i + 1), metricas.size(), metrica.nombre());

            Object resultado = metrica.calcular(datosAnalitica);
            imprimirResultado(metrica.nombre(), resultado);

            System.out.println();
        }
    }

    // Formatea e imprime en consola el resultado de cada estrategia de forma visual
    // y clara.

    @SuppressWarnings("unchecked")
    private static void imprimirResultado(String nombreMetrica, Object resultado) {
        if (resultado instanceof List<?> lista) {
            // Caso PlatosTop
            System.out.printf("%-4s | %-28s | %-16s%n", "Top", "Plato", "Cantidad Vendida");
            System.out.println("-----+------------------------------+-----------------");
            List<String> resumen = new ArrayList<>();
            int posicion = 1;
            for (Object obj : lista) {
                if (obj instanceof PlatosTop.PlatoVendido pv) {
                    System.out.printf(" #%-3d | %-28s | %4d unidades%n",
                            posicion, pv.nombrePlato(), pv.cantidad());
                    resumen.add(posicion + ". " + pv.nombrePlato() + " (" + pv.cantidad() + ")");
                    posicion++;
                }
            }
            System.out.println("-----+------------------------------+-----------------");
            System.out.println("Resumen: " + String.join(" | ", resumen));

        } else if (resultado instanceof Map<?, ?> mapa) {
            // Caso PedidosPorHora
            Map<Integer, Integer> horas = (Map<Integer, Integer>) mapa;
            System.out.printf("%-8s | %-15s | %s%n", "Hora", "Cant. Pedidos", "Grafico de demanda");
            System.out.println("---------+-----------------+----------------------------------------------");

            int maxHora = -1;
            int maxCant = -1;
            for (Map.Entry<Integer, Integer> entry : horas.entrySet()) {
                int hora = entry.getKey();
                int cant = entry.getValue();
                if (cant > maxCant) {
                    maxCant = cant;
                    maxHora = hora;
                }
                String barra = "#".repeat(cant / 2);
                System.out.printf("  %02d:00  |   %3d pedidos   | %s%n", hora, cant, barra);
            }
            System.out.println("---------+-----------------+----------------------------------------------");
            int totalPedidosHoras = horas.values().stream().mapToInt(Integer::intValue).sum();
            System.out.printf("Hora pico: %02d:00 con %d pedidos | Total pedidos: %d%n", maxHora, maxCant,
                    totalPedidosHoras);

        } else if (resultado instanceof Double promedio) {
            // Caso TicketPromedio
            System.out.printf("Ticket promedio por pedido: $%,.2f dolares por pedido%n", promedio);
            System.out.printf("(Valor exacto: $%.4f calculado sobre pedidos unicos)%n", promedio);

        } else {
            System.out.println("Resultado: " + resultado);
        }
    }
}
