import javafx.application.*;
import javafx.collections.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.event.*;
import javafx.geometry.*;

import java.util.*;
import java.util.stream.*;
import java.time.*;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.text.*;

import javafx.print.*;

// Change the class name and do a Save As to start a new project
public class UpdateStatusReport extends Application {

    // make the main container a BorderPane
    BorderPane root = new BorderPane();

    // declare ui component and other needed variables
    Button runBtn = new Button("Run");
    Button clearOutputBtn = new Button("Clear Output");
    Button clearFieldsBtn = new Button("Clear Fields");
    Button printBtn = new Button("Print");
    CheckBox check = new CheckBox("Monospace Font");
    TextArea display = new TextArea();
    Label statusLab = new Label("Status");

    private Clipboard clipboard = Clipboard.getSystemClipboard();
    private ClipboardContent content = new ClipboardContent();

    StringBuilder output = new StringBuilder(128);
    LocalDate today = LocalDate.now();
    int outputCount = 0;
    String appTitle;
    ProjectUI ui;

    // fill root container with UI components and return it
    private Parent createContent() {
        root.setPadding(new Insets(25));
        display.setPrefColumnCount(50);
        display.setWrapText(true);
        clearFieldsBtn.setDisable(ui.getRows() == 0);

        // set up action listeners
        clearOutputBtn.setOnAction(
                e -> {
                    clearOutput();
                });
        clearFieldsBtn.setOnAction(
                e -> {
                    ui.clearFields();
                });
        printBtn.setOnAction(
                e -> {
                    content.putString(display.getText());
                    clipboard.setContent(content);
                    String filename = appTitle + ".txt";
                    try {
                        writeToFile(display.getText(), new File(filename));
                        getHostServices().showDocument(filename);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    //ui.printNode(display);
                });
        runBtn.setOnAction(
                e -> {
                    if (check.isSelected()) useMonospaceFont(12);
                    else useModenaFont(12);
                    run();
                });

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(runBtn, clearOutputBtn, clearFieldsBtn, printBtn, list, check);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.getChildren().addAll(hbox, display);

        root.setTop(ui);
        root.setCenter(vbox);
        root.setBottom(statusLab);
        return root;
    } // end createContent()

    public void start(Stage stage) {
        setup();
        stage.setTitle(appTitle);
        Scene scene = new Scene(createContent(), 800, 400);
        // un-comment and edit the following line to load a css file
        // setStyleSheet(scene, "cssFile.css");
        stage.setScene(scene);
        stage.show();
        stage.sizeToScene();
    } // end start  

    // Replace "String" below with the object type that populates the ComboBox
    ObservableList<String> items = FXCollections.observableArrayList();
    ComboBox<String> list = new ComboBox<>(items);

    // commonly changed content properties.  Moved here for proximity to the run method
    private void setup() {
        ui = new ProjectUI("Enter space separated ID numbers",
                "Change status to");

        appTitle = "Update Status Report";
        check.setSelected(false);
    } // end setup()

    private void run() {
        int[] idList = ui.getFieldAsIntArray(0);
        String status = ui.getField(1).toUpperCase();
        outputln(String.format("The status of the following customers has been changed to %s%n", status ));

        try {
            for(int id: idList){
                CustomerDAO.updateStatus(id, status);
                Customer customer = CustomerDAO.searchCustomer(String.valueOf(id));
                outputln(String.format("%d %s", id, customer));
            }
        } catch (Exception e){
            println(e.getMessage());
        }

    } // end run()

    // helper methods can go here 

    // do not change anything below this line
    private void print(Object obj) {
        System.out.print(obj);
    }

    private void println(Object obj) {
        System.out.println(obj);
    }

    private void println() {
        System.out.println();
    }

    private void output(Object value) {
        String stringValue = String.valueOf(value);
        if (!stringValue.equals("")) {
            output.append(stringValue);
            updateOutput();
        } // end if
    } // end output() 

    private void outputln(Object value) {
        String stringValue = String.valueOf(value);
        if (!stringValue.equals("")) {
            output.append(stringValue).append("\n");
            updateOutput();
        } // end if
    } // end outputln()

    private void outputln() {
        output.append("\n");
        updateOutput();
    } // end outputln()

    private void updateOutput() {
        display.setText(output.toString());
        outputCount++;
    } // end updateOutput()

    private void clearOutput() {
        output.setLength(0);
        display.setText("");
        outputCount = 0;
    } // end clearOutput()

    private void writeToFile(String string, File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(string));
             PrintWriter writer = new PrintWriter(new FileWriter(file));
        ) {
            reader.lines().forEach(line -> writer.println(line));
        }
    }

    private void useModenaFont(int size) {
        display.setFont(Font.font("Modena", FontWeight.NORMAL, size));
    } // end useModenaFong()

    private void useMonospaceFont(int size) {
        display.setFont(Font.font("monospace", FontWeight.BOLD, size));
    } // end useMonospaceFont() 

    private void setStyleSheet(Scene scene, String fileName) {
        File file = new File(fileName);
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:///" + file.getAbsolutePath().replace("\\", "/"));
    } // end setStyleSheet()

    public static void main(String[] args) {
        launch(args);
    } // end main
} // end class
