[README.md](https://github.com/user-attachments/files/33365541/README.md)
# QuindíoExpress – Parcial 1 Estructuras de Datos

Sistema de gestión de operaciones de un centro de distribución de paquetes (QuindíoExpress).
Hecho en Java **sin librerías externas**, solo con el JDK.

**Integrante**



**Leidy Alexandra Marin Galeano**



\---

## 1\. Cómo compilar y ejecutar

Se necesita Java 11 o superior (lo probamos con Java 21).

Desde la carpeta del proyecto (la que tiene `src`), en **CMD de Windows**:

```
javac -encoding UTF-8 -d bin src\\\\estructuras\\\\\\\*.java src\\\\modelo\\\\\\\*.java src\\\\comparadores\\\\\\\*.java src\\\\logica\\\\\\\*.java src\\\\app\\\\\\\*.java
```

```
java -cp bin app.Main
```

Eso abre el **menú**. Si se quiere ver todo de corrido sin menú:

```
java -cp bin app.Main demo
```

En Linux/Mac es lo mismo pero con `/` en vez de `\\\\`.

\---

## 2\. Estructura del proyecto

```
src/
├── estructuras/              <- nuestra lista propia (NO usa colecciones de Java por dentro)
│   ├── IColeccion.java       interfaz genérica (el contrato): agregar, eliminar, obtener, tamaño, vacía
│   ├── Nodo.java             nodo genérico: valor + referencia al siguiente
│   └── ListaSimple.java      lista simplemente enlazada genérica + Iterator interno
├── modelo/
│   ├── Paquete.java          implements Comparable (orden natural = código)
│   └── Repartidor.java
├── comparadores/             criterios alternativos (Comparator)
│   ├── ComparadorPrioridad.java   mayor a menor
│   ├── ComparadorPeso.java        mayor a menor
│   ├── ComparadorTiempo.java      menor a mayor
│   └── ComparadorDespacho.java    prioridad -> tiempo -> código (lo usa la PriorityQueue)
├── logica/
│   ├── CentroDistribucion.java    todas las colecciones y las operaciones del negocio
│   └── Algoritmos.java            recursión, búsqueda binaria y divide y vencerás
└── app/
    ├── Main.java                  menú por consola
    ├── DatosIniciales.java        30 paquetes, 7 destinos, 7 repartidores
    ├── PruebasEscritorio.java     datos pequeños del enunciado con traza de la pila
    └── PruebasListaSimple.java    casos de la lista: vacía, 1 elemento, borrar primero/medio/último/inexistente
```

\---

## 3\. Qué estructura usamos para cada cosa

|Necesidad|Estructura|Por qué (resumen)|
|-|-|-|
|Registro de auditoría (orden exacto de registro)|`ArrayList<Paquete>`|agregar al final O(1), no mueve a los demás, `get(i)` O(1)|
|Pendientes por orden de llegada|`ArrayDeque<Paquete>` (como `Deque`)|FIFO: `offerLast` / `pollFirst` en O(1). Si un paquete no se puede asignar vuelve al frente con `offerFirst`|
|Pendientes por prioridad|`PriorityQueue<Paquete>` + `ComparadorDespacho`|`poll()` saca el que sigue en O(log n)|
|Buscar paquete por código / validar código único|`HashMap<String, Paquete>`|`get` y `containsKey` en O(1) promedio|
|Municipios sin repetidos|`LinkedHashSet<String>`|no repite, O(1), y conserva el orden de aparición|
|Municipios ordenados|`TreeSet<String>`|no repite y siempre está ordenado (O(log m))|
|Paquetes de cada municipio|`HashMap<String, List<Paquete>>`|municipio → lista de sus paquetes|
|Repartidores por identificación|`LinkedHashMap<String, Repartidor>`|id único y se listan en orden de registro|
|Paquetes que lleva cada repartidor|`ArrayList<Paquete>` dentro de `Repartidor`|hasta su capacidad máxima; quitar es O(c) con c pequeño|
|Paquetes en ruta|`HashMap<String, Repartidor>`|código → repartidor que lo lleva|
|Historial de entregas|`ListaSimple<Paquete>` (**propia**)|lo exige el enunciado; agregar al final O(1) gracias a `nodoUltimo`|

La explicación completa (complejidad, alternativa descartada, tiempo vs. memoria) está en el documento PDF, parte A.

\---

## 4\. Algoritmos

|Algoritmo|Dónde|Técnica|Tiempo|Espacio|
|-|-|-|-|-|
|Peso total de un municipio|`Algoritmos.pesoTotalMunicipio`|recursión (sin ciclos)|O(n)|O(n) pila|
|Contar paquetes con prioridad ≥ x|`Algoritmos.contarPrioridadMinima`|recursión (sin ciclos)|O(n)|O(n) pila|
|Buscar por código en lista ordenada|`Algoritmos.busquedaBinaria`|búsqueda binaria recursiva|O(log n)|O(log n) pila|
|Paquete más pesado|`Algoritmos.paqueteMasPesado`|divide y vencerás|O(n)|O(log n) pila|

**Precondición de la búsqueda binaria:** la lista tiene que estar ordenada por código. Por eso en el `Main`
primero se llama `getPaquetesOrdenados(null)` (orden natural) y después se busca.

Con la opción **19** del menú se ven las trazas de la pila con los mismos datos del enunciado
(14.0 kg, 4 paquetes, PQ731 en la posición 5, 18.7 kg).

\---

## 5\. Decisiones que tomamos

1. **Rango de prioridad:** el enunciado dice "0 - 10" en la descripción del atributo, pero en las
*reglas del sistema* dice "entre 1 y 5". Usamos **1 a 5** (todas las prioridades de los ejemplos están
en ese rango) con 5 por defecto. Está en constantes (`Paquete.PRIORIDAD\\\_MINIMA` y `PRIORIDAD\\\_MAXIMA`),
así que cambiarlo es cambiar dos números.
2. **Nombres sin tilde** en los datos (`Calarca`) para que se vean bien en la consola de Windows.
3. **Filandia no tiene repartidor** y **R06 (Quimbaya) está inactivo**: es a propósito, para
poder mostrar las reglas "repartidor no disponible" y "la zona debe coincidir con el destino".
4. Cuando se despacha un paquete por una de las dos colas, también se quita de la otra
(`remove(Object)`, que es O(n)). Lo aceptamos porque así las dos colas siempre tienen los mismos
pendientes; está analizado en el documento.
5. La búsqueda por código "normal" se hace con el `HashMap`; la búsqueda binaria se usa para los
reportes que ya vienen ordenados por código (como pide el enunciado).
6. **Capacidad de los repartidores:** cada repartidor lleva varios paquetes a la vez, hasta su
capacidad (2 o 3 en los datos iniciales). `isDisponible()` se calcula como
`activo \\\&\\\& !enRuta \\\&\\\& carga < capacidad`. Si hay varios repartidores libres en la zona, se escoge
el menos cargado.
7. **Salida a ruta:** el repartidor sale del centro (y pasa a NO disponible) cuando se **llena**
o cuando **ya no quedan paquetes pendientes para su zona**, aunque no esté lleno (ej: a Armenia
solo llega 1 paquete → sale con 1/3). También se puede mandar a salir a mano (opción 22).
Mientras está en ruta no recibe paquetes; vuelve a estar disponible cuando entrega **todos**
los que lleva. No se puede registrar la entrega de un paquete si su repartidor no ha salido.
8. **Despacho por prioridad sin bloqueo:** si el paquete más prioritario no tiene repartidor
disponible en su zona, se aparta y se despacha el siguiente; los apartados vuelven a la cola
con su misma prioridad. En el despacho por orden de llegada no se salta a nadie (orden estricto).

\---

## 6\. Uso de inteligencia artificial

Usamos IA (Claude) durante todo el parcial. Teníamos muchas dudas, en varios puntos no sabíamos por
dónde iniciar y en algunos casos no nos sentimos seguros de nuestro análisis, así que preferimos
basarnos en lo que propuso la IA. La bitácora completa (incluido lo que la IA programó y documentó
directamente, como la salida a ruta de los repartidores) y el caso de una respuesta incorrecta están
en el documento PDF (parte D).

