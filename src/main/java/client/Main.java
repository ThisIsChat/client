package client;

import components.login_password.LoginPassword;
import components.connecting.Connecting;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import components.signin.Signin;
import java.util.logging.Logger;
import controller.Controller;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.IOException;

public class Main extends Application implements Iface
{
    private Controller controller;
    private Logger logger;

    {
        logger = Logger.getLogger(Main.class.getName());
    }

    private void checkCmdParams()
    {
        if(!CommandLinesParams.handleParameters(getParameters()))
        {
            logger.info(CommandLinesParams.getUsage());
            Platform.exit();
            System.exit(0);
        }
    }

    @Override
    public void start(Stage stage) throws IOException
    {
        checkCmdParams();

        controller = new Controller(this);
        controller.setStage(stage);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent windowEvent)
            {
                controller.stop();
            }
        });

        Connecting connecting = new Connecting();
        Scene scene = new Scene(connecting);
        stage.setTitle("Post");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void loadLogInSceneOn(Stage stage)
    {
        Platform.runLater(()->
        {
            stage.close();
            LoginPassword loginPassword = new LoginPassword(controller);
            Scene scene = new Scene(loginPassword);
            stage.setTitle("Post");
            stage.setScene(scene);
            stage.show();
        });
    }

    @Override
    public void loadSignInSceneOn(Stage stage)
    {
        Platform.runLater(()->
        {
            stage.close();
            Signin signin = new Signin(controller);
            Scene scene = new Scene(signin);
            stage.setTitle("Post");
            stage.setScene(scene);
            stage.show();
        });
    }

    @Override
    public void loadMainSceneOn(Stage stage)
    {
        Platform.runLater(()->
        {
            stage.close();
            InterfaceController interfaceController = new InterfaceController(controller, controller.getMyId());
            Scene scene = new Scene(interfaceController);
            stage.setTitle("Post");
            stage.setScene(scene);
            stage.show();
            controller.setHandlerInputMessage(interfaceController);
        });
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}