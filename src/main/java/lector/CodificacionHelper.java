package lector;

import java.nio.charset.StandardCharsets;

/**
 * Utilidad para corregir problemas comunes de codificación (mojibake),
 * como texto UTF-8 interpretado erróneamente como ISO-8859-1.
 */
public final class CodificacionHelper {

    private CodificacionHelper() {
        // Constructor privado para clase utilitaria
    }

    /**
     * Repara una cadena de texto dañada por doble codificación UTF-8 / ISO-8859-1.
     * Si contiene la secuencia característica 'Ã', reinterpreta los bytes de ISO-8859-1 a UTF-8.
     *
     * @param texto texto original posiblemente dañado
     * @return texto reparado o el texto original si no presentaba daño
     */
    public static String repararTexto(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        if (texto.contains("Ã")) {
            return new String(texto.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }
        return texto;
    }
}

