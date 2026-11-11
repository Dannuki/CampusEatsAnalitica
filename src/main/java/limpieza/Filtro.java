package limpieza;

import java.util.List;
import modelo.LineaPedido;

/**
 * Interfaz solicitada que define el contrato de un filtro dentro del patrón Pipeline (Pipes & Filters).
 * Cada filtro encapsula una única regla de limpieza o enriquecimiento sobre las líneas de pedido.
 */
public interface Filtro {

    String nombre();


    List<LineaPedido> aplicar(List<LineaPedido> lineas);
}

