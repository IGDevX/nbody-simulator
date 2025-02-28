package org.acme.model;

import lombok.Data;
import lombok.Getter;

@Data
public class Body {
    @Getter
    private double mass;
    @Getter
    private Vector2D position;
    private Vector2D velocity;

    public Body(double mass, Vector2D position, Vector2D velocity) {
        this.mass = mass;
        this.position = position;
        this.velocity = velocity;
    }

    public void update(double deltaTime) {
        position = position.add(velocity.multiply(deltaTime));
    }

    public void applyForce(Vector2D force, double deltaTime) {
        Vector2D acceleration = force.scale(1.0 / mass);
        velocity = velocity.add(acceleration.scale(deltaTime));
        position = position.add(velocity.scale(deltaTime));
        System.out.println("New position: " + position + ", velocity: " + velocity);
    }

}
