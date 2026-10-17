package tecnihogar;
import java.math.BigDecimal;
import java.util.*;
public final class Main {
    private final Scanner entrada = new Scanner(System.in);
    private final Sistema sistema = Sistema.conDatosIniciales();
    private String leer(String mensaje) { System.out.print(mensaje + ": "); return entrada.nextLine().trim(); }
    private int entero(String mensaje) { return Integer.parseInt(leer(mensaje)); }
    private BigDecimal decimal(String mensaje) { return new BigDecimal(leer(mensaje)); }
    private boolean si(String mensaje) {
        String r = leer(mensaje + " (s/n)");
        Validacion.exigir(r.equalsIgnoreCase("s") || r.equalsIgnoreCase("n"), "Responda s o n.");
        return r.equalsIgnoreCase("s");
    }
    private Orden leerOrden() {
        int tipo = entero("Categoría: 1 Diagnóstico, 2 Instalación, 3 Eléctrica certificada, 4 Aire");
        Validacion.exigir(tipo >= 1 && tipo <= 4, "Categoría inválida.");
        String c = leer("Código"), n = leer("Cliente"), d = leer("Dirección");
        BigDecimal h = decimal("Horas estimadas (decimal con punto)");
        int z = entero("Zona: 1 Metropolitana, 2 Periférica");
        Validacion.exigir(z == 1 || z == 2,"Zona inválida.");
        Zona zona = z == 1 ? Zona.METROPOLITANA : Zona.PERIFERICA;
        return switch (tipo) {
            case 1 -> new Diagnostico(c,n,d,h,zona);
            case 2 -> new Instalacion(c,n,d,h,zona,entero("Puntos"));
            case 3 -> new InstalacionCertificada(c,n,d,h,zona,entero("Puntos"),decimal("Valor de materiales"));
            case 4 -> new MantenimientoAire(c,n,d,h,zona,entero("BTU"));
            default -> throw new IllegalArgumentException("Categoría inválida.");
        };
    }
    private void ejecutar() {
        System.out.println("TecniHogar - datos de ejemplo cargados sin ingresos.");
        while (true) {
            System.out.println("\n1 Cotizar  2 Registrar orden  3 Registrar cuadrilla  4 Crear jornada\n5 Asignar  6 Retirar  7 Iniciar jornada  8 Registrar resultado\n9 Consultar órdenes  10 Consultar cuadrillas  11 Reportes  12 Consultar jornadas  0 Salir");
            try {
                int opcion = entero("Opción");
                switch (opcion) {
                    case 0: return;
                    case 1: System.out.println(sistema.cotizar(leerOrden())); break;
                    case 2: {
                        Orden o = leerOrden();
                        System.out.println(sistema.cotizar(o));
                        if (si("¿El cliente acepta y paga?")) { sistema.registrar(o); System.out.println("Orden registrada y cobrada."); }
                        break;
                    }
                    case 3: sistema.registrarCuadrilla(new Cuadrilla(leer("Código"),decimal("Capacidad en horas"),si("¿Certificada en refrigeración?"))); break;
                    case 4: sistema.crearJornada(leer("Código de jornada"),leer("Código de cuadrilla")); break;
                    case 5: sistema.asignar(leer("Jornada"),leer("Orden")); break;
                    case 6: sistema.retirar(leer("Jornada"),leer("Orden")); break;
                    case 7: sistema.iniciar(leer("Jornada")); break;
                    case 8: sistema.resultado(leer("Jornada"),leer("Orden"),si("¿Visita completada? (n = fallida)")); break;
                    case 9: sistema.ordenes().forEach(System.out::println); break;
                    case 10: sistema.cuadrillas().forEach(System.out::println); break;
                    case 11: System.out.println(sistema.reporte()); break;
                    case 12: sistema.jornadas().forEach(j -> { System.out.println(j); j.ordenes().forEach(o -> System.out.println("  " + o.codigo())); }); break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) { System.out.println("Entrada numérica inválida. Use enteros donde corresponda y punto decimal."); }
              catch (IllegalArgumentException e) { System.out.println("Operación rechazada: " + e.getMessage()); }
              catch (NoSuchElementException e) { System.out.println("Fin de entrada."); return; }
        }
    }
    public static void main(String[] args) { new Main().ejecutar(); }
}
