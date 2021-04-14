package services.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class EdgeData {
    private int from;
    private int to;

    @EqualsAndHashCode.Exclude
    private int index;

    @Override
    public String toString() {
        return String.format("(%d, %d)", from, to);
    }

}
