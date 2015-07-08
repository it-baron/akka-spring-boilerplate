package com.akudsoft.akka.actors;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.akudsoft.akka.App;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static akka.cluster.ClusterEvent.*;

@Named(App.CLUSTER_LISTENER)
@Scope("prototype")
public class ClusterListener extends UntypedActor {
    final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    final Cluster cluster = Cluster.get(getContext().system());

    final List<ActorRef> backends = new ArrayList<ActorRef>();

    public static class RegistrationSuccess implements Serializable {
    }

    //subscribe to cluster changes
    @Override
    public void preStart() throws Exception {
        cluster.subscribe(self(), initialStateAsEvents(), MemberEvent.class, UnreachableMember.class);
    }

    //re-subscribe when restart
    @Override
    public void postStop() throws Exception {
        cluster.unsubscribe(self());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof MemberUp) {
            final MemberUp mUp = (MemberUp) message;

            log.info("MemberUp: {}", mUp);
            log.info("Member is Up: {}", mUp.member());

            register(mUp.member());

        } else if (message instanceof ReachableMember) {
            final ReachableMember mReachable = (ReachableMember) message;

            log.info("ReachableMember: {}", mReachable);
            log.info("Member detected as reachable: {}", mReachable.member());

        } else if (message instanceof UnreachableMember) {
            final UnreachableMember mUnreachable = (UnreachableMember) message;

            log.info("UnreachableMember: {}", mUnreachable);
            log.info("Member detected as unreachable: {}", mUnreachable.member());

        } else if (message instanceof MemberRemoved) {
            final MemberRemoved mRemoved = (MemberRemoved) message;

            log.info("MemberRemoved: {}", mRemoved);
            log.info("Member is Removed: {}", mRemoved.member());

        } else if (message instanceof MemberEvent) {
            final MemberEvent mEvent = (MemberEvent) message;

            log.info("MemberEvent: {}", mEvent);
            log.info("Member has an event: {}", mEvent.member());

        } else if (message instanceof CurrentClusterState) {
            final CurrentClusterState state = (CurrentClusterState) message;
            for (Member member : state.getMembers()) {
                if (member.status().equals(MemberStatus.up())) {
                    register(member);
                }
            }

        } else if (message instanceof RegistrationSuccess) {
            final RegistrationSuccess registrationSuccess = (RegistrationSuccess) message;
            log.info("RegistrationSuccess: {}", registrationSuccess);

            context().watch(sender());
            backends.add(sender());

        } else if (message instanceof Terminated) {
            final Terminated terminated = (Terminated) message;
            log.info("Terminated: {}", terminated);
            log.info("Member is terminated: {}", terminated.actor());
            backends.remove(terminated.actor());

        } else {
            unhandled(message);
        }
    }

    private void register(Member member) {
        log.info("Registering member: {}", member);
        context().actorSelection(member.address() + "/" + App.CLUSTER_LISTENER).tell(new RegistrationSuccess(), self());
    }
}
