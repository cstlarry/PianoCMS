import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.canvas.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.collections.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.print.*;
import java.math.*;
import java.util.*;
import java.util.stream.*;
import java.time.*;
import java.time.chrono.IsoChronology;
import java.time.temporal.ChronoUnit;
import java.time.chrono.IsoChronology;
import java.time.format.*;
import java.security.*;
import java.io.*;
import java.nio.file.*;
import java.util.regex.*;
import javax.swing.*;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.net.URL;
import java.net.URI;
import java.net.URLEncoder;
import java.awt.Desktop;

/**
 This class provides a convinent way for first-year programming students
 to write applications that prompt the user at runtime for needed pieces
 of information using a modern graphical user interface (GUI).  It also
 provides many useful methods for the common programming tasks of a first
 year java programming student.  Requires java 8.
 @author  Larry Henderson
 */
class ProjectUI extends GridPane {

    /** MM/ddd/yyyy date formatter */
    private DateTimeFormatter MDYFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    /** Common regular expression patterns */
    private static final Pattern number = Pattern.compile("\\d+(\\.\\d*)?|\\.\\d+");
    private static final Pattern email = Pattern.compile("[a-z0-9._%+-]+@.+\\.[a-z]+");
    private static final Pattern zipCode = Pattern.compile("^\\d{5}(-\\d{4})?$");
    private static final Pattern name = Pattern.compile("^[A-Za-z'\\s]{1,}[\\.]{0,1}[A-Za-z'\\s]{0,}$");
    private static final Pattern ssn =
            Pattern.compile("^(?!000|666)[0-8][0-9]{2}-(?!00)[0-9]{2}-(?!0000)[0-9]{4}$");
    /** For generating random numbers */
    private SecureRandom random = new SecureRandom();
    /** For usefull javascript functions */
    private ScriptEngine jse = null;
    /** For displaying images */
    private ImageIcon image = null;
    /** For keeping track of how many times output has been called */
    private int outputCount = 0;
    /** An array of TextField objects */
    private TextField[] fields;
    /** An array of Label objects */
    private Label[] labels;
    /** The next available row of the GridPane */
    private int nextRow;
    /** How many prompts there are */
    private int rows;
    /** List of ui nodes */
    private List<Node> gridNodes = new ArrayList<>();
    /** For storing table columnNames */
    private String[] columnNames = null;

    /**
     * Constructor can take no prompts or any number of String values separated
     * by commas.  This class requires java 8 and uses JavaFX for ui components.
     * Models a customizable GUI for building student programmer assignment
     * applications that require gathering user input and then making that input
     * available in a run method.  The results are displayed in a text area
     * section of the GUI.  Each project must create an instance of this class
     * to solve different problems by creating a customized grid of
     * label/field pairs and by adding statements to the run button handler
     * with problem solving code.
     * @param prompts any number of String values
     */
    public ProjectUI(String... prompts) {

        //setAlignment(Pos.CENTER);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHalignment(HPos.RIGHT);
        getColumnConstraints().add(column1);

        setHgap(10);
        setVgap(10);
        setPadding(new Insets(25, 25, 25, 25));
        rows = prompts.length;
        nextRow = rows + 1;
        fields = new TextField[rows];
        labels = new Label[rows];

        for(int i = 0; i < rows; i++) {
            String label = prompts[i]+":";
            fields[i] = new TextField();
            if(label.startsWith("p-")) {
                label = label.substring(2);
                fields[i] = new PasswordField();
            }
            labels[i] = new Label(label);
            fields[i].setMaxWidth(720);
            fields[i].setPrefWidth(400);
            add(labels[i], 0, i);
            add(fields[i], 1, i);
        } // end for
    } // end constructor
    /*
     * Validate names by regular expression
     * @param value   String to be validated
     * @return boolean
     */
    public boolean isValidName(String value) {
        return name.matcher(value).matches();
    }
    /*
     * Validate zipCodes by regular expression
     * @param value   String to be validated
     * @return boolean
     */
    public boolean isValidZip(String value) {
        return zipCode.matcher(value).matches();
    }
    /*
     * Validate email addresses by regular expression
     * @param value   String to be validated
     * @return boolean
     */
    public boolean isValidEmail(String value) {
        return email.matcher(value).matches();
    }
    /*
     * Validate numbers by regular expression
     * @param value   String to be validated
     * @return boolean
     */
    public boolean isValidNumber(String value) {
        return number.matcher(value).matches();
    }
    /*
     * Validate SSNs by regular expression
     * @param value   String to be validated
     * @return boolean
     */
    public boolean isValidSSN(String value) {
        return ssn.matcher(value).matches();
    }
    /**
     * Get the array of prompts
     * @return String[]
     */
    public String[] getPrompts() {
        String[] prompts = new String[rows];
        for(int i = 0; i < rows; i++)
            prompts[i] = labels[i].getText();
        return prompts;
    } // end getPrompts()

    /**
     * Get number of prompts
     * @return int
     */
    int getRows() {
        return rows;
    } // end getRows()

    /**
     * Method for setting field default values.  You must have the same number
     * of default values as you have prompts.
     * @param values must be string values - one for each textField
     */
    public void setDefaultValues(String... values) {
        if(fields.length == values.length) {
            for(int i = 0; i < fields.length; i++) {
                fields[i].setText(values[i]);
            } // end for
        } // end if
    } // end setDefaultValues()

