package servicio;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import metricas.Metrica;
import metricas.PedidosPorHora;
import metricas.PlatosTop;
import metricas.TicketPromedio;

public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("                 CAMPUS EATS - MODULO DE ANALITICA                        ");
        System.out.println("==========================================================================");

        Path configPath = Path.of("C:/Users/RICH/Downloads/CampusEatsAnalitica/config/fuentes.json");
        if (!java.nio.file.Files.exists(configPath)) {
            configPath = Path.of("config/fuentes.json");
        }

        RepoAnalitica repo = new RepoAnaliticaMemoria();
        List<Metrica<?>> metricas = List.of(
            new PlatosTop(),
            new PedidosPorHora(),
            new TicketPromedio()
        );

        ServicioAnalitica servicio = new ServicioAnalitica(repo, metricas, configPath);

        servicio.cargar();

        LocalDateTime desde = LocalDateTime.of(2026, 10, 19, 0, 0);
        LocalDateTime hasta = LocalDateTime.of(2026, 10, 30, 23, 59);

        servicio.resumen(desde, hasta);
    }
}