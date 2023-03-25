package archive;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.io.File;

/**
 * Пишет архивы сообщений на жесткий диск
 */
public class MessageArchive implements Closeable
{
    private String pathToArchive;

    public MessageArchive(String pathToArchive)
    {
        this.pathToArchive = pathToArchive;
        File theArchive = new File(this.pathToArchive);
        if(!theArchive.exists())
            theArchive.mkdir();
    }

    public void addArchiver(final String id)
    {
        archivers.put(id, new ArchiverById(Paths.get(pathToArchive + "/" + id)));
    }

    public boolean addMessage(final String id, final MessageRecord msg)
    {
        if(!archivers.containsKey(id))
            addArchiver(id);

        ArchiverById archiver = archivers.get(id);
        return archiver.addMessage(msg);
    }

    public void refresh()
    {
        for(ArchiverById archiver: archivers.values())
            archiver.refresh();
    }

    public LinkedList<MessageRecord> getAllMessagesFor(final String id)
    {
        if(!archivers.containsKey(id))
            addArchiver(id);

        ArchiverById archiver = archivers.get(id);
        return archiver.getMessages();
    }

    public MessageRecord getLastMessageFor(final String id)
    {
        final List<MessageRecord> allMessages = getAllMessagesFor(id);
        if(allMessages.isEmpty())
            return new MessageRecord(id, LocalDateTime.now(), "");

        return allMessages.get(allMessages.size() - 1);
    }

    @Override
    public void close() throws IOException
    {
        for(ArchiverById archiver: archivers.values())
            archiver.close();
    }

    private Map<String, ArchiverById> archivers = new HashMap<>();
}
