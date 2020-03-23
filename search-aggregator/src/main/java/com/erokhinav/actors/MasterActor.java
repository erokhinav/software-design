package com.erokhinav.actors;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.erokhinav.search.SearchEngineResult;
import com.erokhinav.search.StubSearchClient;

public class MasterActor extends AbstractActor {
    private List<SearchEngineResult> results;
    private ActorRef mainSender;
    private Duration timeout;
    private final List<String> SEARCH_ENGINES = List.of("google", "yandex", "bing");
    private static final TimeoutMessage TIMOUT_MSG = new TimeoutMessage();

    MasterActor(Duration timeout) {
        results = new ArrayList<>();
        this.timeout = timeout;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, msg -> {
                    mainSender = getSender();

                    SEARCH_ENGINES.forEach(searchEngine ->
                            createChildActor(searchEngine, msg));

                    context().system()
                            .scheduler()
                            .scheduleOnce(
                                    timeout,
                                    () -> self().tell(TIMOUT_MSG, ActorRef.noSender()),
                                    context().system().dispatcher());
                })
                .match(TimeoutMessage.class, msg -> {
                    sendResult();
                })
                .match(SearchEngineResult.class, msg -> {
                    this.results.add(msg);

                    if (this.results.size() == SEARCH_ENGINES.size()) {
                        sendResult();
                    }
                })
                .build();
    }

    void createChildActor(String searchEngine, String request) {
        getContext()
                .actorOf(
                        Props.create(ChildActor.class, new StubSearchClient(searchEngine)),
                        searchEngine
                ).tell(request, self());
    }

    void sendResult() {
        mainSender.tell(new SearchResults(results), self());
        getContext().stop(this.self());
    }

    public static class SearchResults {
        List<SearchEngineResult> results;

        public SearchResults(List<SearchEngineResult> results) {
            this.results = results;
        }

        public List<SearchEngineResult> getResults() {
            return results;
        }
    }

    private static final class TimeoutMessage {
    }
}
