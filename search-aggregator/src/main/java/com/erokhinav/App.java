package com.erokhinav;

import java.time.Duration;
import java.util.Scanner;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.erokhinav.actors.MasterActor;
import com.erokhinav.search.SearchEngineResult;

import static akka.pattern.Patterns.ask;

public class App {
    private static final String EXIT_MSG = "exit()";
    private static final Duration TIMEOUT = Duration.ofMillis(1000);

    public static void main(String[] args) {
        final var scanner = new Scanner(System.in);
        ActorSystem system = ActorSystem.create("system");


        while (true) {
            System.out.print("Search: ");
            final var request = scanner.nextLine();

            if (request.equals(EXIT_MSG)) {
                break;
            }

            ActorRef master = system.actorOf(Props.create(MasterActor.class, TIMEOUT), "master");

            Object response = ask(master,
                    request,
                    Duration.ofMinutes(1))
                    .toCompletableFuture()
                    .join();

            MasterActor.SearchResults results = (MasterActor.SearchResults) response;
            printSearchResults(results);
        }
    }

    private static void printSearchResults(MasterActor.SearchResults results) {
        for (SearchEngineResult result : results.getResults()) {
            System.out.println("  Results from '" + result.getEngineName() + "'");
            for (int i = 0; i < result.getUrls().size(); i++) {
                System.out.format("    #%d: \n", i);
                System.out.println("      Title: " + result.getUrls().get(i).getTitle());
                System.out.println("      URL: " + result.getUrls().get(i).getUrl());
            }
        }
    }
}
