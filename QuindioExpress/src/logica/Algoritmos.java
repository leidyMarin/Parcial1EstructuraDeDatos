// Esta clase pertenece al paquete "logica"
package logica;

// Reutilizamos el comparador de peso para la etapa de COMBINAR del divide y venceras.
import comparadores.ComparadorPeso;
// Interfaz List (los algoritmos reciben listas).
import java.util.List;
// Clase Paquete.
import modelo.Paquete;

/**
 * Algoritmos pedidos en el parcial:
 *   1. Peso total por municipio          -> RECURSIVO
 *   2. Contar paquetes con prioridad >= x -> RECURSIVO
 *   3. Busqueda binaria por codigo        -> RECURSIVA (lista ordenada)
 *   4. Paquete mas pesado                 -> DIVIDE Y VENCERAS
 *
 * Ninguno usa for / while / do-while: todo se repite con llamadas recursivas.
 *
 * Cada metodo tiene un parametro "nivel" que solo sirve para imprimir la traza
 * con sangria (asi se ve la pila de llamadas en consola). Si la variable
 * mostrarTraza esta en false no se imprime nada.
 */
public class Algoritmos {

    /** Si es true, los metodos imprimen cada llamada y cada retorno. */
    // "static" = pertenece a la clase, no a un objeto. Se usa como Algoritmos.mostrarTraza.
    public static boolean mostrarTraza = false;

    /** Cuenta cuantas comparaciones hace la busqueda binaria (para mostrar que no revisa todo). */
    private static int comparacionesBinaria = 0;

    // Un solo comparador de peso que usan todas las llamadas del divide y venceras.
    private static ComparadorPeso comparadorPeso = new ComparadorPeso();

    // Devuelve espacios segun el nivel de profundidad (para que la traza se vea como escalera)
    private static String sangria(int nivel) {
        // repeat repite el texto "nivel" veces: nivel 2 -> 6 espacios.
        return "   ".repeat(nivel);
    } // fin de sangria

    // ==================================================================
    // 1. PESO TOTAL DE LOS PAQUETES DE UN MUNICIPIO (recursivo)
    // ==================================================================

    /** Metodo "puerta de entrada": arranca la recursion en la posicion 0. */
    public static double pesoTotalMunicipio(List<Paquete> paquetes, String municipio) {
        // Llama al metodo recursivo empezando en el indice 0 y nivel 0.
        return pesoTotalMunicipio(paquetes, municipio, 0, 0);
    } // fin del metodo de entrada

    /**
     * CASO BASE:      indice == tamanio de la lista -> ya no hay paquetes -> retorna 0.
     * CASO RECURSIVO: peso del paquete actual (si es del municipio, si no 0)
     *                 + pesoTotal del RESTO de la lista (indice + 1).
     * AVANCE:         en cada llamada el indice aumenta en 1, asi que tarde o
     *                 temprano llega al tamanio de la lista (caso base).
     *
     * Tiempo: O(n) -> una llamada por paquete.
     * Espacio: O(n) -> la pila guarda hasta n+1 llamadas pendientes.
     */
    private static double pesoTotalMunicipio(List<Paquete> paquetes, String municipio, int indice, int nivel) {
        // CASO BASE
        if (indice == paquetes.size()) {
            // Si la traza esta activa, mostramos que llegamos al caso base.
            if (mostrarTraza) {
                System.out.println(sangria(nivel) + "pesoTotal(i=" + indice + ") -> CASO BASE, retorna 0.0");
            } // fin del if de traza
            return 0.0; // no quedan paquetes: el peso del "resto" es 0
        } // fin del caso base

        // Tomamos el paquete de la posicion actual (get en ArrayList es O(1)).
        Paquete actual = paquetes.get(indice);
        // Por defecto el paquete no aporta peso.
        double pesoActual = 0.0;
        // Si el destino del paquete es el municipio buscado, aporta su peso.
        if (actual.getDestino().equalsIgnoreCase(municipio)) {
            pesoActual = actual.getPeso();
        } // fin del if

        // Traza: mostramos que esta llamada queda pendiente esperando a la siguiente.
        if (mostrarTraza) {
            System.out.println(sangria(nivel) + "pesoTotal(i=" + indice + ") " + actual.getCodigo() + " "
                    + actual.getDestino() + " -> aporta " + pesoActual + " + pesoTotal(i=" + (indice + 1) + ")  [queda pendiente]");
        } // fin del if de traza

        // CASO RECURSIVO: esta llamada queda "esperando" el resultado de la siguiente
        double resto = pesoTotalMunicipio(paquetes, municipio, indice + 1, nivel + 1);
        // Cuando la llamada de arriba retorna, sumamos el peso de este paquete.
        double resultado = pesoActual + resto;

        // Traza: mostramos lo que retorna esta llamada.
        if (mostrarTraza) {
            System.out.println(sangria(nivel) + "pesoTotal(i=" + indice + ") retorna " + pesoActual + " + "
                    + redondear(resto) + " = " + redondear(resultado));
        } // fin del if de traza
        return resultado; // devolvemos el peso acumulado desde este indice hasta el final
    } // fin de pesoTotalMunicipio

