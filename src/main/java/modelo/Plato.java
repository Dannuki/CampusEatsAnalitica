package modelo;

/**
 *
 * @author USUARIO
 */
public record Plato(
    int idPlato, 
    String nombrePlato, 
    String categoria, 
    double precioActual, 
    String estado
) {}
