import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.image.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.collections.*;
import java.util.*;
import javafx.scene.control.Alert.AlertType;

public class FXTemplate extends Application {

    private BorderPane root = new BorderPane();

    private Parent createContent() {
        root.setPrefSize(600, 600);
        root.setPadding(new Insets(10));
        root.setCenter(new Button("Test"));
        return root;
    } // end createContent()

    @Override
    public void start(Stage stage) {
        stage.setTitle("FXTemplate");
        Scene scene = new Scene(createContent());
        stage.setScene(scene);
        stage.show();
    } // end start()

    public int inputInt(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dialog");
        dialog.setHeaderText(prompt);
        Optional<String> text = dialog.showAndWait();
        if(text.isPresent()) {
            try {
                return Integer.parseInt(text.get());
            }
            catch (Exception e) {
                showMessage(String.format("%s cannot be converted to an int", text.get()));
                return 0; }
        } else
            return -1;
    } // end inputInt()

    public void showMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION, message);
        alert.showAndWait();
    } // end showMessage()

    public static void main(String[] args) {
        launch(args);
    } // end main

} // end class