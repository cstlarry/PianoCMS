import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerDAO {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    static Customer searchCustomer(String customerDBNum) throws SQLException, ClassNotFoundException {
        String selectStmt = "SELECT * FROM customer WHERE customerDBNum=" + customerDBNum;
        try {
            ResultSet rsCustomer = DBUtil.dbExecuteQuery(selectStmt);
            return getCustomerFromResultSet(rsCustomer);
        } catch (SQLException e) {
            showMessage("While searching an customer with " + customerDBNum + " id, an error occurred: " + e);
            throw e;
        }
    }

    private static Customer getCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = null;
        if (rs.next()) {
            customer = new Customer();
            customer.setCustomerDBNum(rs.getInt("customerDBNum"));
            customer.setFirstName(rs.getString("firstName"));
            customer.setLastName(rs.getString("lastName"));
            customer.setPhone(rs.getString("phone"));
            customer.setOtherPhone(rs.getString("otherPhone"));
            customer.setAddress1(rs.getString("address1"));
            customer.setAddress2(rs.getString("address2"));
            customer.setCity(rs.getString("city"));
            customer.setState(rs.getString("state"));
            customer.setZipCode(rs.getString("zipcode"));
            customer.setRemarks(rs.getString("remarks"));
            customer.setPiano(rs.getString("piano"));
            customer.setModel(rs.getString("model"));
            customer.setSerialNbr(rs.getString("serialNbr"));
            customer.setStyle(rs.getString("style"));
            customer.setSize(rs.getString("size"));
            customer.setEntryDate(rs.getDate("entryDate"));
            customer.setLastServiceDate(rs.getDate("lastServiceDate"));
            customer.setTaxcode(rs.getInt("taxcode"));
            customer.setCustomerStatus(rs.getString("customerStatus"));
        }
        return customer;
    }

    static ObservableList<Customer> searchCustomers() throws SQLException, ClassNotFoundException {
        String selectStmt = "SELECT * FROM customer WHERE customerStatus = 'A' ORDER BY customerDBNum DESC";
        try {
            ResultSet rs = DBUtil.dbExecuteQuery(selectStmt);
            return getCustomerList(rs);
        } catch (SQLException e) {
            showMessage("SQL select operation has been failed: " + e);
            throw e;
        }
    }

    static ArrayList<Customer> getCustomerList() throws SQLException, ClassNotFoundException {
        String selectStmt = "SELECT * FROM customer WHERE customerStatus = 'A' ORDER BY lastName, firstName";
        try {
            ResultSet rs = DBUtil.dbExecuteQuery(selectStmt);
            System.out.println(selectStmt);
            return getListFromResultSet(rs);
        } catch (SQLException e) {
            showMessage("SQL select operation has been failed: " + e);
            throw e;
        }
    }

    static ArrayList<Customer> getCustomers() throws SQLException, ClassNotFoundException {
        String selectStmt = "SELECT * FROM customer order by lastName, firstName";
        try {
            ResultSet rs = DBUtil.dbExecuteQuery(selectStmt);
            return getListFromResultSet(rs);
        } catch (SQLException e) {
            showMessage("SQL select operation has been failed: " + e);
            throw e;
        }
    }

    public static List<Customer> getCustomersAsList() {
        List<Customer> list = null;
        try {
            list = new ArrayList<>(searchCustomers());
        } catch (Exception e) {
            showMessage("getCustomersAsList operation has failed: " + e);
        }
        return list;
    }

    static ObservableList<Customer> searchCustomersByFieldValue(String fieldName, String value) throws SQLException, ClassNotFoundException {
        String selectStmt = "";
        value = value.trim();
        switch(fieldName) {
            case "lastName":
                selectStmt = String.format("SELECT * FROM customer where lastName like '%%%s%%'", value);
                break;
            case "firstName":
                selectStmt = String.format("SELECT * FROM customer where firstName like '%%%s%%'", value);
                break;
            case "phone":
                selectStmt = String.format("SELECT * FROM customer where phone like '%%%s%%'", value);
                break;
            case "city":
                selectStmt = String.format("SELECT * FROM customer where city like '%%%s%%'", value);
                break;
            case "piano":
                selectStmt = String.format("SELECT * FROM customer where piano like '%%%s%%'", value);
                break;
            case "street":
                selectStmt = String.format("SELECT * FROM customer where address1 like '%%%s%%'", value);
                break;
            case "status":
                selectStmt = String.format("SELECT * FROM customer where customerStatus = '%s'", value);
                break;
        }
        try {
            ResultSet rs = DBUtil.dbExecuteQuery(selectStmt);
            return getCustomerList(rs);
        } catch (SQLException e) {
            showMessage("SQL select operation has been failed: " + e);
            throw e;
        }
    }

    static ArrayList<String> getCallBackList(LocalDate callBackDate) throws SQLException, ClassNotFoundException {
        String selectStmt = String.format(
                "SELECT c.firstName, left(c.lastName, 15), c.phone, s.workDone, s.servicedate FROM service s, customer c " +
                "WHERE s.customerdbnum = c.customerdbnum && s.calldate = '%s'",
                callBackDate);
        try {
            ResultSet rs = DBUtil.dbExecuteQuery(selectStmt);
            return getListFromCallBackResults(rs);
        } catch (Exception e){
            showMessage("While searching servce call back records " + e);
            throw e;
        }
    }

    private static ArrayList<String> getListFromCallBackResults(ResultSet rs) throws SQLException {
        ArrayList<String> list = new ArrayList<>();
        String header = String.format("%-20s%-20s%15s%15s  %s","First Name","Last Name","Phone Nbr","Service Date","Work");
        list.add(header);
        while (rs.next()) {
            String record = String.format("%-20s%-20s%15s%15s  %s",
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                formatter.format(LocalDate.parse(String.valueOf(rs.getDate("servicedate")))),
                rs.getString("workDone").substring(0, 4)
            );
            list.add(record);
        }
        return list;
    }

    static void saveCustomer(Customer customer) throws SQLException, ClassNotFoundException {
        String updateStmt = String.format(
                "UPDATE customer " +
                    "SET lastName = '%s', " +
                    "firstName = '%s', " +
                    "phone = '%s', " +
                    "otherPhone = '%s', " +
                    "address1 = '%s', " +
                    "address2 = '%s', " +
                    "city = '%s', " +
                    "state = '%s', " +
                    "zipcode = '%s', " +
                    "remarks = '%s', " +
                    "piano = '%s', " +
                    "model = '%s', " +
                    "serialNbr = '%s', " +
                    "style = '%s', " +
                    "size = '%s', " +
                    "customerStatus = '%s' " +
                "WHERE customerDBNum = %d",
                customer.getLastName().trim(),
                customer.getFirstName().trim(),
                customer.getPhone().trim(),
                customer.getOtherPhone().trim(),
                customer.getAddress1().trim(),
                customer.getAddress2().trim(),
                customer.getCity().trim(),
                customer.getState().trim(),
                customer.getZipCode().trim(),
                customer.getRemarks(),
                customer.getPiano().trim(),
                customer.getModel().trim(),
                customer.getSerialNbr().trim(),
                customer.getStyle().trim(),
                customer.getSize().trim(),
                customer.getCustomerStatus().trim(),
                customer.getCustomerDBNum());
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (Exception e) {
            showMessage("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }
    static void updateStatus(int customerDBNum, String status) throws SQLException, ClassNotFoundException {
        String updateStmt = String.format(
                "UPDATE customer SET customerStatus = '%s' WHERE customerDBNum = %d",
                status, customerDBNum);
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            showMessage("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }
    static void updateLastServiceDate(int customerDBNum, LocalDate serviceDate) throws SQLException, ClassNotFoundException {
        String updateStmt = String.format(
                "UPDATE customer SET lastServiceDate = '%s' WHERE customerDBNum = %d",
                serviceDate, customerDBNum);
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            showMessage("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }
    static void updateLastService() throws SQLException, ClassNotFoundException{
        String updateStmt = "UPDATE pianodb.customer\n" +
                "SET    lastServiceDate = (SELECT   serviceDate\n" +
                "                  FROM     pianodb.service\n" +
                "                  WHERE    pianodb.service.customerDBNum = pianodb.customer.customerDBNum\n" +
                "                  ORDER BY serviceDate DESC\n" +
                "                  LIMIT    1);";
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            showMessage("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    static void deleteCustomerWithId(String customerDBNum) throws SQLException, ClassNotFoundException {
        String updateStmt = String.format(
                "DELETE FROM customer WHERE customerDBNum = %s", 
                customerDBNum);
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            showMessage("Error occurred while DELETE Operation: " + e);
            throw e;
        }
    }

    static void insertCustomer(String firstName, String lastName, String phone, String city, String zip, int taxCode) throws SQLException, ClassNotFoundException {
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        String updateStmt = String.format(
            "INSERT INTO customer (firstName, lastName, phone, city, state, zipcode, taxCode, entryDate, remarks)"+
            "VALUES ('%s','%s','%s','%s','WA','%s','%s','%s','%s')",
            firstName, lastName, phone, city, zip, taxCode, date,' ');
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            showMessage("Error occurred while Insert Operation: " + e);
            throw e;
        }
    }

    private static ArrayList<Customer> getListFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<Customer> list = new ArrayList<>();
        return (ArrayList<Customer>)buildCustomerListFromResultSet(list, rs);
    }

    private static ObservableList<Customer> getCustomerList(ResultSet rs) throws SQLException {
        ObservableList<Customer> list = FXCollections.observableArrayList();
        return (ObservableList<Customer>)buildCustomerListFromResultSet(list, rs);
    }

    private static List<Customer> buildCustomerListFromResultSet(List<Customer> list, ResultSet rs) throws SQLException {
        while (rs.next()) {
            Customer customer = new Customer();
            customer.setCustomerDBNum(rs.getInt("customerDBNum"));
            customer.setFirstName(rs.getString("firstName"));
            customer.setLastName(rs.getString("lastName"));
            customer.setPhone(rs.getString("phone"));
            customer.setOtherPhone(rs.getString("otherPhone"));
            customer.setAddress1(rs.getString("address1"));
            customer.setAddress2(rs.getString("address2"));
            customer.setCity(rs.getString("city"));
            customer.setState(rs.getString("state"));
            customer.setZipCode(rs.getString("zipcode"));
            customer.setRemarks(rs.getString("remarks"));
            customer.setPiano(rs.getString("piano"));
            customer.setModel(rs.getString("model"));
            customer.setSerialNbr(rs.getString("serialNbr"));
            customer.setStyle(rs.getString("style"));
            customer.setSize(rs.getString("size"));
            customer.setEntryDate(rs.getDate("entryDate"));
            customer.setLastServiceDate(rs.getDate("lastServiceDate"));
            customer.setTaxcode(rs.getInt("taxcode"));
            customer.setCustomerStatus(rs.getString("customerStatus"));

            list.add(customer);
        }
        return list;
    }

    public static void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }
} // end class
