package tecnihogar;
import java.math.BigDecimal;
public enum Zona {
    METROPOLITANA("100"), PERIFERICA("175");
    private final BigDecimal tarifaBase;
    Zona(String tarifa) { tarifaBase = new BigDecimal(tarifa); }
    public BigDecimal tarifaBase() { return tarifaBase; }
}
