package components.login_password;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import сontroller.HandlerLogIn;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.fxml.FXML;


/**
 * Контроллер виджета для авторизации в приложении
 */

public class LoginPassword extends VBox
{
    HandlerLogIn handlerLogIn;

    @FXML
    private TextField login;
    @FXML
    private PasswordField password;


    public LoginPassword(HandlerLogIn handlerLogIn)
    {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginPassword.class.getResource("login_password.fxml"));

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

        this.handlerLogIn = handlerLogIn;
    }

    @FXML
    public void register()
    {
        Platform.runLater(()->{handlerLogIn.setFormForRegistration();});
    }

    @FXML
    public void authentification()
    {
        if(login.getText().isEmpty() || password.getText().isEmpty())
            return;

        handlerLogIn.sendMessageForLogIn(login.getText(), password.getText());
    }
}
