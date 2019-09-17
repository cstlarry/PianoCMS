import javafx.application.Application;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;


public class RootLayoutController {
    public MenuItem balanceDueReport;
    private ProjectUI ui = new ProjectUI();

    public void getBalanceReport() { new BalanceDueReport().start(new Stage()); }

    public void taxReport(){
        new QuarterlyTaxReport().start(new Stage());
    }

    public void callBackReport() {new CallBackReport().start(new Stage()); }

    public void taxRateListReport() {
        new TaxRateListReport().start(new Stage());
    }

    public void listCustomersReport() {
        new ListCustomersReport().start(new Stage());
    }

    public void statusReport() {
        new StatusReport().start(new Stage());
    }

    public void updateStatusReport() {
        new UpdateStatusReport().start(new Stage());
    }

    private Stage stage = null;

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public boolean backupDB() {
        try {
            String path="cmd /c start c:\\backups\\backupdb.bat";
            Runtime rn=Runtime.getRuntime();
            Process pr=rn.exec(path);
        } catch (Exception e){
            System.out.println("failed");
        }
        return false;
    }

    public void dumpDB() {
        Process p = null;
        String fileName = "dbdump" + LocalDate.now().toString() + ".sql";
        String expr = new StringBuilder()
                .append("mysqldump").append(' ')
                .append("-u").append("root").append(' ')
                .append("-p").append("Hedied4u").append(' ')
                .append("pianodb").append(' ')
                .append(">").append(' ')
                .append("C:\\PianoApp\\Backups\\").append(fileName)
                .toString();
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", expr);
            Process pr = pb.start();

            int processComplete = pr.waitFor();
            if (processComplete == 0) {
                ui.showMessage("Backup created successfully!");
            } else {
                ui.showMessage("Backup was NOT created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayWebPage(){
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        ui.displayWebpage("https://google.com");
    }

    public void chooseFileListener(ActionEvent event) {
            FileChooser fileChooser = new FileChooser();
            String currentPath = Paths.get("c:/PianoApp").toAbsolutePath().normalize().toString();
            fileChooser.setInitialDirectory(new File(currentPath));
            fileChooser.getExtensionFilters()
                .addAll(
                    new FileChooser.ExtensionFilter("TXT files (*.TXT)", "*.TXT"),
                    new FileChooser.ExtensionFilter("txt files (*.txt)", "*.txt"),
                    new FileChooser.ExtensionFilter("sql files (*.sql)", "*.sql"));

            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    java.awt.Desktop.getDesktop().edit(file);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
    }

    @FXML
    public void showTaxRateView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/TaxRateView.fxml"));
            BorderPane taxRateView = loader.load();
            Scene scene = new Scene(taxRateView); //We are sending rootLayout to the Scene.
            scene.getStylesheets().add("customer.css");
            Stage stage = new Stage();
            stage.setTitle("Enter/Edit Cities and TaxCodes");
            stage.setScene(scene); //Set the scene in primary stage.
            stage.initStyle(StageStyle.UTILITY); // added by Larry 8/30/18
            stage.show(); //Display the primary stage
            TaxRateController controller = loader.getController();
            controller.searchTaxRates();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleExit(ActionEvent actionEvent) {
        System.exit(0);
    }

    //Help Menu button behavior
    public void handleHelp(ActionEvent actionEvent) {
        Alert alert = new Alert (Alert.AlertType.INFORMATION);
        alert.setTitle("Customer Information System");
        alert.setHeaderText("Piano CMS");
        alert.setContentText("Version 1.1 - August 26, 2019");
        alert.show();
    }
}
