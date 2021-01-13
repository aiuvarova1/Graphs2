package main;

import entities.*;
import entities.InfiniteManager.Type;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;



public class Controller {

    private Drawer drawer;

    @FXML
    private ToggleButton showDistances;

//    @FXML
//    private ToggleGroup showHide;

    @FXML
    private Button makeGif;

    @FXML
    private ImageView gifIcon;

    @FXML
    private ToggleButton hideDistances;

    @FXML
    private Button setAll;

    @FXML
    private ImageView setAllIcon;

    @FXML
    private CheckBox numeric;

    @FXML
    private CheckBox colour;

    @FXML
    private CheckBox arrows;

    @FXML
    private Button stopVisualize;

    @FXML
    private ImageView stopIcon;

    @FXML
    private TextField allLengths;


    @FXML
    private ImageView leftClick;

    @FXML
    private ImageView rightClick;

    @FXML
    private TitledPane helpTitledPane;

    @FXML
    private ImageView trashIcon;

    @FXML
    private Accordion accordion;

    @FXML
    private TitledPane drawTitledPane;

    @FXML
    private Button visualizeAmplitudes;

    @FXML
    private ImageView startIcon;

    @FXML
    private Button resetDistances;

    @FXML
    private Button clearButton;

    @FXML
    private CheckBox calculate;

    @FXML
    private Button undoButton;

    @FXML
    private ImageView undoIcon;

    @FXML
    private Button redoButton;

    @FXML
    private TitledPane distancesTitledPane;

    @FXML
    private TitledPane graphTypes;

    @FXML
    private ImageView redoIcon;

    @FXML
    private ImageView resetIcon;

    @FXML
    private AnchorPane drawingArea;

    @FXML
    private ImageView nodeClick;

    @FXML
    private ImageView drag;

    @FXML
    private Label tip;

    @FXML
    private Label minColor;

    @FXML
    private Label maxColor;

    @FXML
    private Button saveButton;

    @FXML
    private ImageView saveIcon;

    @FXML
    private Button openButton;

    @FXML
    private ImageView openIcon;

    @FXML
    private Button saveAsButton;

    @FXML
    private ImageView saveAsIcon;

    @FXML
    private StackPane dialog;

    @FXML
    private Button saveButton2;

    @FXML
    private ImageView saveIcon2;

    @FXML
    private Button discardButton;

    @FXML
    private ImageView discardIcon;

    @FXML
    private Button cancelButton;

    @FXML
    private ImageView cancelIcon;

    @FXML
    private TextField time;

    @FXML
    private Button simpleGraph;

    @FXML
    private Button lineGraph;

    @FXML
    private Button latticeGraph;

    @FXML
    private ImageView graphIcon;

    @FXML
    private ImageView lineIcon;

    @FXML
    private ImageView latticeIcon;

    @FXML
    private Label points;

    public Controller() {
        drawer = Drawer.getInstance();
    }

    @FXML
    public void changeIcon() {
        trashIcon.setImage(new Image(Manager.class.getResource("/assets/opened.png").toExternalForm()));
    }

    @FXML
    public void setOldIcon() {
        trashIcon.setImage(new Image(Manager.class.getResource("/assets/trash.png").toExternalForm()));
    }

