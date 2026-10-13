// Esta clase pertenece al paquete "comparadores"
package comparadores;

// Interfaz de Java para criterios de orden alternativos.
import java.util.Comparator;
// Clase que vamos a comparar.
import modelo.Paquete;

/**
 * Ordena los paquetes por PESO de MAYOR a MENOR.
 * Tambien sirve para saber cual paquete "pesa mas" en otras operaciones
 * (por ejemplo en el divide y venceras del paquete mas pesado).
 */
public class ComparadorPeso implements Comparator<Paquete> {

    // Negativo = p1 pesa mas (va primero), positivo = p2 pesa mas, 0 = pesan igual.
    @Override
    public int compare(Paquete p1, Paquete p2) {
        // Double.compare compara dos double. Se pone p2 primero para que quede de mayor a menor.
        return Double.compare(p2.getPeso(), p1.getPeso());
    } // fin de compare
} // fin de la clase ComparadorPeso
