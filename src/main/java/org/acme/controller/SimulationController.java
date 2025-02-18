package org.acme.controller;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/simulation")
public class SimulationController {
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("New session opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);
        broadcast(message, session);
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