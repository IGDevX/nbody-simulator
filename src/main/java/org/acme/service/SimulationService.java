package org.acme.service;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.acme.interf.SimulationInterface;
import org.acme.model.Body;

import java.util.ArrayList;
import java.util.List;


@ApplicationScoped
public class SimulationService implements SimulationInterface {

    private List<Body> bodies;

    void onStart(@Observes StartupEvent ev) {
        bodies = new ArrayList<>();
    }

    void onStop(@Observes ShutdownEvent ev) {
        bodies.clear();
    }

    @Override
    public void addBody(Double x, Double y, Double vx, Double vy, Double mass, Boolean fixed) {
        // TODO
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
