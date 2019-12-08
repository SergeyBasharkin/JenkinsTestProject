package ru.alta.svd.client.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.context.annotation.*;
import ru.alta.svd.client.core.service.api.EncryptorService;
import ru.alta.svd.client.core.service.impl.DesEncryptorService;
import ru.alta.svd.transport.protocol.config.SvdTransportProtocolConfig;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author pechenko
 */
 
@Configuration
@ComponentScan(basePackages = {
    "ru.alta.svd.client.core.route",
    "ru.alta.svd.client.core.service",
    "ru.alta.svd.client.core.listeners",
    "ru.alta.svd.client.core.dao"
})
@PropertySources({
        @PropertySource("classpath:application-protocol.properties"),
})
@Import(SvdTransportProtocolConfig.class)
public class SvdClientCoreConfig {


    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        return mapper;
    }

    @Bean
    public CamelContextConfiguration contextConfiguration(){
        return new CamelContextConfiguration() {
            @Override
            public void beforeApplicationStart(CamelContext camelContext) {
                camelContext.getShutdownStrategy().setTimeout(3);
                camelContext.getShutdownStrategy().setTimeUnit(TimeUnit.SECONDS);
            }

            @Override
            public void afterApplicationStart(CamelContext camelContext) {

            }
        };
    }
    @Bean
    public EncryptorService encryptorService(){
        return new DesEncryptorService("AltaJhttpPassphrase1");
    }
}