    /**
     * Clears all fields by setting values to an empty String.
     */
    public void clearFields() {
        for (TextField field: fields) {
            field.setText("");
        } // end for
    } // end clearFields()

    /**
     * Clear single textfield specified by index number
     * @param index       fields are numbered starting with 0
     */
    public void clearField(int index) {
        if(index >= 0 && index < rows)
            fields[index].setText("");
    } // end clearField()

    /**
     * Gets the next row then increments nextRow count
     * @return int
     */
    public int nextRow() {
        return nextRow++;
    } // end nextRow()

    /**
     * Gets the next row value without incrementing
     * @return int
     */
    public int getNextRow() {
        return nextRow;
    } // end getNextRow()
    /**
     * Test if string is a number
     * @param value String to be tested
     * @return   boolean
     */
    public boolean isNumber(String value) {
        return number.matcher(value).matches();
    } // end isNumber

    /**
     * This method can be used to insert default values into the text fields
     * @param index       fields are numbered starting with 0
     * @param value       the value to put into the text field
     */
    void setField(int index, String value) {
        if(index < rows) fields[index].setText(value);
        else showMessage("Field number is out of range: " + index);
    } // end setField()

    /**
     * This method will get the string value stored in the field number: fieldIndex
     * @param index       integer number of the field requested
     * @return String     value stored in the text field
     */
    String getField(int index) {
        if(index < rows) {
            String value = fields[index].getText();
            value = value.replaceAll("\\s+", " ").trim();
            if(value.isEmpty())
                return "";
            return value;
        }
        return "Error using getField() method.";
    } // end getField()

    /**
     * This method will get the string value stored in the field
     * number: fieldIndex
     * @param index   integer number of the field requested
     * @param valid   String holding all valid UPPERCASE entry values
     * @return String value stored in the text field
     */
    public String getField(int index, String valid) {
        String input;
        if(index < rows) {
            do {
                input = fields[index].getText().toUpperCase();
                if(valid.contains(input))
                    return input;
                else {
                    showMessage(input + " is not valid option");
                    fields[index].setText("");
                    fields[index].requestFocus();
                }
            } while(!valid.contains(input));
        }
        return "Error using getField() method.";
    } // end getField()

    /**
     Converts the String value in the input field to an int
     @param   index of field starting at 0
     @return  int string in field converted to an integer
     */
    public int getFieldAsInt(int index) {
        if(index < rows) {
            String value = getField(index);
            if(value.equals(""))
                return 0;
            try {
                return Integer.parseInt(value);
            }
            catch (Exception e) {
                showMessage(String.format("%s is not an integer", value));
            }
        } // end if
        else showMessage("Field number is out of range: " + index);
        return 0;
    } // end getFieldAsInt()

    /**
     Converts the String value in the input field to a long
     @param   index of field starting at 0
     @return  long string in field converted to an long
     */
    public long getFieldAsLong(int index) {
        if(index < rows) {
            String value = getField(index);
            if(value.equals(""))
                return 0;
            try {
                return Long.parseLong(value);
            }
            catch (Exception e) {
                showMessage(String.format("%s cannot be converted to a long", value));
            }
        } // end if
        else showMessage("Field number is out of range: " + index);
        return 0;
    } // end getFieldAsInt()

    /**
     Converts the String value in the input field to an double
     @param   index of field starting at 0
     @return  int string in field converted to a double
     */
    public double getFieldAsDouble(int index) {
        if(index < rows) {
            String value = getField(index);
            if(value.equals(""))
                return 0;
            try {
                return Double.parseDouble(value);
            }
            catch (Exception e) {
                showMessage(String.format("%s is not an double", value));
            }
        } // end if
        else showMessage("Field number is out of range: " + index);
        return 0;
    } // end getFieldAsDouble()

    /**
     Converts the String value in the input field to an int with valid character string
     @param   valid constrains user input to value contained within valid string
     @param   index of field starting at 0
     @return  int string in field converted to an integer
     */
    public int getFieldAsInt(int index, String valid) {
        String input;
        if(index < rows) {
            do {
                input = getField(index);
                if(valid.contains(input)) {
                    if(!input.equals(""))
                        return Integer.parseInt(input);
                }
                else {
                    showMessage(input + " is not valid option");
                    fields[index].setText("");
                    fields[index].requestFocus();
                }
            } while(!valid.contains(input));
        } // end if
        else showMessage("Field number is out of range: " + index);
        return -1;
    } // end getFieldAsInt()
    /**
     Converts the String value in the input field to an float
     @param   index of field starting at 0
     @return  float string in field converted to an float
     */
    public float getFieldAsFloat(int index) {
        if(index < rows) {
            String value = getField(index);
            if(value.equals(""))
                return 0;
            try {
                return Float.parseFloat(value);
            }
            catch (Exception e) {
                showMessage(String.format("%s is not an float", value));
            }
        } // end if
        else showMessage("Field number is out of range: " + index);
        return 0;
    } // end getFieldAsFloat()
    /**
     Converts the String value in the input field to an char
     @param   index of field starting at 0
     @return  string in field converted to an char
     */
    public char getFieldAsChar(int index) {
        if(index < rows) {
            String value = getField(index);
            try {
                return value.charAt(0);
            }
            catch (Exception e) {
                showMessage(String.format("%s is not an a char", value));
            }
        } // end if
        else showMessage("Field number is out of range: " + index);
        return 0;
    } // end getFieldAsChar()

