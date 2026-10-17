package tecnihogar;
import java.math.BigDecimal;
import java.util.*;
public final class Sistema {
    private final Map<String, Orden> ordenes = new LinkedHashMap<>();
    private final Map<String, Cuadrilla> cuadrillas = new LinkedHashMap<>();
    private final Map<String, Jornada> jornadas = new LinkedHashMap<>();
    private final List<Ingreso> ingresos = new ArrayList<>();
    public String cotizar(Orden o) { return o.categoria() + " | Q" + o.costo() + " | atención: " + o.diasAtencion() + " días hábiles"; }
    public void registrar(Orden o) { agregarOrden(o, true); }
    private void agregarOrden(Orden o, boolean cobrar) {
        Validacion.exigir(!ordenes.containsKey(o.codigo()) && o.estado() == EstadoOrden.PENDIENTE && o.visitas() == 0, "Código de orden repetido o estado inicial inválido.");
        BigDecimal monto = o.costo();
        ordenes.put(o.codigo(), o);
        if (cobrar) ingresos.add(new Ingreso(o.codigo(), o.categoria(), "REGISTRO", monto));
    }
    public void registrarCuadrilla(Cuadrilla c) {
        Validacion.exigir(!cuadrillas.containsKey(c.codigo()), "Código de cuadrilla repetido.");
        cuadrillas.put(c.codigo(), c);
    }
    public void crearJornada(String codigo, String codigoCuadrilla) {
        codigo = Validacion.texto(codigo, "Código de jornada");
        Validacion.exigir(!jornadas.containsKey(codigo), "Código de jornada repetido.");
        Cuadrilla c = cuadrilla(codigoCuadrilla);
        Validacion.exigir(jornadas.values().stream().noneMatch(j -> j.cuadrilla() == c && j.estado() != EstadoJornada.CERRADA), "La cuadrilla ya tiene una jornada sin cerrar.");
        jornadas.put(codigo, new Jornada(codigo,c));
    }
    public void asignar(String j, String o) { jornada(j).asignar(orden(o)); }
    public void retirar(String j, String o) { jornada(j).retirar(orden(o)); }
    public void iniciar(String codigo) {
        Jornada j = jornada(codigo);
        j.iniciar();
        for (Orden o : j.ordenes()) if (o.visitas() > 1)
            ingresos.add(new Ingreso(o.codigo(), o.categoria(), "VISITA_ADICIONAL", new BigDecimal("40.00")));
    }
    public void resultado(String j, String o, boolean completada) { jornada(j).resultado(orden(o),completada); }
    public Orden orden(String c) { return buscar(ordenes,c,"Orden"); }
    public Cuadrilla cuadrilla(String c) { return buscar(cuadrillas,c,"Cuadrilla"); }
    public Jornada jornada(String c) { return buscar(jornadas,c,"Jornada"); }
    private <T> T buscar(Map<String,T> mapa, String c, String nombre) {
        T v = mapa.get(Validacion.texto(c,"Código"));
        Validacion.exigir(v != null, nombre + " no encontrada.");
        return v;
    }
    public List<Orden> ordenes() { return List.copyOf(ordenes.values()); }
    public List<Cuadrilla> cuadrillas() { return List.copyOf(cuadrillas.values()); }
    public List<Jornada> jornadas() { return List.copyOf(jornadas.values()); }
    public List<Ingreso> ingresos() { return List.copyOf(ingresos); }
    public BigDecimal total(String concepto) {
        return ingresos.stream().filter(i -> concepto == null || i.concepto().equals(concepto)).map(Ingreso::monto).reduce(new BigDecimal("0.00"),BigDecimal::add);
    }
    public String reporte() {
        StringBuilder r = new StringBuilder("ÓRDENES POR CATEGORÍA Y ESTADO\n");
        Set<String> categorias = new LinkedHashSet<>();
        ordenes.values().forEach(o -> categorias.add(o.categoria()));
        for (String c : categorias) {
            r.append(c);
            for (EstadoOrden e : EstadoOrden.values()) r.append(" | ").append(e).append("=").append(ordenes.values().stream().filter(o -> o.categoria().equals(c) && o.estado() == e).count());
            BigDecimal monto = ingresos.stream().filter(i -> i.categoria().equals(c)).map(Ingreso::monto).reduce(new BigDecimal("0.00"), BigDecimal::add);
            r.append(" | ingreso=Q").append(monto).append('\n');
        }
        r.append("Registro: Q").append(total("REGISTRO")).append(" | Visitas adicionales: Q").append(total("VISITA_ADICIONAL")).append(" | Total: Q").append(total(null)).append("\nJORNADAS SIN CERRAR\n");
        jornadas.values().stream().filter(j -> j.estado() != EstadoJornada.CERRADA).forEach(j -> r.append(j).append('\n'));
        return r.toString();
    }
    public static Sistema conDatosIniciales() {
        Sistema s = new Sistema();
        s.registrarCuadrilla(new Cuadrilla("C1",new BigDecimal("8"),true));
        s.registrarCuadrilla(new Cuadrilla("C2",new BigDecimal("6"),false));
        s.registrarCuadrilla(new Cuadrilla("C3",new BigDecimal("10"),true));
        s.agregarOrden(new Diagnostico("D1","Ana","Zona 1",new BigDecimal("1"),Zona.METROPOLITANA),false);
        s.agregarOrden(new Diagnostico("D2","Luis","Mixco",new BigDecimal("1.5"),Zona.PERIFERICA),false);
        s.agregarOrden(new Instalacion("I1","Marta","Zona 2",new BigDecimal("2.5"),Zona.METROPOLITANA,5),false);
        s.agregarOrden(new Instalacion("I2","Pedro","Villa Nueva",new BigDecimal("2.1"),Zona.PERIFERICA,1),false);
        s.agregarOrden(new InstalacionCertificada("E1","Sara","Zona 3",new BigDecimal("2.5"),Zona.METROPOLITANA,5,new BigDecimal("2000")),false);
        s.agregarOrden(new InstalacionCertificada("E2","José","Amatitlán",new BigDecimal("3"),Zona.PERIFERICA,2,new BigDecimal("1000")),false);
        s.agregarOrden(new MantenimientoAire("A1","Rosa","Zona 10",new BigDecimal("1.5"),Zona.METROPOLITANA,9000),false);
        s.agregarOrden(new MantenimientoAire("A2","Mario","Zona 14",new BigDecimal("2"),Zona.METROPOLITANA,60000),false);
        return s;
    }
}
