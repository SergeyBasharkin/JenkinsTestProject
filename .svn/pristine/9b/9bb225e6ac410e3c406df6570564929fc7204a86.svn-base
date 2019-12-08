package ru.alta.svd.client.rest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.alta.svd.client.rest.formatter.TemporalFormatter;

@Configuration
public class FormatterConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new TemporalFormatter());
    }
}
