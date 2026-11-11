package limpieza;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modelo.LineaPedido;

/**
 * Orquestador del flujo de procesamiento mediante el patrón Pipes & Filters.
 * Recibe una secuencia ordenada de filtros y los ejecuta sucesivamente, registrando y mostrando el impacto de cada etapa sin acoplarse a reglas específicas.
 */
public class Pipeline {

    /**
     * Registro con las métricas de ejecución de un filtro individual.
     */
    public record EstadisticaFiltro(
            String nombreFiltro,
            int lineasEntrada,
            int lineasDescartadas,
            int lineasSalida) {
    }

    private final List<Filtro> filtros;
    private final List<EstadisticaFiltro> ultimasEstadisticas;

    /**
     * Crea un Pipeline con la lista ordenada de filtros a aplicar.
     *
     * @param filtros lista de filtros que componen la tubería
     */
    public Pipeline(List<Filtro> filtros) {
        this.filtros = (filtros != null) ? List.copyOf(filtros) : List.of();
        this.ultimasEstadisticas = new ArrayList<>();
    }

    /**
     * Ejecuta secuencialmente todos los filtros configurados sobre la lista de líneas.
     * Imprime en consola un informe tabular del proceso y deja las estadísticas disponibles.
     *
     * @param lineasIniciales líneas de pedido sin filtrar
     * @return lista resultante tras pasar por todos los filtros
     */
    public List<LineaPedido> ejecutar(List<LineaPedido> lineasIniciales) {
        ultimasEstadisticas.clear();
        List<LineaPedido> actuales = (lineasIniciales != null) ? lineasIniciales : List.of();

        System.out.println("                   Ejecucion del Pipeline de Limpieza                 ");
        System.out.printf("%-25s | %-14s | %-13s | %-13s%n", "Filtro", "Lineas Entrada", "Descartadas",
                "Lineas Salida");
        System.out.println("--------------------------------------------------------------------------");

        for (Filtro filtro : filtros) {
            int entrada = actuales.size();
            actuales = filtro.aplicar(actuales);
            int salida = actuales.size();
            int descartadas = entrada - salida;

            EstadisticaFiltro estadistica = new EstadisticaFiltro(filtro.nombre(), entrada, descartadas, salida);
            ultimasEstadisticas.add(estadistica);

            System.out.printf("%-25s | %-14d | %-13d | %-13d%n",
                    filtro.nombre(), entrada, descartadas, salida);
        }
        System.out.println("__________________________________________________________________________________");

        return actuales;
    }

    /**
     * Devuelve las estadísticas de la última ejecución del pipeline.
     * @return 
     */
    public List<EstadisticaFiltro> getUltimasEstadisticas() {
        return Collections.unmodifiableList(ultimasEstadisticas);
    }
}
