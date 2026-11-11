package lector.adaptadores;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lector.CodificacionHelper;
import lector.FuenteDeDatos;
import modelo.Plato;

/**
 * Adaptador para leer el menú de platos desde un archivo CSV.
 */
public class MenuCsvAdapter implements FuenteDeDatos<Plato> {
    private final Path ruta;

    public MenuCsvAdapter(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public List<Plato> leer() {
        List<Plato> platos = new ArrayList<>();
        try {
            List<String> lineas = Files.readAllLines(ruta);
            for (int i = 1; i < lineas.size(); i++) {
                String linea = lineas.get(i).trim();
                if (linea.isEmpty()) continue;
                
                String[] columnas = linea.split(",");        
                int idPlato = Integer.parseInt(columnas[0].trim());
                String nombrePlato = CodificacionHelper.repararTexto(columnas[1].trim());
                String categoria = CodificacionHelper.repararTexto(columnas[2].trim());
                double precioActual = Double.parseDouble(columnas[3].trim());
                String estado = columnas[4].trim();
                platos.add(new Plato(idPlato, nombrePlato, categoria, precioActual, estado));
            }
        } catch (Exception e) {
            System.err.println("Error procesando menu CSV: " + e.getMessage());
        }
        return platos;
    }
}
