# TecniHogar - Laboratorio 3: Herencia

Aplicación de consola en Java para cotizar y registrar servicios técnicos, administrar cuadrillas y jornadas, registrar visitas y consultar ingresos. Los datos se mantienen únicamente en memoria.

## Requisitos y ejecución

JDK 17 o posterior. No requiere bibliotecas externas ni conexión a Internet. Desde esta carpeta:

```powershell
javac -encoding UTF-8 -d out src/tecnihogar/*.java
java -cp out tecnihogar.Main
```

En una terminal de Windows, si los acentos no se muestran bien, use `chcp 65001` antes de ejecutar. Los importes y horas decimales se ingresan con punto, sin separadores de miles; por ejemplo, `2000.50`.

La carpeta `bin/` incluye los archivos compilados compatibles con Java 17 o posterior. Para ejecutar directamente sin compilar:

```powershell
java -cp bin tecnihogar.Main
```

## Uso

El menú permanece disponible hasta elegir `0`. Las opciones 1 y 2 permiten cotizar y registrar las cuatro categorías. La opción 2 muestra la cotización y solicita aceptar el pago (`s`) antes de registrar. Una cotización o una respuesta `n` no registra ni cobra nada.

Las opciones 3 a 8 permiten registrar cuadrillas, crear jornadas, asignar y retirar órdenes, iniciar y registrar resultados. En el resultado de una visita, `s` significa completada y `n` fallida. Las opciones 9 a 12 muestran órdenes, cuadrillas, reportes y jornadas.

Cada arranque carga dos órdenes pendientes por categoría (`D1`, `D2`, `I1`, `I2`, `E1`, `E2`, `A1`, `A2`) y tres cuadrillas (`C1`, `C2`, `C3`), sin ingresos. C1 y C3 están certificadas en refrigeración; C2 no. Los códigos distinguen mayúsculas y minúsculas, y se eliminan espacios en los extremos.

Ejemplo: cree la jornada `J1` con `C1`; asigne `I1` y `A1`; inicie `J1`; registre un resultado para cada orden. I1 ocupa 2.5 horas de capacidad aunque se facturan 4 horas. Si una visita falla, la orden vuelve a pendiente o queda cancelada al agotar su límite.

## Pruebas reproducibles

```powershell
javac -encoding UTF-8 -d out src/tecnihogar/*.java
java -cp out tecnihogar.Pruebas pruebas.tsv
```

La suite ejecuta 118 comprobaciones con aserciones propias (no necesita `-ea`). Si alguna falla, termina con un error. `pruebas.tsv` contiene entrada/caso, resultado esperado, resultado obtenido y estado. Se puede abrir como texto o con una hoja de cálculo.

## Diseño

`Orden` concentra los datos y estados comunes. Las subclases calculan costo, tiempo, compatibilidad y límite de visitas mediante polimorfismo. `InstalacionCertificada` extiende `Instalacion` y reutiliza su costo con `super`. No se consulta el tipo concreto para decidir reglas del negocio. El `switch` de la consola únicamente construye la categoría elegida.

`Jornada` valida capacidad, compatibilidad y estado antes de modificar órdenes. `Sistema` conserva las colecciones y el libro de ingresos; registra Q40 por visita adicional únicamente después de iniciar correctamente la jornada. Los montos usan `BigDecimal` y redondeo monetario HALF_UP a dos decimales.

El PDF de la carpeta `documentacion` incluye análisis, estados, catálogo de clases, diagramas UML y evidencia de las pruebas. El archivo `diseno.puml` contiene el diagrama editable; el PDF ya lleva los diagramas incorporados y no depende de un enlace.

