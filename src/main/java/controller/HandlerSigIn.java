package controller;

public interface HandlerSigIn
{
    void sendMessageForSignIn(String name, String surname, String login, String password, String phoneNumber);

    void cancel();
}
