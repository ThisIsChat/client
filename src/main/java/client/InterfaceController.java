package client;

import controller.OutMessenger;
import archive.MessageArchive;
import archive.MessageRecord;
import components.chat_room.ChatRoomController;
import components.chat_room.ChatRoomRecord;
import components.chat_room.ChatRoomsHolder;
import components.founded_person.FoundedPerson;
import components.msg_cloud.MsgCloud;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import msg_processor.GenericPack;
import msg_processor.SearchPersonAnswer;
import msg_processor.TextMessage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * Контроллер основной формы
 */
public class InterfaceController extends VBox implements HandlerInputMessage, EventHandler, HandlerSearchedPerson
{
    @FXML
    public TextArea textMessage;
    @FXML
    public VBox chatRoomLayout;
    @FXML
    public VBox messagesLayout;
    @FXML
    public VBox searchPersonLayout;
    @FXML
    public TextField phoneNumber;
    @FXML
    public Label notFoundLabel;

    private ListView<FoundedPerson> foundedPersons;
    private ListView<ChatRoomController> chatRooms;
    private ChatRoomsHolder chatRoomsHolder;
    private ListView<MsgCloud> messages;
    private OutMessenger outMessenger;
    private MessageArchive archive;
    private Logger logger;
    private String myId;

    {
        archive = new MessageArchive(CommandLinesParams.getSystemDirectory() + "/archive");
        logger = Logger.getLogger(InterfaceController.class.getName());
        chatRoomsHolder = new ChatRoomsHolder();
        foundedPersons = new ListView<>();
        chatRooms = new ListView<>();
        messages = new ListView<>();
    }

