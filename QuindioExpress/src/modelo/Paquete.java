// Esta clase pertenece al paquete "modelo" (clases que representan cosas del problema)
package modelo;

/**
 * Representa un paquete que llega al centro de distribucion.
 *
 * Implementa Comparable<Paquete> para definir el ORDEN NATURAL:
 * los paquetes se ordenan por codigo de forma ascendente (PQ105, PQ318, PQ567...).
 *
 * Los otros ordenes (por prioridad, peso, tiempo) NO van aqui, van en
 * clases Comparator aparte (paquete "comparadores"), asi el orden natural
 * nunca cambia.
 */
// "implements Comparable<Paquete>" nos obliga a programar compareTo (el orden natural).
public class Paquete implements Comparable<Paquete> {

    // Reglas del enunciado (seccion "reglas del sistema"): prioridad entre 1 y 5.
    // Nota: en la descripcion del atributo dice 0-10, pero en las reglas dice 1-5.
    // Decidimos usar 1-5 (ver README). Si el profe pide cambiarlo, solo se
    // cambian estas dos constantes.
    // "static final" = constante: es una sola para toda la clase y no se puede cambiar.
    public static final int PRIORIDAD_MINIMA = 1;      // prioridad mas baja permitida
    public static final int PRIORIDAD_MAXIMA = 5;      // prioridad mas alta permitida
    public static final int PRIORIDAD_POR_DEFECTO = 5; // la que se usa si no se indica prioridad

    // Estados posibles del paquete
    public static final String PENDIENTE = "PENDIENTE"; // registrado, esperando despacho
    public static final String ASIGNADO = "ASIGNADO";   // ya tiene repartidor, va en ruta
    public static final String ENTREGADO = "ENTREGADO"; // ya se entrego (esta en el historial)

    // Atributos del paquete (private = solo se acceden con los getters).
    private String codigo;        // identificador unico, ej: PQ731
    private String destino;       // municipio al que va
    private double peso;          // en kilogramos
    private int prioridad;        // de 1 a 5
    private int tiempoEstimado;   // en minutos
    private String estado;        // PENDIENTE, ASIGNADO o ENTREGADO

    /** Constructor completo. Valida todos los datos antes de crear el objeto. */
    public Paquete(String codigo, String destino, double peso, int prioridad, int tiempoEstimado) {
        // Validaciones (si algo esta mal se lanza la excepcion y NO se crea el paquete)
        // trim() quita los espacios del inicio y el final; isEmpty() pregunta si quedo vacio.
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El codigo del paquete no puede estar vacio");
        } // fin validacion codigo
        // El destino no puede ser null ni solo espacios.
        if (destino == null || destino.trim().isEmpty()) {
            throw new IllegalArgumentException("El destino del paquete " + codigo + " no puede ser nulo ni vacio");
        } // fin validacion destino
        // El peso tiene que ser mayor que cero.
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso del paquete " + codigo + " debe ser mayor que cero (llego " + peso + ")");
        } // fin validacion peso
        // La prioridad tiene que estar dentro del rango permitido.
        if (prioridad < PRIORIDAD_MINIMA || prioridad > PRIORIDAD_MAXIMA) {
            throw new IllegalArgumentException("La prioridad del paquete " + codigo + " debe estar entre "
                    + PRIORIDAD_MINIMA + " y " + PRIORIDAD_MAXIMA + " (llego " + prioridad + ")");
        } // fin validacion prioridad
        // El tiempo estimado tiene que ser positivo.
        if (tiempoEstimado <= 0) {
            throw new IllegalArgumentException("El tiempo estimado del paquete " + codigo + " debe ser mayor que cero");
        } // fin validacion tiempo

        // Si paso todas las validaciones, guardamos los datos.
        this.codigo = codigo.trim().toUpperCase(); // sin espacios y en mayuscula (pq731 -> PQ731)
        this.destino = destino.trim();             // destino sin espacios sobrantes
        this.peso = peso;                          // peso en kg
        this.prioridad = prioridad;                // prioridad 1-5
        this.tiempoEstimado = tiempoEstimado;      // minutos
        this.estado = PENDIENTE;                   // todo paquete nuevo empieza pendiente
    } // fin del constructor completo

    /** Constructor sin prioridad: se usa la prioridad por defecto. */
    public Paquete(String codigo, String destino, double peso, int tiempoEstimado) {
        // "this(...)" llama al constructor completo de arriba, pasando la prioridad por defecto.
        this(codigo, destino, peso, PRIORIDAD_POR_DEFECTO, tiempoEstimado);
    } // fin del constructor sin prioridad

    // ---------------- ORDEN NATURAL ----------------

    /**
     * Orden natural: por codigo ascendente.
     * Retorna negativo si this va antes, 0 si son iguales, positivo si va despues.
     */
    @Override // metodo que viene de la interfaz Comparable
    public int compareTo(Paquete otro) {
        // Usamos el compareTo de String, que compara alfabeticamente.
        return this.codigo.compareTo(otro.codigo);
    } // fin de compareTo

    // ---------------- EQUALS Y HASHCODE ----------------
    // Dos paquetes son "el mismo" si tienen el mismo codigo.
    // Esto es necesario para que remove(...) de las colecciones y el
    // eliminar(...) de nuestra ListaSimple funcionen bien.

    @Override // sobrescribe el equals de Object
    public boolean equals(Object obj) {
        if (this == obj) return true;                  // si es exactamente el mismo objeto, son iguales
        if (!(obj instanceof Paquete)) return false;   // si el otro no es un Paquete, no son iguales
        Paquete otro = (Paquete) obj;                  // convertimos (cast) el Object a Paquete
        return this.codigo.equals(otro.codigo);        // son iguales si tienen el mismo codigo
    } // fin de equals

    @Override // sobrescribe el hashCode de Object
    public int hashCode() {
        // Debe ser coherente con equals: mismo codigo -> mismo hash (lo usa el HashMap).
        return codigo.hashCode();
    } // fin de hashCode

    // ---------------- GETTERS Y SETTERS ----------------

    public String getCodigo() { return codigo; }                // devuelve el codigo
    public String getDestino() { return destino; }              // devuelve el destino
    public double getPeso() { return peso; }                    // devuelve el peso
    public int getPrioridad() { return prioridad; }             // devuelve la prioridad
    public int getTiempoEstimado() { return tiempoEstimado; }   // devuelve el tiempo estimado
    public String getEstado() { return estado; }                // devuelve el estado

    // El estado es lo unico que cambia despues de crear el paquete (pendiente -> asignado -> entregado).
    public void setEstado(String estado) {
        this.estado = estado; // guardamos el nuevo estado
    } // fin de setEstado

    // ---------------- MOSTRAR ----------------

    /** toString corto: solo el codigo (asi el historial se ve PQ105 -> PQ942 -> ...). */
    @Override // sobrescribe el toString de Object
    public String toString() {
        return codigo; // al imprimir el paquete solo se muestra su codigo
    } // fin de toString

    /** Toda la informacion del paquete en una linea. */
    public String getDetalle() {
        // String.format arma el texto con columnas alineadas:
        // %-6s = texto de 6 espacios alineado a la izquierda, %6.1f = numero con 1 decimal, %d = entero.
        return String.format("%-6s | %-11s | %6.1f kg | prioridad %d | %3d min | %s",
                codigo, destino, peso, prioridad, tiempoEstimado, estado);
    } // fin de getDetalle
} // fin de la clase Paquete
