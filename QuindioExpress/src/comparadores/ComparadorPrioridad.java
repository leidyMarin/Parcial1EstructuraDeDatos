// Esta clase pertenece al paquete "comparadores"
package comparadores;

// Interfaz de Java para definir un criterio de orden distinto al orden natural.
import java.util.Comparator;
// Importamos la clase Paquete porque es lo que vamos a comparar.
import modelo.Paquete;

/**
 * Ordena los paquetes por PRIORIDAD de MAYOR a MENOR.
 * Como queremos de mayor a menor, comparamos p2 contra p1 (al reves).
 */
// "implements Comparator<Paquete>" nos obliga a programar el metodo compare.
public class ComparadorPrioridad implements Comparator<Paquete> {

    // Devuelve negativo si p1 va antes, 0 si empatan, positivo si p2 va antes.
    @Override
    public int compare(Paquete p1, Paquete p2) {
        // Integer.compare(a, b) devuelve negativo si a < b. Al poner p2 primero se invierte el orden
        // y el de mayor prioridad queda primero.
        return Integer.compare(p2.getPrioridad(), p1.getPrioridad());
    } // fin de compare
} // fin de la clase ComparadorPrioridad
