package org.acme.interf;

import org.acme.model.Body;

import java.util.List;

public interface SimulationInterface {
    void addBody(Body body);

    void removeBody(Body body);

    List<Body> step();
}
