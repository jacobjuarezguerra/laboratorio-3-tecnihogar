package tecnihogar;
import java.math.BigDecimal;
import java.util.*;
public final class Jornada {
    private final String codigo;
    private final Cuadrilla cuadrilla;
    private final List<Orden> ordenes = new ArrayList<>();
    private final Set<String> resultados = new HashSet<>();
    private EstadoJornada estado = EstadoJornada.PREPARACION;
    Jornada(String codigo, Cuadrilla cuadrilla) {
        this.codigo = Validacion.texto(codigo, "Código de jornada");
        this.cuadrilla = Objects.requireNonNull(cuadrilla);
    }
    public String codigo() { return codigo; }
    public Cuadrilla cuadrilla() { return cuadrilla; }
    public EstadoJornada estado() { return estado; }
    public List<Orden> ordenes() { return List.copyOf(ordenes); }
    public BigDecimal horasAsignadas() { return ordenes.stream().map(Orden::horas).reduce(BigDecimal.ZERO, BigDecimal::add); }
    public BigDecimal disponible() { return cuadrilla.capacidad().subtract(horasAsignadas()); }
    void asignar(Orden o) {
        Validacion.exigir(estado == EstadoJornada.PREPARACION, "Solo puede asignar durante la preparación.");
        Validacion.exigir(o.estado() == EstadoOrden.PENDIENTE, "La orden no está pendiente: puede estar asignada a otra jornada.");
        Validacion.exigir(o.compatible(cuadrilla), "La cuadrilla requiere certificación en refrigeración.");
        Validacion.exigir(o.horas().compareTo(disponible()) <= 0, "La asignación supera la capacidad de la cuadrilla.");
        o.asignar();
        ordenes.add(o);
    }
    void retirar(Orden o) {
        Validacion.exigir(estado == EstadoJornada.PREPARACION && ordenes.contains(o), "Solo puede retirar órdenes de esta jornada en preparación.");
        o.retirar();
        ordenes.remove(o);
    }
    void iniciar() {
        Validacion.exigir(estado == EstadoJornada.PREPARACION && !ordenes.isEmpty(), "La jornada debe estar en preparación y tener órdenes.");
        for (Orden o : ordenes) Validacion.exigir(o.estado() == EstadoOrden.ASIGNADA && o.visitas() < o.limiteVisitas(), "Orden inválida para iniciar.");
        for (Orden o : ordenes) o.iniciarVisita();
        estado = EstadoJornada.EN_CURSO;
    }
    void resultado(Orden o, boolean completada) {
        Validacion.exigir(estado == EstadoJornada.EN_CURSO && ordenes.contains(o) && !resultados.contains(o.codigo()), "La jornada no está en curso, la orden no pertenece a ella o ya tiene resultado.");
        o.resultado(completada);
        resultados.add(o.codigo());
        if (resultados.size() == ordenes.size()) estado = EstadoJornada.CERRADA;
    }
    @Override public String toString() { return codigo + " | " + estado + " | cuadrilla=" + cuadrilla.codigo() + " | horas=" + horasAsignadas() + " | disponibles=" + disponible(); }
}
