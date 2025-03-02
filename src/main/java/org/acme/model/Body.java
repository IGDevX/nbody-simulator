package org.acme.model;

import lombok.Data;

@Data
public class Body {
    Double x,y;
    Double vx, vy;
    Double mass;
    Double dt = 0.5;

    public void updatePosition() {
        this.x += this.vx * dt;
        this.y += this.vy * dt;
    }

    public void updateVelocity(Double ax, Double ay) {
        this.vx += ax * dt;
        this.vy += ay * dt;
    }
}
