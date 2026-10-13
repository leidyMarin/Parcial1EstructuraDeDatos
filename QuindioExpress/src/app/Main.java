// Esta clase pertenece al paquete "app"
package app;

// Comparadores para los reportes ordenados.
import comparadores.ComparadorPeso;
import comparadores.ComparadorPrioridad;
import comparadores.ComparadorTiempo;
// Colecciones e interfaz para leer del teclado.
import java.util.List;
import java.util.Map;
import java.util.Scanner;   // sirve para leer lo que el usuario escribe en consola
// Clases de la logica.
import logica.Algoritmos;
import logica.CentroDistribucion;
// Clases del modelo.
import modelo.Paquete;
import modelo.Repartidor;

/**
 * Programa principal de QuindioExpress.
 * Muestra un menu por consola para probar cada operacion.
 *
 * Tambien se puede ejecutar asi:   java -cp bin app.Main demo
 * y corre la demostracion completa sin menu.
 */
public class Main {

    // Un solo centro de distribucion para todo el programa (static para usarlo en todos los metodos).
    private static CentroDistribucion centro = new CentroDistribucion();
    // Un solo Scanner leyendo del teclado (System.in).
    private static Scanner teclado = new Scanner(System.in);

    // Punto de entrada del programa: Java empieza a ejecutar aqui.
    public static void main(String[] args) {
        DatosIniciales.cargar(centro); // cargamos los 30 paquetes y 7 repartidores
        // Mostramos cuantos datos se cargaron.
        System.out.println("Datos cargados: " + centro.getCantidadPaquetes() + " paquetes y "
                + centro.getRepartidores().size() + " repartidores.");

        // Si se ejecuto con el argumento "demo", corremos todo sin menu y terminamos.
        if (args.length > 0 && args[0].equalsIgnoreCase("demo")) {
            demostracionCompleta();
            return; // termina el main
        } // fin del if

        int opcion = -1; // arranca en -1 para que entre al ciclo
        // El menu se repite hasta que el usuario escriba 0.
        while (opcion != 0) {
            mostrarMenu();                       // imprimimos las opciones
            opcion = leerEntero("Opcion: ");     // leemos la opcion escogida
            // try-catch para que un error de validacion no cierre el programa.
            try {
                ejecutarOpcion(opcion); // hacemos lo que pidio el usuario
            } catch (IllegalArgumentException e) {
                // los errores de validacion se muestran en pantalla, no se esconden
                System.out.println("ERROR: " + e.getMessage());
            } // fin del try-catch
        } // fin del while
        System.out.println("Fin del programa.");
    } // fin del main

    // Imprime el menu de opciones.
    private static void mostrarMenu() {
        System.out.println("\n================ QUINDIOEXPRESS ================");
        System.out.println(" 1. Listar paquetes en orden de registro");
        System.out.println(" 2. Buscar paquete por codigo (HashMap)");
        System.out.println(" 3. Registrar paquete nuevo");
        System.out.println(" 4. Listar repartidores");
        System.out.println(" 5. Registrar repartidor nuevo");
        System.out.println(" 6. Municipios (sin repetidos y ordenados)");
        System.out.println(" 7. Paquetes agrupados por municipio");
        System.out.println(" 8. Ver orden de atencion (llegada y prioridad)");
        System.out.println(" 9. Reportes ordenados (codigo, prioridad, peso, tiempo)");
        System.out.println("10. Despachar siguiente por ORDEN DE LLEGADA");
        System.out.println("11. Despachar siguiente por PRIORIDAD");
        System.out.println("12. Asignar paquete a repartidor (manual)");
        System.out.println("13. Registrar entrega");
        System.out.println("14. Ver historial de entregas (lista propia)");
        System.out.println("15. Peso total por municipio (RECURSIVO)");
        System.out.println("16. Contar paquetes con prioridad minima (RECURSIVO)");
        System.out.println("17. Busqueda binaria por codigo");
        System.out.println("18. Paquete mas pesado (DIVIDE Y VENCERAS)");
        System.out.println("19. Pruebas de escritorio (datos del enunciado con traza)");
        System.out.println("20. Pruebas de la lista simple propia");
        System.out.println("21. Demostracion completa");
        System.out.println("22. Enviar repartidor a ruta (salida manual con lo que lleva)");
        System.out.println(" 0. Salir");
    } // fin de mostrarMenu

