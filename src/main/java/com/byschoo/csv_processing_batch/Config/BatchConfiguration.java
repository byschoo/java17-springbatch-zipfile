package com.byschoo.csv_processing_batch.Config;

import com.byschoo.csv_processing_batch.Service.IPersonService;
import com.byschoo.csv_processing_batch.Steps.ItemUnziperStep;
import com.byschoo.csv_processing_batch.Steps.ItemProcessorStep;
import com.byschoo.csv_processing_batch.Steps.ItemReaderStep;
import com.byschoo.csv_processing_batch.Steps.ItemWriterStep;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration // Indica que esta clase contiene definiciones de beans
public class BatchConfiguration {

    @Autowired // Inyecta el JobRepository proporcionado por Spring Batch
    private JobRepository jobRepository;

    @Autowired // Inyecta el PlatformTransactionManager proporcionado por Spring Batch
    private PlatformTransactionManager transactionManager;

    //  1. Definición del bean para el paso de descompresión
    @Bean
    public ItemUnziperStep itemUnzipStep() {
        return new ItemUnziperStep();
    }


    //  2. Definición del bean para el paso de lectura
    @Bean
    public ItemReaderStep itemReaderStep() {
        return new ItemReaderStep();
    }


    //  3. Definición del bean para el paso de procesamiento
    @Bean
    public ItemProcessorStep itemProcessorStep() {
        return new ItemProcessorStep();
    }


    //  4. Definición del bean para el paso de escritura
    @Bean
    public ItemWriterStep itemWriterStep(IPersonService personService) {
        //  4.1. Inyección de IPersonService para la escritura
        return new ItemWriterStep(personService);
    }


    //  5. Definición del bean para el job principal
    @Bean
    public Job processPersonJob(Step unzipStep, Step readPersonStep, Step processPersonStep, Step writePersonStep) {
        //  5.1. Construcción del job con los pasos definidos
        return new JobBuilder("processPersonJob", jobRepository)
                .start(unzipStep)
                .next(readPersonStep)
                .next(processPersonStep)
                .next(writePersonStep)
                .build();
    }


    //  6. Definición del bean para el paso de descompresión (Step)
    @Bean
    public Step unzipStep() {
        //  6.1. Construcción del paso con el tasklet y transactionManager
        return new StepBuilder("unzipStep", jobRepository)
                .tasklet(itemUnzipStep(), transactionManager)
                .build();
    }


    //  7. Definición del bean para el paso de lectura (Step)
    @Bean
    public Step readPersonStep() {
        //  7.1. Construcción del paso con el tasklet y transactionManager
        return new StepBuilder("readPersonStep", jobRepository)
                .tasklet(itemReaderStep(), transactionManager)
                .build();
    }


    //  8. Definición del bean para el paso de procesamiento (Step)
    @Bean
    public Step processPersonStep() {
        //  8.1. Construcción del paso con el tasklet y transactionManager
        return new StepBuilder("processPersonStep", jobRepository)
                .tasklet(itemProcessorStep(), transactionManager)
                .build();
    }

    
    //  9. Definición del bean para el paso de escritura (Step)
    @Bean
    public Step writePersonStep(IPersonService personService) {
        //  9.1. Construcción del paso con el tasklet y transactionManager
        return new StepBuilder("writePersonStep", jobRepository)
                .tasklet(itemWriterStep(personService), transactionManager)
                .build();
    }
}