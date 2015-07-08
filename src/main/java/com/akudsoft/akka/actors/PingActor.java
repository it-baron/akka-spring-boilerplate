package com.akudsoft.akka.actors;

import akka.actor.UntypedActor;
import com.akudsoft.akka.App;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.io.Serializable;

@Named(App.PING_ACTOR)
@Scope("prototype")
public class PingActor extends UntypedActor {
    public static class Ping implements Serializable {
        private Ping() {
        }

        private static Ping instance = new Ping();

        public static Ping getInstance() {
            return instance;
        }
    }

    public static class Pong implements Serializable {
        private Pong() {
        }

        private static Pong instance = new Pong();

        public static Pong getInstance() {
            return instance;
        }
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof Ping) {
            sender().tell(Pong.getInstance(), self());
            context().stop(self());
        } else {
            unhandled(msg);
        }
    }
}
