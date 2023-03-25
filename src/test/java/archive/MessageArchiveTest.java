package archive;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class MessageArchiveTest
{
    @Test
    public void addMessage_Test()
    {
        MessageArchive archive = new MessageArchive("src/test/java/archive/arch");
        MessageRecord record = new MessageRecord("1", LocalDateTime.now(), "Hello, how are you?");

        assertTrue(archive.addMessage("1", record));

        try
        {
            archive.close();
        } catch (IOException e)
        {
            System.out.println("Cant close");
        }
    }

    @Test
    public void readAllMessage_Test()
    {
        MessageArchive archive = new MessageArchive("src/test/java/archive/arch");

        archive.addArchiver("2");

        archive.refresh();

        MessageRecord record1 = new MessageRecord("2", LocalDateTime.now(), "Hello, how are you?");
        MessageRecord record2 = new MessageRecord("2", LocalDateTime.now(), "Hello, how are you?");
        MessageRecord record3 = new MessageRecord("2", LocalDateTime.now(), "Hello, how are you?");

        archive.addMessage("2", record1);
        archive.addMessage("2", record2);
        archive.addMessage("2", record3);

        LinkedList<MessageRecord> messages = archive.getAllMessagesFor("2");

        assertEquals(3, messages.size());
        try
        {
            archive.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

}