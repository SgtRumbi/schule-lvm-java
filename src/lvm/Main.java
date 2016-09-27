package lvm;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

// TODO: 27.09.2016 Validate input

/**
 * Created by Johannes on 27.09.2016.
 */
public class Main extends Application {
    // FIELDS
    /* private int inputMaxBalloonsInPackage = 0;
    private int inputMaxBalloonsInCompartment = 0;
    private int inputCompartmentsCount = 0; */
    private final List<LabelArrayReference> labelArrayReferences = new CopyOnWriteArrayList<>();
    private HBox labelsBox;
    private Label labelOutput, labelFillSequence;
    private Queue<Integer> fillSequenceQueue = new ArrayDeque<>();
    private List<Integer> fillSequenceInfoList = new ArrayList<>();

    // CLASS METHODS
    private GridPane createInputContainer(String labelText, final TextField textField) {
        Label label = new Label(labelText + ": ");
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5));
        gridPane.setHgap(3);
        gridPane.setVgap(1);
        gridPane.add(label, 0, 0);
        gridPane.add(textField, 3, 0);
        return gridPane;
    }

    private TextField createInputField(String hint, int defaultValue) {
        final TextField textField = new TextField(String.valueOf(defaultValue));
        textField.setPromptText(hint);
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        return textField;
    }

    private Label createItem() {
        Label sampleItem = new Label("2");
        sampleItem.setBackground(new Background(new BackgroundFill(new Color(0.8, 0.8, 0.8, 1.0), new CornerRadii(12),
                new Insets(2))));
        sampleItem.setPadding(new Insets(10, 10, 20, 10));
        return sampleItem;
    }

    private final class LabelArrayReference {
        private Label label;
        private boolean clearing = false;
        private int index, value;

        public LabelArrayReference(Label label, int index, int value) {
            this.label = label;
            this.index = index;
            this.value = value;

            label.setText(index + ": " + value);
        }

        public void update(int newValue) {
            this.value = newValue;
            label.setText(index + ": " + value);
        }

        public void setClearing(boolean clearing) {
            this.clearing = clearing;
            if (clearing) {
                label.setBackground(new Background(new BackgroundFill(new Color(0.7, 0.7, 0.9, 1.0),
                        new CornerRadii(12), new Insets(2))));
            } else {
                // label.setBorder(null);
                label.setBackground(new Background(new BackgroundFill(new Color(0.8, 0.8, 0.8, 1.0),
                        new CornerRadii(12), new Insets(2))));
            }
        }

        public boolean isClearing() {
            return clearing;
        }

        public Label getLabel() {
            return label;
        }
    }

    private void showLabels() {
        labelsBox.getChildren().clear();
        for (LabelArrayReference lar : labelArrayReferences) {
            labelsBox.getChildren().add(lar.getLabel());
        }
    }

    private void setOutput(LVMResult result) {
        String add = "";
        LVMLastMatchItem[] fields = result.lastMatch;
        int value = 0;
        for (int i = 0; i < fields.length; i++) {
            value += fields[i].value;
            add += "" + fields[i].value;
            if (i != fields.length - 1)
                add += " + ";
        }
        labelOutput.setText("Output: " + add + " = " + value + ", Überschuss: " + result.finalTolerance);
    }

    private void updateFillSequenceLabel() {
        String string = "";

        for (int i : fillSequenceInfoList) {
            string += i + ", ";
        }

        labelFillSequence.setText(string);
    }

    private void addValueToSequence(TextField textFieldMaxCompartments, int value) {
        if (textFieldMaxCompartments.getText().equals(""))
            textFieldMaxCompartments.setText("10");

        int compartmentsCount = Integer.valueOf(textFieldMaxCompartments.getText());
        // If there are not enough compartments, add a new
        if (labelArrayReferences.size() < compartmentsCount) {
            labelArrayReferences.add(new LabelArrayReference(createItem(), labelArrayReferences.size(), value));
        } else {
            fillSequenceQueue.add(value);
            fillSequenceInfoList.add(value);
        }

        updateFillSequenceLabel();

        showLabels();
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final TextField textFieldMaxBalloonsInPackage = createInputField("z. B. 20", 20);
        final TextField textFieldMaxBalloonsInCompartment = createInputField("z. B. 15", 10);
        final TextField textFieldMaxCompartments = createInputField("z. B. 10", 10);

        labelOutput = new Label("Output: -");
        labelOutput.setBackground(new Background(new BackgroundFill(new Color(0.9, 0.7, 0.7, 1.0), new CornerRadii(12),
                new Insets(2))));
        labelOutput.setPadding(new Insets(10));

        final Button buttonCalculate = new Button("Sortieren");
        final Button buttonClear = new Button("Leeren");
        buttonClear.setDisable(true);
        buttonCalculate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                buttonClear.setDisable(false);

                if (textFieldMaxBalloonsInPackage.getText().equals(""))
                    textFieldMaxBalloonsInPackage.setText("20");

                int maxBalloonsInPackage = Integer.valueOf(textFieldMaxBalloonsInPackage.getText());

                // Get the array from the already generated lars
                int[] array = new int[labelArrayReferences.size()];
                for (int i = 0; i < labelArrayReferences.size(); i++) {
                    array[i] = labelArrayReferences.get(i).value;
                }

                // Calculate result
                LVMResult result = LVM.lvmCalculate(array, maxBalloonsInPackage);

                // Mark all labels to non clear
                for (LabelArrayReference lar : labelArrayReferences) {
                    lar.setClearing(false);
                }

                // Mark labels to clear
                for (int i = 0; i < result.lastMatch.length; i++) {
                    LVMLastMatchItem item = result.lastMatch[i];
                    labelArrayReferences.get(item.key).setClearing(true);
                }

                // Set output
                setOutput(result);

                updateFillSequenceLabel();

                buttonCalculate.setDisable(true);
            }
        });
        buttonClear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                buttonClear.setDisable(true);

                for (final LabelArrayReference lar : labelArrayReferences) {
                    if (lar.isClearing()) {
                        labelsBox.getChildren().remove(lar.getLabel());
                        lar.getLabel().setVisible(false);
                        labelArrayReferences.remove(lar);

                        if (fillSequenceQueue.size() > 0) {
                            int value = fillSequenceQueue.poll();
                            labelArrayReferences.add(new LabelArrayReference(createItem(), labelArrayReferences.size() - 1,
                                    value));
                            fillSequenceInfoList.remove(fillSequenceInfoList.size() - 1);
                        }
                    }
                }

                showLabels();
                updateFillSequenceLabel();

                buttonCalculate.setDisable(false);
            }
        });

        Label labelFillSequenceDescription = new Label("Füllfolge: ");
        labelFillSequence = new Label("-");
        /* Button updateFillSequence = new Button("Anpassen");
        updateFillSequence.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final Stage dialogStage = new Stage(StageStyle.DECORATED);

                TableView<DataObject> table = new TableView<>();
                table.setEditable(true);

                TableColumn<DataObject, Integer> indexColumn = new TableColumn<>("Index");
                indexColumn.setCellValueFactory(new PropertyValueFactory<DataObject, Integer>("Index"));
                TableColumn<DataObject, Integer> valueColumn = new TableColumn<>("Wert");
                valueColumn.setCellValueFactory(new PropertyValueFactory<DataObject, Integer>("Wert"));
                valueColumn.setOnEditCommit(
                        new EventHandler<TableColumn.CellEditEvent<DataObject, Integer>>() {
                            @Override
                            public void handle(TableColumn.CellEditEvent<DataObject, Integer> t) {
                                t.getTableView().getItems().get(
                                        t.getTablePosition().getRow()).setValue(t.getNewValue());
                            }
                        }
                );

                List<DataObject> dataObjectList = new ArrayList<>();
                for (int i = 0; i < fillSequenceQueue.size(); i++) {
                    dataObjectList.add(new DataObject(i, fillSequenceQueue.poll()));
                }
                fillSequenceInfoList.clear();
                updateFillSequenceLabel();

                ObservableList<DataObject> dataObjects = FXCollections.observableArrayList(dataObjectList);

                table.setItems(dataObjects);

                table.getColumns().addAll(indexColumn, valueColumn);

                Button submit = new Button("Ok");
                submit.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {


                        primaryStage.show();
                        dialogStage.hide();
                    }
                });
                Button cancel = new Button("Abbrechen");
                cancel.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        primaryStage.show();
                        dialogStage.hide();
                    }
                });

                VBox root = new VBox(new Label("Editieren:"), table, new HBox(submit, cancel));

                primaryStage.hide();
                dialogStage.setScene(new Scene(root, 200, 200));
                dialogStage.setAlwaysOnTop(true);
                dialogStage.show();
            }
        }); */
        Button randomFillSequence = new Button("Zufällig");
        randomFillSequence.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (textFieldMaxBalloonsInCompartment.getText().equals(""))
                    textFieldMaxBalloonsInCompartment.setText("20");

                int maxBalloonsInCompartment = Integer.valueOf(textFieldMaxBalloonsInCompartment.getText());

                Random random = new Random();
                int randomValue = random.nextInt(maxBalloonsInCompartment);

                addValueToSequence(textFieldMaxCompartments, randomValue);
            }
        });
        Button definedFillSequence = new Button("Bestimmt");
        definedFillSequence.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final Stage dialogStage = new Stage(StageStyle.UNIFIED);

                final TextField numberInput = createInputField("z. B. 10", 10);
                final Label errorMessage = new Label("Bitte geben Sie eine Zahl ein");
                errorMessage.setVisible(false);
                Button submit = new Button("Ok");
                submit.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (numberInput.getText().equals("")) {
                            errorMessage.setVisible(true);
                        } else {
                            addValueToSequence(textFieldMaxCompartments, Integer.valueOf(numberInput.getText()));

                            primaryStage.show();
                            dialogStage.hide();
                        }
                    }
                });
                Button cancel = new Button("Abbrechen");
                cancel.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        primaryStage.show();
                        dialogStage.hide();
                    }
                });

                VBox root = new VBox(new Label("Wert zum Hinzufügen:"), numberInput, errorMessage,
                        new HBox(submit, cancel));

                primaryStage.hide();
                dialogStage.setScene(new Scene(root, 200, 200));
                dialogStage.setAlwaysOnTop(true);
                dialogStage.show();
            }
        });

        ToolBar buttonContainer = new ToolBar(buttonCalculate, buttonClear, new Separator(),/* updateFillSequence, */
                randomFillSequence, definedFillSequence, labelFillSequenceDescription, labelFillSequence);
        buttonContainer.setPadding(new Insets(5));

        labelsBox = new HBox();
        ScrollPane labelsBoxScrollPane = new ScrollPane(labelsBox);
        labelsBoxScrollPane.hbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        labelsBoxScrollPane.vbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.NEVER);

        VBox root = new VBox(
                createInputContainer("Max. Luftballons in einer Packung", textFieldMaxBalloonsInPackage),
                createInputContainer("Max. Luftballons in einem Fach", textFieldMaxBalloonsInCompartment),
                createInputContainer("Anzahl der Fächer", textFieldMaxCompartments),
                buttonContainer,
                labelsBoxScrollPane,
                labelOutput
                // hBoxCompartments
        );

        Scene lvmScene = new Scene(root, 600, 300);
        primaryStage.setScene(lvmScene);
        primaryStage.setTitle("Luftballonverpackungsmaschine");
        primaryStage.show();
    }

    // STATIC METHODS
    /**
     * Prints an int array to {@link System#out}.
     *
     * @param array The array to print
     */
    private static void echoArray(int[] array) {
        System.out.println("array: " + Arrays.toString(array));
    }

    /**
     * Creates a new array, filled with randoms.
     *
     * @param length Length of the array.
     * @param maxElementSize Max size of an element of the array.
     * @return The array filled with randoms.
     */
    private static int[] generateRandomFilledArray(int length, int maxElementSize) {
        int[] array = new int[length];
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(maxElementSize);
        }
        return array;
    }

    public static void main(String[] args) {
        launch(args);
    }

    /* public static class DataObject {
        SimpleIntegerProperty index, value;

        public DataObject(int index, int value) {
            this.index = new SimpleIntegerProperty(index);
            this.value = new SimpleIntegerProperty(value);
        }

        public int getIndex() {
            return index.get();
        }

        public SimpleIntegerProperty indexProperty() {
            return index;
        }

        public void setIndex(int index) {
            this.index.set(index);
        }

        public int getValue() {
            return value.get();
        }

        public SimpleIntegerProperty valueProperty() {
            return value;
        }

        public void setValue(int value) {
            this.value.set(value);
        }
    } */
}
