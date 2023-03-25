package components.founded_person;

import client.HandlerSearchedPerson;
import components.chat_room.ChatRoomRecord;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Контроллер виджета для поиска человека по номеру телефона
 */

public class FoundedPerson extends VBox
{
    HandlerSearchedPerson handlerSearchedPerson;

    @FXML
    Label name;
    @FXML
    Label surname;
    @FXML
    Label phoneNumber;
    @FXML
    Label id;

    @FXML
    public void addToChatList()
    {
        final String chatRoomName = name.getText() + " " + surname.getText();
        handlerSearchedPerson.addChatRoomWidget(new ChatRoomRecord(id.getText(), chatRoomName));
    }

    public FoundedPerson(final String name, final String surname, final String phoneNumber, final String id, HandlerSearchedPerson handlerSearchedPerson)
    {
        FXMLLoader fxmlLoader = new FXMLLoader(FoundedPerson.class.getResource("founded_person.fxml"));

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

        this.handlerSearchedPerson = handlerSearchedPerson;
        this.phoneNumber.setText(phoneNumber);
        this.surname.setText(surname);
        this.name.setText(name);
        this.id.setText(id);
    }
}
