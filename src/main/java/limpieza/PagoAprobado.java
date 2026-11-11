package limpieza;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.LineaPedido;
import modelo.Pago;

/**
 * Filtro que comprueba la consistencia financiera entre los pedidos y las transacciones de pasarela.
 * Exige que cada pedido posea al menos una transacción con estado APROBADA en la pasarela, cuya referencia coincida con el número de pedido y cuyo monto sea igual al total del pedido
 */
public class PagoAprobado implements Filtro {

    private static final double TOLERANCIA = 0.001;
    private final Map<String, List<Double>> montosAprobadosPorPedido;

    public PagoAprobado(List<Pago> pagos) {
        this.montosAprobadosPorPedido = new HashMap<>();
        if (pagos != null) {
            for (Pago pago : pagos) {
                if (pago != null && "Aprobada".equalsIgnoreCase(pago.estadoTx())) {
                    montosAprobadosPorPedido
                            .computeIfAbsent(pago.referenciaPedido(), k -> new ArrayList<>())
                            .add(pago.monto());
                }
            }
        }
    }

    @Override
    public String nombre() {
        return "PagoAprobado";
    }

    @Override
    public List<LineaPedido> aplicar(List<LineaPedido> lineas) {
        List<LineaPedido> resultado = new ArrayList<>();
        if (lineas == null) {
            return resultado;
        }

        for (LineaPedido linea : lineas) {
            if (linea.numeroPedido() != null && tienePagoAprobadoValido(linea.numeroPedido(), linea.totalPedido())) {
                resultado.add(linea);
            }
        }
        return resultado;
    }

    private boolean tienePagoAprobadoValido(String numeroPedido, double totalPedido) {
        List<Double> montos = montosAprobadosPorPedido.get(numeroPedido);
        if (montos == null || montos.isEmpty()) {
            return false;
        }
        for (double monto : montos) {
            if (Math.abs(monto - totalPedido) < TOLERANCIA) {
                return true;
            }
        }
        return false;
    }
}