    @FXML
    private void setIcons() {
        leftClick.setImage(new Image(Manager.class.getResource("/assets/leftClick.png").toExternalForm()));
        rightClick.setImage(new Image(Manager.class.getResource("/assets/rightClick.png").toExternalForm()));
        nodeClick.setImage(new Image(Manager.class.getResource("/assets/nodeClick.png").toExternalForm()));
        drag.setImage(new Image(Manager.class.getResource("/assets/drag.png").toExternalForm()));
        undoIcon.setImage(new Image(Manager.class.getResource("/assets/undo.png").toExternalForm()));
        redoIcon.setImage(new Image(Manager.class.getResource("/assets/redo.png").toExternalForm()));
        resetIcon.setImage(new Image(Manager.class.getResource("/assets/reset.png").toExternalForm()));
        startIcon.setImage(new Image(Manager.class.getResource("/assets/play.png").toExternalForm()));
        stopIcon.setImage(new Image(Manager.class.getResource("/assets/stop.png").toExternalForm()));
        setAllIcon.setImage(new Image(Manager.class.getResource("/assets/confirm.png").toExternalForm()));
        openIcon.setImage(new Image(Manager.class.getResource("/assets/open.png").toExternalForm()));
        saveIcon.setImage(new Image(Manager.class.getResource("/assets/save.png").toExternalForm()));
        saveAsIcon.setImage(new Image(Manager.class.getResource("/assets/save.png").toExternalForm()));
        saveIcon2.setImage(new Image(Manager.class.getResource("/assets/save.png").toExternalForm()));
        discardIcon.setImage(new Image(Manager.class.getResource("/assets/discard.png").toExternalForm()));
        cancelIcon.setImage(new Image(Manager.class.getResource("/assets/close.png").toExternalForm()));
        gifIcon.setImage(new Image(Manager.class.getResource("/assets/gif.png").toExternalForm()));
        graphIcon.setImage(new Image(Manager.class.getResource("/assets/graph.png").toExternalForm()));
        latticeIcon.setImage(new Image(Manager.class.getResource("/assets/lattice.png").toExternalForm()));
        lineIcon.setImage(new Image(Manager.class.getResource("/assets/line.png").toExternalForm()));

    }

