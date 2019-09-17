
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application  {
    private Stage stage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Piano Customer Management System");
        initRootLayout();
        showCustomerView();
    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().add("customer.css");
            scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    Node focusOwner = scene.getFocusOwner();
                    if (focusOwner instanceof Button) {
                        ((Button) focusOwner).fire();
                    }
                }
            });
            stage.setScene(scene);
            RootLayoutController controller = loader.getController();
            controller.setStage(stage);
            stage.initStyle(StageStyle.UTILITY); // added by Larry 8/30/18
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showCustomerView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/CustView.fxml"));
            AnchorPane customerOperationsView = loader.load();
            CustomerController controller = loader.getController();

            customerOperationsView.setMinSize(830.0, 600.0);
            rootLayout.setCenter(customerOperationsView);
            controller.setData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
} // end class
