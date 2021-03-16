package utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String NO_DISTANCES_SHOWN_MESSAGE = "Distances are not displayed";
    public static final String GRAPH_HAS_CYCLES_MESSAGE = "Graph must have no cycles";
    public static final String EMPTY_GRAPH_MESSAGE = "No graph";
    public static final String GRAPH_NOT_CONNECTED_MESSAGE = "Graph is not connected";

    public static final String PATH_TO_MAGNITUDE_SCRIPT = "/scripts/magnitude.py";
    public static final String PATH_TO_MAGNITUDE_DATA = "/scripts/data/matrix.txt";

    public static final String PATH_TO_IHARA_SCRIPT = "/scripts/ihara.py";
    public static final String PATH_TO_IHARA_DATA = "/scripts/data/ihara_data.json";
}
