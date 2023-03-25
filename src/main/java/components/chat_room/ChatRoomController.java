package components.chat_room;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import archive.MessageRecord;
import java.io.IOException;
import javafx.fxml.FXML;
import java.util.Arrays;
import java.util.List;

/**
 * Контроллер виджета чат-комнаты
 */

public class ChatRoomController extends VBox
{
    @FXML
    Label chatroom;
    @FXML
    Label lastMsgPreview;
    @FXML
    Label abbreviation;

    /**
     * Установить аббревиатуру чат-комнаты исходя из названия
     * @param chatroomName
     */
    public void setChatroomName(final String chatroomName)
    {
        Platform.runLater(()->
        {
            chatroom.setText(chatroomName);

            String abbr = new String();

            List<String> words = Arrays.asList(chatroomName.split(" "));

            for(int i = 0; i < words.size() && i < 2; ++i)
                abbr += (words.get(i).charAt(0));

            abbreviation.setText(abbr);
        });
    }

    /**
     * Установить превью последнего сообщения
     * @param messageRecord
     */
    public void setLastMsgPreview(final MessageRecord messageRecord)
    {
        setLastMsgPreview(messageRecord.getText());
    }

    /**
     * Установить превью последнего сообщения
     * @param message
     */
    public void setLastMsgPreview(final String message)
    {
        Platform.runLater(()->{
            lastMsgPreview.setText(message);
        });
    }

    public ChatRoomController(ChatRoomRecord record)
    {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatRoomController.class.getResource("chat_room.fxml"));

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

        setChatroomName(record.getChatRoomName());
        setLastMsgPreview("");
    }
}
