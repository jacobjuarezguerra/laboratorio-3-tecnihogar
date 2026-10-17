package tecnihogar;
import java.math.BigDecimal;
public final class InstalacionCertificada extends Instalacion {
    private final BigDecimal materiales;
    public InstalacionCertificada(String c, String n, String d, BigDecimal h, Zona z, int puntos, BigDecimal materiales) {
        super(c,n,d,h,z,puntos);
        this.materiales = Validacion.positivo(materiales, "Materiales");
    }
    public BigDecimal materiales() { return materiales; }
    @Override public String categoria() { return "Instalación eléctrica certificada"; }
    @Override public BigDecimal costo() { return dinero(super.costo().add(new BigDecimal("120")).add(materiales.multiply(new BigDecimal("0.05")))); }
    @Override public int diasAtencion() { return super.diasAtencion() + 2; }
    @Override public int limiteVisitas() { return 2; }
}
