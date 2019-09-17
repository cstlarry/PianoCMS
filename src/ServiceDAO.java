import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    static ArrayList<Service> getQuarterlyTaxes(LocalDate start, LocalDate end) throws SQLException, ClassNotFoundException {
        String selectStmt = String.format(
                "SELECT * FROM service WHERE serviceDate >= '%s' && serviceDate <= '%s'",
                start, end);
        try {
            ResultSet rsTaxes = DBUtil.dbExecuteQuery(selectStmt);
            return getListFromResultSet(rsTaxes);
        } catch (Exception e){
            showMessage("While searching servce tax records " + e);
            throw e;
        }
    }

    static ArrayList<Service> getPaymentsDue() throws SQLException, ClassNotFoundException {
        String selectStmt = "SELECT * FROM service WHERE balance > 0";
        try {
            ResultSet rsTaxes = DBUtil.dbExecuteQuery(selectStmt);
            return getListFromResultSet(rsTaxes);
        } catch (Exception e){
            showMessage("While searching servce payments due " + e);
            throw e;
        }
    }

    static String getTaxCode(Service service) throws SQLException, ClassNotFoundException {
        String selectStmt = String.format("SELECT taxCode FROM customer WHERE customerDBNum = %d",
                service.getCustomerDBNum());
        try {
            ResultSet rs = DBUtil.dbExecuteQuery(selectStmt);
            if(rs.next())
                return String.valueOf(rs.getInt("taxCode"));
        } catch (Exception e){
            showMessage("While searching customer tax code " + e);
            throw e;
        }
        return "";
    }

    static Service searchService(int customerDBNum) throws SQLException, ClassNotFoundException {
        String selectStmt = "SELECT * FROM service WHERE customerDBNum=" + customerDBNum;
        try {
            ResultSet rsService = DBUtil.dbExecuteQuery(selectStmt);
            return getServiceFromResultSet(rsService);
        } catch (Exception e) {
            showMessage("While searching servce with " + customerDBNum + " id, an error occurred: " + e);
            throw e;
        }
    }

    private static Service getServiceFromResultSet(ResultSet rs) throws SQLException {
        Service service = null;
        if (rs.next()) {
            service = new Service();
            service.setServiceDBNum(rs.getInt("serviceDBNum"));
            service.setCustomerDBNum(rs.getInt("customerDBNum"));
            service.setServiceDate(LocalDate.parse(rs.getDate("serviceDate").toString()));
            service.setWorkDone(rs.getString("workDone"));
            service.setServiceIncome(rs.getBigDecimal("serviceIncome"));
            service.setTaxableIncome(rs.getBigDecimal("taxableIncome"));
            service.setSalesTax(rs.getBigDecimal("salesTax"));
            service.setAmountPaid(rs.getBigDecimal("amountPaid"));
            service.setBalance(rs.getBigDecimal("balance"));
            service.setCallDate(LocalDate.parse(rs.getDate("callDate").toString()));
            service.setPaymentNotes(rs.getString("paymentNotes"));
            service.setRecordStatus(rs.getString("recordStatus"));
        }
        return service;
    }

    static void updateService(Service service) throws SQLException, ClassNotFoundException {
        String updateStmt = String.format(
                "UPDATE service " +
                        "SET customerDBNum = %d, " +
                        "workDone = '%s', " +
                        "serviceDate = '%s', " +
                        "serviceIncome = %.2f, " +
                        "taxableIncome = %.2f, " +
                        "salesTax = %.2f, " +
                        "amountPaid = %.2f, " +
                        "balance = %.2f, " +
                        "callDate = '%s', " +
                        "paymentNotes = '%s', " +
                        "remarks = '%s', " +
                        "recordStatus = '%s' " +
                        "WHERE serviceDBNum = %d",
                service.getCustomerDBNum(),
                service.getWorkDone(),
                service.getServiceDate(),
                service.getServiceIncome(),
                service.getTaxableIncome(),
                service.getSalesTax(),
                service.getAmountPaid(),
                service.getBalance(),
                service.getCallDate(),
                service.getPaymentNotes(),
                service.getRemarks(),
                service.getRecordStatus(),
                service.getServiceDBNum());
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (Exception e) {
            showMessage("Error occurred while UPDATE Operation: " + e);
            throw e;
        }
    }

    static void transferServices(int from, int to) throws SQLException, ClassNotFoundException {
        String updateStmt = String.format("UPDATE service SET customerDBNum = %d, recordStatus = 'T' WHERE customerDBNum = %d",
                                          to, from );
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
            System.out.println(updateStmt);
        } catch (Exception e) {
            System.out.println(updateStmt);
            System.out.println("Error occurred while transferring services: " + e);
            throw e;
        }
    }

    static void transferService(int from, int to) throws SQLException, ClassNotFoundException {
        String updateStmt = String.format("UPDATE service SET customerDBNum = %d, recordStatus = 'T' WHERE serviceDBNum = %d",
                to, from );
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (Exception e) {
            showMessage("Error occurred while transferring services: " + e);
            throw e;
        }
    }


    static void deleteService(Service service) throws SQLException, ClassNotFoundException {
        String deleteStmt = String.format("DELETE FROM service WHERE serviceDBNum = %d", service.getServiceDBNum());
        try {
            DBUtil.dbExecuteUpdate(deleteStmt);
        } catch (Exception e) {
            showMessage("Error occurred while delete Operation: " + e);
            throw e;
        }
    }

    static void deleteServicesForCustomer(Customer customer) throws SQLException, ClassNotFoundException {
        String deleteStmt = String.format("DELETE FROM service WHERE customerDBNum = %d", customer.getCustomerDBNum());
        try {
            DBUtil.dbExecuteUpdate(deleteStmt);
            CustomerDAO.deleteCustomerWithId(String.valueOf(customer.getCustomerDBNum()));
        } catch (Exception e) {
            showMessage("Error occurred while delete Operation: " + e);
            throw e;
        }
    }

    static void insertService(Service service) throws SQLException, ClassNotFoundException {
        String insertStmt = String.format(
                "INSERT INTO service (customerDBNum, workDone, serviceDate, serviceIncome, taxableIncome, " +
                        "salesTax, amountPaid, balance, callDate, paymentNotes, remarks, recordStatus) " +
                        "VALUES(%d,'%s','%s',%.2f,%.2f,%.2f,%.2f,%.2f,'%s','%s','%s','%s')",
                service.getCustomerDBNum(),
                service.getWorkDone(),
                service.getServiceDate(),
                service.getServiceIncome(),
                service.getTaxableIncome(),
                service.getSalesTax(),
                service.getAmountPaid(),
                service.getBalance(),
                service.getCallDate(),
                service.getPaymentNotes(),
                service.getRemarks(),
                service.getRecordStatus());
        try {
            DBUtil.dbExecuteUpdate(insertStmt);
        } catch (Exception e) {
            showMessage("Error occurred while Insert Operation: " + e);
            throw e;
        }
    }

    static ObservableList<Service> searchServices(int customerDBNum) throws SQLException, ClassNotFoundException {
        String selectStmt = String.format(
                "SELECT * FROM service WHERE customerDBNum = %d ORDER BY customerDBNum, serviceDate desc", customerDBNum);
        try {
            ResultSet rs = DBUtil.dbExecuteQuery(selectStmt);
            return getServiceList(rs);
        } catch (Exception e) {
            showMessage("SQL select operation has failed: " + e);
            throw e;
        }
    }

    private static ArrayList<Service> getListFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<Service> list = new ArrayList<>();
        return (ArrayList<Service>)buildServiceListFromResultSet(list, rs);
    }

    private static ObservableList<Service> getServiceList(ResultSet rs) throws SQLException {
        ObservableList<Service> list = FXCollections.observableArrayList();
        return (ObservableList<Service>)buildServiceListFromResultSet(list, rs);
    }

    private static List<Service> buildServiceListFromResultSet(List<Service> list, ResultSet rs) throws SQLException {
        while (rs.next()) {
            Service service = new Service();
            service.setServiceDBNum(rs.getInt("serviceDBNum"));
            service.setCustomerDBNum(rs.getInt("customerDBNum"));
            service.setServiceDate(LocalDate.parse(rs.getDate("serviceDate").toString()));
            service.setWorkDone(rs.getString("workDone"));
            service.setServiceIncome(rs.getBigDecimal("serviceIncome"));
            service.setTaxableIncome(rs.getBigDecimal("taxableIncome"));
            service.setSalesTax(rs.getBigDecimal("salesTax"));
            service.setAmountPaid(rs.getBigDecimal("amountPaid"));
            service.setBalance(rs.getBigDecimal("balance"));
            service.setCallDate(LocalDate.parse(rs.getDate("callDate").toString()));
            service.setRemarks(rs.getString("remarks"));
            service.setPaymentNotes(rs.getString("paymentNotes"));
            service.setRecordStatus(rs.getString("recordStatus"));
            list.add(service);
        }
        return list;
    }

    public static void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }
} // end class
