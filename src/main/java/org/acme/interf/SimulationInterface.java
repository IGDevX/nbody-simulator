package org.acme.interf;

import org.acme.model.Body;

import java.util.List;

public interface SimulationInterface {
    void addBody(Double x, Double y, Double vx, Double vy, Double mass, Boolean fixed);

    void removeBody(Body body);

    List<Body> step();
}
