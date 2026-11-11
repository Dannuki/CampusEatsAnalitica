package lector.adaptadores;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lector.CodificacionHelper;
import lector.FuenteDeDatos;
import modelo.LineaPedido;
import modelo.Pedido;

/**
 * Adaptador para leer pedidos desde un archivo JSON proveniente de la aplicación móvil.
 */
public class AppPedidosJsonAdapter implements FuenteDeDatos<Pedido> {
    private final Path ruta;
    private final ObjectMapper objectMapper;

    public AppPedidosJsonAdapter(Path ruta) {
        this.ruta = ruta;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Pedido> leer() {
        List<Pedido> pedidos = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(ruta.toFile());
            if (rootNode == null || !rootNode.isArray()) {
                return pedidos;
            }

            for (JsonNode pedidoNode : rootNode) {
                String numeroPedido = pedidoNode.path("numeroPedido").asText();
                
                // Estudiante
                JsonNode estudianteNode = pedidoNode.path("estudiante");
                String estudianteId = estudianteNode.path("id").asText();
                String carrera = CodificacionHelper.repararTexto(estudianteNode.path("carrera").asText());

                // Fecha de registro
                String registradoEnStr = pedidoNode.path("registradoEn").asText();
                LocalDateTime registradoEn = (registradoEnStr != null && !registradoEnStr.isEmpty())
                        ? LocalDateTime.parse(registradoEnStr)
                        : null;

                String estado = pedidoNode.path("estado").asText();
                double total = pedidoNode.path("total").asDouble();

                // Código de retiro
                JsonNode codigoRetiroNode = pedidoNode.path("codigoRetiro");
                String codigoRetiro = codigoRetiroNode.has("codigo") 
                        ? codigoRetiroNode.path("codigo").asText() 
                        : codigoRetiroNode.asText();

                // Extraer marcas de tiempo del historial de estados
                LocalDateTime fechaPagado = null;
                LocalDateTime fechaEnPreparacion = null;
                LocalDateTime fechaListoRetiro = null;

                JsonNode historialNode = pedidoNode.path("historialEstados");
                if (historialNode.isArray()) {
                    for (JsonNode hNode : historialNode) {
                        String est = hNode.path("estado").asText();
                        String fhStr = hNode.path("fechaHora").asText();
                        if (fhStr != null && !fhStr.isEmpty()) {
                            LocalDateTime fh = LocalDateTime.parse(fhStr);
                            if ("PAGADO".equalsIgnoreCase(est)) {
                                fechaPagado = fh;
                            } else if ("EN_PREPARACION".equalsIgnoreCase(est)) {
                                fechaEnPreparacion = fh;
                            } else if ("LISTO_RETIRO".equalsIgnoreCase(est)) {
                                fechaListoRetiro = fh;
                            }
                        }
                    }
                }

                // Líneas de detalle del pedido
                List<LineaPedido> lineas = new ArrayList<>();
                JsonNode lineasNode = pedidoNode.path("lineas");
                if (lineasNode.isArray()) {
                    for (JsonNode lNode : lineasNode) {
                        int platoId = lNode.path("platoId").asInt();
                        int cantidad = lNode.path("cantidad").asInt();
                        double precioUnitario = lNode.path("precioUnitario").asDouble();
                        
                        lineas.add(new LineaPedido(
                            platoId,
                            cantidad,
                            precioUnitario,
                            numeroPedido,
                            registradoEn,
                            estado,
                            total,
                            fechaPagado,
                            fechaEnPreparacion,
                            fechaListoRetiro,
                            null
                        ));
                    }
                }

                pedidos.add(new Pedido(
                    numeroPedido,
                    estudianteId,
                    carrera,
                    registradoEn,
                    estado,
                    lineas,
                    total,
                    codigoRetiro,
                    fechaPagado,
                    fechaEnPreparacion,
                    fechaListoRetiro
                ));
            }
        } catch (Exception e) {
            System.err.println("Error procesando pedidos JSON: " + e.getMessage());
        }
        return pedidos;
    }
}
