package lector;

import java.nio.charset.StandardCharsets;

//Utilidad para corregir problemas comunes de codificación (mojibake), como texto UTF-8 interpretado erróneamente como ISO-8859-1.
public final class CodificacionHelper {

    private CodificacionHelper() {
        // Constructor privado para clase utilitaria
    }

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