    @FXML
    private void setButtons() {
        clearButton.addEventHandler(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        clearButton.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        undoButton.addEventHandler(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        undoButton.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        redoButton.addEventHandler(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        redoButton.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        resetDistances.addEventHandler(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        resetDistances.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        visualizeAmplitudes.addEventHandler(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        visualizeAmplitudes.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        stopVisualize.addEventFilter(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        stopVisualize.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        setAll.addEventHandler(MouseEvent.MOUSE_ENTERED, event ->
        {
            setAllIcon.setScaleX(11 / 10.0);
            setAllIcon.setScaleY(11 / 10.0);
        });
        setAll.addEventHandler(MouseEvent.MOUSE_EXITED, event ->
        {
            setAllIcon.setScaleX(1);
            setAllIcon.setScaleY(1);

        });

        saveButton.addEventFilter(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        saveButton.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        saveAsButton.addEventFilter(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        saveAsButton.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        openButton.addEventFilter(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        openButton.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        makeGif.addEventFilter(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        makeGif.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        String unselected = "-fx-background-color: #e1e1e1;" + "-fx-font-size: 16px;"
                + "-fx-font-family: \"Constantia\";";

        String selected = "-fx-background-color: #e1e1e1;" + "-fx-font-size: 17px;"
                + "-fx-font-family: \"Constantia\";";

        cancelButton.addEventFilter(MouseEvent.MOUSE_ENTERED, event ->
            ((Button)event.getSource()).setStyle(selected));
        cancelButton.addEventHandler(MouseEvent.MOUSE_EXITED, event ->
                ((Button)event.getSource()).setStyle(unselected));

        saveButton2.addEventFilter(MouseEvent.MOUSE_ENTERED,event ->
            ((Button)event.getSource()).setStyle(selected));
        saveButton2.addEventHandler(MouseEvent.MOUSE_EXITED, event ->
                ((Button) event.getSource()).setStyle(unselected)
        );

        discardButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event ->
                ((Button) event.getSource()).setStyle(selected));

        discardButton.addEventHandler(MouseEvent.MOUSE_EXITED, event ->
                ((Button) event.getSource()).setStyle(unselected)
        );

        simpleGraph.addEventFilter(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        simpleGraph.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        latticeGraph.addEventFilter(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        latticeGraph.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        lineGraph.addEventFilter(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        lineGraph.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);
    }

    private void addListeners() {

        Visualizer.bindPointsLabel(points);
        drawingArea.widthProperty().addListener((axis, oldVal, newVal) -> {

            if (Visualizer.isRunning()) {
                return;
            }
            drawingArea.setPrefWidth(newVal.doubleValue());
            InfiniteManager.rescale('x', oldVal.doubleValue(), newVal.doubleValue());
        });

        drawingArea.heightProperty().addListener((axis, oldVal, newVal) -> {

            if (Visualizer.isRunning()) {
                return;
            }
            InfiniteManager.rescale('y', oldVal.doubleValue(), newVal.doubleValue());
        });

        calculate.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                drawingArea.getChildren().filtered(x -> x instanceof Distance).
                        forEach((x) -> ((Distance) x).calculate());
            else
                drawingArea.getChildren().filtered(x -> x instanceof Distance).
                        forEach((x) -> ((Distance) x).decalculate());
            Distance.setCalc(newValue);

        });

        numeric.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                drawingArea.getChildren().filtered(x -> x instanceof Point).
                        forEach((x) -> ((Point) x).showNumbers());
            else
                drawingArea.getChildren().filtered(x -> x instanceof Point).
                        forEach((x) -> ((Point) x).hideNumbers());
            Visualizer.setNumeric(newValue);

        });

        colour.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                drawingArea.getChildren().filtered(x -> x instanceof Point).
                        forEach((x) -> ((Point) x).showColour());
            else
                drawingArea.getChildren().filtered(x -> x instanceof Point).
                        forEach((x) -> ((Point) x).hideColour());
            Visualizer.setColour(newValue);

        });

        arrows.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                drawingArea.getChildren().filtered(x -> x instanceof Point).
                        forEach((x) -> ((Point) x).showArrow());
            else
                drawingArea.getChildren().filtered(x -> x instanceof Point).
                        forEach((x) -> ((Point) x).hideArrow());
            Visualizer.setArrows(newValue);

        });

        allLengths.setOnAction(event -> changeDist());
        allLengths.setOnKeyTyped(event -> {
            String string = allLengths.getText();

            if (string.length() > Distance.MAX_LENGTH) {
                allLengths.setText(string.substring(0, Distance.MAX_LENGTH));
                allLengths.positionCaret(string.length());
            }
        });

        saveButton.setOnAction(event -> FileManager.save());
        saveAsButton.setOnAction(event -> FileManager.saveAs());
        openButton.setOnAction(event -> openFile());

        saveButton.disableProperty().bind(FileManager.getDisable());

    }

    private void openFile(){
        if(FileManager.isSaveNeeded())
        {
            dialog.setDisable(false);
            dialog.setVisible(true);

        }else{
            FileManager.open();
        }

    }

    @FXML
    void saveUnchanged(){
        hideDialog();
        FileManager.save();
        FileManager.open();
    }

    @FXML
    void discardAndOpen(){
        hideDialog();
        FileManager.open();
    }

    @FXML
    void hideDialog(){
        dialog.setDisable(true);
        dialog.setVisible(false);
    }



    @FXML
    void initialize() {
        drawingArea.addEventFilter(MouseEvent.MOUSE_CLICKED, Filter.dragFilter);
        drawingArea.addEventFilter(MouseEvent.MOUSE_CLICKED, Filter.clickFilter);

        setOldIcon();

        drawer.setPane(drawingArea, dialog);

        setButtons();

        addListeners();
        Visualizer.bindBounds(minColor, maxColor);

        drawTitledPane.setAnimated(true);
        helpTitledPane.setAnimated(true);
        accordion.setExpandedPane(drawTitledPane);
        setIcons();

        new Distance();
        PopupMessage.setPopup(tip);

        time.focusedProperty().addListener((observableValue, old, newVal) -> {
            if (!newVal) {
                setTime();
            }
        });
    }

    /**
     * Creates the node on click
     *
     * @param event click-info
     */
    @FXML
    void createNode(MouseEvent event) {
        if (Visualizer.isRunning() || !InfiniteManager.canEdit()) {
            return;
        }

        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }
        if (SimpleGraph.getInstance().getSize() < SimpleGraph.MAX_SIZE) {

            Node node = drawer.drawNode(event);
            Invoker.getInstance().createElement(node);
            //node.create();
        }
    }

    /**
     * Removes all nodes from the pane
     */
    @FXML
    void clearWorkingArea() {

        drawingArea.getChildren().removeIf(x -> x.getClass() == Node.class || x.getClass() == Edge.class
                || x.getClass() == Distance.class);
        SimpleGraph.getInstance().clearGraph();
    }



    @FXML
    void undoAction() {
        Invoker.getInstance().undoLast();
    }

    @FXML
    void redoAction() {
        Invoker.getInstance().redoLast();
    }

    /**
     * Shows lengths of the edges
     */
    @FXML
    void showDist() {

        if (SimpleGraph.areDistancesShown()) {
            showDistances.setSelected(true);
            return;
        }
        SimpleGraph.getInstance().setLengths();
        calculate.setDisable(false);
        setAll.setDisable(false);
        allLengths.setDisable(false);

    }

    /**
     * Makes current graph not weighed
     */
    @FXML
    void hideDist() {

        if (!SimpleGraph.areDistancesShown()) {
            hideDistances.setSelected(true);
            return;
        }

        SimpleGraph.getInstance().hideLengths();
        calculate.setDisable(true);
        setAll.setDisable(true);
        allLengths.setDisable(true);
    }

    @FXML
    void resetDist() {

        SimpleGraph.getInstance().resetDistances();
    }

    @FXML
    private void changeDist(){
        SimpleGraph.getInstance().changeDistances(allLengths.getText());
    }

    /**
     * Shortcuts event handlers for undo, redo, save
     */
    @FXML
    static final EventHandler<KeyEvent> shortCuts = new EventHandler<KeyEvent>() {
        final KeyCodeCombination undoComb = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
        final KeyCodeCombination redoComb = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
        final KeyCodeCombination saveComb =
                new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        final KeyCodeCombination saveAsComb =
                new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_ANY, KeyCombination.CONTROL_DOWN);
        final KeyCodeCombination openComb =
                new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);


        @Override
        public void handle(KeyEvent event) {
            if (Visualizer.isRunning() || !InfiniteManager.canEdit()) {
                return;
            }

            if (undoComb.match(event))
                Invoker.getInstance().undoLast();
            else if (redoComb.match(event))
                Invoker.getInstance().redoLast();
            else if (saveComb.match(event) && FileManager.isSaveNeeded())
                FileManager.save();
            else if(saveAsComb.match(event))
                FileManager.saveAs();
//            else if(openComb.match(event))
//                Controller.openFile();

        }
    };

