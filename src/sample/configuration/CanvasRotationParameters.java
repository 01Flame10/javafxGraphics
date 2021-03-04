package sample.configuration;

import lombok.Builder;
import lombok.Data;
import sample.graphical.entity.PointHolder;

@Data
@Builder
public class CanvasRotationParameters {
    private double rotationDegrees;
    private PointHolder rotationCenter;
}