    // Segun el numero escogido, llama al metodo correspondiente.
    private static void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1: listarRegistro(); break;                               // lista en orden de registro
            case 2: buscarPaquete(leerTexto("Codigo: ")); break;           // pide el codigo y lo busca
            case 3: registrarPaquete(); break;                             // pide los datos y registra
            case 4: listarRepartidores(); break;                           // muestra los repartidores
            case 5: registrarRepartidor(); break;                          // registra un repartidor nuevo
            case 6: mostrarMunicipios(); break;                            // municipios sin repetir / ordenados
            case 7: mostrarAgrupados(); break;                             // paquetes por municipio
            case 8: mostrarOrdenesDeAtencion(); break;                     // orden por llegada y por prioridad
            case 9: mostrarReportesOrdenados(); break;                     // reportes con Comparable/Comparator
            case 10: System.out.println(centro.despacharPorLlegada()); break;   // despacha el primero que llego
            case 11: System.out.println(centro.despacharPorPrioridad()); break; // despacha el mas prioritario
            case 12: // asignacion manual
                // Pedimos el codigo y el repartidor y llamamos a la asignacion (puede lanzar error).
                String salidas = centro.asignarManual(leerTexto("Codigo del paquete: "), leerTexto("Identificacion del repartidor: "));
                System.out.println("Asignacion realizada." + salidas); // avisa si algun repartidor salio a ruta
                break;
            case 13: // registrar entrega
                centro.registrarEntrega(leerTexto("Codigo del paquete entregado: "));
                System.out.println("Entrega registrada.");
                break;
            case 14: mostrarHistorial(); break;                                             // historial (lista propia)
            case 15: pesoPorMunicipio(leerTexto("Municipio: "), preguntarTraza()); break;  // recursivo
            case 16: contarPrioridad(leerEntero("Prioridad minima: "), preguntarTraza()); break; // recursivo
            case 17: busquedaBinaria(leerTexto("Codigo a buscar: "), preguntarTraza()); break;  // binaria
            case 18: masPesado(preguntarTraza()); break;                                    // divide y venceras
            case 19: PruebasEscritorio.ejecutar(); break;                                   // pruebas con traza
            case 20: PruebasListaSimple.ejecutar(); break;                                  // pruebas de la lista
            case 21: demostracionCompleta(); break;                                         // todo de corrido
            case 22: // salida manual a ruta
                centro.enviarARuta(leerTexto("Identificacion del repartidor: "));
                System.out.println("El repartidor salio a ruta (queda NO disponible hasta entregar todo).");
                break;
            case 0: break;                                                                  // salir (el while termina)
            default: System.out.println("Opcion no valida");                                // cualquier otro numero
        } // fin del switch
    } // fin de ejecutarOpcion

    // ==================================================================
    // OPCIONES
    // ==================================================================

    // Opcion 1: muestra todos los paquetes en el orden en que se registraron.
    private static void listarRegistro() {
        System.out.println("\n--- Paquetes en orden de registro (" + centro.getCantidadPaquetes() + ") ---");
        imprimirPaquetes(centro.getRegistroLlegada());
    } // fin de listarRegistro

    // Opcion 2: busca un paquete por codigo usando el HashMap.
    private static void buscarPaquete(String codigo) {
        Paquete p = centro.buscarPaquete(codigo); // O(1) promedio
        // Si no existe, avisamos; si existe, mostramos todos sus datos.
        if (p == null) {
            System.out.println("El paquete " + codigo + " NO existe.");
        } else {
            System.out.println("Encontrado: " + p.getDetalle());
        } // fin del if-else
    } // fin de buscarPaquete

    // Opcion 3: pide los datos por teclado y registra un paquete nuevo.
    private static void registrarPaquete() {
        String codigo = leerTexto("Codigo: ");
        String destino = leerTexto("Destino: ");
        double peso = leerDouble("Peso (kg): ");
        // La prioridad se lee como texto para permitir dejarla vacia (Enter = prioridad por defecto).
        String textoPrioridad = leerTexto("Prioridad (" + Paquete.PRIORIDAD_MINIMA + "-" + Paquete.PRIORIDAD_MAXIMA
                + ", Enter = " + Paquete.PRIORIDAD_POR_DEFECTO + "): ");
        int tiempo = leerEntero("Tiempo estimado (min): ");

        Paquete p; // aqui se guarda el paquete que vamos a crear
        // Si no escribio prioridad, usamos el constructor sin prioridad.
        if (textoPrioridad.isEmpty()) {
            p = new Paquete(codigo, destino, peso, tiempo);
        } else { // si escribio, la convertimos a entero y usamos el constructor completo
            p = new Paquete(codigo, destino, peso, Integer.parseInt(textoPrioridad), tiempo);
        } // fin del if-else
        centro.registrarPaquete(p); // lo registramos (aqui se valida que el codigo no exista)
        System.out.println("Paquete registrado: " + p.getDetalle());
    } // fin de registrarPaquete

    // Opcion 4: muestra todos los repartidores.
    private static void listarRepartidores() {
        System.out.println("\n--- Repartidores ---");
        // Recorremos la lista de repartidores y los imprimimos (usa el toString de Repartidor).
        for (Repartidor r : centro.getRepartidores()) {
            System.out.println(r);
        } // fin del for
    } // fin de listarRepartidores

    // Opcion 5: registra un repartidor nuevo (queda activo).
    private static void registrarRepartidor() {
        String id = leerTexto("Identificacion: ");
        String nombre = leerTexto("Nombre: ");
        String zona = leerTexto("Zona (municipio): ");
        // La capacidad se lee como texto para permitir dejarla vacia (Enter = capacidad por defecto).
        String textoCapacidad = leerTexto("Capacidad (paquetes a la vez, Enter = "
                + Repartidor.CAPACIDAD_POR_DEFECTO + "): ");

        Repartidor r; // aqui se guarda el repartidor que vamos a crear
        // Si no escribio capacidad, usamos el constructor sin capacidad (usa la de por defecto).
        if (textoCapacidad.isEmpty()) {
            r = new Repartidor(id, nombre, zona, true);
        } else { // si escribio, la convertimos a entero y usamos el constructor completo
            r = new Repartidor(id, nombre, zona, true, Integer.parseInt(textoCapacidad));
        } // fin del if-else
        centro.registrarRepartidor(r); // aqui se valida id unico
        System.out.println("Repartidor registrado: " + r);
    } // fin de registrarRepartidor

    // Opcion 6: muestra los municipios sin repetidos y ordenados.
    private static void mostrarMunicipios() {
        System.out.println("Municipios sin repetidos (LinkedHashSet): " + centro.getMunicipios());
        System.out.println("Municipios ordenados      (TreeSet):       " + centro.getMunicipiosOrdenados());
    } // fin de mostrarMunicipios

    // Opcion 7: muestra los paquetes de cada municipio.
    private static void mostrarAgrupados() {
        System.out.println("\n--- Paquetes por municipio (Map<String, List<Paquete>>) ---");
        // Recorremos los municipios en orden alfabetico.
        for (String m : centro.getMunicipiosOrdenados()) {
            List<Paquete> lista = centro.getPaquetesDeMunicipio(m); // paquetes de ese municipio
            System.out.println(m + " (" + lista.size() + ") -> " + lista);
        } // fin del for
    } // fin de mostrarAgrupados

    // Opcion 8: muestra el orden en que se atenderian los pendientes en cada modalidad.
    private static void mostrarOrdenesDeAtencion() {
        System.out.println("Pendientes: " + centro.getCantidadPendientes());
        System.out.println("Orden por LLEGADA   (Deque FIFO):     " + centro.verOrdenPorLlegada());
        System.out.println("Orden por PRIORIDAD (PriorityQueue):  " + centro.verOrdenPorPrioridad());
    } // fin de mostrarOrdenesDeAtencion

    // Opcion 9: reportes con el orden natural y con cada Comparator.
    private static void mostrarReportesOrdenados() {
        System.out.println("\n--- Orden NATURAL (Comparable, por codigo) ---");
        System.out.println(centro.getPaquetesOrdenados(null)); // null = orden natural

        System.out.println("\n--- Por PRIORIDAD de mayor a menor (Comparator) ---");
        imprimirPaquetes(centro.getPaquetesOrdenados(new ComparadorPrioridad()));

        System.out.println("\n--- Por PESO de mayor a menor (Comparator) ---");
        imprimirPaquetes(centro.getPaquetesOrdenados(new ComparadorPeso()));

        System.out.println("\n--- Por TIEMPO de menor a mayor (Comparator) ---");
        imprimirPaquetes(centro.getPaquetesOrdenados(new ComparadorTiempo()));

        // Demostramos que ordenar las copias no cambio el orden de registro.
        System.out.println("\nEl registro original NO cambio: " + centro.getRegistroLlegada());
    } // fin de mostrarReportesOrdenados

    // Opcion 14: muestra el historial de entregas y los paquetes en ruta.
    private static void mostrarHistorial() {
        // Usa el toString de la ListaSimple: [PQ105 -> PQ942 -> ...]
        System.out.println("Historial (" + centro.getHistorialEntregas().getTamanio() + " entregas): "
                + centro.getHistorialEntregas());
        // recorrido con for-each: usa el Iterator de nuestra lista
        int numero = 1; // numero de la entrega
        for (Paquete p : centro.getHistorialEntregas()) {
            System.out.println("  " + numero + ". " + p.getDetalle());
            numero++; // siguiente numero
        } // fin del for-each
        // Paquetes que tienen repartidor pero aun no se entregan.
        Map<String, Repartidor> enRuta = centro.getAsignaciones();
        System.out.println("Paquetes en ruta (asignados sin entregar): " + enRuta.keySet()); // keySet = los codigos
    } // fin de mostrarHistorial

    // Opcion 15: peso total de un municipio con el algoritmo recursivo.
    private static void pesoPorMunicipio(String municipio, boolean traza) {
        Algoritmos.mostrarTraza = traza; // activamos o no la traza segun lo que respondio el usuario
        double total = Algoritmos.pesoTotalMunicipio(centro.getRegistroLlegada(), municipio);
        Algoritmos.mostrarTraza = false; // la apagamos de nuevo
        System.out.println("Peso total de " + municipio + ": " + Math.round(total * 10) / 10.0 + " kg");
    } // fin de pesoPorMunicipio

    // Opcion 16: cuenta paquetes con prioridad >= minima (recursivo).
    private static void contarPrioridad(int minima, boolean traza) {
        Algoritmos.mostrarTraza = traza;
        int cantidad = Algoritmos.contarPrioridadMinima(centro.getRegistroLlegada(), minima);
        Algoritmos.mostrarTraza = false;
        System.out.println("Paquetes con prioridad >= " + minima + ": " + cantidad);
    } // fin de contarPrioridad

    // Opcion 17: busqueda binaria por codigo.
    private static void busquedaBinaria(String codigo, boolean traza) {
        // PRECONDICION: ordenar por codigo antes de buscar (orden natural)
        List<Paquete> ordenados = centro.getPaquetesOrdenados(null);
        System.out.println("Lista ordenada por codigo: " + ordenados);
        Algoritmos.mostrarTraza = traza;
        int pos = Algoritmos.busquedaBinaria(ordenados, codigo); // posicion o -1
        Algoritmos.mostrarTraza = false;
        // -1 significa que no se encontro.
        if (pos == -1) {
            System.out.println(codigo + " NO encontrado (" + Algoritmos.getComparacionesBinaria() + " comparaciones)");
        } else { // si se encontro, mostramos el paquete y cuantas comparaciones se hicieron
            System.out.println("Encontrado en la posicion " + pos + ": " + ordenados.get(pos).getDetalle()
                    + "  (" + Algoritmos.getComparacionesBinaria() + " comparaciones de " + ordenados.size() + ")");
        } // fin del if-else
    } // fin de busquedaBinaria

    // Opcion 18: paquete mas pesado con divide y venceras.
    private static void masPesado(boolean traza) {
        Algoritmos.mostrarTraza = traza;
        Paquete p = Algoritmos.paqueteMasPesado(centro.getRegistroLlegada());
        Algoritmos.mostrarTraza = false;
        System.out.println("Paquete mas pesado: " + p.getDetalle());
    } // fin de masPesado

    // ==================================================================
    // DEMOSTRACION COMPLETA (corre todo sin menu)
    // ==================================================================

    // Ejecuta todas las operaciones en orden para mostrar que todo funciona.
    private static void demostracionCompleta() {
        System.out.println("\n############## DEMOSTRACION COMPLETA ##############");

        listarRegistro();      // los 30 paquetes
        listarRepartidores();  // los 7 repartidores

        System.out.println("\n--- Consultas por codigo ---");
        buscarPaquete("PQ942"); // existe
        buscarPaquete("pq105"); // existe (en minuscula, se normaliza)
        buscarPaquete("PQ999"); // no existe

        System.out.println("\n--- Validaciones (deben salir errores) ---");
        // Cada probarError ejecuta algo que rompe una regla. "() -> ..." es una lambda: el codigo que se va a probar.
        probarError("Codigo repetido", () -> centro.registrarPaquete(new Paquete("PQ731", "Armenia", 2.0, 3, 10)));
        probarError("Peso cero", () -> new Paquete("PQ001", "Armenia", 0, 3, 10));
        probarError("Prioridad 9", () -> new Paquete("PQ002", "Armenia", 2.0, 9, 10));
        probarError("Tiempo negativo", () -> new Paquete("PQ003", "Armenia", 2.0, 3, -5));
        probarError("Destino vacio", () -> new Paquete("PQ004", "  ", 2.0, 3, 10));
        probarError("Repartidor repetido", () -> centro.registrarRepartidor(new Repartidor("R01", "Otro", "Armenia", true)));
        probarError("Repartidor NO disponible", () -> centro.asignarManual("PQ214", "R06"));
        probarError("Zona diferente al destino", () -> centro.asignarManual("PQ214", "R01"));
        probarError("Entregar paquete no asignado", () -> centro.registrarEntrega("PQ347"));

        System.out.println();
        mostrarMunicipios();          // municipios
        mostrarAgrupados();           // agrupacion por municipio
        System.out.println();
        mostrarOrdenesDeAtencion();   // orden por llegada y por prioridad
        mostrarReportesOrdenados();   // reportes ordenados

        System.out.println("\n--- Despachos por PRIORIDAD (cada repartidor lleva varios paquetes hasta su capacidad) ---");
        // Despachamos 10 veces seguidas para ver: reparto de carga, repartidores que se llenan
        // y paquetes que se saltan cuando su zona no tiene repartidor disponible (sin bloquear a los demas).
        for (int i = 1; i <= 10; i++) {
            System.out.println(i + ". " + centro.despacharPorPrioridad());
        } // fin del for
        listarRepartidores(); // se ve la carga de cada uno (ej: 2/2 = lleno)

        System.out.println("\n--- Despachos por ORDEN DE LLEGADA ---");
        System.out.println(centro.despacharPorLlegada());   // el primero que llego y sigue pendiente
        System.out.println(centro.despacharPorLlegada());   // si su repartidor esta lleno, vuelve al frente (orden estricto)

        System.out.println("\n--- Regla de salida a ruta (deben salir errores) ---");
        // R04 (Salento, capacidad 2) se lleno y SALIO A RUTA: ya no recibe mas paquetes.
        probarError("Repartidor en ruta", () -> centro.asignarManual("PQ887", "R04"));
        // R01 (Armenia) tiene paquetes pero sigue en el centro: todavia no puede entregar.
        probarError("Entregar sin haber salido", () -> centro.registrarEntrega("PQ442"));

        System.out.println("\n--- Salida MANUAL: R01 sale con lo que lleva aunque no este lleno ---");
        centro.enviarARuta("R01"); // el funcionario decide que no espera mas
        listarRepartidores();      // R01 queda NO disponible (en ruta) con 2/3

        System.out.println("\n--- Entregas (en este orden: PQ105, PQ442, PQ812) ---");
        centro.registrarEntrega("PQ105"); // R04 entrega 1 de 2: sigue en ruta
        centro.registrarEntrega("PQ442"); // R01 entrega 1 de 2: sigue en ruta
        centro.registrarEntrega("PQ812"); // R04 entrega su ULTIMO paquete: vuelve al centro
        mostrarHistorial();               // debe verse PQ105 -> PQ442 -> PQ812

        System.out.println("\n--- R04 volvio al centro (entrego todo): asignacion manual ---");
        System.out.println("PQ887 -> R04" + centro.asignarManual("PQ887", "R04")); // ahora si se puede
        System.out.println("Pendientes: " + centro.getCantidadPendientes());
        listarRepartidores(); // R04 vuelve a tener carga 1/2

        demostracionUnSoloPaquete(); // el caso de la zona con un solo paquete

        System.out.println("\n--- Algoritmos sobre los 30 paquetes ---");
        pesoPorMunicipio("Armenia", false); // recursivo sin traza
        contarPrioridad(4, false);          // recursivo sin traza
        busquedaBinaria("PQ731", true);     // binaria con traza
        busquedaBinaria("PQ500", false);    // codigo que no existe
        masPesado(false);                   // divide y venceras sin traza

        PruebasEscritorio.ejecutar();  // pruebas con los datos del enunciado
        PruebasListaSimple.ejecutar(); // pruebas de la lista propia
    } // fin de demostracionCompleta

    /**
     * Caso: a una zona solo llega UN paquete y el repartidor tiene capacidad 3.
     * No se queda esperando los otros 2: como no quedan pendientes de su zona, sale con 1/3.
     * Se usa un centro aparte para no mezclarlo con los 30 paquetes.
     */
    private static void demostracionUnSoloPaquete() {
        System.out.println("\n--- Zona con UN SOLO paquete: el repartidor sale con 1/3 ---");
        CentroDistribucion prueba = new CentroDistribucion();                         // centro vacio solo para esta prueba
        prueba.registrarRepartidor(new Repartidor("A1", "Repartidor Armenia", "Armenia", true, 3));
        prueba.registrarRepartidor(new Repartidor("S1", "Repartidor Salento", "Salento", true, 3));
        prueba.registrarPaquete(new Paquete("PQ900", "Armenia", 2.0, 4, 20)); // el UNICO de Armenia
        prueba.registrarPaquete(new Paquete("PQ901", "Salento", 1.0, 3, 20)); // Salento tiene 2
        prueba.registrarPaquete(new Paquete("PQ902", "Salento", 1.5, 2, 20));
        // Despachamos los 3: Armenia sale con 1/3; Salento sale cuando se le asigna el ultimo (2/3).
        for (int i = 1; i <= 3; i++) {
            System.out.println(i + ". " + prueba.despacharPorPrioridad());
        } // fin del for
        // Mostramos el estado final de los dos repartidores.
        for (Repartidor r : prueba.getRepartidores()) {
            System.out.println(r);
        } // fin del for
    } // fin de demostracionUnSoloPaquete

    /** Ejecuta algo que deberia fallar y muestra el mensaje del error. */
    // Runnable es una interfaz de Java con un solo metodo run(); aqui recibe el codigo a probar.
    private static void probarError(String nombrePrueba, Runnable accion) {
        try {
            accion.run(); // ejecutamos el codigo que deberia lanzar error
            // Si llega aqui es porque NO salio error (y deberia haber salido).
            System.out.println("[" + nombrePrueba + "] NO salio error (revisar!)");
        } catch (IllegalArgumentException e) {
            // Salio el error esperado: lo mostramos.
            System.out.println("[" + nombrePrueba + "] ERROR esperado: " + e.getMessage());
        } // fin del try-catch
    } // fin de probarError

    // ==================================================================
    // UTILIDADES DE LECTURA E IMPRESION
    // ==================================================================

    // Imprime cada paquete de la lista con todos sus datos.
    private static void imprimirPaquetes(List<Paquete> lista) {
        for (Paquete p : lista) {
            System.out.println("  " + p.getDetalle());
        } // fin del for
    } // fin de imprimirPaquetes

    // Pregunta si se quiere ver la traza y devuelve true si la respuesta es "s".
    private static boolean preguntarTraza() {
        String r = leerTexto("Mostrar traza de llamadas? (s/n): ");
        return r.equalsIgnoreCase("s"); // acepta "s" o "S"
    } // fin de preguntarTraza

    // Muestra un mensaje y lee una linea de texto del teclado.
    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);          // print (sin salto) para escribir en la misma linea
        return teclado.nextLine().trim();   // leemos la linea y quitamos espacios
    } // fin de leerTexto

    // Lee un numero entero; si el usuario escribe otra cosa, vuelve a preguntar.
    private static int leerEntero(String mensaje) {
        while (true) { // se repite hasta que el numero sea valido
            try {
                return Integer.parseInt(leerTexto(mensaje)); // convierte el texto a int
            } catch (NumberFormatException e) {
                System.out.println("ERROR: debe escribir un numero entero."); // no era un numero
            } // fin del try-catch
        } // fin del while
    } // fin de leerEntero

    // Lee un numero decimal; acepta coma o punto (4,5 o 4.5).
    private static double leerDouble(String mensaje) {
        while (true) { // se repite hasta que el numero sea valido
            try {
                // replace cambia la coma por punto porque parseDouble solo entiende punto.
                return Double.parseDouble(leerTexto(mensaje).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("ERROR: debe escribir un numero (ej: 4.5)."); // no era un numero
            } // fin del try-catch
        } // fin del while
    } // fin de leerDouble
} // fin de la clase Main