    // ==================================================================
    // 2. CONTAR PAQUETES CON PRIORIDAD >= MINIMA (recursivo)
    // ==================================================================

    // Metodo de entrada: arranca la recursion en el indice 0.
    public static int contarPrioridadMinima(List<Paquete> paquetes, int prioridadMinima) {
        return contarPrioridadMinima(paquetes, prioridadMinima, 0, 0);
    } // fin del metodo de entrada

    /**
     * CASO BASE:      indice == tamanio -> retorna 0 (no hay nada que contar).
     * CASO RECURSIVO: (1 si el paquete actual cumple, 0 si no) + contar(indice + 1).
     * AVANCE:         indice + 1 en cada llamada.
     *
     * Tiempo: O(n).  Espacio: O(n) por la pila.
     */
    private static int contarPrioridadMinima(List<Paquete> paquetes, int prioridadMinima, int indice, int nivel) {
        // CASO BASE
        if (indice == paquetes.size()) {
            // Traza del caso base.
            if (mostrarTraza) {
                System.out.println(sangria(nivel) + "contar(i=" + indice + ") -> CASO BASE, retorna 0");
            } // fin del if de traza
            return 0; // no quedan paquetes por contar
        } // fin del caso base

        // Prioridad del paquete de la posicion actual.
        int prioridad = paquetes.get(indice).getPrioridad();
        // Arrancamos suponiendo que no cumple.
        int cuenta = 0;
        // Si cumple la prioridad minima, cuenta como 1.
        if (prioridad >= prioridadMinima) {
            cuenta = 1;
        } // fin del if

        // Traza: mostramos la comparacion y que la llamada queda pendiente.
        if (mostrarTraza) {
            System.out.println(sangria(nivel) + "contar(i=" + indice + ") prioridad=" + prioridad
                    + (cuenta == 1 ? " >= " : " < ") + prioridadMinima + " -> " + cuenta
                    + " + contar(i=" + (indice + 1) + ")  [queda pendiente]");
        } // fin del if de traza

        // CASO RECURSIVO
        int resto = contarPrioridadMinima(paquetes, prioridadMinima, indice + 1, nivel + 1);
        // Sumamos lo de este paquete (0 o 1) con lo que contaron las llamadas siguientes.
        int resultado = cuenta + resto;

        // Traza del retorno.
        if (mostrarTraza) {
            System.out.println(sangria(nivel) + "contar(i=" + indice + ") retorna " + cuenta + " + " + resto + " = " + resultado);
        } // fin del if de traza
        return resultado; // cantidad de paquetes que cumplen desde este indice hasta el final
    } // fin de contarPrioridadMinima

