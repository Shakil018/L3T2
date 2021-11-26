package CLIENT;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private Button LoginBtn;

    @FXML
    private Label LoginLabel;

    @FXML
    private TextField StudentIdField;

    @FXML
    private PasswordField StudentPwField;

    public void Login(ActionEvent event){
        if(StudentIdField.getText().equals("user") && StudentPwField.getText().equalsIgnoreCase("12")){
            LoginLabel.setText("Login success");
        }
        else{
            LoginLabel.setText("Login failed");
        }
    }



}
