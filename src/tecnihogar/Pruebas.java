package tecnihogar;
import java.math.BigDecimal;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
public final class Pruebas {
    private static final List<String> evidencia = new ArrayList<>();
    private static BigDecimal b(String s) { return new BigDecimal(s); }
    private static void igual(String caso, Object esperado, Object obtenido) {
        boolean ok = esperado instanceof BigDecimal e && obtenido instanceof BigDecimal o ? e.compareTo(o) == 0 : Objects.equals(esperado, obtenido);
        evidencia.add(caso + "\t" + esperado + "\t" + obtenido + "\t" + (ok ? "OK" : "FALLO"));
        if (!ok) throw new AssertionError(caso + ": " + obtenido + " != " + esperado);
    }
    private static void rechaza(String caso, Runnable accion) {
        boolean rechazo = false;
        try { accion.run(); } catch (IllegalArgumentException e) { rechazo = true; }
        igual(caso, true, rechazo);
    }
    private static Orden diagnostico(String c, String horas, Zona zona) { return new Diagnostico(c,"Ana","Zona 1",b(horas),zona); }
    private static void politica(Orden o) {
        Sistema s = new Sistema();
        s.registrar(o);
        BigDecimal registro = o.costo();
        s.registrarCuadrilla(new Cuadrilla("C",b("20"),true));
        for (int visita = 1; visita <= o.limiteVisitas(); visita++) {
            String j = "J" + visita;
            s.crearJornada(j,"C"); s.asignar(j,o.codigo());
            igual(o.categoria()+" asignar visita "+visita+" no cobra",registro.add(b("40").multiply(BigDecimal.valueOf(visita-1L > 0 ? visita-2L : 0))),s.total(null));
            s.iniciar(j);
            igual(o.categoria()+" contador visita "+visita,visita,o.visitas());
            igual(o.categoria()+" ingreso al iniciar visita "+visita,registro.add(b("40").multiply(BigDecimal.valueOf(visita-1L))),s.total(null));
            s.resultado(j,o.codigo(),false);
            igual(o.categoria()+" resultado fallido "+visita,visita == o.limiteVisitas() ? EstadoOrden.CANCELADA : EstadoOrden.PENDIENTE,o.estado());
            igual(o.categoria()+" jornada cerrada "+visita,EstadoJornada.CERRADA,s.jornada(j).estado());
        }
        s.crearJornada("EXTRA","C");
        rechaza(o.categoria()+" cancelada no se reasigna",()->s.asignar("EXTRA",o.codigo()));
        igual(o.categoria()+" rechazo sin cargos",b("40").multiply(BigDecimal.valueOf(o.limiteVisitas()-1L)),s.total("VISITA_ADICIONAL"));
    }
    public static void main(String[] args) throws Exception {
        Orden dm = diagnostico("D","1.5",Zona.METROPOLITANA), dp = diagnostico("P","0.5",Zona.PERIFERICA);
        igual("Diagnóstico metropolitano 1.5h",b("150"),dm.costo());
        igual("Diagnóstico periférico 0.5h",b("225"),dp.costo());
        igual("Días diagnóstico metropolitano",1,dm.diasAtencion());
        igual("Días diagnóstico periférico",2,dp.diasAtencion());
        Instalacion im = new Instalacion("I","Ana","Zona 1",b("2.5"),Zona.METROPOLITANA,5);
        Instalacion ip = new Instalacion("IP","Ana","Mixco",b("2.1"),Zona.PERIFERICA,1);
        igual("Instalación 2.5h y 5 puntos: techo de 3.75",b("4"),im.horasFacturables());
        igual("Instalación metropolitana 5 puntos",b("460"),im.costo());
        igual("Instalación periférica 2.1h: techo de 3",b("445"),ip.costo());
        igual("Días instalación metropolitana",3,im.diasAtencion());
        igual("Días instalación periférica",5,ip.diasAtencion());
        InstalacionCertificada em = new InstalacionCertificada("E","Ana","Zona 1",b("2.5"),Zona.METROPOLITANA,5,b("2000"));
        InstalacionCertificada ep = new InstalacionCertificada("EP","Ana","Mixco",b("3"),Zona.PERIFERICA,2,b("1000"));
        igual("Certificada: 460+120+5% de 2000",b("680"),em.costo());
        igual("Certificada periférica: 175+270+120+50",b("615"),ep.costo());
        igual("Días certificada metropolitana",5,em.diasAtencion());
        igual("Días certificada periférica",7,ep.diasAtencion());
        Orden aire = new MantenimientoAire("A","Ana","Zona 1",b("1.5"),Zona.METROPOLITANA,9000);
        igual("Aire 1.5h: 100+165+80",b("345"),aire.costo());
        igual("Días mantenimiento",2,aire.diasAtencion());
        igual("Aire máximo BTU válido",60000,new MantenimientoAire("B","Ana","Zona 1",b("1"),Zona.METROPOLITANA,60000).btu());
        rechaza("Diagnóstico 1.51h",()->diagnostico("D","1.51",Zona.METROPOLITANA));
        rechaza("Horas cero",()->diagnostico("D","0",Zona.METROPOLITANA));
        rechaza("Horas negativas",()->diagnostico("D","-1",Zona.METROPOLITANA));
        rechaza("Código vacío",()->diagnostico(" ","1",Zona.METROPOLITANA));
        rechaza("Cliente vacío",()->new Diagnostico("X"," ","D",b("1"),Zona.METROPOLITANA));
        rechaza("Dirección vacía",()->new Diagnostico("X","Ana"," ",b("1"),Zona.METROPOLITANA));
        rechaza("Cero puntos",()->new Instalacion("X","Ana","D",b("1"),Zona.METROPOLITANA,0));
        rechaza("Materiales cero",()->new InstalacionCertificada("X","Ana","D",b("1"),Zona.METROPOLITANA,1,b("0")));
        rechaza("BTU 8999",()->new MantenimientoAire("X","Ana","D",b("1"),Zona.METROPOLITANA,8999));
        rechaza("BTU 60001",()->new MantenimientoAire("X","Ana","D",b("1"),Zona.METROPOLITANA,60001));
        rechaza("Aire periférico",()->new MantenimientoAire("X","Ana","D",b("1"),Zona.PERIFERICA,9000));
        rechaza("Capacidad cero",()->new Cuadrilla("X",b("0"),false));
        Sistema s = Sistema.conDatosIniciales();
        igual("Semilla: ocho órdenes",8,s.ordenes().size());
        igual("Semilla: tres cuadrillas",3,s.cuadrillas().size());
        igual("Semilla: todas pendientes",true,s.ordenes().stream().allMatch(o -> o.estado()==EstadoOrden.PENDIENTE));
        igual("Semilla sin ingresos",b("0"),s.total(null));
        s.cotizar(em); s.cotizar(em);
        igual("Cotizar dos veces no registra",8,s.ordenes().size());
        igual("Cotizar dos veces no cobra",b("0"),s.total(null));
        s.registrar(dm);
        igual("Registro cobra monto completo",b("150"),s.total("REGISTRO"));
        rechaza("Registro duplicado",()->s.registrar(diagnostico("D","1",Zona.PERIFERICA)));
        igual("Duplicado no cambia cantidad",9,s.ordenes().size());
        igual("Duplicado no cambia ingresos",b("150"),s.total(null));
        rechaza("Cuadrilla duplicada",()->s.registrarCuadrilla(new Cuadrilla("C1",b("1"),true)));
        s.crearJornada("J1","C1"); s.crearJornada("J2","C2");
        rechaza("Una jornada abierta por cuadrilla",()->s.crearJornada("J3","C1"));
        rechaza("Jornada duplicada",()->s.crearJornada("J1","C3"));
        rechaza("Jornada sin órdenes no inicia",()->s.iniciar("J1"));
        rechaza("Aire con cuadrilla sin certificado",()->s.asignar("J2","A1"));
        igual("Rechazo conserva orden pendiente",EstadoOrden.PENDIENTE,s.orden("A1").estado());
        igual("Rechazo conserva jornada vacía",0,s.jornada("J2").ordenes().size());
        s.asignar("J1","I1");
        igual("Capacidad usa 2.5h estimadas, no 4 facturables",b("5.5"),s.jornada("J1").disponible());
        rechaza("Doble asignación",()->s.asignar("J2","I1"));
        s.retirar("J1","I1");
        igual("Retiro restaura pendiente",EstadoOrden.PENDIENTE,s.orden("I1").estado());
        igual("Retiro restaura capacidad",b("8"),s.jornada("J1").disponible());
        igual("Cotizaciones, retiros y rechazos sin ingresos",b("150"),s.total(null));
        s.registrarCuadrilla(new Cuadrilla("PEQ",b("1.5"),false)); s.crearJornada("JP","PEQ");
        rechaza("Exceso de capacidad 2.5h > 1.5h",()->s.asignar("JP","I1"));
        s.asignar("JP","D");
        igual("Capacidad exacta aceptada",b("0"),s.jornada("JP").disponible());
        rechaza("Resultado antes de iniciar",()->s.resultado("JP","D",true));
        s.iniciar("JP");
        rechaza("No retirar en curso",()->s.retirar("JP","D"));
        rechaza("No asignar en curso",()->s.asignar("JP","D1"));
        rechaza("No iniciar dos veces",()->s.iniciar("JP"));
        s.resultado("JP","D",true);
        igual("Resultado exitoso completa",EstadoOrden.COMPLETADA,s.orden("D").estado());
        rechaza("No repetir resultado",()->s.resultado("JP","D",false));
        rechaza("Completada no se reasigna",()->s.asignar("J1","D"));
        igual("Primera visita y rechazos no cobran extra",b("0"),s.total("VISITA_ADICIONAL"));
        s.crearJornada("JP2","PEQ");
        s.asignar("J1","D1"); s.asignar("J1","D2"); s.iniciar("J1");
        s.resultado("J1","D1",true);
        igual("Resultado parcial mantiene jornada abierta",EstadoJornada.EN_CURSO,s.jornada("J1").estado());
        rechaza("Orden ajena no admite resultado",()->s.resultado("J1","A1",true));
        s.resultado("J1","D2",false);
        igual("Último resultado cierra jornada",EstadoJornada.CERRADA,s.jornada("J1").estado());
        igual("Reporte contiene ingreso por categoría",true,s.reporte().contains("ingreso=Q150.00"));
        igual("Reporte excluye jornadas cerradas",false,s.reporte().contains("J1 | CERRADA"));
        politica(diagnostico("VD","1",Zona.METROPOLITANA));
        politica(new Instalacion("VI","Ana","D",b("1"),Zona.METROPOLITANA,1));
        politica(new InstalacionCertificada("VE","Ana","D",b("1"),Zona.METROPOLITANA,1,b("100")));
        politica(new MantenimientoAire("VA","Ana","D",b("1"),Zona.METROPOLITANA,9000));
        List<String> salida = new ArrayList<>(); salida.add("Caso / entrada\tEsperado\tObtenido\tEstado"); salida.addAll(evidencia);
        Path archivo = Path.of(args.length > 0 ? args[0] : "pruebas.tsv");
        Files.write(archivo,salida,StandardCharsets.UTF_8);
        System.out.println(evidencia.size()+" comprobaciones correctas. Evidencia: "+archivo);
    }
}