    // ==================================================================
    // 3. BUSQUEDA BINARIA POR CODIGO (recursiva)
    // ==================================================================

    /**
     * PRECONDICION: la lista DEBE estar ordenada por codigo ascendente
     * (orden natural de Paquete). Si no esta ordenada el resultado no sirve,
     * porque el algoritmo descarta mitades suponiendo ese orden.
     *
     * @return la posicion del paquete en la lista, o -1 si no esta
     */
    public static int busquedaBinaria(List<Paquete> ordenada, String codigo) {
        comparacionesBinaria = 0; // reiniciamos el contador en cada busqueda nueva
        // Buscamos en todo el rango [0, tamanio-1] con el codigo normalizado (mayuscula, sin espacios).
        return busquedaBinaria(ordenada, codigo.trim().toUpperCase(), 0, ordenada.size() - 1, 0);
    } // fin del metodo de entrada

    /**
     * CASO BASE 1: inicio > fin  -> el rango quedo vacio, no esta -> -1.
     * CASO BASE 2: el del medio es el buscado -> retorna medio.
     * CASO RECURSIVO: si el buscado es menor, se busca en la mitad izquierda;
     *                 si es mayor, en la mitad derecha.
     * AVANCE: el rango se reduce a la mitad en cada llamada.
     *
     * Tiempo: O(log n).  Espacio: O(log n) por la pila (la version con while seria O(1)).
     */
    private static int busquedaBinaria(List<Paquete> lista, String codigo, int inicio, int fin, int nivel) {
        // CASO BASE 1: rango vacio
        if (inicio > fin) {
            // Traza: no se encontro.
            if (mostrarTraza) {
                System.out.println(sangria(nivel) + "binaria(inicio=" + inicio + ", fin=" + fin
                        + ") -> rango vacio, NO encontrado, retorna -1");
            } // fin del if de traza
            return -1; // -1 significa "no esta"
        } // fin del caso base 1

        // Posicion del medio del rango actual.
        int medio = (inicio + fin) / 2;
        // Codigo del paquete que esta en el medio.
        String codigoMedio = lista.get(medio).getCodigo();
        // Comparamos: negativo = buscado es menor, 0 = iguales, positivo = buscado es mayor.
        int comparacion = codigo.compareTo(codigoMedio);
        comparacionesBinaria++; // contamos esta comparacion

        // Traza: mostramos el rango, el medio y el resultado de comparar.
        if (mostrarTraza) {
            System.out.println(sangria(nivel) + "binaria(inicio=" + inicio + ", fin=" + fin + ") medio=" + medio
                    + " (" + codigoMedio + ")  " + codigo + (comparacion == 0 ? " == " : (comparacion < 0 ? " < " : " > ")) + codigoMedio);
        } // fin del if de traza

        // CASO BASE 2: encontrado
        if (comparacion == 0) {
            // Traza: encontrado y cuantas comparaciones se necesitaron.
            if (mostrarTraza) {
                System.out.println(sangria(nivel) + "-> ENCONTRADO en la posicion " + medio
                        + " (comparaciones hechas: " + comparacionesBinaria + " de " + lista.size() + " paquetes)");
            } // fin del if de traza
            return medio; // devolvemos la posicion donde esta
        } // fin del caso base 2

        // CASO RECURSIVO
        if (comparacion < 0) {
            // El buscado es menor que el del medio: solo puede estar a la izquierda.
            return busquedaBinaria(lista, codigo, inicio, medio - 1, nivel + 1); // mitad izquierda
        } else {
            // El buscado es mayor que el del medio: solo puede estar a la derecha.
            return busquedaBinaria(lista, codigo, medio + 1, fin, nivel + 1);    // mitad derecha
        } // fin del if-else
    } // fin de busquedaBinaria

    // Devuelve cuantas comparaciones hizo la ultima busqueda binaria.
    public static int getComparacionesBinaria() {
        return comparacionesBinaria;
    } // fin de getComparacionesBinaria

