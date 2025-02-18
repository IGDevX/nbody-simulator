package org.acme.model;

import lombok.Data;

@Data
public class Body {
    Double x,y;
    Double vx, vy;
    Double mass;
    Boolean fixed;
}
