package archive;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.logging.Logger;
import java.util.LinkedList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.Closeable;
import java.io.File;

/**
 * Архиватор переписки для определенной чат комнаты
 */

public class ArchiverById implements Closeable
{
    private LinkedList<MessageRecord> messages;
    private ObjectMapper objectMapper;
    private Logger logger;
    private File archive;

    {
        objectMapper = new ObjectMapper();
        logger = Logger.getLogger(ArchiverById.class.getName());
    }

    public ArchiverById(Path pathToArchive)
    {
        Path path = pathToArchive;

        if(!Files.exists(path))
        {
            try
            {
                Files.createDirectory(path);
            } catch (IOException e)
            {
                logger.info("Cant create directory: " + path);
            }
        }

        path = Paths.get(path + "/archive.json");

        archive = new File(path.toString());

        if(!archive.exists())
        {
            try
            {
                Files.createFile(path);
            } catch (IOException e)
            {
                logger.info("Cant create file: " + path);
            }
            refresh();
        }

        if(archiveIsNotArray())
        {
            refresh();
        }

        try
        {
            messages = objectMapper.readValue(archive, new TypeReference<LinkedList<MessageRecord>>(){});
        }
        catch (IOException e)
        {
            messages = new LinkedList<>();
            throw new RuntimeException(e);
        }
    }

    private boolean archiveIsNotArray()
    {
        JsonNode root;
        try
        {
            root = objectMapper.readTree(archive);
        }
        catch (IOException e)
        {
            return true;
        }
        return !root.isArray();
    }

    public boolean addMessage(final MessageRecord msg)
    {
        messages.add(msg);
        return true;
    }

    public void refresh()
    {
        try(JsonGenerator g = objectMapper.createGenerator(archive, JsonEncoding.UTF8))
        {
            g.writeStartArray();
            g.writeEndArray();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        if(messages != null)
            messages.clear();
    }

    @Override
    public void close() throws IOException
    {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(archive, messages);
    }

    public LinkedList<MessageRecord> getMessages()
    {
        return messages;
    }
}
