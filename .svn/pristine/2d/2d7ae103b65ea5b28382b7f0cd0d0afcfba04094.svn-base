package ru.alta.svd.client.rest.formatter;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Locale;

import static org.junit.Assert.*;

public class TemporalFormatterTest {
    TemporalFormatter temporalFormatter = new TemporalFormatter();

    @Test
    public void parse() {
        Temporal temporal = temporalFormatter.parse("27.01.1996T00:00", Locale.ROOT);
        assertNotNull(temporal);
    }

    @Test
    public void parseDate() {
        Temporal temporal = temporalFormatter.parse("27.01.1996", Locale.ROOT);
        assertNotNull(temporal);
    }

    @Test
    public void print() {
        assertNotNull(temporalFormatter.print(temporalFormatter.parse("27.01.1996", Locale.ROOT), Locale.ROOT));
    }
}