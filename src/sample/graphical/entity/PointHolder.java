package sample.graphical.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PointHolder {
    protected double x;
    protected double y;
}
