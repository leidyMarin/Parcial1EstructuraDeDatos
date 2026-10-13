// Esta clase pertenece al paquete "estructuras"
package estructuras;

// Importamos la interfaz Iterator de Java (la necesitamos para poder recorrer la lista con for-each).
import java.util.Iterator;
// Excepcion que se lanza cuando se pide next() y ya no hay mas elementos.
import java.util.NoSuchElementException;

/**
 * Lista simplemente enlazada PROPIA y GENERICA.
 *
 * Se usa para el historial de entregas, pero como es generica tambien
 * se puede usar con String, Integer o cualquier otro tipo.
 *
 * IMPORTANTE: por dentro NO usa ArrayList, LinkedList ni ninguna coleccion
 * de Java. Solo usa nodos enlazados.
 *
 * Atributos:
 *   nodoPrimero -> referencia al primer nodo (cabeza)
 *   nodoUltimo  -> referencia al ultimo nodo (cola). La guardamos para que
 *                  agregar al final sea O(1) y no tener que recorrer toda la lista.
 *   tamanio     -> cantidad de elementos (asi getTamanio() es O(1))
 */
// "implements IColeccion<T>" = esta clase se compromete a programar todos los metodos de la interfaz.
public class ListaSimple<T> implements IColeccion<T> {

    // Referencia al primer nodo de la lista (la cabeza). Si es null, la lista esta vacia.
    private Nodo<T> nodoPrimero;
    // Referencia al ultimo nodo de la lista. Sirve para agregar al final sin recorrer.
    private Nodo<T> nodoUltimo;
    // Contador de cuantos elementos hay en la lista.
    private int tamanio;

    // Constructor: crea una lista vacia.
    public ListaSimple() {
        nodoPrimero = null; // no hay primer nodo
        nodoUltimo = null;  // no hay ultimo nodo
        tamanio = 0;        // la lista arranca con 0 elementos
    } // fin del constructor

    // ------------------------------------------------------------------
    // AGREGAR
    // ------------------------------------------------------------------

    /**
     * Agrega el elemento al FINAL de la lista.
     * Asi el historial queda en el mismo orden en que se entregaron los paquetes.
     * Complejidad: O(1) porque tenemos la referencia nodoUltimo.
     */
    @Override // indica que estamos programando un metodo que viene de la interfaz
    public void agregar(T elemento) {
        // Creamos un nodo nuevo con el elemento recibido (su siguiente arranca en null).
        Nodo<T> nuevo = new Nodo<>(elemento);

        // Preguntamos si la lista no tiene ningun elemento.
        if (estaVacia()) {
            // si la lista esta vacia, el nuevo nodo es el primero y el ultimo
            nodoPrimero = nuevo; // la cabeza ahora es el nuevo nodo
            nodoUltimo = nuevo;  // y tambien es el ultimo porque es el unico
        } else { // si la lista ya tenia elementos...
            // el que era ultimo ahora apunta al nuevo, y el nuevo pasa a ser el ultimo
            nodoUltimo.setSiguienteNodo(nuevo); // enlazamos el ultimo actual con el nuevo
            nodoUltimo = nuevo;                 // movemos la referencia del ultimo al nuevo nodo
        } // fin del if-else
        tamanio++; // aumentamos en 1 la cantidad de elementos
    } // fin de agregar

    /**
     * Agrega el elemento al INICIO de la lista. O(1).
     * (No lo pide el parcial pero es una operacion basica de la lista.)
     */
    public void agregarInicio(T elemento) {
        // Creamos el nodo nuevo con el elemento.
        Nodo<T> nuevo = new Nodo<>(elemento);
        // Si la lista esta vacia...
        if (estaVacia()) {
            nodoPrimero = nuevo; // el nuevo es el primero
            nodoUltimo = nuevo;  // y tambien el ultimo
        } else { // si ya habia elementos...
            nuevo.setSiguienteNodo(nodoPrimero); // el nuevo apunta al que era primero
            nodoPrimero = nuevo;                 // y el nuevo pasa a ser la cabeza
        } // fin del if-else
        tamanio++; // hay un elemento mas
    } // fin de agregarInicio

