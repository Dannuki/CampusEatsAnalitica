package modelo;

import java.time.LocalDateTime;

/**
 * Representa una línea de detalle de un pedido dentro del flujo de analítica, llevando consigo el contexto del pedido para la aplicación de filtros y métricas.
 */
public record LineaPedido(
    int platoId,
    int cantidad,
    double precioUnitario,
    String numeroPedido,
    LocalDateTime registradoEn,
    String estadoPedido,
    double totalPedido,
    LocalDateTime fechaPagado,
    LocalDateTime fechaEnPreparacion,
    LocalDateTime fechaListoRetiro,
    String nombrePlato
) {
    /**
     * Constructor de compatibilidad para líneas sin contexto de pedido asociado.
     */
    public LineaPedido(int platoId, int cantidad, double precioUnitario) {
        this(platoId, cantidad, precioUnitario, null, null, null, 0.0, null, null, null, null);
    }

    /**
     * Devuelve una nueva copia inmutable de la línea con el nombre del plato asignado.
     *
     * @param nuevoNombrePlato nombre del plato enriquecido desde el menú
     * @return nueva LineaPedido con el nombre del plato completado
     */
    public LineaPedido conNombrePlato(String nuevoNombrePlato) {
        return new LineaPedido(
            platoId,
            cantidad,
            precioUnitario,
            numeroPedido,
            registradoEn,
            estadoPedido,
            totalPedido,
            fechaPagado,
            fechaEnPreparacion,
            fechaListoRetiro,
            nuevoNombrePlato
        );
    }
}
