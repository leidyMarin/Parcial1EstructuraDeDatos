// Esta clase pertenece al paquete "modelo"
package modelo;

// Lista para guardar los paquetes que el repartidor lleva en este momento.
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un repartidor de QuindioExpress.
 * La zona es el municipio donde normalmente entrega (debe coincidir con el
 * destino del paquete para poder asignarselo).
 *
 * CAPACIDAD: cada repartidor puede llevar varios paquetes a la vez, hasta su
 * capacidadMaxima. Por eso "disponible" ya NO es un atributo que se cambia a mano:
 * se CALCULA. Un repartidor esta disponible si:
 *   1. esta activo (trabaja hoy), y
 *   2. esta en el centro (todavia no ha salido a ruta), y
 *   3. todavia le cabe al menos un paquete (carga actual < capacidad maxima).
 *
 * SALIDA A RUTA: el repartidor sale (y pasa a NO disponible) cuando se llena,
 * o cuando ya no quedan paquetes pendientes de su zona aunque no este lleno
 * (ej: a Armenia solo llego 1 paquete -> sale con 1). Vuelve a estar disponible
 * cuando entrega TODOS sus paquetes (regresa al centro).
 */
public class Repartidor {

    // Capacidad que se usa si no se indica una (por ejemplo al registrar desde el menu sin escribir nada).
    public static final int CAPACIDAD_POR_DEFECTO = 3;

    // Atributos del repartidor.
    private String identificacion;        // identifica de forma unica al repartidor (ej: R01)
    private String nombre;                // nombre completo
    private String zona;                  // municipio donde reparte
    private boolean activo;               // true = trabaja hoy, false = no recibe paquetes (ej: incapacitado)
    private int capacidadMaxima;          // cuantos paquetes puede llevar al mismo tiempo
    private List<Paquete> paquetesEnRuta; // paquetes que lleva ahora (asignados y sin entregar)
    private boolean enRuta;               // true = ya salio del centro a repartir (no recibe mas paquetes)

