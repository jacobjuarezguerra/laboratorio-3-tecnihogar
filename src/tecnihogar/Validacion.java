package tecnihogar;
import java.math.BigDecimal;
final class Validacion {
    private Validacion() {}
    static String texto(String v, String campo) {
        if (v == null || v.trim().isEmpty()) throw new IllegalArgumentException(campo + " no puede estar vacío.");
        return v.trim();
    }
    static BigDecimal positivo(BigDecimal v, String campo) {
        if (v == null || v.signum() <= 0) throw new IllegalArgumentException(campo + " debe ser positivo.");
        return v;
    }
    static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new IllegalArgumentException(mensaje);
    }
}
