/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.campuseats.campuseatsanalitica;

import java.nio.file.Path;
import java.util.List;
import lector.FabricaDeFuentes;
import lector.FuenteDeDatos;
import modelo.Pago;
import modelo.Plato;

/**
 *
 * @author USUARIO
 */
public class CampusEatsAnalitica {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        System.out.println("COMPROBACION DEL MODULO DE ANALITICA");
        
        // 1. Instanciamos la fábrica de la Tarea 2
        FabricaDeFuentes fabrica = new FabricaDeFuentes();
        try {
            // 2. Probamos la lectura del Menú CSV
            System.out.println("Probando MenuCsvAdapter");
            FuenteDeDatos<Plato> fuenteMenu = (FuenteDeDatos<Plato>) fabrica.crear("menu_csv", Path.of("datos/menu_admin.csv"));
            List<Plato> platos = fuenteMenu.leer();
            System.out.println("EXITO! Se cargaron " + platos.size() + " platos del menu.");
            if (!platos.isEmpty()) {
                System.out.println("Primer plato detectado: " + platos.get(0).nombrePlato() + " - Precio: $" + platos.get(0).precioActual());
            }
            // 3. Probamos la lectura de la Pasarela CSV
            System.out.println("Probando PasarelaCsvAdapter...");
            FuenteDeDatos<Pago> fuentePagos = (FuenteDeDatos<Pago>) fabrica.crear("pasarela_csv", Path.of("datos/pagos_pasarela.csv"));
            List<Pago> pagos = fuentePagos.leer();
            System.out.println("EXITO! Se cargaron " + pagos.size() + " transacciones de pago.");
            if (!pagos.isEmpty()) {
                System.out.println("Primer pago detectado: Pedido " + pagos.get(0).referenciaPedido() + " - Monto: $" + pagos.get(0).monto() + " - Estado: " + pagos.get(0).estadoTx());
            }
        } catch (Exception e) {
            System.err.println("[ERROR EN LA PRUEBA]: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
