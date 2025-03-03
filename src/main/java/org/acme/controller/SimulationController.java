package org.acme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.quarkus.runtime.StartupEvent;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.acme.model.Body;
import org.acme.service.SimulationService;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@ServerEndpoint("/simulation")
public class SimulationController {

    @Inject
    ManagedExecutor executor;

    private static final Map<Session, SimulationService> sessionStates = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    private static final Gson gson = new GsonBuilder().create();

    @OnOpen
    public void onOpen(Session session) {
        sessionStates.put(session, new SimulationService());
        StartupEvent ev = new StartupEvent();
        sessionStates.get(session).onStart(ev);
        startSimulationForSession(session);
        System.out.println("New session opened: " + session.getId());
    }

    private void startSimulationForSession(Session session) {
        System.out.println("Starting simulation for session: " + session.getId());
        scheduledExecutor.scheduleAtFixedRate(() -> updateAndSend(session), 0, 10, TimeUnit.MILLISECONDS);
    }

    private void updateAndSend(Session session) {
        SimulationService state = sessionStates.get(session);
        if (state == null || !session.isOpen()) return;

        var updatedBodies =  state.step(); // Update simulation step for this session

        String jsonResponse = gson.toJson(updatedBodies); // Convert session-specific data to JSON
        sendMessage(session, jsonResponse);
    }

    private void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
            System.out.println("Sent message: " + message);
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }


    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received: " + message);

        JsonObject jsonMessage = JsonParser.parseString(message).getAsJsonObject();

        if (jsonMessage.has("remove")) {
            String bodyId = jsonMessage.get("remove").getAsString();
            SimulationService simulationService = sessionStates.get(session);
            simulationService.removeBodyByID(bodyId);
            System.out.println("Removed body with id: " + bodyId);
        } else {
            Body data = new Gson().fromJson(message, Body.class);
            SimulationService simulationService = sessionStates.get(session);
            simulationService.addBody(data);
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessionStates.remove(session);
        System.out.println("Session closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error in session " + session.getId() + ": " + throwable.getMessage());
    }

}