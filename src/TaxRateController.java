import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaxRateController {
    public Button Save;
    public Button clearBtn;
    public Button addNew;
    @FXML
    private TextField taxcode;
    @FXML
    private TextField city;
    @FXML
    private TextField zipcode;
    @FXML
    private TextField taxrate;
    @FXML
    private TextField local;
    @FXML
    private TextField state;

    @FXML
    private TableView<TaxRate> taxRateTable;
    @FXML
    private TableColumn<TaxRate, Integer> codeColumn;
    @FXML
    private TableColumn<TaxRate, String> cityColumn;
    @FXML
    private TableColumn<TaxRate, String> zipcodeColumn;
    @FXML
    private TableColumn<TaxRate, BigDecimal> rateColumn;
    @FXML
    private TableColumn<TaxRate, BigDecimal> localColumn;
    @FXML
    private TableColumn<TaxRate, Integer> taxRateDBNumColumn;
    @FXML
    private TableColumn<TaxRate, BigDecimal> stateColumn;

    private int selectedRateID = 0;

    private Executor exec; // for multi-threading

    //This method is automatically called after the fxml file has been loaded.
    @FXML
    private void initialize () {
        //For multithreading: Create executor that uses daemon threads:
        exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread (runnable);
            t.setDaemon(true);
            return t;
        });

        codeColumn.setCellValueFactory(cellData -> cellData.getValue().codeProperty().asObject());
        cityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
        zipcodeColumn.setCellValueFactory(cellData -> cellData.getValue().zipcodeProperty());
        rateColumn.setCellValueFactory(cellData -> cellData.getValue().rateProperty());
        localColumn.setCellValueFactory(cellData -> cellData.getValue().localProperty());
        stateColumn.setCellValueFactory(cellData -> cellData.getValue().stateProperty());
        taxRateDBNumColumn.setCellValueFactory(cellData -> cellData.getValue().taxRateDBNumProperty().asObject());

    }

    public void clearFields(){
        taxcode.setText("");
        city.setText("");
        zipcode.setText("");
        taxrate.setText("");
        local.setText("");
        state.setText("");
    }

    // get taxrate data from data entry form
    private TaxRate getTaxRateData() {
        TaxRate rate = new TaxRate();
        rate.setCode(Integer.parseInt(taxcode.getText().trim()));
        rate.setCity(city.getText().trim());
        rate.setZipcode(zipcode.getText().trim());
        rate.setRate(new BigDecimal(taxrate.getText().trim()));
        rate.setLocal(new BigDecimal(local.getText().trim()));
        rate.setState(new BigDecimal(state.getText().trim()));
        rate.setTaxRateDBNum(selectedRateID);
        return rate;
    }

    public void insertTaxRate(ActionEvent event) {
        try {
            TaxRateDAO.insertTaxRate(getTaxRateData());
            showMessage("New TaxRate record inserted");
            searchTaxRates();
        } catch (ClassNotFoundException | SQLException e) {
            showMessage("Error inserting TaxRate record " + e);
        }
    }

    public void saveTaxRate(ActionEvent actionEvent) {
        try {
            TaxRateDAO.updateTaxRate(getTaxRateData());
            showMessage("TaxRate record updated");
            searchTaxRates();
        } catch (SQLException | ClassNotFoundException e) {
            showMessage("Error saving TaxRate record " + e);
        }
    }

    void searchTaxRates() throws ClassNotFoundException, SQLException {
        try {
            ObservableList<TaxRate> taxRateData = TaxRateDAO.searchTaxRates();
            populateTaxRates(taxRateData);
        } catch (ClassNotFoundException | SQLException e){
            showMessage("Error occurred while getting taxrate information from DB.\n" + e);
            throw e;
        }
    }
    //Populate TaxRates for TableView
    @FXML
    private void populateTaxRates(ObservableList<TaxRate> taxRateData) {
        taxRateTable.setItems(taxRateData);
        taxRateTable.setRowFactory(
            tv -> {
                TableRow<TaxRate> row = new TableRow<>();
                row.setOnMouseClicked(
                        e -> {
                            if (!row.isEmpty() && e.getButton()== MouseButton.PRIMARY
                                    && e.getClickCount() == 2) {
                                TaxRate taxrate = row.getItem();
                                populateTaxRateForm(taxrate);
                            }
                        });
                return row ;
            });
    }

    private void populateTaxRateForm(TaxRate rate) {
        taxcode.setText(String.format("%d", rate.getCode()));
        city.setText(rate.getCity());
        zipcode.setText(rate.getZipcode());
        taxrate.setText(String.valueOf(rate.getRate()));
        local.setText(String.valueOf(rate.getLocal()));
        state.setText(String.valueOf(rate.getState()));
        selectedRateID = rate.getTaxRateDBNum();
    }

    public void handleKeyEvent(KeyEvent e) {
        final TaxRate selectedItem = taxRateTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            if (e.getCode().equals(KeyCode.DELETE)) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete TaxRate Record");
                alert.setHeaderText("Are you sure you want to do this?");
                alert.setContentText(String.format("Deleting TaxRateDBNum: %d", selectedItem.getTaxRateDBNum()));
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get() != ButtonType.CANCEL) {
                    try {
                        TaxRateDAO.deleteTaxRate(selectedItem);
                        taxRateTable.getItems().remove(selectedItem);
                    } catch (Exception ex) {
                        showMessage(ex.getMessage());
                    }
                }
            }
        }
    } // end handleEvent delete key pressed


    public void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }
} // end class
