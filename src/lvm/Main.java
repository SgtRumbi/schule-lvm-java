package lvm;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Johannes on 27.09.2016.
 */
public class Main extends Application {
    // FIELDS
    private int inputMaxBalloonsInPackage = 0;
    private int inputMaxBalloonsInCompartment = 0;
    private int inputCompartmentsCount = 0;

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

    private TextField createInputField(String hint) {
        final TextField textField = new TextField();
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

    private Parent createItem() {
        Label sampleItem = new Label("2");
        sampleItem.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(12), new BorderWidths(6))));
        sampleItem.setBackground(new Background(new BackgroundFill(new Color(0.8, 0.8, 0.8, 1.0), new CornerRadii(12), new Insets(2))));
        sampleItem.setPadding(new Insets(20));
        return sampleItem;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TextField textFieldMaxBalloonsInPackage = createInputField("z. B. 20");
        TextField textFieldMaxBalloonsInCompartment = createInputField("z. B. 15");
        TextField textFieldMaxCompartments = createInputField("z. B. 10");

        Button buttonFillRandom = new Button("Fülle");
        Button buttonCalculate = new Button("Sortiere");

        ToolBar buttonContainer = new ToolBar(buttonFillRandom, buttonCalculate);
        buttonContainer.setPadding(new Insets(5));
        // buttonContainer.setSpacing(5);

        HBox hBoxCompartments = new HBox(createItem(), createItem());
        hBoxCompartments.setPadding(new Insets(10));
        hBoxCompartments.setSpacing(10);

        VBox root = new VBox(
                createInputContainer("Max. Luftballons in einer Packung", textFieldMaxBalloonsInPackage),
                createInputContainer("Max. Luftballons in einem Fach", textFieldMaxBalloonsInCompartment),
                createInputContainer("Anzahl der Fächer", textFieldMaxCompartments),
                buttonContainer,
                hBoxCompartments
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
        /* int[] array = generateRandomFilledArray(10, 20);
        echoArray(array);

        LVMResult result = LVM.lvmCalculate(array, 20);
        System.out.println(result.toString()); */
        launch(args);
    }
}
