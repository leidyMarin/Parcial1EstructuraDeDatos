// Esta clase pertenece al paquete "logica" (las reglas y operaciones del negocio)
package logica;

// Importamos nuestro comparador para la cola de prioridad.
import comparadores.ComparadorDespacho;
// Importamos nuestra lista enlazada propia (para el historial).
import estructuras.ListaSimple;
// Colecciones de Java que usamos (cada una para una necesidad distinta):
import java.util.ArrayDeque;     // cola doble basada en arreglo circular
import java.util.ArrayList;      // lista basada en arreglo
import java.util.Collections;    // metodos utilitarios como sort
import java.util.Comparator;     // tipo de los criterios de orden
import java.util.Deque;          // interfaz de cola doble (se saca/mete por los dos extremos)
import java.util.HashMap;        // mapa clave -> valor con tabla hash
import java.util.LinkedHashMap;  // HashMap que recuerda el orden de insercion
import java.util.LinkedHashSet;  // conjunto sin repetidos que recuerda el orden de insercion
import java.util.List;           // interfaz de lista
import java.util.Map;            // interfaz de mapa
import java.util.PriorityQueue;  // cola de prioridad (monticulo)
import java.util.Set;            // interfaz de conjunto
import java.util.TreeSet;        // conjunto ordenado (arbol)
// Clases del modelo.
import modelo.Paquete;
import modelo.Repartidor;

/**
 * Clase principal de la logica. Aqui se guardan TODAS las estructuras de datos
 * del centro de distribucion. Cada estructura se eligio para una necesidad
 * distinta (ver la tabla A.1 del documento de diseno).
 */
public class CentroDistribucion {

    // ------------------------------------------------------------------
    // ESTRUCTURAS DE DATOS (cada una con su porque)
    // ------------------------------------------------------------------

    /**
     * Registro de auditoria: TODOS los paquetes en el orden exacto en que se
     * registraron. Nunca se borra nada de aqui. ArrayList porque agregar al
     * final es O(1) amortizado y no cambia la posicion de los demas, y
     * get(i) es O(1) (lo usan los metodos recursivos por indice).
     */
    private List<Paquete> registroLlegada;

    /**
     * Paquetes PENDIENTES en orden de llegada (FIFO). Se usa como cola:
     * se agrega por el final y se atiende por el frente.
     * Usamos Deque (ArrayDeque) y no solo Queue porque si un paquete no se
     * puede asignar (no hay repartidor) lo devolvemos al FRENTE con offerFirst
     * para no perder su turno.
     */
    private Deque<Paquete> pendientesLlegada;

    /**
     * Paquetes PENDIENTES ordenados por prioridad de despacho.
     * PriorityQueue (monticulo) con el ComparadorDespacho:
     * poll() saca siempre el que debe salir primero en O(log n).
     */
    private PriorityQueue<Paquete> pendientesPrioridad;

    /**
     * Codigo -> Paquete. Sirve para:
     *   - buscar rapido un paquete por codigo (O(1) promedio)
     *   - validar que no se repitan codigos (containsKey O(1))
     */
    private Map<String, Paquete> paquetesPorCodigo;

    /**
     * Municipios con al menos un paquete, sin repetidos.
     * LinkedHashSet: no deja repetidos (como HashSet, O(1)) y ademas
     * conserva el orden en que aparecio cada municipio (Armenia, Salento, Calarca).
     */
    private Set<String> municipios;

    /** Municipios ordenados alfabeticamente y sin repetidos (arbol rojo-negro, O(log m)). */
    private TreeSet<String> municipiosOrdenados;

    /** Municipio -> lista de paquetes que van para ese municipio (en orden de llegada). */
    private Map<String, List<Paquete>> paquetesPorMunicipio;

    /** Identificacion -> Repartidor. LinkedHashMap para listar en el orden de registro. */
    private Map<String, Repartidor> repartidores;

    /** Codigo de paquete -> repartidor que lo lleva (paquetes en ruta). */
    private Map<String, Repartidor> asignaciones;

    /** Historial de entregas: NUESTRA lista simplemente enlazada (obligatorio). */
    private ListaSimple<Paquete> historialEntregas;

