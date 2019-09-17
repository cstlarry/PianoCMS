import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.Serializable;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class CustomerController implements Serializable {
    public Button searchCustBtn;
    public Button searchCustomersBtn;
    public Button searchLast;
    public Button searchFirst;
    public Button searchPhone;
    public Button searchCity;
    public Button pianoSearchBtn;
    public Button streetSearchBtn;
    public Button statusSearchBtn;
    @FXML
    private Button addNewBtn;
    @FXML
    private Label filterLabel;
    @FXML
    private TextField customerDBNumText;
    @FXML
    private TextField phoneText;
    @FXML
    private TextField firstNameText;
    @FXML
    private TextField lastNameText;
    @FXML
    private TextField pianoSearch;
    @FXML
    private TextField statusSearch;
    @FXML
    private TextField streetSearch;
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> customerDBNumColumn;
    @FXML
    private TableColumn<Customer, String> firstNameColumn;
    @FXML
    private TableColumn<Customer, String> lastNameColumn;
    @FXML
    private TableColumn<Customer, String> cityColumn;
    @FXML
    private TableColumn<Customer, String> phoneColumn;
    @FXML
    private TableColumn<Customer, Date> lastServiceDateColumn;
    @FXML
    private ComboBox<TaxRate> pickCity;

    private ObservableList<TaxRate> cities = FXCollections.observableArrayList();
    private ObservableList<TaxRate> originalItems;
    private ObservableList<TaxRate> filteredList = FXCollections.observableArrayList();
    private String filter = "";

    //For MultiThreading
    private Executor exec;

    void setData(){
        try {
            pickCity.getItems().removeAll(pickCity.getItems());
            pickCity.getItems().addAll(TaxRateDAO.getTaxRateList());
            originalItems = FXCollections.observableArrayList(pickCity.getItems());
            pickCity.setTooltip(new Tooltip());
        } catch (Exception e) {
            showMessage(e.getMessage());
        }
        lastNameText.requestFocus();
    }

    //This method is automatically called after the fxml file has been loaded.
    @FXML
    private void initialize () {
        pickCity.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && !oldValue) {
                // transition from unfocused to focused -> expand choicebox
                if (!pickCity.isShowing()) {
                    pickCity.show();
                }
            }
        });

        lastNameText.requestFocus();
        //For multithreading: Create executor that uses daemon threads:
        exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread (runnable);
            t.setDaemon(true);
            return t;
        });
        
        customerDBNumColumn.setCellValueFactory(cellData -> cellData.getValue().customerDBNumProperty().asObject());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        cityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        lastServiceDateColumn.setCellValueFactory(cellData -> cellData.getValue().lastServiceDateProperty());
    }

    @FXML
    private void clearFields() {
        firstNameText.setText("");
        lastNameText.setText("");
        phoneText.setText("");
        pianoSearch.setText("");
        streetSearch.setText("");
        statusSearch.setText("");
        customerDBNumText.setText("");
        pickCity.setValue(null);
        lastNameText.requestFocus();
    }

    @FXML
    private void searchCustomers(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            ObservableList<Customer> customerData = CustomerDAO.searchCustomers();
            populateCustomers(customerData);
        } catch (SQLException e){
            showMessage("Error occurred while getting customers information from DB.\n" + e);
            throw e;
        }
    }

    //Search for a customer
    @FXML
    private void searchCustomer(ActionEvent actionEvent) throws ClassNotFoundException, SQLException {
        try {
            Customer customer = CustomerDAO.searchCustomer(customerDBNumText.getText());
            populateAndShowCustomer(customer);
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Error occurred while getting customer information from DB.\n" + e);
            throw e;
        }
    }

    @FXML
    private void searchCustomersByLastName(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            ObservableList<Customer> customerData = CustomerDAO.searchCustomersByFieldValue("lastName", lastNameText.getText());
            populateCustomers(customerData);
        } catch (SQLException e){
            showMessage("Error occurred while getting customers information from DB.\n" + e);
            throw e;
        }
    }
    @FXML
    private void searchCustomersByFirstName(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            ObservableList<Customer> customerData = CustomerDAO.searchCustomersByFieldValue("firstName", firstNameText.getText().trim());
            populateCustomers(customerData);
        } catch (SQLException e){
            showMessage("Error occurred while getting customers information from DB.\n" + e);
            throw e;
        }
    }
    @FXML
    private void searchCustomersByPhone(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            ObservableList<Customer> customerData = CustomerDAO.searchCustomersByFieldValue("phone", phoneText.getText().trim());
            populateCustomers(customerData);
        } catch (SQLException e){
            showMessage("Error occurred while getting customers information from DB.\n" + e);
            throw e;
        }
    }

    @FXML
    private void searchCustomersByCity(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            if(pickCity.getValue() != null) {
                String city = pickCity.getValue().getCity();
                ObservableList<Customer> customerData = CustomerDAO.searchCustomersByFieldValue("city", city);
                populateCustomers(customerData);
            }
        } catch (SQLException e){
            showMessage("Error occurred while getting customers information from DB.\n" + e);
            throw e;
        }
    }

    @FXML
    private void searchByPiano(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            ObservableList<Customer> customerData = CustomerDAO.searchCustomersByFieldValue("piano", pianoSearch.getText().trim());
            populateCustomers(customerData);
        } catch (SQLException e){
            showMessage("Error occurred while getting customers information from DB.\n" + e);
            throw e;
        }
    }

    @FXML
    private void searchByStreet(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            ObservableList<Customer> customerData = CustomerDAO.searchCustomersByFieldValue("street", streetSearch.getText().trim());
            populateCustomers(customerData);
        } catch (SQLException e){
            showMessage("Error occurred while getting customers information from DB.\n" + e);
            throw e;
        }
    }

    @FXML
    private void searchByStatus(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            ObservableList<Customer> customerData = CustomerDAO.searchCustomersByFieldValue("status", statusSearch.getText().trim());
            populateCustomers(customerData);
        } catch (SQLException e){
            showMessage("Error occurred while getting customers information from DB.\n" + e);
            throw e;
        }
    }

    @FXML
    private void populateCustomer(Customer customer) {
        ObservableList<Customer> customerData = FXCollections.observableArrayList();
        customerData.add(customer);
        customerTable.setItems(customerData);
        activateTable();
    }

    private void activateTable(){
        customerTable.setRowFactory(
            tv -> {
                TableRow<Customer> row = new TableRow<>();
                row.setOnMouseClicked(
                    e -> {
                        if (!row.isEmpty() && e.getButton()==MouseButton.PRIMARY
                                && e.getClickCount() == 2) {
                            Customer customer = row.getItem();
                            openCustomerDetails(customer);
                        }
                    });
                return row ;
            });
    }

    //Populate Customer for TableView and Display Customer on TextArea
    @FXML
    private void populateAndShowCustomer(Customer customer) {
        if (customer != null) {
            populateCustomer(customer);
        } else {
            showMessage("This customer does not exist!\n");
        }
    }

    //Populate Customers for TableView
    @FXML
    private void populateCustomers(ObservableList<Customer> customerData) {
        customerTable.setItems(customerData);
        activateTable();
    }

    private void openCustomerDetails(Customer customer) {
        try {
            lastNameText.requestFocus();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/ServiceView.fxml"));
            AnchorPane rootLayout = loader.load();
            DetailsController dc = loader.getController();
            dc.setCustomer(customer);
            dc.setCityList(cities, TaxRateDAO.searchTaxRateCity(customer.getCity()));
            Scene scene = new Scene(rootLayout); //We are sending rootLayout to the Scene.
            scene.getStylesheets().add("customer.css");

            scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    Node focusOwner = scene.getFocusOwner();
                    if (focusOwner instanceof Button) {
                        ((Button) focusOwner).fire();
                    }
                }
            });

            scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                final KeyCombination keyU = new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN);
                final KeyCombination keyS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
                final KeyCombination keyP = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
                final KeyCombination keyG = new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN);
                final KeyCombination keyC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

                public void handle(KeyEvent e) {
                         if (keyU.match(e)) dc.setSize("Upright");
                    else if (keyC.match(e)) dc.setSize("Console");
                    else if (keyS.match(e)) dc.setSize("Studio");
                    else if (keyP.match(e)) dc.setSize("Spinet");
                    else if (keyG.match(e)) dc.setSize("Grand");
                }
            });
            Stage stage = new Stage();
            stage.setTitle("Customer Services Entry/Edit");
            stage.setScene(scene); //Set the scene in primary stage.

            stage.setWidth(1360);
            stage.setHeight(760);
            stage.setMinWidth(stage.getWidth());
            stage.setMinHeight(stage.getHeight());
            stage.setMaximized(true);
            /*
            stage.setOnHiding(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(dc.customerChanged(customer)) {
                                    dc.saveCustomer(new ActionEvent());
                                }
                            } catch (Exception e) {}
                        }
                    });
                }
            });
            */
            stage.show(); //Display the primary stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void insertCustomer(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            if(pickCity.getValue() != null) {
                String city = pickCity.getValue().getCity().trim();
                int taxCode = pickCity.getValue().getCode();
                String zip = pickCity.getValue().getZipcode().trim();
                String lastName = lastNameText.getText().trim();
                String firstName = firstNameText.getText().trim();
                String phone = phoneText.getText().trim();
                if(!lastName.isEmpty() && !phone.isEmpty()) {
                    CustomerDAO.insertCustomer(firstName, lastName, phone, city, zip, taxCode);
                }
                else {
                    searchCustomersByCity(actionEvent);
                    //showMessage("Last name and phone are required fields.");
                    lastNameText.requestFocus();
                    return;
                }
            }
            searchCustomers(actionEvent);
            clearFields();
        } catch (SQLException e) {
            showMessage("Problem occurred while inserting customer " + e);
            throw e;
        }
    }

    //Delete an customer with a given customer Id from DB
    @FXML
    private void deleteCustomer(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            CustomerDAO.deleteCustomerWithId(customerDBNumText.getText());
            showMessage("Customer deleted! Customer id: " + customerDBNumText.getText() + "\n");
        } catch (SQLException e) {
            showMessage("Problem occurred while deleting customer " + e);
            throw e;
        }
    }

    public void handleKeyEvent(KeyEvent e) {
        final Customer customer = customerTable.getSelectionModel().getSelectedItem();
        if (customer != null) {
            if (e.getCode().equals(KeyCode.DELETE)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Service Record");
                alert.setHeaderText("Are you sure you want to do this?");
                alert.setContentText(String.format("Deleting customer ID: %d will also delete all related services", customer.getCustomerDBNum()));
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get() != ButtonType.CANCEL) {
                    try {
                        ServiceDAO.deleteServicesForCustomer(customer);
                        customerTable.getItems().remove(customer);
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            }
        }
    } // end handleEvent delete key pressed

    public void handleOnKeyPressed(KeyEvent e) {
        ObservableList<TaxRate> filteredList = FXCollections.observableArrayList();
        KeyCode code = e.getCode();

        if (code.isLetterKey()) {
            filter += e.getText();
        }
        if (code == KeyCode.BACK_SPACE && filter.length() > 0) {
            filter = filter.substring(0, filter.length() - 1);
            pickCity.getItems().setAll(originalItems);
        }
        if (code == KeyCode.ESCAPE) {
            filter = "";
        }
        if (filter.length() == 0) {
            setData();
            filteredList = originalItems;
            filterLabel.setText("");
        }
        else {
            Stream<TaxRate> items = pickCity.getItems().stream();
            String txtUsr = filter.toLowerCase();
            items.filter(el -> el.toString().toLowerCase().contains(txtUsr)).forEach(filteredList::add);
            filterLabel.setText(txtUsr);
        }
        pickCity.getItems().setAll(filteredList);
    }

    public void handleOnHiding(Event e) {
        filter = "";
        filterLabel.setText("");
        TaxRate city = pickCity.getSelectionModel().getSelectedItem();
        pickCity.getItems().setAll(originalItems);
        pickCity.getSelectionModel().select(city);
        addNewBtn.requestFocus();
    }

    public void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }
} // end class
