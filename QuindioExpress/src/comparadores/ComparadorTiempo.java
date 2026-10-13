// Esta clase pertenece al paquete "comparadores"
package comparadores;

// Interfaz de Java para criterios de orden alternativos.
import java.util.Comparator;
// Clase que vamos a comparar.
import modelo.Paquete;

/**
 * Ordena los paquetes por TIEMPO ESTIMADO de MENOR a MAYOR.
 * Aqui si es p1 contra p2 (orden ascendente normal).
 */
public class ComparadorTiempo implements Comparator<Paquete> {

    // Negativo = p1 tarda menos (va primero), positivo = p2 tarda menos, 0 = tardan igual.
    @Override
    public int compare(Paquete p1, Paquete p2) {
        // Orden ascendente normal: p1 contra p2.
        return Integer.compare(p1.getTiempoEstimado(), p2.getTiempoEstimado());
    } // fin de compare
} // fin de la clase ComparadorTiempo