    /**
     * Starts amplitudes' distribution
     */
    @FXML
    void visualizeAmplitudes() {

        if (InfiniteManager.canEdit()) {
//            if (!SimpleGraph.areDistancesShown()) {
//                PopupMessage.showMessage("The distances are disabled");
//                Visualizer.enableGif(false);
//                return;
//            }

            for (javafx.scene.Node dist : drawingArea.getChildren().filtered(x -> x instanceof Distance)) {
                if (((Distance) dist).isInfty()) {
                    PopupMessage.showMessage("There must be no infinities in distances");
                    Visualizer.enableGif(false);
                    return;
                }
            }

            SimpleGraph.getInstance().visualizeAmplitudes();
        } else {
            InfiniteManager.visualize();
        }

        if (Visualizer.isRunning()) {
            points.setVisible(true);
            time.setDisable(true);
            drawTitledPane.setDisable(true);
            distancesTitledPane.setDisable(true);
            visualizeAmplitudes.setDisable(true);
            makeGif.setDisable(true);
            stopVisualize.setDisable(false);
            graphTypes.setDisable(true);
        }
    }

    @FXML
    void createGIF(){
        Visualizer.enableGif(true);
        visualizeAmplitudes();
    }

    /**
     * Stops amplitudes' distribution
     */
    @FXML
    void stopVisualizing() {
        if (InfiniteManager.canEdit()) {
            Visualizer.stopVisualization();
        } else {
            InfiniteManager.stop();
        }

        visualizeAmplitudes.setDisable(false);
        stopVisualize.setDisable(true);

        if (InfiniteManager.canEdit()) {
            drawTitledPane.setDisable(false);
            distancesTitledPane.setDisable(false);
        }
        graphTypes.setDisable(false);

        makeGif.setDisable(false);
        time.setDisable(false);
        points.setVisible(false);
    }

    @FXML
    void setTime() {
        time.setText(GIFMaker.setTime(time.getText()));
        visualizeAmplitudes.requestFocus();
    }

    @FXML
    void drawSimple() {
        InfiniteManager.init(Type.SIMPLE);
        drawTitledPane.setDisable(false);
        distancesTitledPane.setDisable(false);
    }

    @FXML
    void drawLine() {
        InfiniteManager.init(Type.LINE);
        drawTitledPane.setDisable(true);
        distancesTitledPane.setDisable(true);
    }

    @FXML
    void drawLattice() {
        InfiniteManager.init(Type.LATTICE);
        drawTitledPane.setDisable(true);
        distancesTitledPane.setDisable(true);
    }


}
