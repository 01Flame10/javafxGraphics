package sample.configuration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CanvasParametersWrapper {

    private CanvasScaleParameters scaleParameters;
    private CanvasRotationParameters rotationParameters;

}
