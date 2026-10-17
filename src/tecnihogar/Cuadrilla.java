package tecnihogar;
import java.math.BigDecimal;
public final class Cuadrilla {
    private final String codigo;
    private final BigDecimal capacidad;
    private final boolean certificada;
    public Cuadrilla(String codigo, BigDecimal capacidad, boolean certificada) {
        this.codigo = Validacion.texto(codigo, "Código de cuadrilla");
        this.capacidad = Validacion.positivo(capacidad, "Capacidad");
        this.certificada = certificada;
    }
    public String codigo() { return codigo; }
    public BigDecimal capacidad() { return capacidad; }
    public boolean certificada() { return certificada; }
    @Override public String toString() { return codigo + " | capacidad=" + capacidad + " h | refrigeración=" + certificada; }
}
