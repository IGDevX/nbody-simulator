package org.acme.service;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.Getter;
import org.acme.interf.SimulationInterface;
import org.acme.model.Body;
import org.acme.model.Vector2D;

import java.util.ArrayList;
import java.util.List;


@Getter
@ApplicationScoped
public class SimulationService implements SimulationInterface {

    private static final double G = 6.67430e-11; // Gravitational constant
    private List<Body> bodies;

    public void onStart(@Observes StartupEvent ev) {
        bodies = new ArrayList<>();
    }

    public void onStop(@Observes ShutdownEvent ev) {
        bodies.clear();
    }

    @Override
    public void addBody(Body body) {
        bodies.add(body);
    }

    public void update(double deltaTime) {
        List<Vector2D> forces = new ArrayList<>();

        // Compute forces for each body
        for (Body body : bodies) {
            forces.add(computeNetForce(body));
        }

        // Apply forces and update positions
        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).applyForce(forces.get(i), deltaTime);
        }
    }

    private Vector2D computeNetForce(Body body) {
        Vector2D netForce = new Vector2D(0, 0);
        for (Body other : bodies) {
            if (body != other) {
                netForce = netForce.add(computeGravitationalForce(body, other));
            }
        }
        System.out.println("Net Force on " + body + ": " + netForce);
        return netForce;
    }

    private Vector2D computeGravitationalForce(Body a, Body b) {
        Vector2D direction = b.getPosition().subtract(a.getPosition());
        double distance = direction.magnitude();
        double forceMagnitude = (G * a.getMass() * b.getMass()) / (distance * distance);
        return direction.normalize().multiply(forceMagnitude);
    }

    @Override
    public void removeBody(Body body) {
        // TODO
    }

    @Override
    public List<Body> step() {
        return bodies;
    }
}
