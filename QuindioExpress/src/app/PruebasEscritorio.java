// Esta clase pertenece al paquete "app"
package app;

// Comparador para la prueba de la cola de prioridad.
import comparadores.ComparadorDespacho;
// Colecciones de Java que se usan para armar los datos de prueba.
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
// Los algoritmos que vamos a probar.
import logica.Algoritmos;
// Clase Paquete para crear los datos.
import modelo.Paquete;

/**
 * Pruebas de escritorio con los MISMOS datos pequenos del enunciado.
 * Se activa la traza para ver en consola la pila de llamadas
 * (son las mismas tablas que estan en el documento PDF, parte C).
 */
public class PruebasEscritorio {

    // Ejecuta las 5 pruebas una tras otra.
    public static void ejecutar() {
        Algoritmos.mostrarTraza = true; // activamos la impresion de llamadas y retornos

        // ---------------------------------------------------------------
        System.out.println("\n===== PRUEBA 1: peso total de Armenia (recursivo) =====");
        List<Paquete> datos1 = new ArrayList<>(); // lista con los 4 paquetes del enunciado
        datos1.add(new Paquete("PQ101", "Armenia", 4.5, 3, 20)); // posicion 0
        datos1.add(new Paquete("PQ203", "Salento", 3.0, 3, 20)); // posicion 1 (no es de Armenia)
        datos1.add(new Paquete("PQ415", "Armenia", 7.2, 3, 20)); // posicion 2
        datos1.add(new Paquete("PQ730", "Armenia", 2.3, 3, 20)); // posicion 3
        // Llamamos al metodo recursivo.
        double total = Algoritmos.pesoTotalMunicipio(datos1, "Armenia");
        // Mostramos el resultado redondeado a 1 decimal.
        System.out.println("RESULTADO: " + Math.round(total * 10) / 10.0 + " kg  (esperado 14.0 kg)");

        // ---------------------------------------------------------------
        System.out.println("\n===== PRUEBA 2: contar prioridad >= 4 (recursivo) =====");
        int[] prioridades = {5, 2, 4, 1, 5, 3, 4}; // prioridades del enunciado
        List<Paquete> datos2 = new ArrayList<>();   // aqui se crean los paquetes con esas prioridades
        for (int i = 0; i < prioridades.length; i++) { // este for solo ARMA los datos, no es parte del algoritmo
            // Creamos un paquete por cada prioridad (los otros datos no importan en esta prueba).
            datos2.add(new Paquete("PX" + (i + 1), "Armenia", 1.0, prioridades[i], 10));
        } // fin del for
        // Llamamos al metodo recursivo.
        int cuantos = Algoritmos.contarPrioridadMinima(datos2, 4);
        System.out.println("RESULTADO: " + cuantos + " paquetes  (esperado 4)");

        // ---------------------------------------------------------------
        System.out.println("\n===== PRUEBA 3: busqueda binaria de PQ731 =====");
        // Codigos ya ordenados (precondicion de la busqueda binaria).
        String[] codigos = {"PQ105", "PQ214", "PQ318", "PQ421", "PQ567", "PQ731", "PQ842", "PQ942"};
        List<Paquete> datos3 = new ArrayList<>(); // lista ordenada de paquetes
        // Creamos un paquete por cada codigo, en el mismo orden.
        for (String c : codigos) {
            datos3.add(new Paquete(c, "Armenia", 1.0, 3, 10));
        } // fin del for
        System.out.println("Lista ordenada por codigo (PRECONDICION): " + datos3);
        int pos = Algoritmos.busquedaBinaria(datos3, "PQ731"); // buscamos un codigo que si existe
        System.out.println("RESULTADO: posicion " + pos + "  (esperado 5)");
        System.out.println("-- Busqueda de un codigo que NO existe (PQ500):");
        pos = Algoritmos.busquedaBinaria(datos3, "PQ500"); // buscamos uno que no existe
        System.out.println("RESULTADO: " + pos + "  (esperado -1) con " + Algoritmos.getComparacionesBinaria() + " comparaciones");

        // ---------------------------------------------------------------
        System.out.println("\n===== PRUEBA 4: paquete mas pesado (divide y venceras) =====");
        double[] pesos = {4.5, 12.0, 3.2, 18.7, 7.4, 5.1}; // pesos del enunciado
        List<Paquete> datos4 = new ArrayList<>();          // paquetes con esos pesos
        for (int i = 0; i < pesos.length; i++) {
            datos4.add(new Paquete("PW" + (i + 1), "Armenia", pesos[i], 3, 10)); // un paquete por peso
        } // fin del for
        // Llamamos al algoritmo de divide y venceras.
        Paquete masPesado = Algoritmos.paqueteMasPesado(datos4);
        System.out.println("RESULTADO: " + masPesado.getCodigo() + " con " + masPesado.getPeso() + " kg  (esperado 18.7 kg)");

        // ---------------------------------------------------------------
        System.out.println("\n===== PRUEBA 5: orden de despacho prioritario (PriorityQueue) =====");
        // Cola de prioridad con las reglas de despacho (prioridad, tiempo, codigo).
        PriorityQueue<Paquete> cola = new PriorityQueue<>(new ComparadorDespacho());
        cola.offer(new Paquete("PQ420", "Armenia", 1.0, 3, 30)); // prioridad 3
        cola.offer(new Paquete("PQ731", "Armenia", 1.0, 5, 45)); // prioridad 5, 45 min
        cola.offer(new Paquete("PQ105", "Armenia", 1.0, 5, 25)); // prioridad 5, 25 min -> sale primero
        cola.offer(new Paquete("PQ942", "Armenia", 1.0, 4, 20)); // prioridad 4
        System.out.print("Orden obtenido con poll(): ");
        // Sacamos uno por uno hasta vaciar la cola.
        while (!cola.isEmpty()) {
            System.out.print(cola.poll() + " "); // poll saca el que sigue segun las reglas
        } // fin del while
        System.out.println(" (esperado PQ105 PQ731 PQ942 PQ420)");

        Algoritmos.mostrarTraza = false; // apagamos la traza para no afectar las demas opciones
    } // fin de ejecutar
} // fin de la clase PruebasEscritorio
