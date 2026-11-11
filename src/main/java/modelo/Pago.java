package modelo;

import java.time.LocalDateTime;

/**
 *
 * @author USUARIO
 */
public record Pago(
    String referenciaPedido, 
    double monto, 
    String estadoTx, 
    String codAutorizacion, 
    LocalDateTime fechaHora
) {}
