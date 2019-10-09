import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DetailsController {
    public Button clearBtn;
    @FXML
    private Button saveService;
    @FXML
    private Button closeBtn;
    @FXML
    private TextField last;
    @FXML
    private TextField first;
    @FXML
    private TextField phone;
    @FXML
    private TextField phone2;
    @FXML
    private TextField address1;
    @FXML
    private TextField address2;
    @FXML
    private TextField state;
    @FXML
    private TextField zip;
    @FXML
    private TextField status;
    @FXML
    private TextField id;
    @FXML
    private TextField taxcode;
    @FXML
    private TextField piano;
    @FXML
    private TextField model;
    @FXML
    private TextField serial;
    @FXML
    private TextField style;
    @FXML
    private TextField size;
    @FXML
    private TextField transferID;
    @FXML
    private TextArea remarks;
    @FXML
    private TableView<Service> serviceTable;
    @FXML
    private TableColumn<Service, LocalDate> serviceDateColumn;
    @FXML
    private TableColumn<Service, String> workDoneColumn;
    @FXML
    private TableColumn<Service, BigDecimal> serviceIncColumn;
    @FXML
    private TableColumn<Service, BigDecimal> taxableIncColumn;
    @FXML
    private TableColumn<Service, Double> amountPaidColumn;
    @FXML
    private TableColumn<Service, Double> balanceColumn;
    @FXML
    private TableColumn<Service, String> remarksColumn;
    @FXML
    private DatePicker serviceDate;
    @FXML
    private DatePicker callDate;
    @FXML
    private TextField workDone;
    @FXML
    private TextField serviceInc;
    @FXML
    private TextField taxableInc;
    @FXML
    private TextField totalInc;
    @FXML
    private TextField totalWithTax;
    @FXML
    private TextField amountPaid;
    @FXML
    private TextField salesTax;
    @FXML
    private TextField balanceDue;
    @FXML
    private TextArea paymentNotes;
    @FXML
    private TextArea serviceRemarks;
    @FXML
    private TextField serviceTransferID;

    private Executor exec; // for multi-threading
    private Customer customer;
    private Service currentService;
    private LocalDate lastServiceDate = LocalDate.now();

    @FXML
    private void close(Event event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }
    @FXML
    private ComboBox<TaxRate> pickCity;
    private ProjectUI ui = new ProjectUI();
    private String[] sizeList = {"Console","Grand","Spinet","Studio","Upright"};

    private String[] pianoList = ui.getLinesFromFile("c:/PianoApp/pianos.txt");
    private String[] workList = ui.getLinesFromFile("c:/PianoApp/workTasks.txt");

    //String[] pianoList = {"Steinway","Stieff","Bösendorfer","Fazioli","Bechstein","Blüthner","Yamaha","Kawai","Petrof","Mason and Hamlin","Baldwin","Grotrian","Estonia","Charles Walter","Wilhelm Schimmel","Steingraeber & Söhne","Samick","Crown American","Schulze Pollman","Schafer and Sons","Bösendorfer","Fazioli","Boston","Pearl River","Young Chang","Pleyel","Bartomolo","Essex"};
    //String[] workList = {"Pitch raise","Pitch raise and Tune","Tune","Delivered piano","Moved piano","Removed piano"};

    public void setSize(String value) {
        size.setText(value);
    }

    public void getSizePopup(){
        String sizeString = size.getText();
        sizeString += ui.inputChoice(sizeList) + " / ";
        if(!sizeString.contains("null"))
            size.setText(sizeString);
    }

    public void getPianoPopup(){
        String pianoString = piano.getText();
        pianoString += ui.inputChoice(pianoList) + " / ";
        if(!pianoString.contains("null"))
            piano.setText(pianoString);
    }

    public void getWorkPopup(){
        String workString = workDone.getText();
        workString += ui.inputChoice(workList) + " / ";
        if(!workString.contains("null"))
            workDone.setText(workString);
    }

    public void getMap(){
        String url = String.format("https://www.google.com/maps/place/%s+%s+%s+%s",
                address1.getText().trim(), pickCity.getValue().toString(),
                state.getText(), zip.getText());
        url = url.replaceAll(" ", "%20");
        ui.desktopBrowser(url);
    }

    @FXML
    private void initialize () {
        //For multithreading: Create executor that uses daemon threads:
        Arrays.sort(pianoList);
        paymentNotes.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                serviceRemarks.requestFocus();
            }
        });

        serviceRemarks.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                saveService.requestFocus();
            }
        });

        exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread (runnable);
            t.setDaemon(true);
            return t;
        });
        serviceDateColumn.setCellValueFactory(cellData -> cellData.getValue().serviceDateProperty());
        workDoneColumn.setCellValueFactory(cellData -> cellData.getValue().workDoneProperty());
        serviceIncColumn.setCellValueFactory(cellData -> cellData.getValue().serviceIncomeProperty());
        taxableIncColumn.setCellValueFactory(cellData -> cellData.getValue().taxableIncomeProperty());
        amountPaidColumn.setCellValueFactory(cellData -> cellData.getValue().amountPaidProperty());
        balanceColumn.setCellValueFactory(cellData -> cellData.getValue().balanceProperty());
        remarksColumn.setCellValueFactory(cellData -> cellData.getValue().remarksProperty());
    }

    private ObservableList<TaxRate> cities = FXCollections.observableArrayList();

    @FXML
    public void transferServices() {
        try {
            int from = customer.getCustomerDBNum();
            int to = Integer.parseInt(transferID.getText());
            ServiceDAO.transferServices(from, to);
            serviceTable.getItems().clear();
        } catch (Exception e){
            showMessage("There was a problem transfering services" + e);
        }
    }

    @FXML
    public void transferSingleService() {
        try {
            int from = currentService.getServiceDBNum();
            int to = Integer.parseInt(serviceTransferID.getText());
            ServiceDAO.transferService(from, to);
            clearServiceForm();
            serviceTable.getItems().remove(serviceTable.getSelectionModel().getSelectedItem());
            serviceTable.refresh();
        } catch (Exception e){
            showMessage("There was a problem transfering service" + e);
        }
    }

    public void setCityList(ObservableList<TaxRate> cities, TaxRate taxrate){
        this.cities = cities;
        try {
            pickCity.getItems().removeAll(pickCity.getItems());
            pickCity.getItems().addAll(TaxRateDAO.getTaxRateList());
        } catch (Exception e) {
            showMessage(e.getMessage());
        }
        pickCity.getSelectionModel().select(taxrate);
    }

    public void setCustomer(Customer customer){
        this.customer = customer;
        last.setText(customer.getLastName());
        first.setText(customer.getFirstName());
        phone.setText(customer.getPhone());
        phone2.setText(customer.getOtherPhone());
        address1.setText(customer.getAddress1());
        address2.setText(customer.getAddress2());
        state.setText(customer.getState());
        zip.setText(customer.getZipCode());
        id.setText(String.valueOf(customer.getCustomerDBNum()));
        taxcode.setText(String.valueOf(customer.getTaxcode()));
        piano.setText(customer.getPiano());
        model.setText(customer.getModel());
        serial.setText(customer.getSerialNbr());
        style.setText(customer.getStyle());
        size.setText(customer.getSize());
        remarks.setText(customer.getRemarks());
        status.setText(customer.getCustomerStatus());

        try {
            populateServices(ServiceDAO.searchServices(customer.getCustomerDBNum()));
        } catch (Exception e){
            showMessage(e.getMessage());
        }
    }

    // get customer data from data entry form
    private Customer getCustomerData() {
        customer.setLastName(last.getText().trim());
        customer.setFirstName(first.getText().trim());
        customer.setPhone(phone.getText().trim());
        customer.setOtherPhone(phone2.getText().trim());
        customer.setAddress1(address1.getText().trim());
        customer.setAddress2(address2.getText().trim());
        customer.setState(state.getText().trim());

        if(pickCity.getValue() != null) {
            String city = pickCity.getValue().getCity().trim();
            int code = pickCity.getValue().getCode();
            String zipCode = pickCity.getValue().getZipcode().trim();
            customer.setCity(city);
            customer.setZipCode(zipCode);
            customer.setTaxcode(code);
            taxcode.setText(String.valueOf(code)); // show change in view
            zip.setText(zipCode); // show change in view
        }

        customer.setPiano(piano.getText().trim());
        customer.setModel(model.getText().trim());
        customer.setSerialNbr(serial.getText().trim());
        customer.setStyle(style.getText().trim());
        customer.setSize(size.getText().trim());
        customer.setRemarks(remarks.getText());
        customer.setCustomerStatus(status.getText().trim().toUpperCase());

        return customer;
    }

    public void saveCustomer(ActionEvent actionEvent) {
        try {
            CustomerDAO.saveCustomer(getCustomerData());
            showMessage("Customer record updated");
        } catch (Exception e) {
            showMessage("Error saving Customer record " + e);
        }
    }

    // added by larry on 8/29/18
    public void processTab(KeyEvent e) {
        if(e.getCode() == KeyCode.TAB) {
            if(e.getSource() == serviceInc)
                sIncomeAction(new ActionEvent());
            if(e.getSource() == taxableInc)
                tIncomeAction(new ActionEvent());
            if(e.getSource() == amountPaid)
                amountPaidAction(new ActionEvent());
            if(e.getSource() == balanceDue)
                balanceDueAction(new ActionEvent());
        }
    }

    public void sIncomeAction(ActionEvent e){
        if(serviceDate.getValue() == null)
            serviceDate.setValue(LocalDate.now());
        if(serviceInc.getText().isEmpty())
            serviceInc.setText("0");
        if(taxableInc.getText().isEmpty())
            taxableInc.setText("0");
        taxableInc.requestFocus();
        if(serviceRemarks.getText().equals(""))
            serviceRemarks.setText(piano.getText());
    }

    public void tIncomeAction(ActionEvent e){
        BigDecimal si = new BigDecimal(serviceInc.getText());
        BigDecimal ti = new BigDecimal(taxableInc.getText());
        totalInc.setText(String.format("%.2f", si.add(ti)));
        totalInc.setAlignment(Pos.BASELINE_RIGHT);
        BigDecimal total = new BigDecimal(totalInc.getText());

        TaxRate rate = null;
        try {
            rate = TaxRateDAO.searchTaxRate(Integer.parseInt(taxcode.getText()));
        } catch (Exception ex) {}

        BigDecimal tax = ti.multiply(rate.getRate());
        salesTax.setText(String.format("%.2f", tax));
        salesTax.setAlignment(Pos.BASELINE_RIGHT);
        BigDecimal totalDue = total.add(tax);
        totalWithTax.setText(String.format("%.2f", total.add(tax)));
        totalWithTax.setAlignment(Pos.BASELINE_RIGHT);
        amountPaid.setText(String.format("%.2f", total.add(tax)));
        amountPaid.setAlignment(Pos.BASELINE_RIGHT);
        if(total.compareTo(BigDecimal.ZERO) > 0)
            amountPaid.requestFocus();
    }

    public void amountPaidAction(ActionEvent e) {
        BigDecimal total = new BigDecimal(totalWithTax.getText());
        BigDecimal paid = new BigDecimal(amountPaid.getText());
        BigDecimal balance = total.subtract(paid);
        balanceDue.setText(String.format("%.2f", balance));
        balanceDue.requestFocus();
    }

    public void balanceDueAction(ActionEvent e){
        //serviceRemarks.requestFocus();
        saveService.requestFocus();
    }

    public void saveServiceRecord() {
        if(serviceDate.getValue() != null) { // && !totalInc.getText().equals("0.00")) {
            if(callDate.getValue() == null) {
                LocalDate callBack = serviceDate.getValue().plusYears(1);
                LocalDate firstDay = callBack.with(TemporalAdjusters.firstDayOfMonth());
                LocalDate defaultDate = LocalDate.of(1900,01,01);
                callDate.setValue(defaultDate);
            }
            updateServiceObjectFromForm();
            lastServiceDate = serviceDate.getValue();
        }
        //else
            //showMessage("Cannot save a zero amount service record");
        serviceDate.setValue(lastServiceDate);
        closeBtn.requestFocus();
    }

    private void updateServiceObjectFromForm() {
        if(currentService != null){
            currentService.setServiceDate(serviceDate.getValue());
            currentService.setCustomerDBNum(customer.getCustomerDBNum());
            currentService.setWorkDone(workDone.getText().trim());
            currentService.setServiceIncome(new BigDecimal(serviceInc.getText()));
            currentService.setTaxableIncome(new BigDecimal(taxableInc.getText()));
            currentService.setSalesTax(new BigDecimal(salesTax.getText()));
            currentService.setAmountPaid(new BigDecimal(amountPaid.getText()));
            currentService.setBalance(new BigDecimal(balanceDue.getText()));
            currentService.setCallDate(callDate.getValue());
            currentService.setPaymentNotes(paymentNotes.getText());
            currentService.setRemarks(serviceRemarks.getText());
            currentService.setRecordStatus("A");
            try {
                ServiceDAO.updateService(currentService);
                lastServiceDate = currentService.getServiceDate();
                showMessage("Service record updated");
            } catch (Exception e) {
                showMessage(e.getMessage());
            }
        }
        else {
            if(!workDone.getText().equals("")) {
                currentService = new Service();
                currentService.setCustomerDBNum(customer.getCustomerDBNum());
                currentService.setServiceDate(serviceDate.getValue());
                currentService.setWorkDone(workDone.getText().trim());
                currentService.setServiceIncome(new BigDecimal(serviceInc.getText()));
                currentService.setTaxableIncome(new BigDecimal(taxableInc.getText()));
                currentService.setSalesTax(new BigDecimal(salesTax.getText()));
                currentService.setAmountPaid(new BigDecimal(amountPaid.getText()));
                currentService.setBalance(new BigDecimal(balanceDue.getText()));
                currentService.setCallDate(callDate.getValue());
                currentService.setPaymentNotes(paymentNotes.getText());
                currentService.setRemarks(serviceRemarks.getText());
                currentService.setRecordStatus("A");
                try {
                    ServiceDAO.insertService(currentService);
                    CustomerDAO.updateLastServiceDate(customer.getCustomerDBNum(), serviceDate.getValue());
                    serviceTable.getItems().add(currentService);
                    lastServiceDate = currentService.getServiceDate();
                    showMessage("New Service record inserted");
                } catch (Exception e) {
                    showMessage(e.getMessage());
                }
            }
            else{
                ui.showMessage("Work done is a required field");
            }
        }
    }

    @FXML
    public void clearServiceForm(){
        currentService = null;
        serviceDate.setValue(lastServiceDate);
        callDate.setValue(null);
        workDone.setText("");
        serviceInc.setText("");
        taxableInc.setText("");
        totalInc.setText("");
        salesTax.setText("");
        totalWithTax.setText("");
        amountPaid.setText("");
        balanceDue.setText("");
        paymentNotes.setText("");
        serviceRemarks.setText("");
        serviceTransferID.setText("");
    }

    //Populate Services for TableView with MultiThreading
    private void fillServiceTable(ActionEvent event) {
        Task<List<Service>> task = new Task<List<Service>>(){
            @Override
            public ObservableList<Service> call() throws Exception{
                return ServiceDAO.searchServices(customer.getCustomerDBNum());
            }
        };

        task.setOnFailed(e-> task.getException().printStackTrace());
        task.setOnSucceeded(e-> serviceTable.setItems((ObservableList<Service>) task.getValue()));
        exec.execute(task);
    }

    @FXML
    private void searchService(ActionEvent actionEvent) throws ClassNotFoundException, SQLException {
        try {
            Service service = ServiceDAO.searchService(customer.getCustomerDBNum());
            populateAndShowService(service);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    //Populate services for TableView
    @FXML
    private void populateAndShowService(Service service) throws ClassNotFoundException {
        if (customer != null) {
            populateService(service);
        } else {
            System.out.println("service not found");
        }
    }

    public void handleKeyEvent(KeyEvent e) {
        final Service selectedItem = serviceTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (e.getCode().equals(KeyCode.DELETE)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Service Record");
                alert.setHeaderText("Are you sure you want to do this?");
                alert.setContentText(String.format("Deleting serviceID: %d", selectedItem.getServiceDBNum()));
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get() != ButtonType.CANCEL) {
                    try {
                        ServiceDAO.deleteService(selectedItem);
                        serviceTable.getItems().remove(selectedItem);
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            }
        }
    } // end handleEvent delete key pressed

    //Populate services for TableView
    @FXML
    private void populateServices(ObservableList<Service> serviceData)  {
        serviceTable.setItems(serviceData);
        serviceTable.setRowFactory(
            tv -> {
                TableRow<Service> row = new TableRow<>();
                row.setOnMouseClicked(
                    e -> {
                        if (!row.isEmpty() && e.getButton()== MouseButton.PRIMARY
                                && e.getClickCount() == 2) {
                            Service service = row.getItem();
                            populateServiceForm(service);
                        }
                    });
                return row ;
            });
    }

    private void populateServiceForm(Service service) {
        currentService = service;
        workDone.setText(service.getWorkDone());
        serviceDate.setValue(service.getServiceDate());
        callDate.setValue(service.getCallDate());

        BigDecimal sIncome = service.getServiceIncome();
        BigDecimal tIncome = service.getTaxableIncome();
        BigDecimal tax = service.getSalesTax();
        BigDecimal paid = service.getAmountPaid();
        BigDecimal bal = service.getBalance();

        serviceInc.setText(String.format("%.2f", sIncome));
        serviceInc.setAlignment(Pos.BASELINE_RIGHT);

        taxableInc.setText(String.format("%.2f", tIncome));
        taxableInc.setAlignment(Pos.BASELINE_RIGHT);

        totalInc.setText(String.format("%.2f", sIncome.add(tIncome))); // form only
        totalInc.setAlignment(Pos.BASELINE_RIGHT);

        salesTax.setText(String.format("%.2f", tax));
        salesTax.setAlignment(Pos.BASELINE_RIGHT);

        BigDecimal allIncome = new BigDecimal(totalInc.getText());
        totalWithTax.setText(String.format("%.2f", allIncome.add(tax))); // form only
        totalWithTax.setAlignment(Pos.BASELINE_RIGHT);

        amountPaid.setText(String.format("%.2f", paid));
        amountPaid.setAlignment(Pos.BASELINE_RIGHT);

        balanceDue.setText(String.format("%.2f", bal));
        balanceDue.setAlignment(Pos.BASELINE_RIGHT);

        paymentNotes.setText(service.getPaymentNotes());
        serviceRemarks.setText(service.getRemarks());
    }

    @FXML
    private void populateService(Service service) throws ClassNotFoundException {
        ObservableList<Service> serviceData = FXCollections.observableArrayList();
        serviceData.add(service);
        serviceTable.setItems(serviceData);
    }

    public void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }

    public boolean customerChanged(Customer c) {
        if(!last.getText().equals(c.getLastName()))
            return true;
        else if(!first.getText().equals(c.getFirstName()))
            return true;
        else if(!phone.getText().equals(c.getPhone()))
            return true;
        else if(!phone2.getText().equals(c.getOtherPhone()))
            return true;
        else if(!piano.getText().equals(c.getPiano()))
            return true;
        else if(!model.getText().equals(c.getModel()))
            return true;
        else if(!serial.getText().equals(c.getSerialNbr()))
            return true;
        else if(!size.getText().equals(c.getSize()))
            return true;
        else if(!style.getText().equals(c.getStyle()))
            return true;
        else if(!address1.getText().equals(c.getAddress1()))
            return true;
        else if(!address2.getText().equals(c.getAddress2()))
            return true;
        else if(!pickCity.getValue().getCity().equals(c.getCity()))
            return true;
        else if(!state.getText().equals(c.getState()))
            return true;
        else if(!zip.getText().equals(c.getZipCode()))
            return true;
        else return !remarks.getText().equals(c.getRemarks());
    }

    public boolean serviceChanged(Service s){
        if(!workDone.getText().equals(s.getWorkDone()))
            return true;
        else if(!new BigDecimal(serviceInc.getText()).equals(s.getServiceIncome()))
            return true;
        else if(!new BigDecimal(taxableInc.getText()).equals(s.getTaxableIncome()))
            return true;
        else if(!new BigDecimal(balanceDue.getText()).equals(s.getBalance()))
            return true;
        else if(!paymentNotes.getText().equals(s.getPaymentNotes()))
            return true;
        else if(!serviceRemarks.getText().equals(s.getRemarks()))
            return true;
        else return serviceDate.getValue() != s.getServiceDate();
    }

    //Help Menu button behavior
    public void handleHelp(ActionEvent actionEvent) {
        Alert alert = new Alert (Alert.AlertType.INFORMATION);
        alert.setTitle("Program Information");
        alert.setHeaderText("This is a JavaFX Application");
        alert.setContentText("You can search, delete, update, insert a new Customer with this program.");
        alert.show();
    }

    public void setWorkFocus() {
        workDone.requestFocus();
    }
} // end class
