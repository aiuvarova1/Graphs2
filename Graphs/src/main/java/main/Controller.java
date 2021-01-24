package main;

import entities.*;
import exceptions.ValidationException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import services.MagnitudeService;

public class Controller {

    private final Drawer drawer;

    @FXML
    private ToggleButton showDistances;

    @FXML
    private ToggleButton hideDistances;

    @FXML
    private Button setAll;

    @FXML
    private ImageView setAllIcon;

    @FXML
    private TextField allLengths;

    @FXML
    private TitledPane helpTitledPane;

    @FXML
    private ImageView trashIcon;

    @FXML
    private Accordion accordion;

    @FXML
    private TitledPane drawTitledPane;

    @FXML
    private Button resetDistances;

    @FXML
    private Button clearButton;

    @FXML
    private CheckBox calculate;

    @FXML
    private Button undoButton;

    @FXML
    private Button magnitude;

    @FXML
    private Button redoButton;

    @FXML
    private AnchorPane drawingArea;
    @FXML
    private Label tip;

    @FXML
    private Button saveButton;

    @FXML
    private Button openButton;
    @FXML
    private Button saveAsButton;
    @FXML
    private StackPane dialog;

    @FXML
    private Button saveButton2;
    @FXML
    private Button discardButton;
    @FXML
    private Button cancelButton;

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
    private void setButtons() {
        clearButton.addEventHandler(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        clearButton.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        undoButton.addEventHandler(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        undoButton.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        redoButton.addEventHandler(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        redoButton.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        resetDistances.addEventHandler(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        resetDistances.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

        magnitude.addEventHandler(MouseEvent.MOUSE_ENTERED, Filter.buttonEnterHandler);
        magnitude.addEventHandler(MouseEvent.MOUSE_EXITED, Filter.buttonExitHandler);

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

        String unselected = "-fx-background-color: #e1e1e1;" + "-fx-font-size: 16px;"
            + "-fx-font-family: \"Constantia\";";

        String selected = "-fx-background-color: #e1e1e1;" + "-fx-font-size: 17px;"
            + "-fx-font-family: \"Constantia\";";

        cancelButton.addEventFilter(MouseEvent.MOUSE_ENTERED, event ->
            ((Button) event.getSource()).setStyle(selected));
        cancelButton.addEventHandler(MouseEvent.MOUSE_EXITED, event ->
            ((Button) event.getSource()).setStyle(unselected));

        saveButton2.addEventFilter(MouseEvent.MOUSE_ENTERED, event ->
            ((Button) event.getSource()).setStyle(selected));
        saveButton2.addEventHandler(MouseEvent.MOUSE_EXITED, event ->
            ((Button) event.getSource()).setStyle(unselected)
        );

        discardButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event ->
            ((Button) event.getSource()).setStyle(selected));

        discardButton.addEventHandler(MouseEvent.MOUSE_EXITED, event ->
            ((Button) event.getSource()).setStyle(unselected)
        );
    }

    private void addListeners() {
        drawingArea.widthProperty().addListener((axis, oldVal, newVal) -> {
            drawingArea.setPrefWidth(newVal.doubleValue());
            Graph.getInstance().rescale('x', oldVal.doubleValue(), newVal.doubleValue());
        });

        drawingArea.heightProperty().addListener((axis, oldVal, newVal) -> {
            Graph.getInstance().rescale('y', oldVal.doubleValue(), newVal.doubleValue());
        });

        calculate.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                drawingArea.getChildren().filtered(x -> x instanceof Distance).
                    forEach((x) -> ((Distance) x).calculate());
            } else {
                drawingArea.getChildren().filtered(x -> x instanceof Distance).
                    forEach((x) -> ((Distance) x).decalculate());
            }
            Distance.setCalc(newValue);

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

    private void openFile() {
        if (FileManager.isSaveNeeded()) {
            dialog.setDisable(false);
            dialog.setVisible(true);

        } else {
            FileManager.openGraphFile();
        }

    }

    @FXML
    void saveUnchanged() {
        hideDialog();
        FileManager.save();
        FileManager.openGraphFile();
    }

    @FXML
    void discardAndOpen() {
        hideDialog();
        FileManager.openGraphFile();
    }

    @FXML
    void hideDialog() {
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

        drawTitledPane.setAnimated(true);
        helpTitledPane.setAnimated(true);
        accordion.setExpandedPane(drawTitledPane);

        new Distance();
        PopupMessage.setPopup(tip);
    }

    /**
     * Creates the node on click
     *
     * @param event click-info
     */
    @FXML
    void createNode(MouseEvent event) {

        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }
        if (Graph.getInstance().getSize() < Graph.MAX_SIZE) {

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
        Graph.getInstance().clearGraph();
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

        if (Graph.areDistancesShown()) {
            showDistances.setSelected(true);
            return;
        }
        Graph.getInstance().setLengths();
        calculate.setDisable(false);
        setAll.setDisable(false);
        allLengths.setDisable(false);

    }

    /**
     * Makes current graph not weighed
     */
    @FXML
    void hideDist() {

        if (!Graph.areDistancesShown()) {
            hideDistances.setSelected(true);
            return;
        }

        Graph.getInstance().hideLengths();
        calculate.setDisable(true);
        setAll.setDisable(true);
        allLengths.setDisable(true);
    }

    @FXML
    void resetDist() {

        Graph.getInstance().resetDistances();
    }

    @FXML
    private void changeDist() {
        Graph.getInstance().changeDistances(allLengths.getText());
    }

    @FXML
    private void calculateMagnitude() {
        try {
            MagnitudeService.calculateMagnitude();
        } catch (ValidationException ex) {
            PopupMessage.showMessage(ex.getMessage());
        }
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

            if (undoComb.match(event)) {
                Invoker.getInstance().undoLast();
            } else if (redoComb.match(event)) {
                Invoker.getInstance().redoLast();
            } else if (saveComb.match(event) && FileManager.isSaveNeeded()) {
                FileManager.save();
            } else if (saveAsComb.match(event)) {
                FileManager.saveAs();
            }
//            else if(openComb.match(event))
//                Controller.openFile();

        }
    };


}
