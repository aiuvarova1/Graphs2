package services.dto;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomFunctionDto {
    private List<String> paths;
    private int vertex;
}
