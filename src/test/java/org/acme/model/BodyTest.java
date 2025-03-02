package org.acme.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BodyTest {
    @Test
    void testBodyMovement() {
        Body body = new Body(1.0, new Vector2D(0, 0), new Vector2D(1, 0));
        body.update(1.0);  // Update for 1 second

        assertEquals(1.0, body.getPosition().getX(), 0.001);
        assertEquals(0.0, body.getPosition().getY(), 0.001);
    }

}