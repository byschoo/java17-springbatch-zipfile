package com.byschoo.csv_processing_batch.Exceptions;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) // Este parámetro indica que la generación de los métodos equals() y hashCode() debe incluir los campos de la superclase (si la hay).
public class ResourceNotFoundException extends RuntimeException{

    private String code; // Código dinámico específico para cada una de las excepciones
    private HttpStatus status; // Estatus dinámico específico para cada una de las excepciones

    public ResourceNotFoundException(String message, String code, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public ResourceNotFoundException(String message, String code, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = status;
    }
}