    // Constructor: crea todas las estructuras vacias.
    public CentroDistribucion() {
        registroLlegada = new ArrayList<>();                                // lista vacia de auditoria
        pendientesLlegada = new ArrayDeque<>();                             // cola FIFO vacia
        pendientesPrioridad = new PriorityQueue<>(new ComparadorDespacho()); // cola de prioridad con nuestras reglas
        paquetesPorCodigo = new HashMap<>();                                // mapa codigo -> paquete vacio
        municipios = new LinkedHashSet<>();                                 // conjunto de municipios vacio
        municipiosOrdenados = new TreeSet<>();                              // conjunto ordenado vacio
        paquetesPorMunicipio = new HashMap<>();                             // mapa municipio -> lista vacio
        repartidores = new LinkedHashMap<>();                               // mapa id -> repartidor vacio
        asignaciones = new HashMap<>();                                     // mapa codigo -> repartidor vacio
        historialEntregas = new ListaSimple<>();                            // nuestra lista propia vacia
    } // fin del constructor

    // ==================================================================
    // REGISTRO Y CONSULTA DE PAQUETES
    // ==================================================================

    /**
     * Registra un paquete nuevo.
     * 1. Verifica que el codigo no exista (HashMap.containsKey -> O(1)).
     * 2. Lo agrega a todas las estructuras que lo necesitan.
     * Complejidad total: O(log n) por la PriorityQueue y el TreeSet; lo demas es O(1).
     */
    public void registrarPaquete(Paquete p) {
        // No se permite registrar un paquete null.
        if (p == null) {
            throw new IllegalArgumentException("El paquete no puede ser null");
        } // fin del if
        // Regla: no pueden existir dos paquetes con el mismo codigo.
        if (paquetesPorCodigo.containsKey(p.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un paquete con el codigo " + p.getCodigo());
        } // fin del if

        registroLlegada.add(p);              // auditoria (orden de registro)
        pendientesLlegada.offerLast(p);      // cola FIFO de pendientes
        pendientesPrioridad.offer(p);        // cola de prioridad de pendientes
        paquetesPorCodigo.put(p.getCodigo(), p); // guardamos la relacion codigo -> paquete

        municipios.add(p.getDestino());          // si ya estaba, el Set no lo repite
        municipiosOrdenados.add(p.getDestino()); // igual pero ordenado

        // agrupar por municipio: si el municipio no tiene lista todavia, se crea
        if (!paquetesPorMunicipio.containsKey(p.getDestino())) {
            paquetesPorMunicipio.put(p.getDestino(), new ArrayList<>()); // lista nueva para ese municipio
        } // fin del if
        // Buscamos la lista del municipio y le agregamos el paquete al final.
        paquetesPorMunicipio.get(p.getDestino()).add(p);
    } // fin de registrarPaquete

    /** true si existe un paquete con ese codigo. O(1) promedio. */
    public boolean existePaquete(String codigo) {
        // Normalizamos el codigo (mayuscula, sin espacios) y preguntamos al mapa.
        return paquetesPorCodigo.containsKey(normalizarCodigo(codigo));
    } // fin de existePaquete

    /** Devuelve el paquete o null si no existe. O(1) promedio. */
    public Paquete buscarPaquete(String codigo) {
        // get devuelve el paquete asociado al codigo, o null si no esta.
        return paquetesPorCodigo.get(normalizarCodigo(codigo));
    } // fin de buscarPaquete

    // Deja el codigo igual a como se guardo (sin espacios y en mayuscula) para que la busqueda coincida.
    private String normalizarCodigo(String codigo) {
        if (codigo == null) return "";        // si llega null devolvemos texto vacio (no existe ningun paquete asi)
        return codigo.trim().toUpperCase();   // quitamos espacios y pasamos a mayuscula
    } // fin de normalizarCodigo

    /** Copia de los paquetes en el orden exacto de registro (para auditoria). */
    public List<Paquete> getRegistroLlegada() {
        // Devolvemos una copia para que nadie de afuera pueda modificar el registro original.
        return new ArrayList<>(registroLlegada);
    } // fin de getRegistroLlegada

    // Cantidad total de paquetes registrados en la jornada.
    public int getCantidadPaquetes() {
        return registroLlegada.size();
    } // fin de getCantidadPaquetes

    // ==================================================================
    // REGISTRO Y CONSULTA DE REPARTIDORES
    // ==================================================================

    // Registra un repartidor validando que su identificacion no este repetida.
    public void registrarRepartidor(Repartidor r) {
        // No se permite un repartidor null.
        if (r == null) {
            throw new IllegalArgumentException("El repartidor no puede ser null");
        } // fin del if
        // Regla: no pueden existir dos repartidores con la misma identificacion.
        if (repartidores.containsKey(r.getIdentificacion())) {
            throw new IllegalArgumentException("Ya existe un repartidor con la identificacion " + r.getIdentificacion());
        } // fin del if
        repartidores.put(r.getIdentificacion(), r); // guardamos id -> repartidor
    } // fin de registrarRepartidor

    // Busca un repartidor por su identificacion. Devuelve null si no existe.
    public Repartidor buscarRepartidor(String identificacion) {
        if (identificacion == null) return null;       // sin identificacion no hay a quien buscar
        return repartidores.get(identificacion.trim()); // O(1) promedio
    } // fin de buscarRepartidor

    // Devuelve una lista con todos los repartidores (copia, en orden de registro).
    public List<Repartidor> getRepartidores() {
        return new ArrayList<>(repartidores.values()); // values() son los repartidores del mapa
    } // fin de getRepartidores

    // ==================================================================
    // MUNICIPIOS
    // ==================================================================

    /** Municipios sin repetidos (en el orden en que aparecieron). */
    public Set<String> getMunicipios() {
        return new LinkedHashSet<>(municipios); // copia para proteger el original
    } // fin de getMunicipios

    /** Municipios sin repetidos y en orden alfabetico. */
    public Set<String> getMunicipiosOrdenados() {
        return new TreeSet<>(municipiosOrdenados); // copia, ya viene ordenada
    } // fin de getMunicipiosOrdenados

    /** Paquetes de un municipio (lista vacia si no hay). O(1) promedio para encontrar la lista. */
    public List<Paquete> getPaquetesDeMunicipio(String municipio) {
        // Buscamos la lista de ese municipio en el mapa.
        List<Paquete> lista = paquetesPorMunicipio.get(municipio);
        // Si el municipio no tiene paquetes, devolvemos una lista vacia (en vez de null).
        if (lista == null) {
            return new ArrayList<>();
        } // fin del if
        return new ArrayList<>(lista); // copia de la lista del municipio
    } // fin de getPaquetesDeMunicipio

    // ==================================================================
    // ORDEN DE ATENCION (sin sacar a nadie de las colas)
    // ==================================================================

    /** Pendientes en orden de llegada: el primero de la lista es el primero que se atiende. */
    public List<Paquete> verOrdenPorLlegada() {
        // recorrer el Deque con for-each NO lo modifica
        List<Paquete> orden = new ArrayList<>(); // lista donde vamos a copiar el orden
        // Recorremos la cola desde el frente hasta el final.
        for (Paquete p : pendientesLlegada) {
            orden.add(p); // copiamos cada paquete en el mismo orden
        } // fin del for
        return orden; // devolvemos la copia
    } // fin de verOrdenPorLlegada

    /**
     * Pendientes en orden de despacho prioritario.
     * OJO: recorrer una PriorityQueue con for-each NO da el orden correcto
     * (el monticulo solo garantiza que el primero es el menor). Por eso se
     * hace una COPIA y se le hace poll() repetidamente. O(n log n).
     */
    public List<Paquete> verOrdenPorPrioridad() {
        // Copia de la cola de prioridad (usa el mismo comparador que la original).
        PriorityQueue<Paquete> copia = new PriorityQueue<>(pendientesPrioridad);
        List<Paquete> orden = new ArrayList<>(); // aqui guardamos el orden de salida
        // Mientras la copia tenga elementos...
        while (!copia.isEmpty()) {
            orden.add(copia.poll()); // poll saca el que debe despacharse primero
        } // fin del while
        return orden; // la original no se toco
    } // fin de verOrdenPorPrioridad

    // Cantidad de paquetes que todavia no se han despachado.
    public int getCantidadPendientes() {
        return pendientesLlegada.size();
    } // fin de getCantidadPendientes

    // ==================================================================
    // REPORTES ORDENADOS (Comparable y Comparator)
    // ==================================================================

    /**
     * Devuelve una COPIA de los paquetes registrados ordenada con el criterio dado.
     * Si el comparador es null se usa el orden natural (Comparable -> por codigo).
     * Se ordena una copia para no alterar el orden de registro original.
     * Collections.sort es O(n log n).
     */
    public List<Paquete> getPaquetesOrdenados(Comparator<Paquete> criterio) {
        // Copiamos el registro para no desordenar el original.
        List<Paquete> copia = new ArrayList<>(registroLlegada);
        // Si no se paso criterio, ordenamos por el orden natural.
        if (criterio == null) {
            Collections.sort(copia);           // usa compareTo de Paquete
        } else { // si se paso un criterio...
            Collections.sort(copia, criterio); // usa el Comparator
        } // fin del if-else
        return copia; // devolvemos la copia ordenada
    } // fin de getPaquetesOrdenados

    // ==================================================================
    // DESPACHO Y ASIGNACION A REPARTIDORES
    // ==================================================================

    /**
     * Toma el siguiente paquete por ORDEN DE LLEGADA y trata de asignarlo
     * a un repartidor disponible de su zona.
     * @return mensaje con lo que paso
     */
    public String despacharPorLlegada() {
        Paquete p = pendientesLlegada.pollFirst(); // O(1) -> saca el primero que llego (null si esta vacia)
        // Si la cola estaba vacia no hay nada que despachar.
        if (p == null) {
            return "No hay paquetes pendientes.";
        } // fin del if
        pendientesPrioridad.remove(p); // mantener las dos colas iguales: O(n)

        // Buscamos un repartidor disponible de la zona del paquete.
        Repartidor r = buscarRepartidorDisponible(p.getDestino());
        // Si no hay ninguno...
        if (r == null) {
            // no se pudo: el paquete vuelve al FRENTE de la cola para no perder su turno
            pendientesLlegada.offerFirst(p);
            pendientesPrioridad.offer(p); // y vuelve a la cola de prioridad
            return "No hay repartidor disponible para " + p.getDestino() + ". El paquete "
                    + p.getCodigo() + " vuelve al frente de la cola.";
        } // fin del if
        String salidas = asignar(p, r); // si hay repartidor, hacemos la asignacion (y vemos si sale a ruta)
        return "Paquete " + p.getCodigo() + " (llegada) asignado a " + r.getNombre() + " [" + r.getZona() + "] carga "
                + r.getCargaActual() + "/" + r.getCapacidadMaxima() + salidas;
    } // fin de despacharPorLlegada

    /**
     * Toma el paquete de MAYOR PRIORIDAD (prioridad, tiempo, codigo) que tenga
     * un repartidor disponible en su zona y se lo asigna.
     *
     * PROBLEMA QUE RESUELVE: antes, si el primero de la cola no tenia repartidor
     * (ej: su repartidor estaba lleno o su zona no tiene repartidor), volvia a la
     * cola con la misma prioridad, quedaba otra vez de primero y BLOQUEABA a todos
     * los demas paquetes, aunque hubiera repartidores libres en otras zonas.
     *
     * SOLUCION: si el primero no se puede asignar se APARTA en una lista temporal
     * y se intenta con el siguiente. Al final los apartados vuelven a la cola con
     * su misma prioridad, asi que no pierden su lugar: cuando su repartidor quede
     * libre seran los primeros de su zona.
     *
     * Complejidad: O(k log n) donde k = paquetes apartados (cada poll/offer es O(log n)),
     * mas O(n) del remove en la cola de llegada.
     */
    public String despacharPorPrioridad() {
        // Si no hay pendientes no hay nada que despachar.
        if (pendientesPrioridad.isEmpty()) {
            return "No hay paquetes pendientes.";
        } // fin del if

        List<Paquete> apartados = new ArrayList<>(); // paquetes que se saltan por no tener repartidor
        Paquete elegido = null;                      // el paquete que si se va a despachar
        Repartidor repartidor = null;                // el repartidor que lo va a llevar

        // Sacamos de la cola de prioridad hasta encontrar uno con repartidor (o hasta vaciarla).
        while (!pendientesPrioridad.isEmpty() && elegido == null) {
            Paquete p = pendientesPrioridad.poll();                      // O(log n): el mas prioritario que queda
            Repartidor posible = buscarRepartidorDisponible(p.getDestino()); // repartidor libre de su zona
            if (posible == null) {
                apartados.add(p);   // no hay repartidor: lo apartamos y seguimos con el siguiente
            } else {
                elegido = p;        // si hay repartidor: este es el paquete que sale
                repartidor = posible;
            } // fin del if-else
        } // fin del while

        // Los apartados vuelven a la cola con su misma prioridad (no pierden su lugar).
        for (Paquete a : apartados) {
            pendientesPrioridad.offer(a); // O(log n) cada uno
        } // fin del for

        // Si ningun pendiente tiene repartidor disponible, todos siguen esperando.
        if (elegido == null) {
            return "Ningun paquete pendiente tiene repartidor disponible en su zona. Siguen en espera: " + apartados;
        } // fin del if

        pendientesLlegada.remove(elegido); // mantener las dos colas iguales: O(n)
        String salidas = asignar(elegido, repartidor); // hacemos la asignacion (y vemos si sale a ruta)

        String mensaje = "Paquete " + elegido.getCodigo() + " (prioridad " + elegido.getPrioridad() + ") asignado a "
                + repartidor.getNombre() + " [" + repartidor.getZona() + "] carga "
                + repartidor.getCargaActual() + "/" + repartidor.getCapacidadMaxima() + salidas;
        // Si hubo paquetes saltados, lo avisamos para que se sepa por que no salieron.
        if (!apartados.isEmpty()) {
            mensaje += "\n   (se saltaron por no tener repartidor disponible en su zona: " + apartados + ")";
        } // fin del if
        return mensaje;
    } // fin de despacharPorPrioridad

    /**
     * Asignacion manual: el funcionario escoge el paquete y el repartidor.
     * Aqui se validan las reglas del enunciado y si alguna no se cumple se lanza error.
     */
    public String asignarManual(String codigoPaquete, String idRepartidor) {
        // Buscamos el paquete por codigo.
        Paquete p = buscarPaquete(codigoPaquete);
        // El paquete tiene que existir.
        if (p == null) {
            throw new IllegalArgumentException("No existe el paquete " + codigoPaquete);
        } // fin del if
        // Solo se pueden asignar paquetes pendientes (no los que ya van en ruta o ya se entregaron).
        if (!Paquete.PENDIENTE.equals(p.getEstado())) {
            throw new IllegalArgumentException("El paquete " + p.getCodigo() + " no esta pendiente (estado: " + p.getEstado() + ")");
        } // fin del if
        // Buscamos el repartidor por identificacion.
        Repartidor r = buscarRepartidor(idRepartidor);
        // El repartidor tiene que existir.
        if (r == null) {
            throw new IllegalArgumentException("No existe el repartidor " + idRepartidor);
        } // fin del if
        // Regla: un repartidor no disponible no puede recibir una nueva asignacion.
        // Ahora hay dos razones para no estar disponible: estar inactivo o estar lleno.
        if (!r.isActivo()) {
            throw new IllegalArgumentException("El repartidor " + r.getNombre() + " NO esta disponible (inactivo)");
        } // fin del if
        if (r.isEnRuta()) {
            throw new IllegalArgumentException("El repartidor " + r.getNombre() + " NO esta disponible: ya salio a ruta con "
                    + r.getCargaActual() + " paquete(s)");
        } // fin del if
        if (r.estaLleno()) {
            throw new IllegalArgumentException("El repartidor " + r.getNombre() + " NO esta disponible: ya lleva "
                    + r.getCargaActual() + " de " + r.getCapacidadMaxima() + " paquetes");
        } // fin del if
        // Regla: la zona del repartidor debe ser el destino del paquete (sin importar mayusculas).
        if (!r.getZona().equalsIgnoreCase(p.getDestino())) {
            throw new IllegalArgumentException("La zona del repartidor (" + r.getZona()
                    + ") no corresponde al destino del paquete (" + p.getDestino() + ")");
        } // fin del if
        pendientesLlegada.remove(p);   // deja de estar pendiente en la cola de llegada
        pendientesPrioridad.remove(p); // y en la de prioridad
        return asignar(p, r);          // hacemos la asignacion (devuelve las salidas a ruta, si hubo)
    } // fin de asignarManual

    /**
     * Busca un repartidor disponible cuya zona sea el municipio dado.
     * Si hay varios, escoge el que lleve MENOS paquetes (asi se reparte la carga).
     * Recorre los repartidores: O(r) donde r = numero de repartidores (pocos).
     */
    private Repartidor buscarRepartidorDisponible(String municipio) {
        Repartidor mejor = null; // el repartidor menos cargado que vamos encontrando
        // Recorremos todos los repartidores en orden de registro.
        for (Repartidor r : repartidores.values()) {
            // Solo sirven los disponibles (activos y con cupo) de esa zona.
            if (r.isDisponible() && r.getZona().equalsIgnoreCase(municipio)) {
                // Nos quedamos con el que tenga menos carga (si empatan, el primero registrado).
                if (mejor == null || r.getCargaActual() < mejor.getCargaActual()) {
                    mejor = r;
                } // fin del if
            } // fin del if
        } // fin del for
        return mejor; // null si ninguno cumple
    } // fin de buscarRepartidorDisponible

    /**
     * Hace la asignacion: el repartidor carga el paquete y el paquete queda asignado.
     * Despues revisa si algun repartidor de esa zona debe SALIR A RUTA.
     * @return texto con las salidas a ruta que se produjeron ("" si ninguna)
     */
    private String asignar(Paquete p, Repartidor r) {
        r.cargarPaquete(p);                  // el repartidor lleva un paquete mas (valida capacidad)
        p.setEstado(Paquete.ASIGNADO);       // el paquete pasa a estar asignado
        asignaciones.put(p.getCodigo(), r);  // guardamos quien lleva el paquete
        return revisarSalidasARuta(p.getDestino()); // puede que el repartidor (o otros de la zona) ya deban salir
    } // fin de asignar

    /**
     * REGLA DE SALIDA A RUTA (se revisa despues de cada asignacion):
     *  1. Si el repartidor se lleno (ej: 3/3) -> sale a ruta.
     *  2. Si ya NO quedan paquetes pendientes para esa zona -> salen TODOS los
     *     repartidores de la zona que tengan al menos un paquete, aunque no esten llenos
     *     (ej: a Armenia solo llego 1 paquete -> el repartidor sale con 1/3).
     * Asi nadie se queda esperando en el centro un paquete que nunca va a llegar.
     * Complejidad: O(r + k), r = repartidores, k = paquetes del municipio.
     */
    private String revisarSalidasARuta(String municipio) {
        boolean quedanPendientes = hayPendientesDeZona(municipio); // O(k)
        StringBuilder salidas = new StringBuilder();               // aqui armamos el aviso
        // Recorremos los repartidores de esa zona.
        for (Repartidor r : repartidores.values()) {
            // Solo los de la zona, que sigan en el centro y que lleven al menos un paquete.
            if (r.getZona().equalsIgnoreCase(municipio) && !r.isEnRuta() && r.getCargaActual() > 0) {
                if (r.estaLleno()) {
                    r.salirARuta(); // regla 1: lleno
                    salidas.append("\n   -> ").append(r.getNombre()).append(" SALE A RUTA (lleno ")
                           .append(r.getCargaActual()).append("/").append(r.getCapacidadMaxima()).append(")");
                } else if (!quedanPendientes) {
                    r.salirARuta(); // regla 2: no hay mas paquetes para su zona
                    salidas.append("\n   -> ").append(r.getNombre()).append(" SALE A RUTA con ")
                           .append(r.getCargaActual()).append("/").append(r.getCapacidadMaxima())
                           .append(" (no quedan paquetes pendientes para ").append(municipio).append(")");
                } // fin del if-else
            } // fin del if
        } // fin del for
        return salidas.toString();
    } // fin de revisarSalidasARuta

    /**
     * true si todavia hay algun paquete PENDIENTE para ese municipio.
     * Usa el mapa municipio -> lista, asi solo recorre los paquetes de esa zona: O(k).
     */
    private boolean hayPendientesDeZona(String municipio) {
        // Buscamos la lista del municipio sin importar mayusculas.
        for (Map.Entry<String, List<Paquete>> e : paquetesPorMunicipio.entrySet()) {
            if (e.getKey().equalsIgnoreCase(municipio)) {
                // Revisamos si alguno de sus paquetes sigue pendiente.
                for (Paquete p : e.getValue()) {
                    if (Paquete.PENDIENTE.equals(p.getEstado())) {
                        return true; // encontramos uno pendiente
                    } // fin del if
                } // fin del for
            } // fin del if
        } // fin del for
        return false; // ninguno pendiente
    } // fin de hayPendientesDeZona

    /**
     * Salida manual: el funcionario decide que el repartidor sale ya con lo que lleva
     * (ej: se hizo tarde y no quiere esperar a que lleguen mas paquetes de su zona).
     */
    public void enviarARuta(String idRepartidor) {
        Repartidor r = buscarRepartidor(idRepartidor); // O(1)
        // El repartidor tiene que existir.
        if (r == null) {
            throw new IllegalArgumentException("No existe el repartidor " + idRepartidor);
        } // fin del if
        r.salirARuta(); // valida que lleve paquetes y que no haya salido ya
    } // fin de enviarARuta

    /** Paquetes que estan en ruta con su repartidor. */
    public Map<String, Repartidor> getAsignaciones() {
        return new HashMap<>(asignaciones); // copia del mapa
    } // fin de getAsignaciones

    // ==================================================================
    // ENTREGAS E HISTORIAL
    // ==================================================================

    /**
     * Registra que un paquete asignado fue entregado:
     *  - deja de estar en ruta
     *  - el repartidor descarga el paquete (le queda un cupo libre)
     *  - se agrega al FINAL del historial (lista propia) -> conserva el orden real
     */
    public void registrarEntrega(String codigo) {
        // Buscamos el paquete.
        Paquete p = buscarPaquete(codigo);
        // Tiene que existir.
        if (p == null) {
            throw new IllegalArgumentException("No existe el paquete " + codigo);
        } // fin del if
        // Solo se puede entregar un paquete que este asignado a un repartidor.
        if (!asignaciones.containsKey(p.getCodigo())) {
            throw new IllegalArgumentException("El paquete " + p.getCodigo()
                    + " no esta asignado a ningun repartidor (estado: " + p.getEstado() + ")");
        } // fin del if
        // Regla: no se puede entregar un paquete si su repartidor todavia no ha salido del centro.
        Repartidor quienLoLleva = asignaciones.get(p.getCodigo());
        if (!quienLoLleva.isEnRuta()) {
            throw new IllegalArgumentException("El repartidor " + quienLoLleva.getNombre()
                    + " todavia no ha salido a ruta (esta en el centro con " + quienLoLleva.getCargaActual()
                    + "/" + quienLoLleva.getCapacidadMaxima() + "). Primero debe salir (opcion 22).");
        } // fin del if
        // remove saca la asignacion del mapa y nos devuelve el repartidor que tenia el paquete.
        Repartidor r = asignaciones.remove(p.getCodigo());
        r.descargarPaquete(p);           // el repartidor lleva un paquete menos (si era el ultimo, vuelve al centro)
        p.setEstado(Paquete.ENTREGADO);  // el paquete queda entregado
        historialEntregas.agregar(p);    // se agrega al final del historial (orden real de entrega)
    } // fin de registrarEntrega

    /** Devuelve el historial (nuestra lista propia). Se recorre con for-each gracias a Iterable. */
    public ListaSimple<Paquete> getHistorialEntregas() {
        return historialEntregas;
    } // fin de getHistorialEntregas
} // fin de la clase CentroDistribucion
