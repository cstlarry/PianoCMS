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
public class StatusReport extends Application {

    // make the main container a BorderPane
    private final BorderPane root = new BorderPane();

    // declare ui component and other needed variables
    private final Button runBtn = new Button("Run");
    private final Button clearOutputBtn = new Button("Clear Output");
    private final Button clearFieldsBtn = new Button("Clear Fields");
    private final Button printBtn = new Button("Print");
    private final CheckBox check = new CheckBox("Monospace Font");
    private final TextArea display = new TextArea();
    private final Label statusLab = new Label("Status");

    private final Clipboard clipboard = Clipboard.getSystemClipboard();
    private final ClipboardContent content = new ClipboardContent();

    private final StringBuilder output = new StringBuilder(128);
    private LocalDate today = LocalDate.now();
    private int outputCount = 0;
    private String appTitle;
    private ProjectUI ui;

    // fill root container with UI components and return it
    private Parent createContent() {
        root.setPadding(new Insets(25));
        display.setPrefColumnCount(50);
        display.setWrapText(true);
        clearFieldsBtn.setDisable(ui.getRows() == 0);

        // set up action listeners
        clearOutputBtn.setOnAction(e -> clearOutput());
        clearFieldsBtn.setOnAction(e -> ui.clearFields());
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
                    /*
                    try {
                        Runtime runTime = Runtime.getRuntime();
                        Process process = runTime.exec("notepad "+ filename);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    */
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
    private final ObservableList<String> items = FXCollections.observableArrayList();
    private final ComboBox<String> list = new ComboBox<>(items);

    private ArrayList<Customer> customers;

    // commonly changed content properties.  Moved here for proximity to the run method
    private void setup() {
        ui = new ProjectUI("Enter status value(s)");
        try {
            customers = CustomerDAO.getCustomers();
            CustomerDAO.updateLastService();
        } catch (Exception e) {
            println(e.getMessage());
        }
        appTitle = "Customer Status Report";
        check.setSelected(false);
    } // end setup()

    private void run() {
        String statusString = ui.getField(0).toUpperCase();
        for(Customer customer: customers) {
            if (statusString.contains(customer.getCustomerStatus())) {
                String date = "";
                String city = customer.getCity();
                String piano = customer.getPiano();
                String size = customer.getSize();
                String style = customer.getStyle();
                try {
                    date = customer.getLastServiceDate().toString();
                } catch (Exception e2){
                    println("No last service date");
                }
                String result = String.format("%s  CITY: %s PIANO: %s %s %s SERVICED: %s", customer, city, piano, size, style, date);
                outputln(result);
            }
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
             PrintWriter writer = new PrintWriter(new FileWriter(file))
        ) { reader.lines().forEach(writer::println); }
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
