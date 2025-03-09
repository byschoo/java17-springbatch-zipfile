package com.byschoo.csv_processing_batch.Steps;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.byschoo.csv_processing_batch.Entities.Person;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ItemReaderStep implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        // 1. Inicio del paso de lectura del archivo
        log.info("-------------------> Inicio del paso de lectura del archivo <-------------------");

        try {
            // 2. Obtención de la ruta absoluta del archivo CSV desde el contexto de ejecución
            String absoluteFilePath = (String) chunkContext.getStepContext()
                    .getStepExecution()
                    .getJobExecution()
                    .getExecutionContext()
                    .get("csvFilePath");

            // 3. Registro de la ruta absoluta del archivo CSV
            log.info("Absolute file path: {}", absoluteFilePath);

            // 4. Verificación de la existencia del archivo CSV
            File file = new File(absoluteFilePath);
            if (!file.exists()) {
                // 4.1. Registro de error si el archivo no se encuentra
                log.error("File not found at: {}", absoluteFilePath);
                // 4.2. Lanzamiento de excepción si el archivo no se encuentra
                throw new FileNotFoundException("File not found: " + absoluteFilePath);
            }

            // 5. Lectura del archivo CSV usando OpenCSV
            try (Reader reader = new FileReader(absoluteFilePath);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

                // 6. Lista para almacenar los objetos Person
                List<Person> personList = new ArrayList<>();
                String[] line;

                // 7. Lectura de cada línea del archivo CSV
                while ((line = csvReader.readNext()) != null) {
                    try {
                        // 8. Creación de un objeto Person a partir de la línea CSV
                        Person person = new Person();
                        person.setName(line[0]);
                        person.setLastName(line[1]);
                        person.setAge(Integer.parseInt(line[2]));
                        // 9. Adición del objeto Person a la lista
                        personList.add(person);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        // 10. Manejo de errores en la lectura de la línea
                        log.error("Error processing line: {}", line, e);
                    }
                }

                // 11. Almacenamiento de la lista de objetos Person en el contexto de ejecución
                if (!personList.isEmpty()) {
                    chunkContext.getStepContext()
                            .getStepExecution()
                            .getJobExecution()
                            .getExecutionContext()
                            .put("personList", personList);
                }

                // 12. Fin del paso de lectura del archivo
                log.info("-------------------> Fin del paso de lectura del archivo <-------------------");
                return RepeatStatus.FINISHED;
            }
        } catch (IOException e) {
            // 13. Manejo de excepciones de E/S
            log.error("Error reading CSV file", e);
            // 13.1. Relanzamiento de la excepción
            throw e;
        }
    }
}