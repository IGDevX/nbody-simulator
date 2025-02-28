package org.acme.service;

import io.quarkus.runtime.StartupEvent;
import org.acme.model.Body;
import org.acme.model.Vector2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulatorTest {

    @Test
    void testGravitationalAttraction() {
        SimulationService simulator = new SimulationService();
        Body body1 = new Body(5.0, new Vector2D(0, 0), new Vector2D(0, 0));
        Body body2 = new Body(5.0, new Vector2D(10, 0), new Vector2D(0, 0));
        simulator.onStart(new StartupEvent());
        simulator.addBody(body1);
        simulator.addBody(body2);

        simulator.update(1.0);  // Simulate 1 second

        assertTrue(body1.getPosition().getX() > 0);
        assertTrue(body2.getPosition().getX() < 10);
    }
}