    // ------------------------------------------------------------------
    // ELIMINAR
    // ------------------------------------------------------------------

    /**
     * Elimina la primera aparicion del elemento (se compara con equals).
     * Casos que se manejan:
     *   - lista vacia                 -> retorna false
     *   - eliminar el primero         -> se mueve nodoPrimero
     *   - eliminar uno del medio      -> el anterior se "salta" al eliminado
     *   - eliminar el ultimo          -> se actualiza nodoUltimo
     *   - lista de un solo elemento   -> queda vacia (primero y ultimo en null)
     *   - elemento que no existe      -> retorna false y la lista no cambia
     * Complejidad: O(n) en el peor caso porque hay que buscarlo.
     */
    @Override // metodo que viene de la interfaz IColeccion
    public boolean eliminar(T elemento) {
        // Caso 1: lista vacia
        if (estaVacia()) {
            return false; // no hay nada que borrar
        } // fin caso 1

        // Caso 2: el elemento esta en el primer nodo
        if (sonIguales(nodoPrimero.getValorNodo(), elemento)) {
            // La cabeza pasa a ser el segundo nodo (el primero queda sin referencias y Java lo libera).
            nodoPrimero = nodoPrimero.getSiguienteNodo();
            // Si despues de moverla la cabeza quedo en null...
            if (nodoPrimero == null) {
                // la lista tenia un solo elemento y quedo vacia
                nodoUltimo = null; // entonces tampoco hay ultimo
            } // fin del if
            tamanio--;   // hay un elemento menos
            return true; // avisamos que si se elimino
        } // fin caso 2

        // Caso 3: buscarlo en el resto de la lista.
        // Necesitamos el nodo ANTERIOR para poder "saltarnos" el nodo a eliminar.
        Nodo<T> anterior = nodoPrimero;                 // empezamos con el anterior en la cabeza
        Nodo<T> actual = nodoPrimero.getSiguienteNodo(); // y el actual en el segundo nodo

        // Recorremos mientras queden nodos por revisar.
        while (actual != null) {
            // Si el valor del nodo actual es el que buscamos...
            if (sonIguales(actual.getValorNodo(), elemento)) {
                // el anterior ahora apunta al siguiente del actual
                anterior.setSiguienteNodo(actual.getSiguienteNodo());

                // si eliminamos el ultimo, el anterior pasa a ser el ultimo
                if (actual == nodoUltimo) {
                    nodoUltimo = anterior; // actualizamos la referencia al ultimo
                } // fin del if
                tamanio--;   // un elemento menos
                return true; // se elimino con exito
            } // fin del if de encontrado
            anterior = actual;                  // el anterior avanza al actual
            actual = actual.getSiguienteNodo(); // el actual avanza al siguiente nodo
        } // fin del while

        // Caso 4: no se encontro
        return false; // recorrimos toda la lista y no estaba
    } // fin de eliminar

    /** Compara dos valores teniendo en cuenta que alguno puede ser null. */
    private boolean sonIguales(T a, T b) {
        // Si el primero es null, solo son iguales si el segundo tambien es null.
        if (a == null) {
            return b == null;
        } // fin del if
        // Si no es null, usamos equals (en Paquete compara por codigo).
        return a.equals(b);
    } // fin de sonIguales

    // ------------------------------------------------------------------
    // OBTENER
    // ------------------------------------------------------------------

    /**
     * Devuelve el valor que esta en la posicion indicada (0 = primero).
     * Como es una lista enlazada no hay acceso directo: hay que avanzar
     * nodo por nodo. Complejidad: O(n).
     */
    @Override // metodo de la interfaz
    public T obtener(int posicion) {
        // Si la posicion no existe en la lista, lanzamos un error con un mensaje claro.
        if (!indiceValido(posicion)) {
            throw new IndexOutOfBoundsException("Posicion " + posicion
                    + " no valida. La lista tiene " + tamanio + " elementos.");
        } // fin del if
        // Variable auxiliar que arranca en la cabeza de la lista.
        Nodo<T> aux = nodoPrimero;
        // Avanzamos "posicion" veces de nodo en nodo.
        for (int i = 0; i < posicion; i++) {
            aux = aux.getSiguienteNodo(); // pasamos al siguiente nodo
        } // fin del for
        // Al terminar, aux esta en la posicion pedida: devolvemos su valor.
        return aux.getValorNodo();
    } // fin de obtener

