package components.signin;

import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import controller.HandlerSigIn;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.fxml.FXML;

/**
 * Контроллер виджета регистрации
 */

public class Signin extends VBox
{
    @FXML
    private TextField name;
    @FXML
    private TextField surname;
    @FXML
    private TextField login;
    @FXML
    private TextField password;
    @FXML
    private TextField phoneNumber;

    private HandlerSigIn handlerSignIn;

    public Signin(HandlerSigIn handlerSignIn)
    {

        FXMLLoader fxmlLoader = new FXMLLoader(Signin.class.getResource("signin.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        this.handlerSignIn = handlerSignIn;
    }

    @FXML
    public void signIn()
    {
        if(     name.getText().isEmpty() ||
                surname.getText().isEmpty() ||
                login.getText().isEmpty() ||
                password.getText().isEmpty() ||
                phoneNumber.getText().isEmpty()    )   return;


        handlerSignIn.sendMessageForSignIn(   name.getText(),
                                surname.getText(),
                                login.getText(),
                                password.getText(),
                                phoneNumber.getText());

    }

    @FXML
    public void cancel()
    {
        handlerSignIn.cancel();
    }

}
