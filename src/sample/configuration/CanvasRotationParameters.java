package sample.configuration;

import lombok.Builder;
import lombok.Data;
import sample.graphical.entity.GraphicalPoint;

@Data
@Builder
public class CanvasRotationParameters {
    private double rotationDegrees;
    private GraphicalPoint rotationCenter;
}
