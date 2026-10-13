// Esta clase pertenece al paquete "app"
package app;

// La interfaz (el contrato) de nuestra coleccion.
import estructuras.IColeccion;
// Nuestra lista enlazada propia.
import estructuras.ListaSimple;

/**
 * Pruebas de la lista simplemente enlazada propia con los casos que pide el enunciado:
 *   lista vacia, un solo elemento, eliminar primero, intermedio, ultimo e inexistente.
 *
 * Se prueba con String (no con Paquete) para demostrar que la lista es GENERICA.
 * La variable se declara con el tipo de la INTERFAZ (IColeccion) para mostrar
 * que el codigo de afuera solo conoce el contrato y no los nodos.
 */
public class PruebasListaSimple {

    // Ejecuta todas las pruebas e imprime los resultados.
    public static void ejecutar() {
        System.out.println("\n===== PRUEBAS DE LA LISTA SIMPLE (con String) ====="); // titulo

        // Variable de tipo interfaz, objeto de tipo ListaSimple, guardando String.
        IColeccion<String> lista = new ListaSimple<>();

        // 1. lista vacia
        // Mostramos la lista, si esta vacia y su tamanio.
        System.out.println("1. Lista nueva: " + lista + "  vacia=" + lista.estaVacia() + "  tamanio=" + lista.getTamanio());
        // Eliminar en lista vacia debe dar false.
        System.out.println("   Eliminar 'X' en lista vacia -> " + lista.eliminar("X"));
        // try-catch: intentamos algo que debe fallar y atrapamos el error para mostrarlo.
        try {
            lista.obtener(0); // en lista vacia no existe la posicion 0
        } catch (IndexOutOfBoundsException e) {
            System.out.println("   obtener(0) en lista vacia -> ERROR: " + e.getMessage()); // mostramos el error
        } // fin del try-catch

        // 2. un solo elemento
        lista.agregar("A"); // agregamos un elemento
        System.out.println("2. Agregar A: " + lista + "  tamanio=" + lista.getTamanio() + "  obtener(0)=" + lista.obtener(0));
        // Eliminar el unico elemento debe dejar la lista vacia.
        System.out.println("   Eliminar A (unico elemento) -> " + lista.eliminar("A") + "  queda: " + lista
                + "  vacia=" + lista.estaVacia());

        // 3. varios elementos
        lista.agregar("A"); // posicion 0
        lista.agregar("B"); // posicion 1
        lista.agregar("C"); // posicion 2
        lista.agregar("D"); // posicion 3
        lista.agregar("E"); // posicion 4
        System.out.println("3. Agregar A,B,C,D,E: " + lista + "  tamanio=" + lista.getTamanio());
        System.out.println("   obtener(2) = " + lista.obtener(2)); // debe mostrar C

        // 4. eliminar el primero
        System.out.println("4. Eliminar primero (A) -> " + lista.eliminar("A") + "  queda: " + lista);
        // 5. eliminar intermedio
        System.out.println("5. Eliminar intermedio (C) -> " + lista.eliminar("C") + "  queda: " + lista);
        // 6. eliminar el ultimo
        System.out.println("6. Eliminar ultimo (E) -> " + lista.eliminar("E") + "  queda: " + lista);
        // despues de borrar el ultimo, agregar debe seguir funcionando (prueba de nodoUltimo)
        lista.agregar("F");
        System.out.println("   Agregar F despues de borrar el ultimo: " + lista);
        // 7. eliminar inexistente
        System.out.println("7. Eliminar inexistente (Z) -> " + lista.eliminar("Z") + "  queda: " + lista
                + "  tamanio=" + lista.getTamanio());
        // 8. posicion invalida
        try {
            lista.obtener(10); // la lista solo tiene 3 elementos
        } catch (IndexOutOfBoundsException e) {
            System.out.println("8. obtener(10) -> ERROR: " + e.getMessage()); // mostramos el error
        } // fin del try-catch

        // 9. recorrido con for-each (usa Iterable / Iterator, sin tocar nodos)
        System.out.print("9. Recorrido con for-each: ");
        // Java llama internamente a lista.iterator(), y luego hasNext() y next() en cada vuelta.
        for (String s : lista) {
            System.out.print(s + " "); // imprimimos cada elemento
        } // fin del for-each
        System.out.println(); // salto de linea

        // 10. la misma lista con Integer (genericidad)
        ListaSimple<Integer> numeros = new ListaSimple<>(); // ahora la lista guarda Integer
        numeros.agregar(5);
        numeros.agregar(2);
        numeros.agregar(4);
        int suma = 0; // acumulador
        // Recorremos con for-each y sumamos.
        for (Integer n : numeros) {
            suma += n; // suma = suma + n
        } // fin del for-each
        System.out.println("10. Lista de Integer " + numeros + " -> suma con for-each = " + suma);
    } // fin de ejecutar
} // fin de la clase PruebasListaSimple
