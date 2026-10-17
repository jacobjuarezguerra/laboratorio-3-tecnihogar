package tecnihogar;
import java.math.BigDecimal;
public record Ingreso(String orden, String categoria, String concepto, BigDecimal monto) {}
