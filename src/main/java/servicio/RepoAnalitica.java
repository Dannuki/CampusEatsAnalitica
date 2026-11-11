package servicio;

import java.time.LocalDateTime;
import java.util.List;

import modelo.LineaPedido;
import modelo.Pago;

public interface RepoAnalitica {
    void guardarLineas(List<LineaPedido> lineas);
    void guardarPagos(List<Pago> pagos);
    List<LineaPedido> obtenerLineas();
    List<Pago> obtenerPagos();
    
    List<LineaPedido> lineasEntre(LocalDateTime desde, LocalDateTime hasta);
}
