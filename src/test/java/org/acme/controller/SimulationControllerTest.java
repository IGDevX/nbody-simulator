package org.acme.controller;

import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.mockito.Mockito.*;

class SimulationControllerTest {

    private SimulationController controller;
    private Session mockSession;

    @BeforeEach
    void setUp() {
        controller = new SimulationController();
        mockSession = mock(Session.class);
        RemoteEndpoint.Basic mockRemote = mock(RemoteEndpoint.Basic.class);

        when(mockSession.getId()).thenReturn("123");
        when(mockSession.getBasicRemote()).thenReturn(mockRemote);
    }

    @Test
    void testOnOpen() {
        controller.onOpen(mockSession);

        // Verify the session was added
        assert SimulationController.sessions.contains(mockSession);
        verify(mockSession, times(1)).getId();
    }

    @Test
    void testOnMessageBroadcast() throws IOException {
        controller.onOpen(mockSession);

        // Simulate another session
        Session mockSession2 = mock(Session.class);
        when(mockSession2.isOpen()).thenReturn(true);
        RemoteEndpoint.Basic mockRemote2 = mock(RemoteEndpoint.Basic.class);
        when(mockSession2.getBasicRemote()).thenReturn(mockRemote2);

        controller.onOpen(mockSession2);
        controller.onMessage("Hello, world!", mockSession);

        // Verify that the second session receives the message
        verify(mockRemote2, times(1)).sendText("Hello, world!");
    }

    @Test
    void testOnClose() {
        controller.onOpen(mockSession);
        controller.onClose(mockSession);

        // Verify session is removed
        assert !SimulationController.sessions.contains(mockSession);
    }

    @Test
    void testOnError() {
        Throwable mockError = new RuntimeException("Test error");
        controller.onError(mockSession, mockError);

        verify(mockSession, times(1)).getId();
    }
}
