package com.byschoo.csv_processing_batch.Steps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;

import com.byschoo.csv_processing_batch.Exceptions.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemUnziperStep implements Tasklet {

    @Autowired
    private ResourceLoader resourceLoader; // Inyección del ResourceLoader para acceder a los recursos

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        // 1. Inicio del paso de descompresión
        log.info("-------------------> Inicio del paso de descompresión del archivo <-------------------");

        try {
            // 2. Obtención del recurso ZIP desde el classpath
            Resource resource = resourceLoader.getResource("classpath:files/persons.zip");

            // 3. Verificación de la existencia del archivo ZIP
            if (!resource.exists()) {
                // 3.1. Registro de error si el archivo ZIP no se encuentra
                log.error("ZIP file not found in classpath: files/persons.zip");
                // 3.2. Lanzamiento de excepción si el archivo ZIP no se encuentra
                throw new ResourceNotFoundException("ZIP file not found in classpath: files/persons.zip", "Exc-404-07", HttpStatus.NOT_FOUND);
            }

            // 4. Obtención de la ruta absoluta del archivo ZIP
            File zipFileResource = resource.getFile();
            String filePath = zipFileResource.getAbsolutePath();
            // 4.1. Registro de la ruta del archivo ZIP
            log.info("ZIP file path: {}", filePath);

            // 5. Descompresión del archivo ZIP
            try (ZipFile zipFile = new ZipFile(filePath)) {
                // 5.1. Creación del directorio de destino
                File destDir = new File(zipFileResource.getParentFile(), "destination");
                // 5.2. Registro del directorio de destino
                log.info("Destination directory: {}", destDir.getAbsolutePath());

                // 5.3. Verificación y creación del directorio de destino
                if (!destDir.exists() && !destDir.mkdirs()) {
                    // 5.3.1. Registro de error si falla la creación del directorio
                    log.error("Failed to create destination directory: {}", destDir.getAbsolutePath());
                    // 5.3.2. Lanzamiento de excepción si falla la creación del directorio
                    throw new IOException("Failed to create destination directory: " + destDir.getAbsolutePath());
                }

                // 5.4. Obtención de las entradas del archivo ZIP
                Enumeration<? extends ZipEntry> entries = zipFile.entries();

                // 5.5. Iteración sobre las entradas del archivo ZIP
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    File file = new File(destDir, entry.getName());

                    // 5.6. Creación de directorios o extracción de archivos
                    if (entry.isDirectory()) {
                        // 5.6.1. Creación de directorios
                        file.mkdirs();
                    } else {
                        // 5.6.2. Extracción de archivos
                        try (InputStream is = zipFile.getInputStream(entry);
                             FileOutputStream fos = new FileOutputStream(file)) {
                            byte[] buffer = new byte[1024];
                            int len;

                            while ((len = is.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }
                    }
                }

                // 6. Almacenamiento de la ruta del archivo CSV en el contexto
                File csvFile = new File(destDir, "persons.csv");
                String csvFilePath = csvFile.getAbsolutePath();
                chunkContext.getStepContext()
                        .getStepExecution()
                        .getJobExecution()
                        .getExecutionContext()
                        .put("csvFilePath", csvFilePath);
            }

            // 7. Fin del paso de descompresión
            log.info("-------------------> Fin del paso de descompresión del archivo <-------------------");

        } catch (IOException e) {
            // 8. Manejo de excepciones de E/S
            log.error("Error during decompression", e);
            // 8.1. Relanzamiento de la excepción
            throw new RuntimeException("Error during decompression", e);
        }

        return RepeatStatus.FINISHED;
    }
}