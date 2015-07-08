package com.akudsoft.akka;

import akka.actor.ActorSystem;
import akka.util.Timeout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import scala.concurrent.duration.Duration;

public class App {
    public static final String PING_ACTOR = "ping-actor";
    public static final String SYSTEM_NAME = "akka";
    public static final Timeout DEFAULT_TIMEOUT = new Timeout(Duration.create(5, "seconds"));

    private static final Log LOG = LogFactory.getLog(App.class);

    private static void shutdown() {
        final ActorSystem actorSystem = SpringAppContext.actorSystem();
        final ConfigurableApplicationContext context = SpringAppContext.getContext();

        if (actorSystem != null) actorSystem.shutdown();
        if (context != null) context.close();
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(App::shutdown));

        new SpringApplicationBuilder()
                .showBanner(false)
                .sources(new Object[]{AppConfig.class})
                .run(args);

        LOG.info("System started...");
    }
}