    /**
     Converts the String value in the input field to a char and check for valid input
     @param   valid constrains user input to value contained within valid string
     @param   index of field starting at 0
     @return  string in field converted to an char
     */
    public char getFieldAsChar(int index, String valid) {
        if(index < rows) {
            String value = getField(index);
            if(value.isEmpty()) {
                showMessage("Field entry is required");
                return 0;
            }
            else {
                char character = value.charAt(0);
                if(!valid.contains(String.valueOf(character))) {
                    showMessage(String.format("Valid characters are %s", valid));
                    return 0;
                }
                return character;
            }
        } // end if
        else showMessage("Field number is out of range: " + index);
        return 0;
    } // end getFieldAsChar()

    /**
     Converts the String value in the input field to a BigDecimal
     @param   index of field starting at 0
     @return  BigDecimal string in field converted to a BigDecimal
     */
    public BigDecimal getFieldAsBigDecimal(int index) {
        if(index < rows) {
            String value = getField(index);
            try {
                BigDecimal result = new BigDecimal(value);
                result = result.setScale(2);
                return result;
            }
            catch (Exception e) {
                showMessage(String.format("%s is not formatted correctly", value));
            }
        } // end if
        else showMessage("Field number is out of range: " + index);
        return null;
    } // end getFieldAsBigDecimal()

    /**
     * This method converts a space-separated string of doubles to an array of
     * doubles: fieldIndex. This method is to be called from the run method
     * which will catch the possible exception and report an appropriate message.
     * @param    index integer number of the field requested
     * @return   values an array of doubles.
     */
    public double[] getFieldAsDoubleArray(int index) {
        if(index < rows) {
            String value = getField(index).replace("  ", " ");
            try {
                String[] stringValues = value.split(" ");
                double[] values = new double[stringValues.length];
                for(int i = 0; i < stringValues.length; i++)
                    values[i] = Double.parseDouble(stringValues[i]);
                return values;
            }
            catch (Exception e) {
                showMessage("Problem with " + value);
            }
        } // end if
        else showMessage("Field number is out of range: " + index);
        return null;
    } // end getFieldAsDoubleArray()

    /**
     * This method converts a space-separated string of ints to an array of
     * ints: fieldIndex. This method is to be called from the run method
     * which will catch the possible exception and report an appropriate message.
     * @param index    integer number of the field requested
     * @return  values an array of ints.
     */
    public int[] getFieldAsIntArray(int index) {
        if(index < rows) {
            String value = getField(index).replace("  ", " ");
            try {
                String[] stringValues = value.split(" ");
                int[] values = new int[stringValues.length];
                for(int i = 0; i < stringValues.length; i++)
                    values[i] = Integer.parseInt(stringValues[i]);
                return values;
            }
            catch (Exception e) {
                showMessage("Problem with " + value);
            }
        } // end if
        else showMessage("Field number is out of range: " + index);
        return null;
    } // end getFieldAsIntArray()

    /**
     * This method converts a space-separated string of ints to an array of
     * ints: This array will be converted to an IntStream and
     * returned to the caller.
     * @param index    integer number of the field requested
     * @return  IntStream a stream of integers.
     */
    public IntStream getFieldAsIntStream(int index) {
        if(index < rows)
            return Arrays.stream(getFieldAsIntArray(index));
        else showMessage("Field number is out of range: " + index);
        return null;
    } // end getFieldAsIntStream()

    /**
     * This method converts a space-separated string of doubles
     * to an array of doubles: This array will be converted to
     * an DoubleStream and returned to the caller.
     * @param index    integer number of the field requested
     * @return  DoubleStream a stream of Doubles.
     */
    public DoubleStream getFieldAsDoubleStream(int index) {
        if(index < rows)
            return Arrays.stream(getFieldAsDoubleArray(index));
        else showMessage("Field number is out of range: " + index);
        return null;
    } // end getFieldAsDoubleStream()

    /**
     * Converts an int array to an IntStream
     * @param    array int[]
     * @return   IntStream
     */
    public IntStream getAsStream(int[] array) {
        return Arrays.stream(array);
    } // end getAsStream()

    /**
     * Converts an double array to an DoubleStream
     * @param    array double[]
     * @return   DoubleStream
     */
    public DoubleStream getAsStream(double[] array) {
        return Arrays.stream(array);
    } // end getAsStream()

    /**
     * Converts an int array to List of Integer
     * @param    array int[]
     * @return   List of Integer
     */
    public List<Integer> getAsList(int[] array) {
        List<Integer> objList = IntStream.of(array).boxed().collect(Collectors.toList());
        return objList;
    } // end getAsList()

    /**
     * Converts an double array to List of Double
     * @param    array double[]
     * @return   List of Double
     */
    public List<Double> getAsList(double[] array) {
        List<Double> objList = DoubleStream.of(array).boxed().collect(Collectors.toList());
        return objList;
    } // end getAsList()

    /**
     * This method converts space-separated strings to an array of
     * strings: fieldIndex. This method is to be called from the run method
     * which will catch the possible exception and report an appropriate message.
     * @param index    integer number of the field requested
     * @return  values an array of Strings.
     */
    public String[] getFieldAsStringArray(int index) {
        if(index < rows) {
            return getField(index).split(" ");
        } // end if
        else showMessage("Field number is out of range: " + index);
        return null;
    } // end getFieldAsStringArray()

