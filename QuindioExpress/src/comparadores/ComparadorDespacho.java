// Esta clase pertenece al paquete "comparadores"
package comparadores;

// Interfaz de Java para criterios de orden alternativos.
import java.util.Comparator;
// Clase que vamos a comparar.
import modelo.Paquete;

/**
 * Criterio de DESPACHO PRIORITARIO (el que usa la PriorityQueue).
 *
 * Reglas del enunciado:
 *   1. Primero el de MAYOR prioridad.
 *   2. Si empatan, el de MENOR tiempo estimado.
 *   3. Si siguen empatados, el de codigo alfabeticamente menor.
 *
 * Reutilizamos los otros comparadores para no repetir codigo
 * (el enunciado pide que los criterios se puedan reutilizar).
 */
public class ComparadorDespacho implements Comparator<Paquete> {

    // Comparador de prioridad (mayor a menor) que reutilizamos para la regla 1.
    private ComparadorPrioridad porPrioridad = new ComparadorPrioridad();
    // Comparador de tiempo (menor a mayor) que reutilizamos para la regla 2.
    private ComparadorTiempo porTiempo = new ComparadorTiempo();

    @Override
    public int compare(Paquete p1, Paquete p2) {
        // 1. prioridad (mayor primero)
        int resultado = porPrioridad.compare(p1, p2);
        // Si las prioridades son distintas, ya sabemos cual va primero.
        if (resultado != 0) {
            return resultado;
        } // fin regla 1
        // 2. tiempo estimado (menor primero) -> solo se llega aqui si empataron en prioridad
        resultado = porTiempo.compare(p1, p2);
        // Si los tiempos son distintos, ese es el desempate.
        if (resultado != 0) {
            return resultado;
        } // fin regla 2
        // 3. codigo (orden natural del paquete) -> ultimo desempate
        return p1.compareTo(p2);
    } // fin de compare
} // fin de la clase ComparadorDespacho
