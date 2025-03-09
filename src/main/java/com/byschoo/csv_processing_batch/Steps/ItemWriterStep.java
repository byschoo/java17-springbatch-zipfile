package com.byschoo.csv_processing_batch.Steps;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.byschoo.csv_processing_batch.Entities.Person;
import com.byschoo.csv_processing_batch.Service.IPersonService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
public class ItemWriterStep implements Tasklet {

    private final IPersonService personService; // Inyección de dependencia del servicio IPersonService

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        // 1. Inicio del paso de escritura
        log.info("-------------------> Inicio del paso de escritura del archivo <-------------------");

        // 2. Obtención de la lista de personas desde el contexto de ejecución
        Object personListObject = chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext().get("personList");

        // 3. Verificación de que el objeto obtenido sea una lista
        if (personListObject instanceof List<?>) {
            // 4. Casting del objeto a una lista de Person
            @SuppressWarnings("unchecked")
            List<Person> personList = (List<Person>) personListObject;

            // 5. Verificación de que la lista no esté vacía ni sea nula
            if (personList != null && !personList.isEmpty()) {
                // 6. Iteración sobre la lista de personas para registro (opcional)
                for (Person person : personList) {
                    if (person != null) {
                        // 6.1. Registro de la información de cada persona
                        log.info(person.toString());
                    }
                }

                // 7. Intento de guardar la lista de personas usando el servicio
                try {
                    personService.saveAllPersons(personList);
                } catch (Exception e) {
                    // 8. Manejo de errores al guardar las personas
                    log.error("Error saving persons", e);
                    // 8.1. Relanzamiento de la excepción
                    throw new RuntimeException("Error saving persons", e);
                }
            } else {
                // 9. Registro de advertencia si la lista de personas está vacía o es nula
                log.warn("Person list is empty or null.");
            }
        } else {
            // 10. Registro de error si el objeto no es una lista
            log.error("El objeto 'personList' no es una lista de Person.");
        }

        // 11. Fin del paso de escritura
        log.info("-------------------> Fin del paso de escritura del archivo <-------------------");

        return RepeatStatus.FINISHED;
    }
}