    /**
     * This method will convert to a LocalDate instance the value stored in the
     * field number: fieldIndex.  This method is to be called from the run
     * method which will catch the possible exception and report an appropriate
     * message to the user.  Date format is 01/01/1900.
     * @param index  integer number of the field requested
     * @return  value stored in the text field converted to a LocalDate instance.
     */
    LocalDate getFieldAsDate(int index) {
        if(index < rows) {
            String dateString = getField(index);
            // fix single digit values and return a LocalDate
            if(dateString.length() < 10) {
                String[] values = dateString.split("/");
                return LocalDate.of(Integer.parseInt(values[2]),
                        Integer.parseInt(values[0]),
                        Integer.parseInt(values[1]));
            } // end if
            LocalDate date = null;
            try {
                if(dateString.contains("-"))
                    date = LocalDate.parse(dateString);
                else
                    date = LocalDate.parse(dateString, MDYFormat);         }
            catch (Exception e) {
                showMessage(String.format("Cannot convert %s to a date", dateString));
            }
            return date;
        } // end if
        else showMessage("Field number is out of range: " + index);
        return null;
    } // end getFieldAsDate()

    /**
     * This method will convert to a LocalDate instance the String value sent
     * This method is to be called from the run method which will catch the
     * possible exception and report an appropriate message to the user.
     * Date format 01/01/1900
     * @param dateString  String value of date in mm/dd/yyyy format.
     * @return  LocalDate instance.
     */
    private LocalDate getStringAsDate(String dateString) {
        if(dateString.equals(""))
            return null;
        if(dateString.length() < 10) { // fix single digit values
            String[] values = dateString.split("/");
            return LocalDate.of(Integer.parseInt(values[2]),
                    Integer.parseInt(values[0]),
                    Integer.parseInt(values[1]));
        }
        LocalDate date = null;
        try {
            if(dateString.contains("-"))
                date = LocalDate.parse(dateString);
            else
                date = LocalDate.parse(dateString, MDYFormat);
        }
        catch (Exception e) {
            showMessage(String.format("%s cannot be converted to a date", dateString));
        }
        return date;
    } // end getStringAsDate()

    /**
     * Return the current date as an instance of LocalDate
     * @return   LocalDate object
     */
    public LocalDate getDate() {
        return LocalDate.now();
    } // end getDate()

    /**
     * Convert current date to a string according to the given format
     * @param formatStyle   holds a valid date format
     * style: FULL, LONG, MEDIUM, SHORT
     * @return   String
     */
    public String getDate(String formatStyle) {
        FormatStyle style = null;
        formatStyle = formatStyle.toUpperCase();
        switch (formatStyle) {
            case "FULL":
                style = FormatStyle.FULL;
                break;
            case "LONG":
                style = FormatStyle.LONG;
                break;
            case "MEDIUM":
                style = FormatStyle.MEDIUM;
                break;
            case "SHORT":
                style = FormatStyle.SHORT;
                break;
        }
        assert style != null;
        return getDate().format(DateTimeFormatter.ofLocalizedDate(style));
    } // end getDate()

    /**
     * Format a date according to a format style
     * @param date instance of LocalDate
     * @param formatStyle String FULL LONG MEDIUM and SHORT
     * @return String
     */
    public String getDate(LocalDate date, String formatStyle) {
        FormatStyle style = null;
        formatStyle = formatStyle.toUpperCase();
        switch (formatStyle) {
            case "FULL":
                style = FormatStyle.FULL;
                break;
            case "LONG":
                style = FormatStyle.LONG;
                break;
            case "MEDIUM":
                style = FormatStyle.MEDIUM;
                break;
            case "SHORT":
                style = FormatStyle.SHORT;
                break;
        }
        assert style != null;
        return date.format(DateTimeFormatter.ofLocalizedDate(style));
    } // end getDate()
    /**
     * Python-like print method for easy output to the System console.
     * @param   obj the entity to be printed
     */
    private void print(Object obj) {
        System.out.println(obj);
    } // end print()
    /**
     * Return an instance of SecureRandom
     * @return  the random object
     */
    public SecureRandom getRandom() {
        return random;
    } // end getRandom()
    /**
     * Return a random number between zero and max
     * @param   max the range limit
     * @return  the random number between zero and max
     */
    public int getRandom(int max) {
        return random.nextInt(max);
    } // end getRandom()

    /**
     * Return a random number between min and max inclusive of both
     * @param   min beginning of range
     * @param   max end of range
     * @return  int the random number between min and max
     */
    public int getRandom(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    } // end getRandom()

    /*
     * Creates a popup dialog of selectable choices
     * @param String choiceList (vararg)
     * @return String the choice selected
     */
    String inputChoice(String... choiceList) {
        try {
            List<String> choices = new ArrayList<>();
            Collections.addAll(choices, choiceList);
            ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
            dialog.setTitle("Choice Dialog");
            dialog.setHeaderText("Make your choice:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                return result.get();
            }
        }
        catch (Exception e) { print("No items in list"); }
        return null;
    } // end inputChoice()

