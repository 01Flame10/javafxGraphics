package sample.configuration;

import lombok.Builder;
import lombok.Data;
import sample.graphical.entity.PointHolder;

@Data
@Builder
public class CanvasPositionParameters {
    private PointHolder offset;
    private PointHolder start;
}
