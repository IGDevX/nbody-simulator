package org.acme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.StartupEvent;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.acme.model.Body;
import org.acme.service.SimulationService;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@ServerEndpoint("/simulation")
public class SimulationController {

    private static final Gson gson = new GsonBuilder().create();

    @Inject
    ManagedExecutor executor;  // Injects a context-aware executor

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

    static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();
    private SimulationService simulator = new SimulationService();

    private Session lastSession;

    @OnOpen
    public void onOpen(Session session) {
        // Add the session to the set of active sessions
        sessions.add(session);
        lastSession = session;
        System.out.println("New session opened: " + session.getId());

        initializeBodies();  // Method to add initial bodies to the simulation

        // Start the simulation updates once a client connects
        scheduledExecutor.scheduleAtFixedRate(this::updateAndBroadcast, 0, 100, TimeUnit.MILLISECONDS); // Update every 100 ms
    }

    // Method to initialize bodies
    private void initializeBodies() {
        // Initialize two bodies with example values
        Body body1 = new Body(1.0, new Vector2D(0.0, 0.0), new Vector2D(1.0, 1.0));
        Body body2 = new Body(1.0, new Vector2D(1.0, 1.0), new Vector2D(-1.0, -1.0));
        simulator.onStart(new StartupEvent());
        simulator.addBody(body1);
        simulator.addBody(body2);
    }

    // Simulation update and broadcasting
    private void updateAndBroadcast() {
        double deltaTime = 0.1; // For example, use a constant time step or calculate deltaTime

        // Run the simulation update
        simulator.update(deltaTime);

        String jsonBodies = gson.toJson(simulator.getBodies());

        // Broadcast updated body states to clients
        broadcast(jsonBodies, lastSession);  // Send the updated positions/velocities as a JSON string
    }


    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Message received: " + message);
        simulator.update(0.1);

        // Offload the task to another thread to send the updated simulation state
        executor.execute(() -> {
            try {
                // Convert the bodies list to JSON string using Jackson
                String bodiesJson = objectMapper.writeValueAsString(simulator.getBodies());
                session.getBasicRemote().sendText(bodiesJson);  // Send the JSON to WebSocket client
            } catch (Exception e) {
                System.err.println("Error sending message: " + e.getMessage());
            }
        });
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Session closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error in session " + session.getId() + ": " + throwable.getMessage());
    }

    private void broadcast(String message, Session senderSession) {
        synchronized (sessions) {
            for (Session session : sessions) {
                if (session.isOpen() && !session.equals(senderSession)) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (Exception e) {
                        System.err.println("Error sending message: " + e.getMessage());
                    }
                }
            }
        }
    }
}