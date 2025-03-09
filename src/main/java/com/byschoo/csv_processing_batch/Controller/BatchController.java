package com.byschoo.csv_processing_batch.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.byschoo.csv_processing_batch.Exceptions.ResourceNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1")
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @PostMapping("/uploadFile")
    public ResponseEntity<?> receiveFile(@RequestParam(name = "file") MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();

        try {
            //  1. Validación inicial y registro del proceso
            log.info("Iniciando el proceso batch para el archivo: {}", fileName);

            if (fileName == null || fileName.isEmpty()) {
                //  1.1. Respuesta si el nombre del archivo es inválido
                return ResponseEntity.badRequest().body("Nombre de archivo inválido.");
            }

            //  2. Guardado del archivo cargado en el sistema
            Path path = Paths.get("src", "main", "resources", "files", fileName);
            Files.createDirectories(path.getParent());
            Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            //  3. Configuración de parámetros y ejecución del job
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("nombre", fileName)
                    .addDate("fecha", new Date())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            //  3.1. Registro del estado de la ejecución del job
            log.info("Job ejecutado: {}, Status: {}", jobExecution.getJobId(), jobExecution.getStatus());

            //  4. Manejo de fallos en la ejecución del job
            if (jobExecution.getStatus() == BatchStatus.FAILED) {
                //  4.1. Iteración para buscar excepciones específicas
                for (Throwable exception : jobExecution.getFailureExceptions()) {
                    if (exception instanceof ResourceNotFoundException) {
                        //  4.1.1. Respuesta específica si no se encuentra el archivo ZIP
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Archivo ZIP no encontrado.");
                    }
                }
                //  4.2. Respuesta genérica si falla el job
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al iniciar el job batch.");
            }

            //  5. Construcción de la respuesta exitosa
            Map<String, String> response = new HashMap<>();
            response.put("archivo", fileName);
            response.put("message", "Job executed");

            //  6. Registro de finalización y retorno de la respuesta
            log.info("Finalizando el proceso batch para el archivo: {}", fileName);
            return ResponseEntity.ok(response);

        } catch (JobExecutionException e) {
            //  7. Manejo de excepciones de Spring Batch
            log.error("Error al iniciar el job batch con el archivo {}. Error: {}", fileName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al iniciar el job batch.");
        } catch (IOException e) {
            //  8. Manejo de excepciones de E/S
            log.error("Error al guardar el archivo {}. Error: {}", fileName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo.");
        } catch (Exception e) {
            //  9. Manejo de excepciones genéricas
            log.error("Error inesperado al procesar el archivo {}. Error: {}", fileName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado.");
        }
    }
}