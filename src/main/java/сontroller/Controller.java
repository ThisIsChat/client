package сontroller;

import static msg_processor.MessageConstants.kAuthentificationOk;
import client.HandlerInputMessage;
import java.util.logging.Logger;
import javafx.stage.Stage;
import msg_processor.*;
import shadow.Shadow;
import client.Iface;

public class Controller  implements HandlerMsg, HandlerLogIn, HandlerSigIn, OutMessenger
{
    private HandlerInputMessage handlerInputMessage;
    private SocketController socketController;
    private Shadow shadow;
    private Logger logger;
    private Stage stage;
    private Iface iface;
    private String myId;

    {
        logger = Logger.getLogger(Controller.class.getName());
        shadow = new Shadow();
        myId = "";
    }

    /**
     * Конструктор
     * @param iface
     */
    public Controller(Iface iface)
    {
        this.iface = iface;
        socketController = new SocketController(this);
    }

    /**
     * Вернуть идентификатор
     * @return
     */
    public final String getMyId()
    {
        return myId;
    }

    /**
     * Ссылка на основную сцену отображения графики
     * @param stage
     */
    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    /**
     * Ссылка на визуализатор входящих сообщений
     * @param handlerInputMessage
     */
    public void setHandlerInputMessage(HandlerInputMessage handlerInputMessage)
    {
        this.handlerInputMessage = handlerInputMessage;
    }

    /**
     * Остановка работы
     */
    public void stop()
    {
        socketController.stop();
        if(handlerInputMessage != null)
            handlerInputMessage.close();
    }

    /**
     * Обработчка входящих сообщений
     * @param inputMessage
     */
    @Override
    public void handleInputMessage(GenericPack inputMessage)
    {
        LoginPasswordRequestMessage loginPasswordRequestMessage = LoginPasswordRequestMessage.convert(inputMessage.customSerializable);
        if(loginPasswordRequestMessage != null)
        {
            handleRequestForLogin();
            return;
        }

        AuthentificationStatus authentificationStatus = AuthentificationStatus.convert(inputMessage.customSerializable);
        if(authentificationStatus != null)
        {
            handleAuthentificationStatus(inputMessage);
            return;
        }

        TextMessage textMessage = TextMessage.convert(inputMessage.customSerializable);
        if(textMessage != null)
        {
            handleTextMessage(inputMessage);
            return;
        }

        SearchPersonAnswer searchPersonAnswer = SearchPersonAnswer.convert(inputMessage.customSerializable);
        if(searchPersonAnswer != null)
        {
            handle(searchPersonAnswer, inputMessage.idTo);
            return;
        }
    }

    /**
     * Получен запрос от сервера с просьбой идентифицироваться
     */
    public void handleRequestForLogin()
    {
        setLogInStage();
        final String login = shadow.getLogin();
        final String password = shadow.getPassword();

        if(login == null || password == null)
            return;

        sendMessageForLogIn(login, password);
    }

    /**
     * Получен пакет со статусом авторизации/регистрации
     * @param status
     */
    public void handleAuthentificationStatus(GenericPack status)
    {
        AuthentificationStatus authStatus = (AuthentificationStatus) status.customSerializable;
        if(authStatus.getStatus().getValue() == kAuthentificationOk.getValue())
        {
            myId = status.idTo;
            logger.info("My id is: " + myId);
            iface.loadMainSceneOn(stage);
            shadow.saveNameSurname(authStatus.name, authStatus.surname);
        }
    }

    /**
     * Обработчка входящего текстового сообщения
     * @param message
     */
    public void handleTextMessage(GenericPack message)
    {
        if(!message.idTo.equals(myId))
            return;

        if(handlerInputMessage != null)
            handlerInputMessage.handleTextMessage(message);
    }

    /**
     * Обработка входящего сообщения с ответом на поиск человека по номеру телефона
     * @param msg
     * @param idTo
     */
    public void handle(SearchPersonAnswer msg, final String idTo)
    {
        if(!idTo.equals(myId))
            return;

        if(handlerInputMessage != null)
            handlerInputMessage.handleSearchPersonAnswer(msg);
    }

    private final String getFrom()
    {
        final String name = shadow.getName();
        final String surname = shadow.getSurname();
        String from = "";
        if(name != null && surname != null)
            from = name + " " + surname;

        return from;
    }

    /**
     * Обработчик события идентификации по логину и паролю. Создание сообщения
     * @param login
     * @param password
     */
    @Override
    public void sendMessageForLogIn(String login, String password)
    {
        LoginPasswordAnswerMessage answerMessage = new LoginPasswordAnswerMessage(login, password);
        shadow.saveLoginPassword(login, password);
        GenericPack genericPack = new GenericPack(answerMessage, myId, "", getFrom());
        socketController.addMsgToQueue(genericPack);
    }

    /**
     * Обработчик события нажатия на кнопку "Зарегистрироваться"
     */
    @Override
    public void setFormForRegistration()
    {
        iface.loadSignInSceneOn(stage);
    }

    /**
     * Обработчик события зарегистрироваться
     * @param name
     * @param surname
     * @param login
     * @param password
     * @param phoneNumber
     */
    @Override
    public void sendMessageForSignIn(String name, String surname, String login, String password, String phoneNumber)
    {
        Registration registration = new Registration(name, surname, login, password, phoneNumber);
        GenericPack genericPack = new GenericPack(registration, myId, "", getFrom());
        socketController.addMsgToQueue(genericPack);
        shadow.savePersonalData(login, password, name, surname);
    }

    private void setLogInStage()
    {
        iface.loadLogInSceneOn(stage);
    }

    @Override
    public void cancel()
    {
        setLogInStage();
    }

    /**
     * Обработчик события найти по номеру телефона
     * @param phoneNumber
     */
    @Override
    public void sendMessageForSearchPerson(String phoneNumber)
    {
        SearchPerson query = new SearchPerson(phoneNumber);
        GenericPack searchPersonPack = new GenericPack(query, myId, "", getFrom());
        socketController.addMsgToQueue(searchPersonPack);
    }

    /**
     * Обработчик события отправки текстового сообщения
     * @param msg
     * @param toPersonId
     */
    @Override
    public void sendTextMessage(String msg, String toPersonId)
    {
        TextMessage message = new TextMessage(msg);
        GenericPack messagePack = new GenericPack(message, myId, toPersonId, getFrom());
        socketController.addMsgToQueue(messagePack);
    }
}
