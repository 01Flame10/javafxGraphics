package sample.graphical.entity;

import lombok.Builder;
import lombok.Data;
import sample.graphical.GraphicalObject;

@Data
@Builder
public class GraphicalPoint extends GraphicalObject {
    private int x;
    private int y;
}