    public InterfaceController(OutMessenger outMessenger, final String myId)
    {
        FXMLLoader fxmlLoader = new FXMLLoader(InterfaceController.class.getResource("main_interface.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            logger.info("Catch in constructor");
        }

        initialize(outMessenger, myId);
    }

    public void initialize(OutMessenger outMessenger, final String myId)
    {
        this.myId = myId;
        messages.setCellFactory((ListView<MsgCloud> fact) -> new ListViewCell(myId));

        this.outMessenger = outMessenger;

        searchPersonLayout.getChildren().add(foundedPersons);
        messagesLayout.getChildren().add(messages);
        chatRoomLayout.getChildren().add(chatRooms);

        VBox.setVgrow(foundedPersons, Priority.ALWAYS);
        VBox.setVgrow(messages, Priority.ALWAYS);
        VBox.setVgrow(chatRooms, Priority.ALWAYS);

        final Collection<ChatRoomRecord> allChatRooms = chatRoomsHolder.getAllChatRooms();

        for(final ChatRoomRecord chatRoom: allChatRooms)
        {
            final MessageRecord lastMessage = archive.getLastMessageFor(chatRoom.getChatRoomId());
            ChatRoomController widgetChatRoom = createChatRoomWidget(chatRoom, lastMessage);
            Platform.runLater(()->
            {
                chatRooms.getItems().add(widgetChatRoom);
            });
        }
    }

    /**
     * Экшн по нажатию кнопки найти человека по номеру телефона
     */
    public void searchPerson()
    {
        if(phoneNumber.getText().isEmpty())
            return;

        Platform.runLater(()->{
            notFoundLabel.setVisible(false);
            foundedPersons.getItems().clear();
        });
        outMessenger.sendMessageForSearchPerson(phoneNumber.getText());
    }

    /**
     * Экшн по нажатию кнопки отправить сообщение
     * @param actionEvent
     */
    public void sendMsg(ActionEvent actionEvent)
    {
        if(chatRooms.getSelectionModel().getSelectedItem() == null)
            return;

        if(textMessage.getText().isEmpty())
            return;

        final String idTo = chatRooms.getSelectionModel().getSelectedItem().getId();

        outMessenger.sendTextMessage(textMessage.getText(), idTo);

        final MessageRecord messageRecord = new MessageRecord(myId,
                LocalDateTime.now(), textMessage.getText());

        if(!thisMessageForMe(idTo))
            archive.addMessage(idTo, messageRecord);

        Platform.runLater(()->{
            if(!thisMessageForMe(idTo))
                messages.getItems().add(new MsgCloud(messageRecord));
            textMessage.clear();
        });
    }

    private boolean thisMessageForMe(final String idTo)
    {
        return idTo.equals(myId);
    }

    private ChatRoomController createChatRoomWidget(final ChatRoomRecord roomRecord, final MessageRecord messageRecord)
    {
        chatRoomsHolder.addRoom(roomRecord);
        ChatRoomController widgetChatRoom = new ChatRoomController(roomRecord);
        widgetChatRoom.setChatroomName(roomRecord.getChatRoomName());
        widgetChatRoom.setId(roomRecord.getChatRoomId());
        widgetChatRoom.setLastMsgPreview(messageRecord);
        widgetChatRoom.setOnMouseClicked(this);
        return widgetChatRoom;
    }

    /**
     * Обработка текстового сообщения
     * @param msg
     */
    @Override
    public void handleTextMessage(GenericPack msg)
    {
        ChatRoomRecord roomRecord = chatRoomsHolder.getRoomBy(msg.idFrom);

        final MessageRecord messageRecord = new MessageRecord(msg.idFrom,
                LocalDateTime.now(),
                ((TextMessage)msg.customSerializable).message);

        if(roomRecord == null)
        {
            roomRecord = new ChatRoomRecord(msg.idFrom, msg.fromName);
            ChatRoomController widgetChatRoom = createChatRoomWidget(roomRecord, messageRecord);

            Platform.runLater(()->
            {
                chatRooms.getItems().add(widgetChatRoom);
            });
        }
        else
        {
            Platform.runLater(()->
            {
                ObservableList<ChatRoomController> rooms = chatRooms.getItems();
                for(ChatRoomController room: rooms)
                    if(room.getId().equals(messageRecord.getIdFrom()))
                        room.setLastMsgPreview(messageRecord);
            });
        }

        final String currentChat = messages.getId();
        if(currentChat != null && currentChat.equals(msg.idFrom))
            Platform.runLater(()->{messages.getItems().add(new MsgCloud(messageRecord));});

        archive.addMessage(msg.idFrom, messageRecord);
    }

    /**
     * Добавить еще одну чат комнату на главную форму
     * @param chatRoomRecord
     */
    @Override
    public void addChatRoomWidget(ChatRoomRecord chatRoomRecord)
    {
        if(chatRoomsHolder.contains(chatRoomRecord))
            return;

        final MessageRecord lastMessage = archive.getLastMessageFor(chatRoomRecord.getChatRoomId());
        ChatRoomController widgetChatRoom = createChatRoomWidget(chatRoomRecord, lastMessage);

        Platform.runLater(()->
        {
            chatRooms.getItems().add(widgetChatRoom);
        });
    }

    /**
     * Обработчик сообщения об найденном по номеру телефона человеке
     * @param msg
     */
    @Override
    public void handleSearchPersonAnswer(SearchPersonAnswer msg)
    {
        if(msg.getId().equals(""))
        {
            Platform.runLater(()->{notFoundLabel.setVisible(true);});
            return;
        }

        FoundedPerson foundedPerson = new FoundedPerson(msg.getName(), msg.getSurname(), msg.getPhoneNumber(), msg.getId(), this);
        Platform.runLater(()->
        {
            foundedPersons.getItems().add(foundedPerson);
        });
    }

    /**
     * Закрыть архив сообщений и архив чатов
     */
    @Override
    public void close()
    {
        try
        {
            chatRoomsHolder.close();
            archive.close();
        }
        catch (IOException e)
        {
            logger.info("cant close chat room holder");
        }
    }

    /**
     * Обработчик нажатия на виджет чат-комнаты
     * @param event
     */
    @Override
    public void handle(Event event)
    {
        LinkedList<MessageRecord> allMessages = archive.getAllMessagesFor(chatRooms.getSelectionModel().getSelectedItem().getId());
        messages.getItems().clear();
        for(final MessageRecord msg: allMessages)
        {
            MsgCloud msgCloud = new MsgCloud(msg);

            Platform.runLater(()->
            {
                messages.getItems().add(msgCloud);
            });
        }
        messages.setId(chatRooms.getSelectionModel().getSelectedItem().getId()); // Теперь на данном виджете сообщения для определенного id
    }
}