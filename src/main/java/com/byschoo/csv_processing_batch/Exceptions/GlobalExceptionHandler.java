package com.byschoo.csv_processing_batch.Exceptions;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.byschoo.csv_processing_batch.Exceptions.Response.ErrorResponse;
import com.byschoo.csv_processing_batch.Exceptions.Response.ValidationResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {    
    
    @ExceptionHandler(MethodArgumentNotValidException.class) // 
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest webRequest) {
        log.error("Exception Occured: " + ex.getClass().getSimpleName() + " - " + ex.getFieldErrors(), ex); //List<FieldError>
        
        Map<String, String> mapErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String clave = ((FieldError) error).getField();
            String valor = error.getDefaultMessage();
            mapErrors.put(clave, valor);
        });
        
        ValidationResponse errorResponse =             
        ValidationResponse.builder()
        .mensaje("Error de validación de los datos suministrados")
        .error(mapErrors) // Asigna el mapa de errores al mensaje
        .code("Exc-400-01")
        .object(ex.getClass().getSimpleName())
        .severity("ERROR")
        .url(webRequest.getDescription(false).replace("uri=", "")) // Asigna la URL
        .dateTime(LocalDateTime.now())
        .build(); // Crea la instancia
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // Devuelve 400 Bad Request. Se lanza cuando fallan las validaciones de los datos de entrada de una solicitud (por ejemplo, datos de un formulario o JSON).
    }
    
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest webRequest) {
        log.error("Exception Occured: " + ex.getClass().getSimpleName() + " - " + ex.getLocalizedMessage(), ex);

        return new ResponseEntity<>(
            ErrorResponse.builder()
                .message(ex.getMessage())
                .code(ex.getCode()) // Generalmente devuelve Exc-404-00. Código dinámico para cada excepción.
                .object(ex.getClass().getSimpleName()) // Opcional: Puedes incluir detalles adicionales en el objeto
                .severity("ERROR")
                .url(webRequest.getDescription(false).replace("uri=", "")) // Asigna la URL
                .dateTime(LocalDateTime.now())
                .build(),
            ex.getStatus() // Generalmente devuelve 400 Bad Requesst o 404 Not Found. HttpStatus dinámico para cada excepción.
        );
    }


    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFoundException(NoHandlerFoundException ex, WebRequest webRequest) {
        log.error("Exception Occured: " + ex.getClass().getSimpleName() + " - " + ex.getMessage(), ex);

        return new ResponseEntity<>(
            ErrorResponse.builder()
                .message(ex.getMessage())
                .code("Exc-404-05")
                .object(ex.getClass().getSimpleName()) // Opcional: Puedes incluir detalles adicionales en el objeto
                .severity("ERROR")
                .url(webRequest.getDescription(false).replace("uri=", "")) // Asigna la URL
                .dateTime(LocalDateTime.now())
                .build(),
            HttpStatus.NOT_FOUND // Devuelve 404 Not Found. Se lanza cuando no se encuentra un controlador para una solicitud. Devuelve 404 Not Found.
        );
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex, WebRequest webRequest) {
        log.error("Exception Occured: " + ex.getClass().getSimpleName() + " - " + ex.getMessage(), ex);

        return new ResponseEntity<>(
            ErrorResponse.builder()
                .message(ex.getMessage())
                .code("Exc-400-02")
                .object(ex.getClass().getSimpleName()) // Opcional: Puedes incluir detalles adicionales en el objeto
                .severity("INFO")
                .url(webRequest.getDescription(false).replace("uri=", "")) // Asigna la URL
                .dateTime(LocalDateTime.now())
                .build(),
            HttpStatus.BAD_REQUEST // Devuelve 400 Bad Request. Se lanza cuando fallan las validaciones de los datos de entrada de una solicitud (por ejemplo, datos de un formulario o JSON).
        );
    }
    

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest webRequest) {
        log.error("Exception Occured: " + ex.getClass().getSimpleName() + " - " + ex.getMessage(), ex);

        return new ResponseEntity<>(
            ErrorResponse.builder()
                .message(ex.getMessage())
                .code("Exc-409-01")
                .object(ex.getClass().getSimpleName()) // Opcional: Puedes incluir detalles adicionales en el objeto
                .severity("INFO")
                .url(webRequest.getDescription(false).replace("uri=", "")) // Asigna la URL
                .dateTime(LocalDateTime.now())
                .build(),
            HttpStatus.CONFLICT // Devuelve 409 Conflict. Se lanza cuando se viola una restricción de integridad de la base de datos (por ejemplo, clave primaria duplicada, violación de clave externa).
        );
    }

    
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest webRequest) {
        log.error("Exception Occured: " + ex.getClass().getSimpleName() + " - " + ex.getMessage(), ex);

        return new ResponseEntity<>(
            ErrorResponse.builder()
                .message(ex.getMessage())
                .code("Exc-404-06")
                .object(ex.getClass().getSimpleName()) // Opcional: Puedes incluir detalles adicionales en el objeto
                .severity("INFO")
                .url(webRequest.getDescription(false).replace("uri=", "")) // Asigna la URL
                .dateTime(LocalDateTime.now())
                .build(),
            HttpStatus.NOT_FOUND // Devuelve 404 Not Found. Se lanza cuando una consulta no devuelve ningún resultado y se esperaba al menos uno. Indica que el recurso no fue encontrado.
        );
    }

    
    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessResourceFailureException(DataAccessResourceFailureException ex, WebRequest webRequest) {
        log.error("Exception Occured: " + ex.getClass().getSimpleName() + " - " + ex.getMessage(), ex);

        return new ResponseEntity<>(
            ErrorResponse.builder()
                .message(ex.getMessage())
                .code("Exc-503-01")
                .severity("INFO")
                .url(webRequest.getDescription(false).replace("uri=", "")) // Asigna la URL
                .object(ex.getClass().getSimpleName()) // Opcional: Puedes incluir detalles adicionales en el objeto
                .dateTime(LocalDateTime.now())
                .build(),
            HttpStatus.SERVICE_UNAVAILABLE // Devuelve 503 Service Unavailable. Se lanza cuando hay un problema al acceder a un recurso de datos (por ejemplo, la base de datos). Indica que el servicio no está disponible temporalmente.
        );
    }
    
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest webRequest) {
        log.error("Exception Occured: " + ex.getClass().getSimpleName() + " - " + ex.getMessage(), ex);

        return new ResponseEntity<>(
            ErrorResponse.builder()
                .message(ex.getMessage())
                .code("Exc-503-01")
                .object(ex.getClass().getSimpleName()) // Opcional: Puedes incluir detalles adicionales en el objeto
                .severity("INFO")
                .url(webRequest.getDescription(false).replace("uri=", "")) // Asigna la URL
                .dateTime(LocalDateTime.now())
                .build(),
            HttpStatus.SERVICE_UNAVAILABLE // Devuelve 503 Service Unavailable. Se lanza cuando hay un problema al acceder a un recurso de datos (por ejemplo, la base de datos). Indica que el servicio no está disponible temporalmente.
        );
    }


    @ExceptionHandler(CannotCreateTransactionException.class)
    public ResponseEntity<ErrorResponse> handleCannotCreateTransactionException(CannotCreateTransactionException ex, WebRequest webRequest) {
        log.error("Exception Occured: " + ex.getClass().getSimpleName() + " - " + ex.getMessage(), ex);

        return new ResponseEntity<>(
            ErrorResponse.builder()
                .message(ex.getMessage())
                .code("Exc-503-01")
                .object(ex.getClass().getSimpleName()) // Opcional: Puedes incluir detalles adicionales en el objeto
                .severity("ERROR")
                .url(webRequest.getDescription(false).replace("uri=", "")) // Asigna la URL
                .dateTime(LocalDateTime.now())
                .build(),
            HttpStatus.SERVICE_UNAVAILABLE // Devuelve 500 Internal Server Error. Se utiliza para capturar cualquier excepción no manejada específicamente.
        );
    }


    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleMultipartException(MultipartException ex, WebRequest webRequest) {
        log.error("Exception Occured: " + ex.getClass().getSimpleName() + " - " + ex.getMessage(), ex);

        return new ResponseEntity<>(
            ErrorResponse.builder()
                .message(ex.getMessage())
                .code("Exc-400-01")
                .object(ex.getClass().getSimpleName()) // Opcional: Puedes incluir detalles adicionales en el objeto
                .severity("ERROR")
                .url(webRequest.getDescription(false).replace("uri=", "")) // Asigna la URL
                .dateTime(LocalDateTime.now())
                .build(),
            HttpStatus.BAD_REQUEST // Devuelve 500 Internal Server Error. Se utiliza para capturar cualquier excepción no manejada específicamente.
        );
    }

    
    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest webRequest) {
        log.error("Exception Occured: " + ex.getClass().getSimpleName() + " - " + ex.getMessage(), ex);

        return new ResponseEntity<>(
            ErrorResponse.builder()
                .message(ex.getMessage())
                .code("Exc-500-00")
                .severity("ERROR")
                .object(ex.getClass()) // Opcional: Puedes incluir detalles adicionales en el objeto
                .url(webRequest.getDescription(false).replace("uri=", "")) // Asigna la URL
                .dateTime(LocalDateTime.now())
                .build(),
            HttpStatus.INTERNAL_SERVER_ERROR // Devuelve 500 Internal Server Error. Se utiliza para capturar cualquier excepción no manejada específicamente.
        );
    }

}

