package com.byschoo.csv_processing_batch.Logs;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.boot.logging.structured.StructuredLogFormatter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class FormatJsonLogger implements StructuredLogFormatter<ILoggingEvent> {

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm:ss.SSS"); // Formateador de fecha
    private final String applicationName;                                              // Nombre de la aplicación
    private final String applicationVersion;                                           // Versión de la aplicación
    private String hostname;                                                          // Nombre del host
    private String ipAddress;                                                         // Dirección IP

    public FormatJsonLogger() {
        // 1. Obtención del nombre y la versión de la aplicación
        this.applicationName = "My_Aplicacion"; // Reemplaza con tu lógica
        this.applicationVersion = "My_Version_Aplicacion"; // Reemplaza con tu lógica

        // 2. Obtención del nombre del host y la dirección IP
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            this.hostname = inetAddress.getHostName();
            this.ipAddress = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            // 3. Manejo de excepción si no se puede obtener el nombre del host o la dirección IP
            this.hostname = "unknown";
            this.ipAddress = "unknown";
        }
    }

    @Override
    public String format(ILoggingEvent event) {
        // 4. Creación de ObjectMapper y JsonGenerator
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); // use the configure method
        StringWriter sw = new StringWriter();
        JsonGenerator jsonGenerator;

        try {
            // 5. Inicio de la generación de JSON
            jsonGenerator = new JsonFactory().createGenerator(sw);
            jsonGenerator.writeStartObject();

            // 6. Escritura de campos de log
            jsonGenerator.writeStringField("Timestand", sdf.format(new Date(event.getTimeStamp())));
            jsonGenerator.writeStringField("Level", event.getLevel().toString());
            jsonGenerator.writeStringField("Thread", event.getThreadName());
            jsonGenerator.writeStringField("Message", event.getFormattedMessage());

            // 7. Escritura de información de la aplicación
            jsonGenerator.writeObjectFieldStart("application");
            jsonGenerator.writeStringField("name", applicationName);
            jsonGenerator.writeStringField("version", applicationVersion);
            jsonGenerator.writeEndObject();

            // 8. Escritura de información del nodo
            jsonGenerator.writeObjectFieldStart("node");
            jsonGenerator.writeStringField("hostname", hostname);
            jsonGenerator.writeStringField("ip", ipAddress);
            jsonGenerator.writeEndObject();

            // 9. Fin de la generación de JSON
            jsonGenerator.writeEndObject();
            jsonGenerator.close();
        } catch (IOException e) {
            // 10. Manejo de excepción si ocurre un error durante la generación de JSON
            return "Error formatting log event: " + e.getMessage();
        }

        // 11. Devolución de la cadena JSON formateada
        return sw.toString() + "\n";
    }
}