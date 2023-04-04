package controller;

public interface OutMessenger
{
    void sendMessageForSearchPerson(final String phoneNumber);

    void sendTextMessage(final String msg, final String toPersonId);
}