    /*
     * Creates a popup dialog of selectable choices
     * @param Object choiceList (vararg)
     * @return Object the choice selected
     * Example: Gender gender = (Gender)ui.inputChoice(Gender.MALE, Gender.FEMALE);
     */
    public Object inputChoice(Object... choiceList) {
        try {
            List<Object> choices = new ArrayList<>();
            Collections.addAll(choices, choiceList);
            ChoiceDialog<Object> dialog = new ChoiceDialog<>(choices.get(0), choices);
            dialog.setTitle("Choice Dialog");
            dialog.setHeaderText("Make your choice:");

            Optional<Object> result = dialog.showAndWait();
            if (result.isPresent()){
                return result.get();
            }
        }
        catch (Exception e) { print("No items in list"); }
        return null;
    } // end inputChoice()
    /*
     * Creates a popup dialog of selectable choices
     * @param Object choices List<Object>
     * @return Object the choice selected - needs to be cast as the object you want
     */
    public Object inputChoice(List<Object> choices) {
        try {
            ChoiceDialog<Object> dialog = new ChoiceDialog<>(choices.get(0), choices);
            dialog.setTitle("Choice Dialog");
            dialog.setHeaderText("Make your choice:");

            Optional<Object> result = dialog.showAndWait();
            if (result.isPresent()){
                return result.get();
            }
        }
        catch (Exception e) { print("No items in list"); }
        return null;
    } // end inputChoice()

    /**
     * Python-like input command for getting input from user.
     * @param    prompt String to prompt user for value
     * @return   String
     */
    public String input(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dialog");
        dialog.setHeaderText(prompt);
        Optional<String> text = dialog.showAndWait();
        return text.orElse("");
    } // end input()

    /**
     * Python-like input command for getting char input from user.
     * @param    prompt String to prompt user for value
     * @return   char (0 is returned on CANCEL and 1 on OK)
     */
    public char inputChar(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dialog");
        dialog.setHeaderText(prompt);
        Optional<String> text = dialog.showAndWait();
        if(text.equals(Optional.empty()))
            return 0;
        char value;
        try {
            value = text.get().charAt(0);
        }
        catch (Exception e) {
            showMessage(String.format("%s cannot be converted to an char", text.get()));
            return 1;}
        return value;
    } // end inputChar()

