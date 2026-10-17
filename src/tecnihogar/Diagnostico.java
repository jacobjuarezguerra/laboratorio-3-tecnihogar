package tecnihogar;
import java.math.BigDecimal;
public final class Diagnostico extends Orden {
    public Diagnostico(String c, String n, String d, BigDecimal h, Zona z) {
        super(c,n,d,h,z);
        Validacion.exigir(h.compareTo(new BigDecimal("1.5")) <= 0, "Un diagnóstico admite como máximo 1.5 horas.");
    }
    @Override public String categoria() { return "Diagnóstico"; }
    @Override public BigDecimal costo() { return dinero(new BigDecimal(zona() == Zona.METROPOLITANA ? "150" : "225")); }
    @Override public int diasAtencion() { return zona() == Zona.METROPOLITANA ? 1 : 2; }
    @Override public int limiteVisitas() { return 3; }
}
