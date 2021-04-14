package services.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IharaPathDto {
    private String[][] edgeMatrix;
    private String edgeOrder;
    private String spanningTree;
    private String notSpanningTree;
}
