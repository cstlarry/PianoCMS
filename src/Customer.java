import javafx.beans.property.*;
import java.sql.Date;

public class Customer {
    private IntegerProperty customerDBNum;
    private IntegerProperty taxcode;
    private StringProperty firstName;
    private StringProperty lastName;
    private StringProperty phone;
    private StringProperty otherPhone;
    private StringProperty email;
    private StringProperty piano;
    private StringProperty model;
    private StringProperty serialNbr;
    private StringProperty style;
    private StringProperty size;
    private StringProperty address1;
    private StringProperty address2;
    private StringProperty city;
    private StringProperty state;
    private StringProperty zipCode;
    private StringProperty remarks;
    private StringProperty customerStatus;
    private SimpleObjectProperty<Date> entryDate;
    private SimpleObjectProperty<Date> lastServiceDate;
    private SimpleObjectProperty<TaxRate> taxrate;

    //Constructor
    public Customer() {
        this.customerDBNum = new SimpleIntegerProperty();
        this.taxcode = new SimpleIntegerProperty();
        this.firstName = new SimpleStringProperty();
        this.lastName = new SimpleStringProperty();
        this.phone = new SimpleStringProperty();
        this.otherPhone = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.address1 = new SimpleStringProperty();
        this.address2 = new SimpleStringProperty();
        this.city = new SimpleStringProperty();
        this.state = new SimpleStringProperty();
        this.zipCode = new SimpleStringProperty();
        this.piano = new SimpleStringProperty();
        this.model = new SimpleStringProperty();
        this.style = new SimpleStringProperty();
        this.size = new SimpleStringProperty();
        this.serialNbr = new SimpleStringProperty();
        this.entryDate = new SimpleObjectProperty<>();
        this.lastServiceDate = new SimpleObjectProperty<>();
        this.taxrate = new SimpleObjectProperty<>();
        this.remarks = new SimpleStringProperty();
        this.customerStatus = new SimpleStringProperty();
    }

    //customerDBNum
    int getCustomerDBNum() {
        return customerDBNum.get();
    }
    void setCustomerDBNum(int customerDBNum){
        this.customerDBNum.set(customerDBNum);
    }
    IntegerProperty customerDBNumProperty(){
        return customerDBNum;
    }

    //taxcode
    int getTaxcode() {
        return taxcode.get();
    }
    void setTaxcode(int taxcode){
        this.taxcode.set(taxcode);
    }
    IntegerProperty taxcodeProperty(){
        return taxcode;
    }

    //firstName
    String getFirstName() {
        return firstName.get();
    }
    void setFirstName(String firstName){
        this.firstName.set(firstName);
    }
    StringProperty firstNameProperty() {
        return firstName;
    }

    //lastName
    String getLastName() {
        return lastName.get();
    }
    void setLastName(String lastName){
        this.lastName.set(lastName);
    }
    StringProperty lastNameProperty() {
        return lastName;
    }

    //phone
    String getPhone () {
        return phone.get();
    }
    void setPhone (String phone){
        this.phone.set(dashIt(phone));
    }
    StringProperty phoneProperty() {
        return phone;
    }

    //otherPhone
    String getOtherPhone() {
        return otherPhone.get();
    }
    void setOtherPhone(String otherPhone){this.otherPhone.set(dashIt(otherPhone)); }
    StringProperty otherPhoneProperty() {
        return otherPhone;
    }

    //address1
    String getAddress1() {
        return address1.get();
    }
    void setAddress1(String address1){
        this.address1.set(address1);
    }
    StringProperty address1Property() {
        return address1;
    }

    //address2
    String getAddress2() {
        return address2.get();
    }
    void setAddress2(String address2){this.address2.set(address2); }
    StringProperty address2Property() {
        return address2;
    }

    //city
    String getCity() {
        return city.get();
    }
    void setCity(String city){
        this.city.set(city);
    }
    StringProperty cityProperty() {
        return city;
    }

    //state
    String getState() {
        return state.get();
    }
    void setState(String state){
        this.state.set(state);
    }
    StringProperty stateProperty() {
        return state;
    }

    //zipCode
    String getZipCode() {
        return zipCode.get();
    }
    void setZipCode(String zipCode){this.zipCode.set(zipCode); }
    StringProperty zipCodeProperty() {
        return zipCode;
    }

    //email
    String getEmail () {
        return email.get();
    }
    void setEmail (String email){
        this.email.set(email);
    }
    StringProperty emailProperty() {
        return email;
    }

    //remarks
    String getRemarks() { return remarks.get(); }
    void setRemarks(String remarks){ this.remarks.set(remarks); }
    StringProperty remarksProperty() {
        return remarks;
    }

    //piano
    String getPiano () {return piano.get();  }
    void setPiano (String piano){
        this.piano.set(piano);
    }
    StringProperty pianoProperty() {
        return piano;
    }

    //model
    String getModel() {return model.get();  }
    void setModel(String model) {this.model.set(model);}
    StringProperty modelProperty() {
        return model;
    }

    //serialNbr
    String getSerialNbr() {return serialNbr.get();  }
    void setSerialNbr(String serialNbr) {this.serialNbr.set(serialNbr);}
    StringProperty SerialNbrProperty() {
        return serialNbr;
    }

    //style
    String getStyle() {return style.get();  }
    void setStyle(String style) {this.style.set(style);}
    StringProperty styleProperty() {
        return style;
    }

    //size
    String getSize() {return size.get();  }
    void setSize(String size) {this.size.set(size);}
    StringProperty sizeProperty() {
        return size;
    }

    //entryDate
    Object getEntryDate(){
        return entryDate.get();
    }
    void setEntryDate(Date entryDate){
        this.entryDate.set(entryDate);
    }
    SimpleObjectProperty<Date> entryDateProperty(){
        return entryDate;
    }

    //lastServiceDate
    Object getLastServiceDate(){
        return lastServiceDate.get();
    }
    void setLastServiceDate(Date lastServiceDate){
        this.lastServiceDate.set(lastServiceDate);
    }
    SimpleObjectProperty<Date> lastServiceDateProperty(){
        return lastServiceDate;
    }

    //taxrate
    Object getTaxrate(){return taxrate.get();    }
    void setTaxrate(TaxRate taxrate){this.taxrate.set(taxrate);}
    SimpleObjectProperty<TaxRate> taxrateProperty(){return taxrate;}

    //customerStatus
    String getCustomerStatus() {return customerStatus.get();  }
    void setCustomerStatus(String customerStatus) {this.customerStatus.set(customerStatus);}
    StringProperty customerStatusProperty() {
        return customerStatus;
    }

    private String dashIt(String num) {
        if (num.length() == 10) {
            String s1 = num.substring(0,3);
            String s2 = num.substring(3,6);
            String s3 = num.substring(6);
            return String.format("%s-%s-%s",s1,s2,s3);
        }
        if (num.length() == 7) {
            String area = "509";
            String s1 = num.substring(0,3);
            String s2 = num.substring(3);
            return String.format("%s-%s-%s",area,s1,s2);
        }
        return num;
    }

    String shortName() {
        String trimName = this.toString();
        int len = Math.min(trimName.length(), 29);
        trimName = trimName.substring(0, len);
        return trimName;
    }

    @Override
    public String toString() {
        return String.format("%s, %s (%s)", getLastName(), getFirstName(), getCustomerStatus());
    }
}
