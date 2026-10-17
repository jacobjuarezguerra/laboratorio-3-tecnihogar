package tecnihogar;
import java.math.BigDecimal;
import java.math.RoundingMode;
public abstract class Orden {
    private final String codigo, cliente, direccion;
    private final BigDecimal horas;
    private final Zona zona;
    private EstadoOrden estado = EstadoOrden.PENDIENTE;
    private int visitas;
    protected Orden(String codigo, String cliente, String direccion, BigDecimal horas, Zona zona) {
        this.codigo = Validacion.texto(codigo, "Código");
        this.cliente = Validacion.texto(cliente, "Cliente");
        this.direccion = Validacion.texto(direccion, "Dirección");
        this.horas = Validacion.positivo(horas, "Horas");
        this.zona = java.util.Objects.requireNonNull(zona, "Zona requerida");
    }
    public abstract String categoria();
    public abstract BigDecimal costo();
    public abstract int diasAtencion();
    public abstract int limiteVisitas();
    public boolean compatible(Cuadrilla cuadrilla) { return true; }
    public final String codigo() { return codigo; }
    public final String cliente() { return cliente; }
    public final String direccion() { return direccion; }
    public final BigDecimal horas() { return horas; }
    public final Zona zona() { return zona; }
    public final EstadoOrden estado() { return estado; }
    public final int visitas() { return visitas; }
    final void asignar() {
        Validacion.exigir(estado == EstadoOrden.PENDIENTE && visitas < limiteVisitas(), "La orden no está pendiente o agotó sus visitas.");
        estado = EstadoOrden.ASIGNADA;
    }
    final void retirar() {
        Validacion.exigir(estado == EstadoOrden.ASIGNADA, "La orden no está asignada.");
        estado = EstadoOrden.PENDIENTE;
    }
    final void iniciarVisita() {
        Validacion.exigir(estado == EstadoOrden.ASIGNADA && visitas < limiteVisitas(), "No se puede iniciar esta visita.");
        visitas++;
        estado = EstadoOrden.EN_ATENCION;
    }
    final void resultado(boolean completada) {
        Validacion.exigir(estado == EstadoOrden.EN_ATENCION, "La orden no está en atención o ya tiene resultado.");
        estado = completada ? EstadoOrden.COMPLETADA :
            (visitas >= limiteVisitas() ? EstadoOrden.CANCELADA : EstadoOrden.PENDIENTE);
    }
    protected static BigDecimal dinero(BigDecimal valor) { return valor.setScale(2, RoundingMode.HALF_UP); }
    @Override public String toString() {
        return codigo + " | " + categoria() + " | " + cliente + " | " + direccion + " | " + zona +
            " | horas=" + horas + " | " + estado + " | visitas=" + visitas + " | Q" + costo().toPlainString();
    }
}
