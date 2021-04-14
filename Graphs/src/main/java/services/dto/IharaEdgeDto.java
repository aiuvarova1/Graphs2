package services.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IharaEdgeDto {
    private int[][] edgeMatrix;
    private String edgeOrder;
}
