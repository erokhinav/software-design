package com.erokhina;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.erokhinav.actors.MasterActor;
import com.erokhinav.search.SearchEngineResult;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static akka.pattern.Patterns.ask;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class MasterActorTest {
    private static final Duration TIMEOUT_3 = Duration.ofMillis(1000);
    private static final Duration TIMEOUT_2 = Duration.ofMillis(600);

    private static ActorSystem system;
    private static Gson gson;

    @BeforeAll
    public static void setup() {
        system = ActorSystem.create();
        gson = new Gson();
    }

    @AfterAll
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testMakeSearchRequest() {
        new TestKit(system) {
            {
                final ActorRef master = system.actorOf(Props.create(MasterActor.class, TIMEOUT_3));

                Object response = ask(master,
                        "some request",
                        Duration.ofMinutes(1))
                        .toCompletableFuture()
                        .join();

                MasterActor.SearchResults results = (MasterActor.SearchResults) response;

                MasterActor.SearchResults expectedResult = getExpectedData();

                var got = results.getResults();
                var expected = expectedResult.getResults();
                assertEquals(got.size(), 3);
                assertTrue(got.size() == expected.size() &&
                        got.containsAll(expected) && got.containsAll(expected));
            }
        };
    }

    @Test
    public void testWithTimeout() {
        new TestKit(system) {
            {
                final ActorRef master = system.actorOf(Props.create(MasterActor.class, TIMEOUT_2));

                Object response = ask(master,
                        "some request",
                        Duration.ofMinutes(1))
                        .toCompletableFuture()
                        .join();

                MasterActor.SearchResults results = (MasterActor.SearchResults) response;

                assertEquals(results.getResults().size(), 2);
            }
        };
    }

    MasterActor.SearchResults getExpectedData() {
        return new MasterActor.SearchResults(List.of(
                getResult("google"),
                getResult("yandex"),
                getResult("bing")));
    }

    SearchEngineResult getResult(String engine) {
        return convertToResult(readContent(String.format("src/main/resources/search-result/%s.json", engine)));
    }

    SearchEngineResult convertToResult(String json) {
        return gson.fromJson(json, SearchEngineResult.class);
    }

    String readContent(String filename) {
        try {
            return new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
