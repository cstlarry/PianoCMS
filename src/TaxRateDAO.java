
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaxRateDAO {

    static ObservableList<TaxRate> searchTaxRates() throws SQLException, ClassNotFoundException {
        String selectStmt = "SELECT * FROM taxRate WHERE recstatus = 'A' ORDER BY taxRateDBNum DESC";
        try {
            ResultSet rs = DBUtil.dbExecuteQuery(selectStmt);
            return getTaxRateList(rs);
        } catch (SQLException e) {
            System.out.println("SQL select operation has been failed: " + e);
            throw e;
        }
    }

    private static ObservableList<TaxRate> getTaxRateList(ResultSet rs) throws SQLException {
        ObservableList<TaxRate> taxRateList = FXCollections.observableArrayList();

        while (rs.next()) {
            TaxRate taxrate = new TaxRate();
            taxrate.setCode(rs.getInt("code"));
            taxrate.setCity(rs.getString("city"));
            taxrate.setZipcode(rs.getString("zipcode"));
            taxrate.setRate(rs.getBigDecimal("rate"));
            taxrate.setLocal(rs.getBigDecimal("local"));
            taxrate.setState(rs.getBigDecimal("state"));
            taxrate.setTaxRateDBNum(rs.getInt("taxRateDBNum"));
            taxRateList.add(taxrate);
        }
        return taxRateList;
    }

    static TaxRate searchTaxRateCity(String cityString) throws SQLException, ClassNotFoundException {
        String selectStmt = "SELECT * FROM taxrate WHERE city = '" + cityString + "'";
        try {
            ResultSet rsTaxrate = DBUtil.dbExecuteQuery(selectStmt);
            return getTaxRateFromResultSet(rsTaxrate);
        } catch (Exception e) {
            System.out.println("While searching an taxrate with " + cityString + " id, an error occurred: " + e);
            throw e;
        }
    }
    static TaxRate searchTaxRate(int code) throws SQLException, ClassNotFoundException {
        String selectStmt = "SELECT * FROM taxrate WHERE code = " + code;
        try {
            ResultSet rsTaxrate = DBUtil.dbExecuteQuery(selectStmt);
            return getTaxRateFromResultSet(rsTaxrate);
        } catch (Exception e) {
            System.out.println("While searching an taxrate with " + code + " id, an error occurred: " + e);
            throw e;
        }
    }
    private static TaxRate getTaxRateFromResultSet(ResultSet rs) throws SQLException
    {
        TaxRate taxrate = null;
        if (rs.next()) {
            taxrate = new TaxRate();
            taxrate.setCode(rs.getInt("code"));
            taxrate.setCity(rs.getString("city"));
            taxrate.setZipcode(rs.getString("zipcode"));
            taxrate.setState(rs.getBigDecimal("state"));
            taxrate.setLocal(rs.getBigDecimal("local"));
            taxrate.setRate(rs.getBigDecimal("rate"));
            taxrate.setTaxRateDBNum(rs.getInt("taxRateDBNum"));
        }
        return taxrate;
    }

    static List<TaxRate> getTaxRateList() {
        List<TaxRate> list = null;
        try {
            list = new ArrayList<>(searchTaxRates());
        } catch (Exception e) {
            System.out.println("getTaxRateList operation has failed: " + e);
        }
        return list;
    }

    private static ArrayList<TaxRate> getTaxRatesFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<TaxRate> rateList = new ArrayList<>();
        while (rs.next()) {
            TaxRate rate = new TaxRate();
            rate.setCode(rs.getInt("code"));
            rate.setCity(rs.getString("city"));
            rate.setZipcode(rs.getString("zipcode"));
            rate.setState(rs.getBigDecimal("state"));
            rate.setLocal(rs.getBigDecimal("local"));
            rate.setRate(rs.getBigDecimal("rate"));
            rate.setTaxRateDBNum(rs.getInt("taxRateDBNum"));
            rateList.add(rate);
        }
        return rateList;
    }

    public static ArrayList<TaxRate> getTaxRates() throws SQLException, ClassNotFoundException{
        String selectStmt = "SELECT * FROM taxrate WHERE recstatus = 'A' ORDER BY city";
        try {
            ResultSet rs = DBUtil.dbExecuteQuery(selectStmt);
            return getTaxRatesFromResultSet(rs);
        } catch (SQLException e){
            System.out.println("SQL getTaxRates operation has failed: " + e);
            throw e;
        }
    }

    static void updateTaxRate(TaxRate taxrate) throws SQLException, ClassNotFoundException {
        String updateStmt = String.format(
                "UPDATE taxRate " +
                        "SET code = %d, " +
                        "city = '%s', " +
                        "zipcode = '%s', " +
                        "state = %.4f, " +
                        "local = %.4f, " +
                        "rate = %.4f, " +
                        "recstatus = 'A' " +
                        "WHERE taxRateDBNum = %d",
                taxrate.getCode(),
                taxrate.getCity(),
                taxrate.getZipcode(),
                taxrate.getState(),
                taxrate.getLocal(),
                taxrate.getRate(),
                taxrate.getTaxRateDBNum());
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (Exception e) {
            System.out.println(updateStmt);
            System.out.println("Error occurred in UPDATE Operation: " + e);
            throw e;
        }
    }

    static void insertTaxRate(TaxRate rate) throws SQLException, ClassNotFoundException {
        String updateStmt = String.format(
                "INSERT INTO taxRate (code, city, zipCode, state, local, rate, recstatus)"+
                        "VALUES ('%d','%s','%s','%f','%f','%f','A')",
                                rate.getCode(), rate.getCity(), rate.getZipcode(), rate.getState(), rate.getLocal(), rate.getRate());
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            System.out.print("Error occurred while Insert Operation: " + e);
            throw e;
        }
    }

    static void deleteTaxRate(TaxRate taxRate) throws SQLException, ClassNotFoundException {
        String deleteStmt = String.format("DELETE FROM taxRate WHERE taxRateDBNum = %d", taxRate.getTaxRateDBNum());
        try {
            DBUtil.dbExecuteUpdate(deleteStmt);
        } catch (Exception e) {
            showMessage("Error occurred while delete Operation: " + e);
            throw e;
        }
    }

    public static void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }
} // end class