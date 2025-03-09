package com.byschoo.csv_processing_batch.Exceptions.Response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Indica al serializador JSON que ignore los campos con valores null
public class SuccessResponse implements Serializable {

    private String mensaje;

    @JsonProperty("persona") // Especifica el nombre del campo en el JSON
    private Object object; // Mantén el nombre interno "object" si es necesario
}
