package servicio;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lector.FabricaDeFuentes;
import lector.FuenteDeDatos;
import limpieza.CantidadValida;
import limpieza.EnriquecerNombrePlato;
import limpieza.Filtro;
import limpieza.PagoAprobado;
import limpieza.Pipeline;
import limpieza.SinDuplicados;
import limpieza.SoloPagados;
import metricas.Metrica;
import modelo.LineaPedido;
import modelo.Pago;
import modelo.Pedido;
import modelo.Plato;

public class ServicioAnalitica {
    private final RepoAnalitica repositorio;
    private final List<Metrica<?>> metricas;
    private final Path rutaConfig;

    public ServicioAnalitica(RepoAnalitica repositorio, List<Metrica<?>> metricas, Path rutaConfig) {
        this.repositorio = repositorio;
        this.metricas = metricas;
        this.rutaConfig = rutaConfig;
    }

    @SuppressWarnings("unchecked")
    public void cargar() {
        FabricaDeFuentes fabrica = new FabricaDeFuentes();
        ObjectMapper mapper = new ObjectMapper();

        List<Pedido> pedidos = new ArrayList<>();
        List<Pago> pagos = new ArrayList<>();
        List<Plato> platos = new ArrayList<>();

        try {
            Path configPath = rutaConfig;
            if (!Files.exists(configPath)) {
                configPath = Path.of("config/fuentes.json");
            }

            if (!Files.exists(configPath)) {
                throw new RuntimeException("No se encuentra el archivo fuentes.json en: " + rutaConfig);
            }

            JsonNode rootNode = mapper.readTree(configPath.toFile());
            JsonNode fuentesNode = rootNode.path("fuentes");

            for (JsonNode fuenteNode : fuentesNode) {
                String tipo = fuenteNode.path("tipo").asText();
                String rutaStr = fuenteNode.path("ruta").asText();
                
                Path ruta = Path.of(rutaStr);
                if (!Files.exists(ruta)) {
                    ruta = Path.of("C:/Users/RICH/Downloads/CampusEatsAnalitica").resolve(rutaStr);
                }
                if (!Files.exists(ruta)) {
                    ruta = Path.of("CampusEatsAnalitica").resolve(rutaStr);
                }

                if (!Files.exists(ruta)) {
                    throw new RuntimeException("No se encuentra el archivo de datos: " + rutaStr);
                }

                FuenteDeDatos<?> fuente = fabrica.crear(tipo, ruta);
                switch (tipo) {
                    case "app_json" -> pedidos = (List<Pedido>) fuente.leer();
                    case "pasarela_csv" -> pagos = (List<Pago>) fuente.leer();
                    case "menu_csv" -> platos = (List<Plato>) fuente.leer();
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar las fuentes de datos: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        List<LineaPedido> lineasIniciales = Pedido.aplanar(pedidos);

        List<Filtro> filtros = List.of(
            new SinDuplicados(),
            new CantidadValida(),
            new SoloPagados(),
            new PagoAprobado(pagos),
            new EnriquecerNombrePlato(platos)
        );

        Pipeline pipeline = new Pipeline(filtros);
        List<LineaPedido> lineasLimpias = pipeline.ejecutar(lineasIniciales);

        repositorio.guardarLineas(lineasLimpias);
        repositorio.guardarPagos(pagos);
    }

    public void resumen(LocalDateTime desde, LocalDateTime hasta) {
        List<LineaPedido> datosFiltrados = repositorio.lineasEntre(desde, hasta);

        System.out.println("\n==========================================================================");
        System.out.printf("         RESUMEN DE ANALITICA (%s al %s)        %n", desde.toLocalDate(), hasta.toLocalDate());
        System.out.println("==========================================================================");
        System.out.println("Total de líneas evaluadas en el período: " + datosFiltrados.size());
        
        long pedidosUnicos = datosFiltrados.stream().map(LineaPedido::numeroPedido).distinct().count();
        System.out.println("Total de pedidos únicos en el período:   " + pedidosUnicos);
        System.out.println("--------------------------------------------------------------------------");

        for (Metrica<?> metrica : metricas) {
            System.out.println("\n[Métrica] -> " + metrica.nombre());
            Object resultado = metrica.calcular(datosFiltrados);
            imprimirResultadoFormateado(resultado);
        }
        System.out.println("\n==========================================================================");
    }

    @SuppressWarnings("unchecked")
    private void imprimirResultadoFormateado(Object resultado) {
        if (resultado instanceof List<?> lista) {
            int pos = 1;
            for (Object obj : lista) {
                System.out.printf("  %d. %s%n", pos, obj.toString());
                pos++;
            }
        } else if (resultado instanceof Map<?, ?> mapa) {
            Map<Integer, Integer> horas = (Map<Integer, Integer>) mapa;
            int maxHora = -1;
            int maxCant = -1;
            for (Map.Entry<Integer, Integer> entry : horas.entrySet()) {
                if (entry.getValue() > maxCant) {
                    maxCant = entry.getValue();
                    maxHora = entry.getKey();
                }
            }
            System.out.printf("  La hora pico es las %02d:00, con %d pedidos.%n", maxHora, maxCant);
        } else if (resultado instanceof Double promedio) {
            System.out.printf("  Resultado obtenido: $%.2f dólares por pedido%n", promedio);
        } else {
            System.out.println("  " + resultado);
        }
    }
}