// Esta clase pertenece al paquete "estructuras"
package estructuras;

/**
 * Nodo de la lista simplemente enlazada.
 * Tiene dos partes (como vimos en clase):
 *   1. valorNodo     -> el dato que se guarda
 *   2. siguienteNodo -> la referencia al siguiente nodo (null si es el ultimo)
 *
 * Es generico (T) para que la lista sirva con cualquier tipo de dato.
 */
// <T> indica que el nodo puede guardar cualquier tipo de dato (Paquete, String, Integer...).
public class Nodo<T> {

    // Primera parte del nodo: el dato que se guarda. Es private para que solo se use con get/set.
    private T valorNodo;
    // Segunda parte del nodo: la referencia (el "enlace") al nodo que va despues.
    private Nodo<T> siguienteNodo;

    // Constructor: se ejecuta cuando se hace "new Nodo<>(dato)".
    public Nodo(T valorNodo) {
        // Guardamos el dato que llego por parametro en el atributo del nodo.
        this.valorNodo = valorNodo;
        this.siguienteNodo = null; // al crearlo todavia no apunta a nadie
    } // fin del constructor

    // Devuelve el dato guardado en este nodo.
    public T getValorNodo() {
        return valorNodo; // retorna el valor
    }

    // Cambia el dato guardado en este nodo.
    public void setValorNodo(T valorNodo) {
        this.valorNodo = valorNodo; // reemplaza el valor anterior por el nuevo
    }

    // Devuelve la referencia al nodo siguiente (o null si este es el ultimo).
    public Nodo<T> getSiguienteNodo() {
        return siguienteNodo; // retorna el enlace al siguiente
    }

    // Cambia a que nodo apunta este nodo. Asi se "enlazan" los nodos entre si.
    public void setSiguienteNodo(Nodo<T> siguienteNodo) {
        this.siguienteNodo = siguienteNodo; // ahora este nodo apunta al nodo recibido
    }
} // fin de la clase Nodo
