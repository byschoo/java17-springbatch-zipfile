package com.byschoo.csv_processing_batch.Logs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.boot.logging.structured.StructuredLogFormatter;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class FormatKeyValueLogger implements StructuredLogFormatter<ILoggingEvent> {

    private final SimpleDateFormat sdf; // Formateador de fecha

    public FormatKeyValueLogger() {
        // 1. Inicialización del formateador de fecha
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        // 2. Establecimiento de la zona horaria a la del sistema
        sdf.setTimeZone(TimeZone.getDefault());
    }

    @Override
    public String format(ILoggingEvent event) {
        // 3. Obtención de la marca de tiempo del evento de log
        long timestamp = event.getTimeStamp();
        // 4. Creación de un objeto Date a partir de la marca de tiempo
        Date date = new Date(timestamp);
        // 5. Formateo de la fecha
        String formattedTime = sdf.format(date);

        // 6. Construcción de la cadena de log formateada
        return formattedTime + " | " + event.getLevel() + " | " + event.getMessage() + "\n";
    }
}