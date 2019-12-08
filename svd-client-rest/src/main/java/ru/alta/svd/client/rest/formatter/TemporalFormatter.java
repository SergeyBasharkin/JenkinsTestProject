package ru.alta.svd.client.rest.formatter;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Locale;

public class TemporalFormatter implements Formatter<Temporal> {

    @Override
    public Temporal parse(String text, Locale locale) {
        try {
            return LocalDateTime.from(DateTimeFormatter.ofPattern("dd.MM.yyyy'T'HH:mm").parse(text));
        }catch (DateTimeParseException ignore){
            return LocalDate.from(DateTimeFormatter.ofPattern("dd.MM.yyyy").parse(text));
        }
    }


    @Override
    public String print(Temporal object, Locale locale) {
        return object.toString();
    }
}
