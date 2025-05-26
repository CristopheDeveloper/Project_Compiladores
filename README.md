# Proyecto: Analizador y Resolutor de Funciones Lineales

Este proyecto es una **herramienta de escritorio** que lee un archivo con instrucciones matemáticas sencillas (funciones lineales), verifica que estén bien escritas, las resuelve y genera dos archivos de resultado:

1. **salida.res**: contiene los resultados válidos, mostrando el valor de la variable.
2. **\<númeroDeCarné>.err**: enumera las líneas con errores y su tipo.

---

## Objetivo

Permitir que cualquier usuario, sin necesidad de conocimientos de programación, pueda:

* Verificar que un conjunto de ecuaciones lineales de primer grado estén correctamente formateadas.
* Detectar y listar errores en la forma en que fueron escritas.
* Resolver automáticamente las ecuaciones bien formadas.
* Obtener los resultados en un archivo listo para revisar.

---

##  Estructura del proyecto

```
ProyectoCompilador/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── umg/edu/gt/...
│   │   └── resources/
│   │       └── test.c25       ← Ejemplo de entrada
├── salida.res                 ← Resultados válidos
├── <Carné>.err                ← Errores encontrados
└── README.md                  ← Este documento
```

* **`src/main/resources/test.c25`**: archivo de entrada con las instrucciones (funciones).
* **`salida.res`**: archivo de salida que contiene las soluciones de las ecuaciones sin errores.
* **`<Carné>.err`**: archivo de errores donde `<Carné>` es tu número de carné (sin guiones), que lista cada error con su línea y código.

---

##  Formato del archivo de entrada (`.c25`)

Cada línea debe seguir esta sintaxis básica:

```
f(<variable>) = <expresión>;
```

donde:

* **`f(x)`**: indica una función con una sola letra dentro de los paréntesis. Ejemplo: `f(x)`, `f(y)`, etc.
* **`expresión`**: combinación de números y la misma variable, usando operaciones de suma, resta o multiplicación implícita (`2x` equivale a `2 * x`).
* **`;`**: punto y coma al final de cada línea.

**Ejemplo de líneas válidas:**

```plaintext
f(x) = x - 1;
f(x) = 2x + 5;
f(y) = y + 2;
```

---

##  Manejo de errores

El programa detecta estos errores y los anota en el archivo `<Carné>.err`:

| Código    | Descripción                                              |
| --------- | -------------------------------------------------------- |
| `Error 1` | Falta `;` al final.                                      |
| `Error 2` | La variable declarada no aparece en la expresión.        |
| `Error 3` | Aparecen dos o más variables diferentes en la expresión. |
| `Error 4` | La línea no inicia con `f(`.                             |
| `Error 5` | La función está mal formada (sintaxis inválida).         |
| `Error 6` | Hay espacios en lugares prohibidos en `f(x)`.            |

El archivo `<Carné>.err` tendrá entradas como:

```
3.- Error 2
5.- Error 6
```

---

##  ¿Cómo usar la herramienta?

1. **Coloca** tu archivo con las funciones en `src/main/resources/`, por ejemplo `misFunciones.c25`.
2. **Abre** la línea de comandos o terminal.
3. **Navega** a la carpeta del proyecto.
4. Ejecuta el programa (asegúrate de tener Java instalado):

   ```bash
   mvn clean compile exec:java -Dexec.mainClass="umg.edu.gt.Main"
   ```
5. Al terminar, en la raíz del proyecto encontrarás:

    * **`salida.res`** con los resultados.
    * **`<Carné>.err`** con los errores.

---

##  Detalles técnicos (simplificado)

* El programa **lee línea por línea** el archivo de entrada.
* **Valida** la sintaxis básica y detecta errores según los códigos.
* Convierte la parte derecha (`expresión`) en una estructura interna.
* **Resuelve** cada ecuación lineal de primer grado: si la ecuación es `ax + b = 0`, calcula `x = -b/a`.
* **Guarda** los resultados y errores en archivos separados.

---

