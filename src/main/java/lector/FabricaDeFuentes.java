/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lector;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lector.adaptadores.AppPedidosJsonAdapter;
import lector.adaptadores.MenuCsvAdapter;
import lector.adaptadores.PasarelaCsvAdapter;

/**
 *
 * @author USUARIO
 */
public class FabricaDeFuentes {
    private final Map<String, Function<Path, FuenteDeDatos<?>>> mapaConstructores;

    public FabricaDeFuentes() {
        this.mapaConstructores = new HashMap<>();
       
        mapaConstructores.put("app_json", AppPedidosJsonAdapter::new);
        mapaConstructores.put("pasarela_csv", PasarelaCsvAdapter::new);
        mapaConstructores.put("menu_csv", MenuCsvAdapter::new);
    }

    public FuenteDeDatos<?> crear(String tipo, Path ruta) {
        Function<Path, FuenteDeDatos<?>> constructor = mapaConstructores.get(tipo);
        
        if (constructor == null) {
            throw new IllegalArgumentException("El tipo de fuente no esta registrado en la fabrica: " + tipo);
        }
        
        return constructor.apply(ruta);
    }
}