    // ==================================================================
    // 4. PAQUETE MAS PESADO (divide y venceras)
    // ==================================================================

    // Metodo de entrada: valida la lista y arranca con todo el rango [0, tamanio-1].
    public static Paquete paqueteMasPesado(List<Paquete> paquetes) {
        // Si no hay paquetes no hay ninguno "mas pesado".
        if (paquetes == null || paquetes.isEmpty()) {
            return null;
        } // fin del if
        return paqueteMasPesado(paquetes, 0, paquetes.size() - 1, 0);
    } // fin del metodo de entrada

    /**
     * Etapas de divide y venceras:
     *   CASO BASE:   inicio == fin -> hay un solo paquete, el es el mas pesado.
     *   DIVIDIR:     medio = (inicio + fin) / 2
     *   SUBPROBLEMA IZQUIERDO: el mas pesado de [inicio .. medio]
     *   SUBPROBLEMA DERECHO:   el mas pesado de [medio+1 .. fin]
     *   COMBINAR:    se comparan los dos ganadores y se queda el de mas peso.
     *   RESULTADO:   lo que retorna la primera llamada (todo el rango).
     *
     * Tiempo: T(n) = 2T(n/2) + O(1) = O(n).
     * Espacio: O(log n) -> la pila solo crece hasta la altura del arbol de llamadas.
     */
    private static Paquete paqueteMasPesado(List<Paquete> lista, int inicio, int fin, int nivel) {
        // CASO BASE: un solo elemento
        if (inicio == fin) {
            Paquete unico = lista.get(inicio); // el unico paquete del rango
            // Traza del caso base.
            if (mostrarTraza) {
                System.out.println(sangria(nivel) + "masPesado[" + inicio + ".." + fin + "] CASO BASE -> "
                        + unico.getCodigo() + " (" + unico.getPeso() + " kg)");
            } // fin del if de traza
            return unico; // con un solo paquete, ese es el mas pesado
        } // fin del caso base

        // DIVIDIR
        int medio = (inicio + fin) / 2; // punto donde partimos el rango en dos
        // Traza de la division.
        if (mostrarTraza) {
            System.out.println(sangria(nivel) + "masPesado[" + inicio + ".." + fin + "] DIVIDIR en ["
                    + inicio + ".." + medio + "] y [" + (medio + 1) + ".." + fin + "]");
        } // fin del if de traza

        // SUBPROBLEMA IZQUIERDO
        Paquete izquierdo = paqueteMasPesado(lista, inicio, medio, nivel + 1);
        // SUBPROBLEMA DERECHO
        Paquete derecho = paqueteMasPesado(lista, medio + 1, fin, nivel + 1);

        // COMBINAR: reutilizamos el ComparadorPeso (negativo = el primero pesa mas)
        Paquete ganador; // aqui guardamos el mas pesado de los dos
        // Si el izquierdo pesa mas o igual, gana el izquierdo (en empate se queda el primero).
        if (comparadorPeso.compare(izquierdo, derecho) <= 0) {
            ganador = izquierdo;
        } else { // si el derecho pesa mas, gana el derecho
            ganador = derecho;
        } // fin del if-else

        // Traza de la combinacion.
        if (mostrarTraza) {
            System.out.println(sangria(nivel) + "masPesado[" + inicio + ".." + fin + "] COMBINAR "
                    + izquierdo.getPeso() + " vs " + derecho.getPeso() + " -> " + ganador.getCodigo()
                    + " (" + ganador.getPeso() + " kg)");
        } // fin del if de traza
        return ganador; // el mas pesado de todo el rango [inicio..fin]
    } // fin de paqueteMasPesado

    // Convierte un double a texto con 1 decimal (para mostrar en la traza).
    private static String redondear(double valor) {
        // redondea a 1 decimal (asi 9.499999999 se ve como 9.5)
        return String.valueOf(Math.round(valor * 10) / 10.0);
    } // fin de redondear
} // fin de la clase Algoritmos
