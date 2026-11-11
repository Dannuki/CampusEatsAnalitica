package lector.adaptadores;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lector.FuenteDeDatos;
import modelo.Pago;

/**
 * Adaptador para leer las transacciones de pago desde un archivo CSV delimitado por tabuladores.
 */
public class PasarelaCsvAdapter implements FuenteDeDatos<Pago> {
    private final Path ruta;

    public PasarelaCsvAdapter(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public List<Pago> leer() {
        List<Pago> pagos = new ArrayList<>();
        DateTimeFormatter formatterFlexible = DateTimeFormatter.ofPattern("d/M/yyyy H:m"); 
        try {
            List<String> lineas = Files.readAllLines(ruta);
            for (int i = 1; i < lineas.size(); i++) {
                String linea = lineas.get(i).trim();              
                if (linea.isEmpty()) {
                    continue;
                }
                // El archivo está delimitado por tabuladores
                String[] columnas = linea.split("\t", -1);            
                if (columnas.length < 6) {
                    continue; 
                }
                String referencia = columnas[0].trim(); 
                double monto = Double.parseDouble(columnas[1].trim().replace(",", "."));
                String estadoTx = columnas[2].trim(); 
                String codAutorizacion = (estadoTx.equals("APROBADA") && !columnas[3].trim().isEmpty()) 
                        ? columnas[3].trim() 
                        : "N/A";
                String fechaHoraStr = columnas[5].trim();
                LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraStr, formatterFlexible);
                pagos.add(new Pago(referencia, monto, estadoTx, codAutorizacion, fechaHora));
            }
        } catch (Exception e) {
            System.err.println("Error procesando pasarela CSV: " + e.getMessage());
        }
        return pagos;
    }
}
