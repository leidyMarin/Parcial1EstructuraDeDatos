// Esta interfaz pertenece al paquete "estructuras" (la carpeta src/estructuras)
package estructuras;

/**
 * Interfaz generica que define el "contrato" (el TAD) de las colecciones
 * internas de QuindioExpress.
 *
 * La interfaz solo dice QUE operaciones debe tener la coleccion.
 * El COMO se hacen esas operaciones lo decide la clase que la implemente
 * (en nuestro caso la ListaSimple).
 *
 * Extiende de Iterable<T> para obligar a que toda coleccion nuestra se pueda
 * recorrer con un for-each sin que el que la usa conozca los nodos.
 *
 * @param <T> tipo de dato que se guarda (Paquete, String, Integer, etc.)
 */
// "interface" = solo declara metodos, no los programa.
// <T> = tipo generico: se reemplaza por el tipo real cuando se usa (ej: IColeccion<Paquete>).
// "extends Iterable<T>" = toda clase que implemente IColeccion tambien debe tener el metodo iterator().
public interface IColeccion<T> extends Iterable<T> {

    /** Agrega un elemento al final de la coleccion. */
    // Recibe un elemento de tipo T y no devuelve nada (void).
    void agregar(T elemento);

    /**
     * Elimina la primera aparicion del elemento.
     * @return true si lo encontro y lo elimino, false si no existia
     */
    // Devuelve boolean para que quien lo llame sepa si de verdad se borro algo.
    boolean eliminar(T elemento);

    /**
     * Devuelve el elemento que esta en la posicion indicada (empieza en 0).
     * Lanza IndexOutOfBoundsException si la posicion no existe.
     */
    // Recibe una posicion (int) y devuelve el elemento guardado ahi (tipo T).
    T obtener(int posicion);

    /** Cantidad de elementos guardados. */
    // Devuelve cuantos elementos hay en la coleccion.
    int getTamanio();

    /** true si no hay elementos. */
    // Devuelve true si la coleccion no tiene elementos, false si tiene al menos uno.
    boolean estaVacia();
} // fin de la interfaz IColeccion