    // Revisa que la posicion este entre 0 y tamanio - 1.
    private boolean indiceValido(int posicion) {
        return posicion >= 0 && posicion < tamanio; // true si la posicion existe
    } // fin de indiceValido

    // ------------------------------------------------------------------
    // TAMANIO Y VACIA
    // ------------------------------------------------------------------

    @Override // metodo de la interfaz
    public int getTamanio() {
        return tamanio; // devolvemos el contador (no hay que recorrer la lista)
    } // fin de getTamanio

    @Override // metodo de la interfaz
    public boolean estaVacia() {
        return nodoPrimero == null; // si no hay cabeza, la lista esta vacia
    } // fin de estaVacia

    // ------------------------------------------------------------------
    // ITERATOR
    // ------------------------------------------------------------------

    /**
     * Metodo que nos obliga a escribir la interfaz Iterable.
     * Devuelve un objeto Iterator que sabe recorrer la lista.
     * Gracias a esto se puede hacer:  for (Paquete p : historial) { ... }
     * y el codigo de afuera nunca toca los nodos.
     */
    @Override // metodo que viene de Iterable (a traves de IColeccion)
    public Iterator<T> iterator() {
        // Cada vez que se llama crea un iterador nuevo que empieza desde el primer nodo.
        return new IteradorListaSimple();
    } // fin de iterator

    /**
     * Clase interna que implementa Iterator.
     * Guarda en "actual" el nodo por el que va el recorrido.
     *
     * Diferencia Iterable vs Iterator:
     *   - Iterable: "esta coleccion SE PUEDE recorrer" (tiene el metodo iterator()).
     *   - Iterator: "el objeto que HACE el recorrido" (hasNext() y next()).
     */
    // Es "private" porque solo la ListaSimple necesita conocer esta clase.
    // Al ser clase interna puede ver nodoPrimero de la lista.
    private class IteradorListaSimple implements Iterator<T> {

        // Nodo en el que va el recorrido.
        private Nodo<T> actual;

        // Constructor del iterador.
        public IteradorListaSimple() {
            actual = nodoPrimero; // empieza en la cabeza
        } // fin del constructor

        // Pregunta si todavia quedan elementos por recorrer.
        @Override
        public boolean hasNext() {
            return actual != null; // si actual no es null, hay un elemento mas
        } // fin de hasNext

        // Devuelve el elemento actual y avanza al siguiente.
        @Override
        public T next() {
            // Si ya no hay elementos y se pide otro, es un error.
            if (!hasNext()) {
                throw new NoSuchElementException("No hay mas elementos en la lista");
            } // fin del if
            T valor = actual.getValorNodo(); // guardamos el valor del nodo actual
            actual = actual.getSiguienteNodo(); // avanzamos al siguiente nodo
            return valor; // devolvemos el valor que guardamos
        } // fin de next
    } // fin de la clase interna IteradorListaSimple

    // ------------------------------------------------------------------
    // TOSTRING
    // ------------------------------------------------------------------

    /** Muestra la lista asi: [A -> B -> C] */
    @Override // sobrescribe el toString que viene de Object
    public String toString() {
        String texto = "[";       // empezamos el texto con el corchete
        Nodo<T> aux = nodoPrimero; // auxiliar para recorrer desde la cabeza
        // Recorremos hasta que no haya mas nodos.
        while (aux != null) {
            texto += aux.getValorNodo(); // agregamos el valor del nodo al texto
            // Si no es el ultimo nodo, ponemos la flecha.
            if (aux.getSiguienteNodo() != null) {
                texto += " -> ";
            } // fin del if
            aux = aux.getSiguienteNodo(); // avanzamos al siguiente nodo
        } // fin del while
        return texto + "]"; // cerramos el corchete y devolvemos el texto
    } // fin de toString
} // fin de la clase ListaSimple
