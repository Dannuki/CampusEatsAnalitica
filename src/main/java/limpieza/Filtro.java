package limpieza;

import java.util.List;
import modelo.LineaPedido;

/**
 * Interfaz solicitada que define el contrato de un filtro dentro del patrón Pipeline (Pipes & Filters).
 * Cada filtro encapsula una única regla de limpieza o enriquecimiento sobre las líneas de pedido.
 */
public interface Filtro {

    /**
     * Nombre representativo del filtro.
     *
     * @return nombre del filtro
     */
    String nombre();

    /**
     * Aplica la regla correspondiente sobre la lista de líneas de pedido.
     *
     * @param lineas lista de líneas de entrada
     * @return lista resultante tras aplicar la regla
     */
    List<LineaPedido> aplicar(List<LineaPedido> lineas);
}

