import javafx.beans.property.*;
import javafx.scene.control.Button;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class Service {
    private IntegerProperty serviceDBNum;
    private IntegerProperty customerDBNum;
    private SimpleObjectProperty<LocalDate> serviceDate;
    private StringProperty workDone;
    private ObjectProperty serviceIncome;
    private ObjectProperty taxableIncome;
    private ObjectProperty salesTax;
    private ObjectProperty amountPaid;
    private ObjectProperty balance;
    private StringProperty paymentNotes;
    private StringProperty remarks;
    private SimpleObjectProperty<LocalDate> callDate;
    private SimpleStringProperty recordStatus;
    private SimpleObjectProperty button;

    public Service() {
        this.serviceDBNum = new SimpleIntegerProperty();
        this.customerDBNum = new SimpleIntegerProperty();
        this.serviceDate = new SimpleObjectProperty();
        this.workDone = new SimpleStringProperty();
        this.serviceIncome = new SimpleObjectProperty();
        this.taxableIncome = new SimpleObjectProperty();
        this.salesTax = new SimpleObjectProperty();
        this.amountPaid = new SimpleObjectProperty();
        this.balance = new SimpleObjectProperty();
        this.paymentNotes = new SimpleStringProperty();
        this.callDate = new SimpleObjectProperty();
        this.remarks = new SimpleStringProperty();
        this.recordStatus = new SimpleStringProperty();
        this.button = new SimpleObjectProperty();
    }

    //serviceDBNum
    int getServiceDBNum() {return serviceDBNum.get(); }
    void setServiceDBNum(int serviceDBNum){this.serviceDBNum.set(serviceDBNum); }
    IntegerProperty serviceDBNumProperty(){return serviceDBNum; }

    //customerDBNum
    int getCustomerDBNum() {return customerDBNum.get(); }
    void setCustomerDBNum(int customerDBNum){this.customerDBNum.set(customerDBNum); }
    IntegerProperty customerDBNumProperty(){return customerDBNum; }

    //serviceDate
    LocalDate getServiceDate(){
        return serviceDate.get();
    }
    void setServiceDate(LocalDate serviceDate){
        this.serviceDate.set(serviceDate);
    }
    SimpleObjectProperty<LocalDate> serviceDateProperty(){return serviceDate; }

    //workDone
    String getWorkDone() {return workDone.get();}
    void setWorkDone(String workDone){this.workDone.set(workDone); }
    StringProperty workDoneProperty(){return workDone;}

    //serviceIncome
    BigDecimal getServiceIncome() {return (BigDecimal)serviceIncome.get(); }
    void setServiceIncome(BigDecimal serviceIncome){this.serviceIncome.set(serviceIncome); }
    ObjectProperty serviceIncomeProperty(){return serviceIncome;}

    //taxableIncome
    BigDecimal getTaxableIncome() {return (BigDecimal)taxableIncome.get(); }
    void setTaxableIncome(BigDecimal taxableIncome){this.taxableIncome.set(taxableIncome); }
    ObjectProperty taxableIncomeProperty(){return taxableIncome;}

    //salesTax
    BigDecimal getSalesTax() {return (BigDecimal)salesTax.get(); }
    void setSalesTax(BigDecimal salesTax){this.salesTax.set(salesTax); }
    ObjectProperty salesTaxProperty(){return salesTax;}

    //amountPaid
    BigDecimal getAmountPaid() {return (BigDecimal)amountPaid.get(); }
    void setAmountPaid(BigDecimal amountPaid){this.amountPaid.set(amountPaid); }
    ObjectProperty amountPaidProperty(){return amountPaid;}

    //balance
    BigDecimal getBalance() {return (BigDecimal)balance.get(); }
    void setBalance(BigDecimal balance){this.balance.set(balance); }
    ObjectProperty balanceProperty(){return balance;}

    //paymentNotes
    String getPaymentNotes() {return paymentNotes.get();}
    void setPaymentNotes(String paymentNotes){this.paymentNotes.set(paymentNotes); }
    StringProperty paymentNotesProperty(){return paymentNotes;}

    //callDate
    LocalDate getCallDate(){
        return callDate.get();
    }
    void setCallDate(LocalDate callDate){
        this.callDate.set(callDate);
    }
    SimpleObjectProperty<LocalDate> callDateProperty(){
        return callDate;
    }

    //remarks
    String getRemarks() {return remarks.get();}
    void setRemarks(String remarks){this.remarks.set(remarks); }
    StringProperty remarksProperty(){return remarks;}

    //recordStatus
    String getRecordStatus() {return recordStatus.get();}
    void setRecordStatus(String recordStatus){this.recordStatus.set(recordStatus); }
    StringProperty recordStatusProperty(){return recordStatus;}

    public String toString() {
        return String.format("%s", getWorkDone());
    }
}
