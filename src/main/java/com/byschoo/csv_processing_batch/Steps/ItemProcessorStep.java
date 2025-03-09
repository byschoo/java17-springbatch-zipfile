package com.byschoo.csv_processing_batch.Steps;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.byschoo.csv_processing_batch.Entities.Person;

import lombok.extern.slf4j.Slf4j;


@Slf4j // Anotación de Lombok para generar un logger
public class ItemProcessorStep implements Tasklet {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    //  1. Definición del formateador de fecha y hora

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        //  2. Inicio del paso de procesamiento
        log.info("-------------------> Inicio del paso de procesamiento del archivo <-------------------");

        //  3. Obtención de la lista de personas del contexto de ejecución
        Object personListObject = chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext().get("personList");

        //  4. Verificación y conversión de la lista de personas
        if (personListObject instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Person> personList = (List<Person>) personListObject;

            //  5. Procesamiento de cada persona en la lista
            if (personList != null) {
                personList = personList.stream()
                        .map(this::processPerson)
                        .collect(Collectors.toList());

                //  6. Almacenamiento de la lista procesada en el contexto
                chunkContext.getStepContext()
                        .getStepExecution()
                        .getJobExecution()
                        .getExecutionContext().put("personList", personList);
            }
        } else {
            //  7. Manejo de error si el objeto no es una lista
            log.error("El objeto 'personList' no es una lista de Person.");
        }

        //  8. Fin del paso de procesamiento
        log.info("-------------------> Fin del paso de procesamiento del archivo <-------------------");

        return RepeatStatus.FINISHED;
    }

    //  9. Método para procesar una persona
    private Person processPerson(Person person) {
        //  9.1. Establecimiento de la fecha de inserción
        person.setInsertionDate(DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        return person;
    }
}