    // Constructor completo: valida los datos y crea el repartidor con su capacidad.
    public Repartidor(String identificacion, String nombre, String zona, boolean activo, int capacidadMaxima) {
        // La identificacion no puede ser null ni vacia.
        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La identificacion del repartidor no puede estar vacia");
        } // fin validacion identificacion
        // El nombre no puede ser null ni vacio.
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del repartidor no puede estar vacio");
        } // fin validacion nombre
        // La zona no puede ser null ni vacia.
        if (zona == null || zona.trim().isEmpty()) {
            throw new IllegalArgumentException("La zona del repartidor no puede estar vacia");
        } // fin validacion zona
        // La capacidad tiene que ser al menos 1 paquete.
        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad del repartidor " + identificacion
                    + " debe ser mayor que cero (llego " + capacidadMaxima + ")");
        } // fin validacion capacidad
        // Guardamos los datos sin espacios sobrantes.
        this.identificacion = identificacion.trim();
        this.nombre = nombre.trim();
        this.zona = zona.trim();
        this.activo = activo;                  // si trabaja hoy o no
        this.capacidadMaxima = capacidadMaxima; // maximo de paquetes a la vez
        this.paquetesEnRuta = new ArrayList<>(); // arranca sin paquetes
        this.enRuta = false;                     // arranca en el centro, esperando paquetes
    } // fin del constructor completo

    /** Constructor sin capacidad: usa la capacidad por defecto. */
    public Repartidor(String identificacion, String nombre, String zona, boolean activo) {
        // "this(...)" llama al constructor completo pasando la capacidad por defecto.
        this(identificacion, nombre, zona, activo, CAPACIDAD_POR_DEFECTO);
    } // fin del constructor sin capacidad

    // ---------------- CAPACIDAD ----------------

    /**
     * Disponible = activo Y esta en el centro (no ha salido a ruta) Y todavia le cabe un paquete.
     * Ya no se cambia a mano: se calcula cada vez que se pregunta. O(1).
     */
    public boolean isDisponible() {
        return activo && !enRuta && paquetesEnRuta.size() < capacidadMaxima;
    } // fin de isDisponible

    // ---------------- SALIDA A RUTA ----------------

    /**
     * El repartidor sale del centro a repartir con los paquetes que lleva.
     * Desde ese momento queda NO disponible aunque tenga cupo (ya va en la calle).
     * Se usa cuando: se llena, o ya no quedan paquetes pendientes de su zona,
     * o el funcionario lo manda a salir a mano. O(1).
     */
    public void salirARuta() {
        // No tiene sentido salir sin paquetes.
        if (paquetesEnRuta.isEmpty()) {
            throw new IllegalArgumentException("El repartidor " + nombre + " no lleva paquetes, no puede salir a ruta");
        } // fin del if
        // Si ya salio, no se puede volver a salir.
        if (enRuta) {
            throw new IllegalArgumentException("El repartidor " + nombre + " ya salio a ruta");
        } // fin del if
        enRuta = true; // queda en la calle: NO disponible
    } // fin de salirARuta

    /** true si ya salio del centro a repartir. */
    public boolean isEnRuta() {
        return enRuta;
    } // fin de isEnRuta

    /** true si ya lleva la cantidad maxima de paquetes. */
    public boolean estaLleno() {
        return paquetesEnRuta.size() >= capacidadMaxima;
    } // fin de estaLleno

    /**
     * Le carga un paquete al repartidor (se llama al ASIGNAR).
     * Si no esta activo o ya esta lleno, lanza error y no carga nada.
     * O(1) porque ArrayList.add agrega al final.
     */
    public void cargarPaquete(Paquete p) {
        // Regla: un repartidor que no trabaja hoy no recibe paquetes.
        if (!activo) {
            throw new IllegalArgumentException("El repartidor " + nombre + " NO esta disponible (inactivo)");
        } // fin del if
        // Regla: si ya salio a ruta no se le pueden cargar mas paquetes.
        if (enRuta) {
            throw new IllegalArgumentException("El repartidor " + nombre + " NO esta disponible (ya salio a ruta)");
        } // fin del if
        // Regla: no puede llevar mas paquetes que su capacidad.
        if (estaLleno()) {
            throw new IllegalArgumentException("El repartidor " + nombre + " ya lleva "
                    + paquetesEnRuta.size() + " de " + capacidadMaxima + " paquetes (esta lleno)");
        } // fin del if
        paquetesEnRuta.add(p); // ahora lleva un paquete mas
    } // fin de cargarPaquete

    /**
     * Le quita un paquete al repartidor (se llama al REGISTRAR LA ENTREGA).
     * O(k) donde k = paquetes que lleva (maximo su capacidad, son pocos).
     */
    public void descargarPaquete(Paquete p) {
        paquetesEnRuta.remove(p); // remove usa equals de Paquete (compara por codigo)
        // Cuando entrega el ULTIMO paquete vuelve al centro y queda disponible otra vez.
        if (paquetesEnRuta.isEmpty()) {
            enRuta = false;
        } // fin del if
    } // fin de descargarPaquete

    // ---------------- GETTERS Y SETTERS ----------------

    public String getIdentificacion() { return identificacion; } // devuelve la identificacion
    public String getNombre() { return nombre; }                 // devuelve el nombre
    public String getZona() { return zona; }                     // devuelve la zona
    public boolean isActivo() { return activo; }                 // devuelve si trabaja hoy
    public int getCapacidadMaxima() { return capacidadMaxima; }  // devuelve la capacidad
    public int getCargaActual() { return paquetesEnRuta.size(); } // cuantos paquetes lleva ahora

    // Copia de los paquetes que lleva (copia para que nadie de afuera los modifique).
    public List<Paquete> getPaquetesEnRuta() {
        return new ArrayList<>(paquetesEnRuta);
    } // fin de getPaquetesEnRuta

    // Sirve para poner al repartidor activo o inactivo (ej: se enfermo, o volvio a trabajar).
    public void setActivo(boolean activo) {
        this.activo = activo;
    } // fin de setActivo

    // ---------------- EQUALS, HASHCODE Y TOSTRING ----------------

    // Dos repartidores son el mismo si tienen la misma identificacion.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;                   // mismo objeto en memoria
        if (!(obj instanceof Repartidor)) return false; // no es un Repartidor
        // Comparamos las identificaciones (el cast convierte el Object a Repartidor).
        return identificacion.equals(((Repartidor) obj).identificacion);
    } // fin de equals

    // hashCode coherente con equals: misma identificacion -> mismo hash.
    @Override
    public int hashCode() {
        return identificacion.hashCode();
    } // fin de hashCode

    // Muestra el repartidor en una linea: datos, carga (ej: 2/3) y estado.
    @Override
    public String toString() {
        String estado; // texto del estado que vamos a mostrar
        if (!activo) {
            estado = "NO disponible (inactivo)";
        } else if (enRuta) {
            estado = "NO disponible (en ruta)";
        } else if (estaLleno()) {
            estado = "NO disponible (lleno)";
        } else {
            estado = "Disponible";
        } // fin del if-else
        return String.format("%-5s | %-22s | %-11s | carga %d/%d %s | %s",
                identificacion, nombre, zona, paquetesEnRuta.size(), capacidadMaxima, paquetesEnRuta, estado);
    } // fin de toString
} // fin de la clase Repartidor
