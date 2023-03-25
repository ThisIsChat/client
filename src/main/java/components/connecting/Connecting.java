package components.connecting;

import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

/**
 * Контроллер виджета с ожиданием подключения
 */
public class Connecting extends VBox
{
    public Connecting()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(Connecting.class.getResource("start_loading.fxml"));

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
    }
}
