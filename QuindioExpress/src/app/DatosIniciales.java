// Esta clase pertenece al paquete "app" (lo que arranca y prueba el programa)
package app;

// Necesitamos el centro para registrar ahi los datos.
import logica.CentroDistribucion;
// Clases para crear los paquetes y los repartidores.
import modelo.Paquete;
import modelo.Repartidor;

/**
 * Carga los datos minimos que pide el enunciado:
 *   - 30 paquetes con codigos NO consecutivos
 *   - 7 destinos diferentes (Armenia, Salento, Calarca, Montenegro, Quimbaya, Circasia, Filandia)
 *   - 7 repartidores con distinta capacidad (uno inactivo y Filandia sin repartidor, para probar las reglas)
 *   - prioridades y pesos diferentes, varios paquetes al mismo municipio
 *
 * El orden en que se llama registrarPaquete ES el orden de llegada.
 */
public class DatosIniciales {

    // Metodo static: se llama como DatosIniciales.cargar(centro) sin crear un objeto.
    public static void cargar(CentroDistribucion centro) {

        // ------------------ REPARTIDORES ------------------
        // Cada linea crea un repartidor (id, nombre, zona, activo, capacidad) y lo registra en el centro.
        // La capacidad es cuantos paquetes puede llevar al mismo tiempo.
        centro.registrarRepartidor(new Repartidor("R01", "Carlos Andres Gomez", "Armenia", true, 3));
        centro.registrarRepartidor(new Repartidor("R02", "Luisa Fernanda Rios", "Armenia", true, 2));
        centro.registrarRepartidor(new Repartidor("R03", "Jhon Fredy Ospina", "Calarca", true, 2));
        centro.registrarRepartidor(new Repartidor("R04", "Maria Camila Torres", "Salento", true, 2));
        centro.registrarRepartidor(new Repartidor("R05", "Andres Felipe Lopez", "Montenegro", true, 3));
        centro.registrarRepartidor(new Repartidor("R06", "Diana Marcela Cardona", "Quimbaya", false, 2)); // inactivo: NO disponible
        centro.registrarRepartidor(new Repartidor("R07", "Sebastian Henao", "Circasia", true, 2));
        // Nota: Filandia no tiene repartidor a proposito (para probar la regla de zona).

        // ------------------ PAQUETES (orden de llegada) ------------------
        // Cada linea crea un paquete y lo registra. El primero registrado es el primero que llego.
        //                       codigo   destino      peso  prio tiempo
        centro.registrarPaquete(new Paquete("PQ731", "Armenia",     7.5, 5, 45)); // llegada 1
        centro.registrarPaquete(new Paquete("PQ105", "Salento",     3.0, 5, 25)); // llegada 2
        centro.registrarPaquete(new Paquete("PQ942", "Armenia",    18.7, 4, 20)); // llegada 3 (el mas pesado)
        centro.registrarPaquete(new Paquete("PQ318", "Calarca",     4.5, 2, 35)); // llegada 4
        centro.registrarPaquete(new Paquete("PQ567", "Salento",    12.0, 3, 50)); // llegada 5
        centro.registrarPaquete(new Paquete("PQ829", "Armenia",     2.3, 1, 15)); // llegada 6
        centro.registrarPaquete(new Paquete("PQ420", "Montenegro",  5.1, 3, 30)); // llegada 7
        centro.registrarPaquete(new Paquete("PQ214", "Quimbaya",    9.8, 4, 60)); // llegada 8
        centro.registrarPaquete(new Paquete("PQ663", "Circasia",    1.2, 2, 40)); // llegada 9
        centro.registrarPaquete(new Paquete("PQ077", "Filandia",    6.4,    70)); // prioridad por defecto (5)
        centro.registrarPaquete(new Paquete("PQ508", "Armenia",    11.3, 3, 18)); // llegada 11
        centro.registrarPaquete(new Paquete("PQ391", "Calarca",     0.8, 5, 22)); // llegada 12
        centro.registrarPaquete(new Paquete("PQ846", "Montenegro", 14.6, 2, 55)); // llegada 13
        centro.registrarPaquete(new Paquete("PQ152", "Salento",     3.9, 4, 33)); // llegada 14
        centro.registrarPaquete(new Paquete("PQ970", "Quimbaya",    7.0, 1, 65)); // llegada 15
        centro.registrarPaquete(new Paquete("PQ233", "Armenia",     4.4, 4, 20)); // llegada 16
        centro.registrarPaquete(new Paquete("PQ614", "Circasia",    8.2, 5, 38)); // llegada 17
        centro.registrarPaquete(new Paquete("PQ489", "Calarca",     2.7, 3, 28)); // llegada 18
        centro.registrarPaquete(new Paquete("PQ305", "Armenia",    16.1, 2, 12)); // llegada 19
        centro.registrarPaquete(new Paquete("PQ758", "Filandia",    5.5, 3, 75)); // llegada 20
        centro.registrarPaquete(new Paquete("PQ126", "Montenegro",  3.3, 5, 32)); // llegada 21
        centro.registrarPaquete(new Paquete("PQ887", "Salento",    10.4, 2, 48)); // llegada 22
        centro.registrarPaquete(new Paquete("PQ442", "Armenia",     6.6, 5, 26)); // llegada 23
        centro.registrarPaquete(new Paquete("PQ019", "Quimbaya",   13.2, 4, 58)); // llegada 24
        centro.registrarPaquete(new Paquete("PQ675", "Calarca",     9.1, 1, 30)); // llegada 25
        centro.registrarPaquete(new Paquete("PQ931", "Circasia",    2.0, 3, 44)); // llegada 26
        centro.registrarPaquete(new Paquete("PQ260", "Armenia",     5.8, 3, 16)); // llegada 27
        centro.registrarPaquete(new Paquete("PQ553", "Montenegro",  7.7, 4, 36)); // llegada 28
        centro.registrarPaquete(new Paquete("PQ812", "Salento",     1.5, 5, 27)); // llegada 29
        centro.registrarPaquete(new Paquete("PQ347", "Armenia",    12.9, 2, 21)); // llegada 30
    } // fin de cargar
} // fin de la clase DatosIniciales
