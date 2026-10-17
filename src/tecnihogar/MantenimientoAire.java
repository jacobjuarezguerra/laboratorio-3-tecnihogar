package tecnihogar;
import java.math.BigDecimal;
public final class MantenimientoAire extends Orden {
    private final int btu;
    public MantenimientoAire(String c, String n, String d, BigDecimal h, Zona z, int btu) {
        super(c,n,d,h,z);
        Validacion.exigir(z == Zona.METROPOLITANA, "El mantenimiento de aire solo se ofrece en zona metropolitana.");
        Validacion.exigir(btu >= 9000 && btu <= 60000, "Los BTU deben estar entre 9000 y 60000.");
        this.btu = btu;
    }
    public int btu() { return btu; }
    @Override public String categoria() { return "Mantenimiento de aire acondicionado"; }
    @Override public BigDecimal costo() { return dinero(zona().tarifaBase().add(horas().multiply(new BigDecimal("110"))).add(new BigDecimal("80"))); }
    @Override public int diasAtencion() { return 2; }
    @Override public int limiteVisitas() { return 1; }
    @Override public boolean compatible(Cuadrilla c) { return c.certificada(); }
}
