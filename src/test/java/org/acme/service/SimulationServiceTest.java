package org.acme.service;

import org.acme.model.Body;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulationServiceTest {

    private final SimulationService simulationService = new SimulationService();

    private Body mockBody;

    @BeforeEach
    void setUp() {
        simulationService.onStart(null);

        mockBody = new Body();
        mockBody.setX(1.0);
        mockBody.setY(2.0);
        mockBody.setVx(0.5);
        mockBody.setVy(0.5);
        mockBody.setMass(10.0);
        mockBody.setFixed(false);
    }

    @Test
    void addBody() {
        simulationService.addBody(mockBody);

        assertEquals(1, simulationService.step().size());
    }

    @Test
    void addEmptyBody() {
        simulationService.addBody(new Body());

        assertTrue(simulationService.step().isEmpty());
    }

    @Test
    void removeFirstBody() {
        simulationService.addBody(mockBody);
        Body bodyToRemove = simulationService.step().getFirst();

        simulationService.removeBody(bodyToRemove);

        assertEquals(0, simulationService.step().size());
    }

    @Test
    void removeNonExistingBody() {
        simulationService.addBody(mockBody);

        simulationService.removeBody(new Body());

        assertEquals(1, simulationService.step().size());
    }

    @Test
    void stepEmptyBodies() {
        List<Body> result = simulationService.step();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void stepBodyUpdated() {
        Double x = mockBody.getX();
        Double y = mockBody.getY();

        simulationService.addBody(mockBody);

        List<Body> result = simulationService.step();

        assertNotEquals(x, result.getFirst().getX(), 0.0);
        assertNotEquals(y, result.getFirst().getY(), 0.0);
    }
}
