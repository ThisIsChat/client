package components.chat_room;

import client.CommandLinesParams;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ChatRoomsHolderTest
{
    {
        CommandLinesParams.setSystemDirectory("src/test/java/components/chat_room/");
    }

    @Test
    public void addChatRoom_Test()
    {
        ChatRoomsHolder chatRoomsHolder = new ChatRoomsHolder();

        ChatRoomRecord record = new ChatRoomRecord("1", "Petr Petrov");

        assertTrue(chatRoomsHolder.addRoom(record));

        try
        {
            chatRoomsHolder.close();
        } catch (IOException e)
        {
            System.out.println("cant close");
        }
    }

    @Test
    public void getChatRoomById_Test()
    {
        ChatRoomsHolder chatRoomsHolder = new ChatRoomsHolder();

        ChatRoomRecord record = chatRoomsHolder.getRoomBy("1");

        assertTrue(record.getChatRoomId().equals("1"));

        try
        {
            chatRoomsHolder.close();
        } catch (IOException e)
        {
            System.out.println("cant close");
        }
    }
}