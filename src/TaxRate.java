import javafx.beans.property.*;

import java.math.BigDecimal;

public class TaxRate {
    private IntegerProperty taxRateDBNum;
    private IntegerProperty code;
    private StringProperty city;
    private StringProperty zipcode;
    private ObjectProperty state;
    private ObjectProperty local;
    private ObjectProperty rate;

    TaxRate() {
        this.taxRateDBNum = new SimpleIntegerProperty();
        this.code = new SimpleIntegerProperty();
        this.city = new SimpleStringProperty();
        this.state = new SimpleObjectProperty();
        this.local = new SimpleObjectProperty();
        this.rate = new SimpleObjectProperty();
        this.zipcode = new SimpleStringProperty();
    }

    //taxRateDBNum
    int getTaxRateDBNum() {return taxRateDBNum.get(); }
    void setTaxRateDBNum(int taxRateDBNum){this.taxRateDBNum.set(taxRateDBNum);}
    IntegerProperty taxRateDBNumProperty(){return taxRateDBNum; }

    //code
    int getCode() {return code.get();    }
    void setCode(int code){this.code.set(code); }
    IntegerProperty codeProperty(){
        return code;
    }

    //zipcode
    String getZipcode() {return zipcode.get(); }
    void setZipcode(String zipcode){this.zipcode.set(zipcode); }
    StringProperty zipcodeProperty(){
        return zipcode;
    }

    //city
    String getCity() {
        return city.get();
    }
    void setCity(String city){this.city.set(city); }
    StringProperty cityProperty(){
        return city;
    }

    //state rate
    BigDecimal getState() {return (BigDecimal)state.get(); }
    void setState(BigDecimal state){this.state.set(state); }
    ObjectProperty stateProperty(){
        return state;
    }

    //local rate
    BigDecimal getLocal() {return (BigDecimal)local.get(); }
    void setLocal(BigDecimal local){this.local.set(local); }
    ObjectProperty localProperty(){
        return local;
    }

    //rate
    BigDecimal getRate() {return (BigDecimal)rate.get(); }
    void setRate(BigDecimal rate){this.rate.set(rate); }
    ObjectProperty rateProperty(){
        return rate;
    }

    public String toString() {
        return String.format("%s", getCity());
    }
}
