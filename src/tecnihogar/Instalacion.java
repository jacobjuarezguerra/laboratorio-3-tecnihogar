package tecnihogar;
import java.math.BigDecimal;
import java.math.RoundingMode;
public class Instalacion extends Orden {
    private final int puntos;
    public Instalacion(String c, String n, String d, BigDecimal h, Zona z, int puntos) {
        super(c,n,d,h,z);
        Validacion.exigir(puntos > 0, "Los puntos deben ser un entero positivo.");
        this.puntos = puntos;
    }
    public final int puntos() { return puntos; }
    public final BigDecimal horasFacturables() {
        return horas().max(BigDecimal.valueOf(puntos).multiply(new BigDecimal("0.75"))).setScale(0, RoundingMode.CEILING);
    }
    @Override public String categoria() { return "Instalación"; }
    @Override public BigDecimal costo() { return dinero(zona().tarifaBase().add(horasFacturables().multiply(new BigDecimal("90")))); }
    @Override public int diasAtencion() { return zona() == Zona.METROPOLITANA ? 3 : 5; }
    @Override public int limiteVisitas() { return 3; }
}
