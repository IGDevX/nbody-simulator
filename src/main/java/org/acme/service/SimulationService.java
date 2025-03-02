package org.acme.service;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.acme.interf.SimulationInterface;
import org.acme.model.Body;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@ApplicationScoped
public class SimulationService implements SimulationInterface {

    private List<Body> bodies;

    private static final double G = 6.67430e-2;

    public void onStart(@Observes StartupEvent ev) {
        bodies = new ArrayList<>();
    }

    void onStop(@Observes ShutdownEvent ev) {
        bodies.clear();
    }

    @Override
    public void addBody(Body body) {
        if(body.getMass() == null || body.getX() == null || body.getY() == null || body.getVx() == null || body.getVy() == null) {
            return;
        }
        bodies.add(body);
    }

    @Override
    public void removeBody(Body body) {
        bodies.remove(body);
    }

    @Override
    public List<Body> step() {
        var forces = computeForces();
        Iterator<Body> iterator = bodies.iterator();
        int index = 0; // Track index manually

        while (iterator.hasNext()) {
            Body body = iterator.next();

            // Prevent IndexOutOfBounds if a body was removed
            if (index >= forces.size()) break;

            System.out.println("Before forces"+body.getX()+" "+body.getY());

            // Apply force from precomputed list
            body.updateVelocity(forces.get(index)[0], forces.get(index)[1]);
            body.updatePosition();
            System.out.println("After forces"+body.getX()+" "+body.getY());

            // Remove bodies if they go out of bounds
            if (body.getX() > 810 || body.getY() > 610 || body.getX() < 0 || body.getY() < 0) {
                iterator.remove();
            } else {
                index++; // Only increment if body is not removed
            }
        }

        return bodies;
    }


    private List<Double[]> computeForces() {
        List<Double[]> forces = new ArrayList<>();
        for (int i = 0; i < bodies.size(); i++) {
            forces.add(new Double[]{0.0, 0.0});
        }
        for (int i = 0; i < bodies.size(); i++) {
            for (int j = i + 1; j < bodies.size(); j++) {
                double dx = bodies.get(j).getX() - bodies.get(i).getX();
                double dy = bodies.get(j).getY() - bodies.get(i).getY();
                double distSq = dx * dx + dy * dy;

                if (distSq < 1e-6) {
                    continue; // Skip force calculation if bodies overlap
                }

                double dist = Math.sqrt(distSq) + 0.1; // Avoid division by zero
                double force = (G * bodies.get(i).getMass() * bodies.get(j).getMass()) / distSq;

                double ax = (force * dx / dist) / bodies.get(i).getMass();
                double ay = (force * dy / dist) / bodies.get(i).getMass();
                double bx = (force * -dx / dist) / bodies.get(j).getMass();
                double by = (force * -dy / dist) / bodies.get(j).getMass();

                forces.get(i)[0] += ax;
                forces.get(i)[1] += ay;
                forces.get(j)[0] += bx;
                forces.get(j)[1] += by;
            }
        }
        return forces;
    }
}
