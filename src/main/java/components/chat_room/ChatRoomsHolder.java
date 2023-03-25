package components.chat_room;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.databind.JsonNode;
import client.CommandLinesParams;
import java.util.logging.Logger;
import java.util.Collection;
import java.util.LinkedList;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.Closeable;
import java.util.HashMap;
import java.io.File;

/**
 * Список чат-конмат, хранящийся в текстовом файле
 */

public class ChatRoomsHolder implements Closeable
{
    private HashMap<String, ChatRoomRecord> roomsList;
    private ObjectMapper objectMapper;
    private Logger logger;
    private File rooms;

    {
        objectMapper = new ObjectMapper();
        logger = Logger.getLogger(ChatRoomRecord.class.getName());
    }

    public final Collection<ChatRoomRecord> getAllChatRooms()
    {
        return roomsList.values();
    }

    public ChatRoomsHolder()
    {
        Path path = Paths.get(CommandLinesParams.getSystemDirectory() + "/chat_rooms.json");

        if(!Files.exists(path))
        {
            try
            {
                Files.createFile(path);
            } catch (IOException e)
            {
                logger.info("Cant create directory: " + path);
            }
        }

        rooms = new File(path.toString());

        if(!rooms.exists())
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
            LinkedList<ChatRoomRecord> records = objectMapper.readValue(rooms, new TypeReference<LinkedList<ChatRoomRecord>>(){});
            roomsList = new HashMap<>(records.size());
            for(final ChatRoomRecord record: records)
                roomsList.put(record.getChatRoomId(), record);
        }
        catch (IOException e)
        {
            roomsList = new HashMap<>();
            throw new RuntimeException(e);
        }
    }

    public void refresh()
    {
        try(JsonGenerator g = objectMapper.createGenerator(rooms, JsonEncoding.UTF8))
        {
            g.writeStartArray();
            g.writeEndArray();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        if(roomsList != null)
            roomsList.clear();
    }

    private boolean archiveIsNotArray()
    {
        JsonNode root;
        try
        {
            root = objectMapper.readTree(rooms);
        }
        catch (IOException e)
        {
            return true;
        }
        return !root.isArray();
    }

    public boolean addRoom(final ChatRoomRecord room)
    {
        roomsList.put(room.getChatRoomId(), room);
        return true;
    }

    public ChatRoomRecord getRoomBy(final String id)
    {
        if(roomsList.containsKey(id))
            return roomsList.get(id);

        return null;
    }

    @Override
    public void close() throws IOException
    {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(rooms, roomsList.values());
    }

    public boolean contains(ChatRoomRecord chatRoomRecord)
    {
        return roomsList.containsKey(chatRoomRecord.getChatRoomId());
    }
}
