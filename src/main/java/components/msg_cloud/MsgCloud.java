package components.msg_cloud;

import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import archive.MessageRecord;
import javafx.geometry.Pos;
import java.io.IOException;
import javafx.fxml.FXML;


/**
 * Контроллер виджета для отображения сообщения
 */

public class MsgCloud extends VBox
{
    @FXML
    public Label text;
    @FXML
    public VBox layout;

    final String authorMessageId;

    public MsgCloud(MessageRecord msgRecord)
    {
        authorMessageId = msgRecord.getIdFrom();

        FXMLLoader fxmlLoader = new FXMLLoader(MsgCloud.class.getResource("msg_cloud.fxml"));

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

        setText(msgRecord.getText());
        layout.widthProperty().addListener(new ResizeEventListener());
    }

    public void setAlignmentRight()
    {
        layout.setAlignment(Pos.TOP_RIGHT);
    }

    public void setAlignmentLeft()
    {
        layout.setAlignment(Pos.TOP_LEFT);
    }

    public void setText(final String text)
    {
        this.text.setText(text);
    }

    public boolean thisIsMyMessage(final String myId)
    {
        return authorMessageId.equals(myId);
    }

    /**
     * Класс-контроллер изменения размера
     */
    private class ResizeEventListener implements ChangeListener<Number>
    {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1)
        {
            text.setPrefWidth(0.5 * (double) observableValue.getValue());
        }
    }

}
