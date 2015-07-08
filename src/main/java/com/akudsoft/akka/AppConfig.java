package com.akudsoft.akka;

import akka.actor.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.akudsoft.akka",
        "com.akudsoft.akka.actors",
        "com.akudsoft.akka.controllers"
})
public class AppConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ClientHttpRequestFactory getClientRequestFactory() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(120000);
        factory.setConnectTimeout(10000); // 10 sec
        return factory;
    }

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = ActorSystem.create(App.SYSTEM_NAME);
        SpringExtension.SpringExtProvider.get(system).initialize(applicationContext);
        return system;
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return container -> {
            ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401.html");
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
            ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
            ErrorPage error505Page = new ErrorPage(HttpStatus.HTTP_VERSION_NOT_SUPPORTED, "/505.html");
            ErrorPage error506Page = new ErrorPage(HttpStatus.METHOD_NOT_ALLOWED, "/405.html");
            container.addErrorPages(error401Page, error404Page, error500Page, error505Page, error506Page);
        };
    }
}
