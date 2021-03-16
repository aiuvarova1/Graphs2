package services.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IharaDto {
    private int[][] A;
    private String[][] W;
    private int[][] Q;
    //r - 1 = |E| - |V|
    private int rm1;
    private boolean weighted;
}
