package servicio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import modelo.LineaPedido;
import modelo.Pago;

/**
 * Implementación en memoria del repositorio de analítica.
 */
public class RepoAnaliticaMemoria implements RepoAnalitica {
    private List<LineaPedido> lineas;
    private List<Pago> pagos;

    public RepoAnaliticaMemoria() {
        this.lineas = new ArrayList<>();
        this.pagos = new ArrayList<>();
    }

    @Override
    public void guardarLineas(List<LineaPedido> lineas) {
        this.lineas = (lineas != null) ? new ArrayList<>(lineas) : new ArrayList<>();
    }

    @Override
    public void guardarPagos(List<Pago> pagos) {
        this.pagos = (pagos != null) ? new ArrayList<>(pagos) : new ArrayList<>();
    }

    @Override
    public List<LineaPedido> obtenerLineas() {
        return lineas;
    }

    @Override
    public List<Pago> obtenerPagos() {
        return pagos;
    }

    @Override
    public List<LineaPedido> lineasEntre(LocalDateTime desde, LocalDateTime hasta) {
        List<LineaPedido> resultado = new ArrayList<>();
        for (LineaPedido l : lineas) {
            if (l.registradoEn() != null) {
                if (!l.registradoEn().isBefore(desde) && !l.registradoEn().isAfter(hasta)) {
                    resultado.add(l);
                }
            }
        }
        return resultado;
    }
}