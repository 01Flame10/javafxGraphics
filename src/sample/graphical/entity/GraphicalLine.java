package sample.graphical.entity;

import lombok.Builder;
import lombok.Data;
import sample.graphical.GraphicalObject;

@Data
@Builder
public class GraphicalLine extends GraphicalObject {
    private GraphicalPoint startPoint;
    private GraphicalPoint endPoint;
}
