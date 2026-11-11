package metricas;

import java.util.List;
import modelo.LineaPedido;

public interface Metrica<T> {

    String nombre();

    // Ejecuta el algoritmo de cálculo de la métrica sobre la colección de líneas de
    // pedido limpias.

    T calcular(List<LineaPedido> datos);
}