    /**
     * Python-like input command for getting valid char input from user.
     * @param    valid String to contain valid characters
     * @param    prompt String to prompt user for value
     * @return   char (0 is returned on CANCEL and 1 on OK)
     */
    public char inputChar(String prompt, String valid) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dialog");
        dialog.setHeaderText(prompt);
        Optional<String> text = dialog.showAndWait();
        if(text.equals(Optional.empty()))
            return 0;
        char value;
        try {
            value = text.get().charAt(0);
            if(!valid.contains(String.valueOf(value))) {
                showMessage(String.format("Valid characters are %s", valid));
                return 0;
            }
        }
        catch (Exception e) {
            showMessage(String.format("%s cannot be converted to an char", text.get()));
            return 0;}
        return value;
    } // end inputChar()

    /**
     * Python-like input command for getting date input from user.
     * @param    prompt String to prompt user for value
     * @return   LocalDate
     */
    public LocalDate inputDate(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dialog");
        dialog.setHeaderText(prompt);
        Optional<String> text = dialog.showAndWait();
        return text.map(this::getStringAsDate).orElse(null);
    } // end inputDate()

    /**
     * Python-like input command for getting byte input from user.
     * @param    prompt String to prompt user for value
     * @return   byte
     */
    public byte inputByte(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dialog");
        dialog.setHeaderText(prompt);
        Optional<String> text = dialog.showAndWait();
        if(text.isPresent()) {
            try {
                return Byte.parseByte(text.get());
            }
            catch (Exception e) {
                showMessage(String.format("%s cannot be converted to an short", text.get()));
                return 0; }
        } else
            return -1;
    } // end inputByte()

    /**
     * Python-like input command for getting short input from user.
     * @param    prompt String to prompt user for value
     * @return   short
     */
    public short inputShort(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dialog");
        dialog.setHeaderText(prompt);
        Optional<String> text = dialog.showAndWait();
        if(text.isPresent()) {
            try {
                return Short.parseShort(text.get());
            }
            catch (Exception e) {
                showMessage(String.format("%s cannot be converted to an short", text.get()));
                return 0; }
        } else
            return -1;
    } // end inputShort()

    /**
     * Python-like input command for getting integer input from user.
     * @param    prompt String to prompt user for value
     * @return   int Default return value is -1
     */
    public int inputInt(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dialog");
        dialog.setHeaderText(prompt);
        Optional<String> text = dialog.showAndWait();
        if(text.isPresent()) {
            try {
                return Integer.parseInt(text.get());
            }
            catch (Exception e) {
                showMessage(String.format("%s cannot be converted to an int", text.get()));
                return 0; }
        } else
            return -1;
    } // end inputInt()

    /**
     * Python-like input command for getting integer input from user.
     * @param    prompt String to prompt user for value
     * @param    max int limit input to values from 0 to max
     * @return   int
     */
    public int inputInt(String prompt, int max) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dialog");
        dialog.setHeaderText(prompt);
        int input = -1;
        do { // keep asking user until input is valid
            try {
                Optional<String> text = dialog.showAndWait();
                input = Integer.parseInt(text.get());
            }
            catch (Exception e) {
                showMessage("Please enter a valid selection");
            }
        } while(input < 0 || input > max);
        return input;
    } // end inputInt(prompt, max)

    /**
     * Python-like input command for getting long input from user.
     * @param    prompt String to prompt user for value
     * @return   long
     */
    public long inputLong(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dialog");
        dialog.setHeaderText(prompt);
        Optional<String> text = dialog.showAndWait();
        if(text.isPresent()) {
            try {
                return Long.parseLong(text.get());
            }
            catch (Exception e) {
                showMessage(String.format("%s cannot be converted to an long", text.get()));
                return 0; }
        } else
            return -1;
    } // end inputLong()

    /**
     * Python-like input command for getting float input from user.
     * @param    prompt String to prompt user for value
     * @return   float
     */
    public float inputFloat(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dialog");
        dialog.setHeaderText(prompt);
        Optional<String> text = dialog.showAndWait();
        if(text.isPresent()) {
            try {
                return Float.parseFloat(text.get());
            }
            catch (Exception e) {
                showMessage(String.format("%s cannot be converted to an float", text.get()));
                return 0; }
        } else
            return -1;
    } // end inputFloat()

    /**
     * Python-like input command for getting double input from user.
     * @param    prompt String to prompt user for value
     * @return   double
     */
    public double inputDouble(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dialog");
        dialog.setHeaderText(prompt);
        Optional<String> text = dialog.showAndWait();
        if(text.isPresent()) {
            try {
                return Double.parseDouble(text.get());
            }
            catch (Exception e) {
                showMessage(String.format("%s cannot be converted to an double", text.get()));
                return 0; }
        } else
            return -1;
    } // end inputDouble()

    /**
     * Python-like input command for getting input from user.
     * @param prompt   prompt String for user instruction
     * @param value    value String for default value
     * @return   String
     */
    public String input(String prompt, String value) {
        TextInputDialog dialog = new TextInputDialog(value);
        dialog.setTitle("Dialog");
        dialog.setHeaderText(prompt);
        Optional<String> text = dialog.showAndWait();
        return text.orElse("");
    } // end input()
    /**
     * Python-like input command for getting input from user.
     * @param prompt   prompt String for user instruction
     * @param value    value String for default value
     * @param imageFileName String for image filename
     * @return   String
     */
    public String input(String prompt, String value, String imageFileName) {
        TextInputDialog dialog = new TextInputDialog(value);
        dialog.setTitle("Dialog");
        dialog.setHeaderText(prompt);
        try {
            dialog.setGraphic(new ImageView(this.getClass().getResource(imageFileName).toString()));
        }
        catch (Exception e) {
            return "no image file"; }
        Optional<String> text = dialog.showAndWait();
        return text.orElse("");
    } // end input()

    /**
     * show popup message.
     * @param message   String
     */
    public void showMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION, message);
        alert.showAndWait();
    } // end showMessage()

    /**
     * All application data (prompts and values) as a single string
     * with appropriate newline characters.
     * @return   String
     */
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Application prompts and values:\n");
        for (int i = 0; i < rows; i++) {
            String str = String.format("%s : %s\n", labels[i].getText(), getField(i));
            sb.append(str);
        }
        return sb.toString();
    } // end toString()

    /**
     * Reading from text files is a common application requirement
     * so this functionality is included here.
     * @param fileName  The file to be read
     * @return          String[] holding lines from the file
     */
    String[] getLinesFromFile(String fileName) {
        ArrayList<String> lines = readListFromFile(fileName);
        //Collections.sort(lines);
        return lines.toArray(new String[0]);
    } // end getLinesFromFile()

    /**
     * Reading from text files is a common application requirement
     * so this functionality is included here.
     * @param fileName  The file to be read
     * @return          ArrayList of lines from the file
     */
    private ArrayList<String> readListFromFile(String fileName) {
        ArrayList<String> linesFromFile = new ArrayList<>();
        Scanner reader = null;
        try {
            reader = new Scanner(new File(fileName));
            while(reader.hasNextLine()) {
                linesFromFile.add(reader.nextLine());
            }
        }
        catch (IOException e) { showMessage(e.getMessage()); }
        if(reader != null) reader.close();
        print("Lines read: " + linesFromFile.size());
        return linesFromFile;
    } // end readListFromFile()

    /**
     * Create a text file from an list of strings
     * @param list      The ArrayList of Strings to write to file
     * @param fileName  The file to be created
     */
    public void writeListToFile(ArrayList<String> list, String fileName) {
        try {
            FileWriter fw = new FileWriter(fileName);
            Writer output = new BufferedWriter(fw);
            for(int i = 0; i < list.size(); i++) {
                output.write(list.get(i) + "\n");
            }
            output.close();
        }
        catch (Exception e) {
            print(e.getMessage());
        }
    } // end writeListToFile
    /**
     * Create a text file from a string
     * @param text      The String to write to file
     * @param fileName  The file to be created
     */
    public void writeStringToFile(String text, String fileName) {
        try {
            FileWriter fw = new FileWriter(fileName);
            Writer output = new BufferedWriter(fw);
            output.write(text);
            output.close();
        }
        catch (Exception e) {
            print(e.getMessage());
        }
    } // end writeStringToFile

    /**
     * Converts an array of strings to an ArrayList
     * @param array String[]
     * @return ArrayList The converted array
     */
    public ArrayList<String> stringArrayToList(String[] array) {
        return new ArrayList<>(Arrays.asList(array));
    } // end stringArrayToList()

    /**
     * Serialize an object to a file
     * @param obj   Object to serialize
     * @param fileName String hold file name value
     */
    public void saveObjectToFile(Object obj, String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(obj);
            out.close();
            fileOut.close();
            //showMessage("Serialization Successful\nCheckout your specified output file");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    } // end saveObjectToFile()

    /**
     * @param fileName String
     * @return Object
     * The returned object will need to be cast
     * to the collection type before it can be used.  If the type
     * was List then we call this method like this:
     * personList = (List) ui.deserialize("personlist.bin");
     * The @SuppressWarnings("unchecked") annotation will prevent
     * the compiler for reporting an error because the actual type
     * stored in the list is not specified in the code.
     */
    public Object readObjectFromFile(String fileName) {
        Object result = null;
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream obj = new ObjectInputStream(fileIn);
            result =  obj.readObject();
            //System.out.println("Deserialized Data: \n" + result.toString());
            obj.close();
            fileIn.close();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    } // end readObjectFromFile()

    /**
     * Returns a single string with newline characters inserted into "text"
     * on or near every "max" character.
     * @param text String to be split up
     * @param max integer to indicate desired line length
     * @return   String
     */
    public String insertLineBreaks(String text, int max) {
        StringBuilder sb = new StringBuilder(text);
        int i = 0;
        while((i = sb.indexOf(" ", i + max)) != -1)
            sb.replace(i, i + 1, "\n");
        return sb.toString();
    } // end insertLineBreaks()

    /**
     * Creates an imageIcon from an image file.
     * @param fileName  The file to be read
     * @return          ImageIcon
     */
    public ImageIcon getImage(String fileName) {
        try {
            image = new ImageIcon(fileName);
        }
        catch (Exception e) { print(e.getMessage()); }
        return image;
    } // end getImage()

    /**
     * Generate an array of filenames given path and type.
     * @param dirPath  The directory path to be read
     * @param ext      The file extention without the dot
     * @return         String[]
     */
    public String[] getFilesByType(String dirPath, String ext) {
        Path path = Paths.get(dirPath);
        ArrayList<String> fileList = new ArrayList<>();
        try {
            DirectoryStream<Path> dir = Files.newDirectoryStream(path, "*." + ext);
            for (Path file : dir) {
                fileList.add(file.toString());
            }
        }
        catch (Exception e) { print(e.getMessage()); }
        return fileList.toArray(new String[0]);
    } // end getFilesByType()

    /**
     *  Utility method for displaying a new window that is populated with a
     *  JPanel with some content.
     *  @param panel   Any previously constructed JPanel object.
     */
    public void displayPanel(JPanel panel) {
        JFrame app = new JFrame();
        app.setSize(300,300);
        app.add(panel);
        app.setVisible(true);
    }  // end displayPanel()

    /**
     * Creates an instance of the Nashorn javascript engine if one has not
     * already been created.
     */
    private void createJSE() {
        if (jse == null) {
            try {
                ScriptEngineManager engineManager = new ScriptEngineManager();
                jse = engineManager.getEngineByName("nashorn");
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }  // end createJSE

    /**
     * Gets a JavaScript engine object
     * @return ScriptEngine
     */
    public ScriptEngine getJSE() {
        if(jse == null) createJSE();
        return jse;
    }

    /**
     Executes the JavaScript eval() function to the supplied string value
     @param   expression String the holds expression to be evaluated
     @return  Ojbect The evaluated expression
     */
    public Object eval(String expression) {
        if(jse == null)
            createJSE();
        try {
            return jse.eval(expression);
        }
        catch (Exception e) {
            return "Expression could not be evaluated";
        }
    } // end eval()

    /**
     * Add a ui component (Node) to the list of nodes
     * @param node ui component
     */
    public void addNode(Node node) {
        gridNodes.add(node);
    } // end addNodes()

    /**
     * Remove all nodes in the list of nodes
     */
    public void clearNodes() {
        gridNodes.clear();
    } // end clearNodes()

    /**
     * Get the list of nodes
     * @return List
     */
    public List<Node> getGridNodes() {
        return gridNodes;
    } // end getGridNodes()

    /**
     * This method  will popup a new Stage with a GridPane of a certain
     * number of rows and columns populated by the nodes currently stored
     * in the list.  There must be nodes stored in the list for this method
     * to work.  Nodes can be added by calling the addNode(node) method
     * @param rows number of grid rows desired
     * @param columns number of grid columns desired
     */
    public void displayGrid(int rows, int columns) {
        if(gridNodes.size() >= (rows * columns - 1)) {
            Stage stage = new Stage();
            stage.setTitle("Display Grid");
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));
            int counter = 0;
            for(int i = 0; i < rows; i++) {
                for(int j = 0; j < columns; j++) {
                    if(counter < gridNodes.size())
                        grid.add(gridNodes.get(counter++),j,i);
                }
            }
            Scene myScene = new Scene(grid, 400,300);
            stage.setScene(myScene);
            stage.show();
        }
        else System.out.println("Check your rows and columns");
    } // end displayGrid()

    /**
     * This method will popup a new Stage with a Canvas node.
     * The canvas object will be returned so that the user can
     * it for 2D drawing.
     * @return Canvas for drawing shapes
     */
    public Canvas popupCanvas() {
        Stage stage = new Stage();
        Group group = new Group();
        Canvas canvas = new Canvas(600,600);
        group.getChildren().add(canvas);
        Scene scene = new Scene(group, 600,600);
        stage.setScene(scene);
        stage.show();
        return canvas;
    } // end popupCanvas()
    /**
     * This method initialized a scene on the stage, sets the title of the window,.
     * and adds a canvas with the width and height specified.
     * @return a GraphicsContext object for drawing shapes and text on the canvas
     */
    public GraphicsContext setUpGraphics(Stage stage, String title,
                                         int height, int width) {
        stage.setTitle(title);
        Canvas canvas = new Canvas(height, width);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Group root = new Group(canvas);
        stage.setScene(new Scene(root));
        stage.show();
        return gc;
    } // end setUpGraphics()
    /**
     * This method will popup a new Stage with a LineChart node.
     * The LineChart object will be returned so that the user can use it
     * @param chartTitle  String holding the chart title
     * @param xLabel      String holding the x-axis label
     * @param yLabel      String holding the y-axis label (Series label)
     * @param yValues     double[] array holding y-values
     * @return LineChart for displaying chart data
     */
    public LineChart<Number, Number> popupLineChart(String chartTitle, String xLabel,
                                                    String yLabel, double[] yValues) {
        Stage stage = new Stage();
        stage.setTitle("Line Chart");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xLabel);
        //creating the chart
        final LineChart<Number,Number> lineChart =
                new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(chartTitle);
        //defining a series
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(yLabel);
        //populating the series with data
        for(int i = 0; i < yValues.length; i++) {
            series.getData().add(new XYChart.Data<>(i + 1, yValues[i]));
        }
        Scene scene  = new Scene(lineChart,400,400);
        lineChart.getData().add(series);
        stage.setScene(scene);
        stage.show();
        return lineChart;
    } // end popupLineChart()
    /**
     * Displays a new window as HTML content.  Pulls up website if the url
     * starts with "http" or "file" and simple HTML content if not.
     * Examples below:
     *   ui.displayWebpage("The word <b>bold</b> is bolded"));
     *   ui.displayWebpage("file:///C:/Code/java/FXApps/ProjectUI.html");
     *   ui.displayWebpage("http://www.skillsource.org");
     * @param url html content or website
     */
    void displayWebpage(String url) {
        Stage stage = new Stage();
        stage.setTitle("HTML");
        stage.setWidth(800);
        stage.setHeight(700);
        Scene s = new Scene(new Group());

        VBox root = new VBox();

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(browser);

        if(url.startsWith("http") || url.startsWith("file")) webEngine.load(url);
        else webEngine.loadContent(url);

        root.getChildren().addAll(scrollPane);

        s.setRoot(root);

        stage.setScene(s);
        stage.show();
    } // end displayWebPage()

    /**
     * Opens the desktop default browser with the given url
     * @param url html content or website
     */
    void desktopBrowser(String url) {
        Desktop d = Desktop.getDesktop();
        try {
            d.browse(new URI(url));
        } catch (Exception e) {
            showMessage(e.getMessage());
        }
    } // end desktopBrowser()

    /**
     * Returns an array of standard Color objects
     * @return Color[] an array of 10 basic colors
     */
    public Color[] getColorsArray() {
        return new Color[]{Color.BLACK, Color.BLUE, Color.CYAN,
                Color.GREEN, Color.ORANGE, Color.RED,
                Color.YELLOW, Color.MAGENTA, Color.GRAY,
                Color.PINK};
    } // end getColorsArray()

    /**
     * Returns an array of the declared TextField objects
     * @return TextField[] array of user input fields
     */
    public TextField[] getTextFields() {
        return fields;
    } // end getTextFields()

    /**
     * Get a specific TextField object by index number
     * @param index of the desired textfield
     * @return TextField specified by index
     */
    public TextField getTextField(int index) {
        if(index < rows)
            return fields[index];
        showMessage("Field index out of range");
        return null;
    } // end getTextField()
    /**
     * @param node Node to print
     */
    public void printNode(Node node) {
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(
                Paper.NA_LETTER, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(node.getScene().getWindow())) {
            boolean success = job.printPage(node);
            if (success) {
                job.endJob();
            }
        }
    } // end printNode
    /**
     Fill the columnNames array with the supplied array elements
     @param   columnNames String array of column names for the popupTableView
     */
    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }
    /**
     Builds an ObservableList object from a 2D Object array
     @param   dataArray Object[][] The data
     @return  ObservableList The data in dataArray
     */
    private ObservableList<ObservableList<Object>> buildData(Object[][] dataArray) {
        ObservableList<ObservableList<Object>> data = FXCollections.observableArrayList();

        for (Object[] row : dataArray) {
            data.add(FXCollections.observableArrayList(row));
        }
        return data;
    }

    /**
     * This method  will popup a new Stage with a TableView node.
     * The TableView object will be returned so that the user can us it
     * @param dataArray The table will be populated from 2D Object array.
     * @return TableView for displaying a table of data
     */
    public TableView<ObservableList<Object>> popupTableView(Object[][] dataArray) {
        Stage stage = new Stage();
        StackPane pane = new StackPane();
        TableView<ObservableList<Object>> table = new TableView<>();
        table.setItems(buildData(dataArray));
        for (int col = 0; col < dataArray[0].length; col++) {
            final int curCol = col;
            final TableColumn<ObservableList<Object>,
                    Object> column = new TableColumn<>(columnNames[col]);
            //Object> column = new TableColumn<>("Col " + (curCol + 1));
            column.setCellValueFactory(param ->
                    new ReadOnlyObjectWrapper<>(param.getValue().get(curCol)));
            table.getColumns().add(column);
        }
        pane.getChildren().add(table);
        Scene scene = new Scene(pane, 300,400);
        stage.setScene(scene);
        stage.show();
        return table;
    } // end popupCanvas()

} // end class
