package limpieza;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import modelo.LineaPedido;
import modelo.Plato;

/**
 * Filtro de enriquecimiento que asocia el nombre del plato correspondiente a cada linea según su platoId, consultando el catálogo de menú.
 * No descarta ninguna línea (incluye platos en estado RETIRADO) y conserva el precio original con el que se realizó la orden en la aplicación.
 */
public class EnriquecerNombrePlato implements Filtro {

    private final Map<Integer, String> nombresPorIdPlato;

    /**
     * Construye el filtro indexando los nombres de platos por su idPlato.
     *
     * @param platosMenu lista de platos del menú
     */
    public EnriquecerNombrePlato(List<Plato> platosMenu) {
        this.nombresPorIdPlato = new HashMap<>();
        if (platosMenu != null) {
            for (Plato plato : platosMenu) {
                if (plato != null) {
                    this.nombresPorIdPlato.put(plato.idPlato(), plato.nombrePlato());
                }
            }
        }
    }

    @Override
    public String nombre() {
        return "EnriquecerNombrePlato";
    }

    @Override
    public List<LineaPedido> aplicar(List<LineaPedido> lineas) {
        List<LineaPedido> resultado = new ArrayList<>();
        if (lineas == null) {
            return resultado;
        }

        for (LineaPedido linea : lineas) {
            String nombrePlato = nombresPorIdPlato.getOrDefault(linea.platoId(), "Desconocido");
            resultado.add(linea.conNombrePlato(nombrePlato));
        }
        return resultado;
    }
}
