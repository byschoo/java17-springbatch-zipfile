package com.byschoo.csv_processing_batch.Exceptions.Response;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationResponse {

    private String mensaje; // Mensaje general de error (opcional)
    private Map<String, String> error; // Mapa con los errores de validación específicos
    private Object object;
    private String code;
    private String severity;
    private String url;
    private final LocalDateTime dateTime;
}
