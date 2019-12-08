package ru.alta.svd.protocol.test.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import javax.annotation.Resource;

@SpringBootApplication
@ComponentScan(basePackages = {
    "ru.alta.svd.protocol.test.controller"
})
@PropertySources({
		@PropertySource("classpath:application-test-server.properties"),
})
public class SvdProtocolTestApplication {

	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer(){
		return container ->{
			if (container instanceof TomcatEmbeddedServletContainerFactory){
				TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory = (TomcatEmbeddedServletContainerFactory) container;
				tomcatEmbeddedServletContainerFactory.addConnectorCustomizers(connector -> connector.setMaxPostSize(1000_000_000));
			}
		};
	}
	public static void main(String[] args) {
		SpringApplication.run(SvdProtocolTestApplication.class, args);
	}
}
