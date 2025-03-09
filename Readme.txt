Video:
https://www.youtube.com/watch?v=EhqnE6NvbHE&t=4330s

Para el banner:
https://devops.datenkollektiv.de/banner.txt/index.html

Se procesa un archivo comprimido en la /files/persons.zip
El archivo descomprimido se guarda en la carpeta /files/destination/persons.csv
Como el archivo .jar se ejecuta en /target. Entonces la aplicación asume esta carpeta
como la raíz. Por lo tanto, los archivos se crean dentro de /target/classes automáticamente.

1. Paso de Descompresión (ItemUnziperStep):
Preparar los datos de entrada para el procesamiento. Esto implica extraer los archivos necesarios de un archivo comprimido (ZIP) para que los pasos posteriores puedan acceder a ellos.
En un contexto de procesamiento de datos, esto es común cuando los datos se reciben en un formato comprimido para ahorrar espacio y facilitar la transferencia.

2. Paso de Lectura (ItemReaderStep):
Leer los datos de la fuente de entrada (por ejemplo, un archivo CSV, una base de datos, etc.) y convertirlos en un formato que la aplicación pueda procesar.
En este caso, se lee el archivo CSV y se convierte cada línea en un objeto Person. Este paso es crucial para la extracción de datos desde la fuente original.

3. Paso de Procesamiento (ItemProcessorStep):
Transformar o enriquecer los datos leídos en el paso anterior.
Este paso permite realizar operaciones como limpieza de datos, validación, transformación de formatos, o enriquecimiento con datos de otras fuentes.
Por ejemplo, podrías usar ItemProcessorStep para validar que los datos de cada persona son correctos, para transformar los nombres a mayúsculas, o para agregar información adicional desde otra base de datos.
En esta aplicación se verifica el objeto lista de persona y se anexa la fecha de inserción de cada persona en ella.

4. Paso de Escritura (ItemWriterStep):
Escribir los datos procesados en el paso anterior en la fuente de salida (por ejemplo, una base de datos, un archivo, etc.).
En nuestro caso, se guardan los objetos Person en una base de datos.
Este paso es el final del proceso de transformación de datos y se encarga de persistir los resultados.