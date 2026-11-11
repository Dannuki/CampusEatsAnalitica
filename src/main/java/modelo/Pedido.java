package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un pedido completo realizado en la aplicación móvil de CampusEats.
 */
public record Pedido(
    String numeroPedido, 
    String estudianteId, 
    String carrera,
    LocalDateTime registradoEn, 
    String estado, 
    List<LineaPedido> lineas, 
    double total,
    String codigoRetiro,
    LocalDateTime fechaPagado,
    LocalDateTime fechaEnPreparacion,
    LocalDateTime fechaListoRetiro
) {
    /**
     * Constructor de compatibilidad para pedidos instanciados sin las marcas de tiempo del historial.
     */
    public Pedido(
        String numeroPedido, 
        String estudianteId, 
        String carrera,
        LocalDateTime registradoEn, 
        String estado, 
        List<LineaPedido> lineas, 
        double total,
        String codigoRetiro
    ) {
        this(numeroPedido, estudianteId, carrera, registradoEn, estado, lineas, total, codigoRetiro, null, null, null);
    }

    /**
     * Aplana una lista de pedidos en una lista de líneas de pedido, propagando a cada línea el contexto completo del pedido al que pertenece.
     *
     * @param pedidos lista de pedidos a aplanar
     * @return lista plana de LineaPedido con su contexto completo
     */
    public static List<LineaPedido> aplanar(List<Pedido> pedidos) {
        List<LineaPedido> lineasAplanadas = new ArrayList<>();
        if (pedidos == null) {
            return lineasAplanadas;
        }
        for (Pedido pedido : pedidos) {
            if (pedido.lineas() == null) {
                continue;
            }
            for (LineaPedido linea : pedido.lineas()) {
                lineasAplanadas.add(new LineaPedido(
                    linea.platoId(),
                    linea.cantidad(),
                    linea.precioUnitario(),
                    pedido.numeroPedido(),
                    pedido.registradoEn(),
                    pedido.estado(),
                    pedido.total(),
                    pedido.fechaPagado(),
                    pedido.fechaEnPreparacion(),
                    pedido.fechaListoRetiro(),
                    linea.nombrePlato()
                ));
            }
        }
        return lineasAplanadas;
    }
}
