package org.example.hammingcode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ChoiceBox<String> ChoicesBox;

    @FXML
    private ChoiceBox<String> bitsChoicesBox;

    @FXML
    private TextArea HammingCodeTextArea;

    @FXML
    private Button browseButton;

    @FXML
    private TextArea inputTextArea;

    @FXML
    private TextField pathTextField;

    @FXML
    private TextArea resutlTextArea;
    TextArea errorIndexes;


    ArrayList<Character> charactersList;
    StringBuilder code;
    TextArea convertMessage;
    TextField error;
    TextArea recivedTextArea;
    TextArea BitsTextedArea;
    StringBuilder copyCode;
    StringBuilder m;


    File selectedFile;
    boolean bits;

    StringBuilder recBits;

    @FXML
    void browse(ActionEvent ignoredEvent) {
        selectedFile = openFileDialog((Stage) browseButton.getScene().getWindow());
        if (selectedFile != null) {
            inputTextArea.clear();
            pathTextField.setText(selectedFile.getAbsolutePath());
            charactersList = new ArrayList<>();
            StringBuilder s = new StringBuilder();

            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                int c;
                while ((c = br.read()) != -1) {
                    char character = (char) c;
                    charactersList.add(character);
//                    inputTextArea.appendText(String.valueOf(character));
                    s.append(character);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            inputTextArea.setText(s.toString());
            BitsType(bits);
        }
    }
    @FXML
    void clear(ActionEvent ignoredEvent) {
        HammingCodeTextArea.clear();
        inputTextArea.clear();
        pathTextField.clear();
        resutlTextArea.clear();
        code = new StringBuilder();
        copyCode = new StringBuilder();
        selectedFile = null;
        recivedTextArea.clear();
        BitsTextedArea.clear();
        error.clear();
        convertMessage.clear();
        errorIndexes.clear();

    }
    @FXML
    void send(ActionEvent ignoredEvent) {
        Random random = new Random();
        if (copyCode == null)
            return;
        code= new StringBuilder(copyCode);
        if ("Send with Single Error".equals(ChoicesBox.getValue())) {
            for (int i = 0; i < code.length(); i += 13) {
                int randomBitIndex = i + 1 + random.nextInt(12);
                    if (randomBitIndex < code.length()) {
                        char bit = code.charAt(randomBitIndex);
                        char newBit = (bit == '0') ? '1' : '0';
                        code.setCharAt(randomBitIndex, newBit);
                    }
            }
        }
        if ("Send with Random Error".equals(ChoicesBox.getValue())) {
            for (int i = 0; i < code.length(); i += 13) {
                int randomBitIndex = i + 1 + random.nextInt(12);
                if (random.nextInt(2) != 0 ) {
                    if (randomBitIndex < code.length()) {
                        char bit = code.charAt(randomBitIndex);
                        char newBit = (bit == '0') ? '1' : '0';
                        code.setCharAt(randomBitIndex, newBit);
                    }
                }
            }
        }
        if ("burst error".equals(ChoicesBox.getValue())) {
            for (int i = 0; i < code.length(); i += 13) {
                int randomBitIndex = i + 1 + random.nextInt(8);
                if (random.nextInt(2) != 0 ) {
                    if (randomBitIndex < code.length()) {
                        char bit = code.charAt(randomBitIndex);
                        char newBit = (bit == '0') ? '1' : '0';
                        code.setCharAt(randomBitIndex, newBit);
                        char bit2 = code.charAt(randomBitIndex);
                        char newBit2 = (bit2 == '0') ? '1' : '0';
                        code.setCharAt(randomBitIndex +1, newBit2);
                        char bit3 = code.charAt(randomBitIndex);
                        char newBit3 = (bit3 == '0') ? '1' : '0';
                        code.setCharAt(randomBitIndex +2, newBit3);
                        char bit4 = code.charAt(randomBitIndex);
                        if (random.nextInt(2) != 0 ) {
                            char newBit4 = (bit4 == '0') ? '1' : '0';
                            code.setCharAt(randomBitIndex + 3, newBit4);
                        }
                    }
                }
            }
        }
        recivedTextArea.setText(code.toString());
        setReceivedTextArea();
        StringBuilder err= new StringBuilder();
        recBits = new StringBuilder();
        m = new StringBuilder();
        double errorNum = 0;
        for (int i = 0; i <= code.length() - 13; i += 13) {
            String substring = code.substring(i, i + 13);

            int xorResult = 0;
            for (int j = 0; j < 13; j++) {
                if (substring.charAt(j) == '1') {
                    xorResult ^= j;
                }
            }
            if (xorResult != 0){
                char c = code.charAt(i+xorResult) == '0'? '1':'0';
                code.setCharAt(i+xorResult, c);
                errorNum++;
            }
            err.append(xorResult).append(", ");
            StringBuilder temp = new StringBuilder();
            for (int j = 0; j < 13; j++) {
                if (isNotPowerOfTwo(j) && j != 0) {
                    temp.append(code.charAt(i+j));
                }

            }
            int c = Integer.parseInt(temp.toString(),2);
            m.append((char) c);
            recBits.append(temp);
        }
        error.setText(String.valueOf((errorNum /((code.length())- (double) code.length() /13)) *100));
        errorIndexes.setText(err.toString());
        convertMessage.setText(m.toString());
        setReceivedBits();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ChoicesBox.getItems().addAll("Send without errors", "Send with Single Error", "Send with Random Error","burst error");
        ChoicesBox.setValue("Send without errors");
        bitsChoicesBox.getItems().addAll("Bytes", "Continuous");
        bitsChoicesBox.setValue("Continuous");
        bits = false;
        bitsChoicesBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

                if (newValue.equals("Bytes")) {
                    BitsType(true);
                    bits = true;
                } else {
                    BitsType(false);
                    bits =false;
                }
                setReceivedTextArea();
                setReceivedBits();
            }
        });

    }
    private File openFileDialog(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        File initialDirectory = new File(System.getProperty("user.home"));
        fileChooser.setInitialDirectory(initialDirectory);

        // Adding a filter to show only .txt files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        return fileChooser.showOpenDialog(primaryStage);
    }

    public static boolean isNotPowerOfTwo(int number) {
        if (number <= 0) {
            return true;
        }
        return (number & (number - 1)) != 0;
    }

    public void BitsType(boolean bo){
        if (selectedFile == null)
            return;
        StringBuilder b = new StringBuilder();
        for (char ch :charactersList){
            String replace = String.format("%8s", Integer.toBinaryString(ch)).replace(' ', '0');
//            replace = new StringBuilder(replace).reverse().toString();

            if (bo)
                b.append(replace).append(", ");
            else
                b.append(replace);
        }
        HammingCodeTextArea.setText(b.toString());
         code = new StringBuilder();
        StringBuilder displayedCode = new StringBuilder();

        for (Character character : charactersList) {
            String bitString = String.format("%8s", Integer.toBinaryString(character)).replace(' ', '0');
//            bitString = new StringBuilder(bitString).reverse().toString();
            short[] bitArray = new short[13];
            int counter = 0;
            for (int j = 1; j < 13; j++) {
                if (isNotPowerOfTwo(j)) {
                    char bitChar = bitString.charAt(counter);
                    bitArray[j] = (short) Character.getNumericValue(bitChar);
                    counter++;
                }
            }
            bitArray[1] = (short) ((bitArray[1]) ^ (bitArray[3]) ^ (bitArray[5]) ^ (bitArray[7]) ^ (bitArray[9]) ^ (bitArray[11]));
            bitArray[2] = (short) ((bitArray[2]) ^ (bitArray[3]) ^ (bitArray[6]) ^ (bitArray[7]) ^ (bitArray[10]) ^ (bitArray[11]));
            bitArray[4] = (short) ((bitArray[4]) ^ (bitArray[5]) ^ (bitArray[6]) ^ (bitArray[7]) ^ (bitArray[12]));
            bitArray[8] = (short) ((bitArray[8]) ^ (bitArray[9]) ^ (bitArray[10]) ^ (bitArray[11]) ^ (bitArray[12]));
//            for (int j = 0; j < bitArray.length; j++)
//                bitArray[0] = (short) ((bitArray[0]) ^ (bitArray[j]));
            for (short t : bitArray) {
                displayedCode.append(String.valueOf(t));
                code.append(String.valueOf(t));
            }
            if (bo)
                displayedCode.append(", ");
            copyCode = new StringBuilder(code);
        }
        resutlTextArea.setText(displayedCode.toString());
    }

    void setReceivedTextArea(){
        if (!bits)
            recivedTextArea.setText(code.toString());
        else {
            StringBuilder copyCode = new StringBuilder(code);
            for (int i = 13; i < copyCode.length(); i += 15) {
                copyCode.insert(i, ", ");
            }
            recivedTextArea.setText(copyCode.toString());
        }
    }

    void setReceivedBits(){
        if (!bits)
            BitsTextedArea.setText(recBits.toString());
        else {
            if (recBits!= null) {
                StringBuilder copyCode = new StringBuilder(recBits);
                for (int i = 8; i < copyCode.length(); i += 10) {
                    copyCode.insert(i, ", ");
                }
                BitsTextedArea.setText(copyCode.toString());
            }
        }
    }
    void input (){
        inputTextArea.clear();
        pathTextField.setText(selectedFile.getAbsolutePath());
        charactersList = new ArrayList<>();
        StringBuilder s = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
            int c;
            while ((c = br.read()) != -1) {
                char character = (char) c;
                charactersList.add(character);
//                    inputTextArea.appendText(String.valueOf(character));
                s.append(character);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        inputTextArea.setText(s.toString());
        BitsType(bits);
    }


}
