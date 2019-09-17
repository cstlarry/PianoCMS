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
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;
import java.time.*;
import java.io.*;
import java.nio.file.*;
import java.sql.*;

// Change the class name and do a Save As to start a new project
public class BalanceDueReport extends Application {

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

    StringBuilder output = new StringBuilder(128);
    LocalDate today = LocalDate.now();

    private Clipboard clipboard = Clipboard.getSystemClipboard();
    private ClipboardContent content = new ClipboardContent();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    int outputCount = 0;
    String appTitle;
    ProjectUI ui;

    // fill root container with UI components and return it
    private Parent createContent() {
        root.setPadding(new Insets(25));
        display.setPrefColumnCount(50);
        display.setPrefRowCount(100);
        clearFieldsBtn.setDisable(ui.getRows() == 0);

        // set up action listeners
        clearOutputBtn.setOnAction(e -> {
            clearOutput();
        });
        clearFieldsBtn.setOnAction(e -> {
            ui.clearFields();
        });
        printBtn.setOnAction(e -> {
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
        runBtn.setOnAction(e -> {
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
        Scene scene = new Scene(createContent(), 900, 500);
        // un-comment and edit the following line to load a css file
        scene.getStylesheets().add("reports.css");
        stage.setScene(scene);
        stage.show();
        stage.sizeToScene();
    } // end start  

    // Replace "String" below with the object type that populates the ComboBox
    ObservableList<String> items = FXCollections.observableArrayList();
    ComboBox<String> list = new ComboBox<>(items);

    // commonly changed content properties.  Moved here for proximity to the run method
    private void setup() {
        ui = new ProjectUI();

        appTitle = "Balance Due Report";
        check.setSelected(false);
    } // end setup()

    private void run() {
        // your code goes here
        BigDecimal total = new BigDecimal(BigInteger.ZERO);

        outputln(String.format("%-30s%12s%26s%14s%n", "Customer Name", "Phone Nbr", "Amount Due", "Service Date"));
        try {
            for (Service service : ServiceDAO.getPaymentsDue()) {
                BigDecimal amount = service.getBalance();
                String serviceDate = formatter.format(service.getServiceDate());
                Customer customer = CustomerDAO.searchCustomer(String.valueOf(service.getCustomerDBNum()));
                outputln(String.format("%-30s%12s%n\t%-40s%10.2f%12s", customer.shortName(), customer.getPhone(), service, amount, serviceDate));
                total = total.add(amount);
            }
        } catch (Exception e){
            outputln(e.getMessage());
        }
        outputln(String.format("%58s%n%46s%12.2f", "----------------------------","Total Due:", total));
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
        try (
                BufferedReader reader = new BufferedReader(new StringReader(string));
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
