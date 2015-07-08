package com.akudsoft.akka.controllers;

import akka.actor.ActorRef;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import com.akudsoft.akka.App;
import com.akudsoft.akka.SpringAppContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import scala.concurrent.Future;

import static com.akudsoft.akka.actors.PingActor.Ping;
import static com.akudsoft.akka.actors.PingActor.Pong;

@RestController
public class DefaultController {
    @RequestMapping("/ping")
    public DeferredResult<String> hello() {
        final DeferredResult<String> response = new DeferredResult<>();
        ActorRef pingActor = SpringAppContext.getActor(App.PING_ACTOR);

        final Future<Object> future = Patterns.ask(pingActor, Ping.getInstance(), App.DEFAULT_TIMEOUT);

        future.onSuccess(new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object result) throws Throwable {
                if (result instanceof Pong) {
                    response.setResult("PONG");
                } else {
                    response.setErrorResult("FAILURE");
                }
            }
        }, SpringAppContext.actorSystem().dispatcher());

        future.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable failure) throws Throwable {
                response.setErrorResult("FAILURE");
            }
        }, SpringAppContext.actorSystem().dispatcher());

        return response;
    }
}
