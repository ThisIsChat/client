package client;

import javafx.application.Application;
import java.io.File;


/**
 * Это синглтон, в котором хранится путь к системной директории запущенного приложения
 * Путь передается командной строкой
 */
public class CommandLinesParams
{
    static
    {
        commandLinesParams = new CommandLinesParams();
    }

    static final private String systemPathParam;
    static final private String serverPortParam;
    static final private String serverAddressParam;

    static
    {
        systemPathParam     = "system_path";
        serverPortParam     = "server_port";
        serverAddressParam  = "server_address";
    }

    static public String getSystemDirectory()
    {
        return commandLinesParams.directory;
    }

    static public int getServerPort()
    {
        return commandLinesParams.serverPort;
    }

    static public String getServerAddress()
    {
        return commandLinesParams.serverAddress;
    }

    static public String getUsage()
    {
        StringBuffer builder = new StringBuffer();

        return "Usage --" + systemPathParam + "=/path/to/server/directory --" + serverPortParam + "=port --" + serverAddressParam + "=ser.ver.add.res";
    }

    static public boolean handleParameters(final Application.Parameters parameters)
    {
        final String directory = parameters.getNamed().get(systemPathParam);
        if(directory == null)
            return false;


        final String port = parameters.getNamed().get(serverPortParam);
        if(port == null)
            return false;
        try
        {
            commandLinesParams.serverPort = Integer.parseInt(port);
        }
        catch (NumberFormatException e)
        {
            return false;
        }

        final String address = parameters.getNamed().get(serverAddressParam);
        if(address == null)
            return false;

        commandLinesParams.serverAddress = address;

        return setSystemDirectory(directory);
    }

    private CommandLinesParams() {}

    static private CommandLinesParams commandLinesParams;

    private String serverAddress;
    private String directory;
    private int serverPort;

    public static boolean setSystemDirectory(final String directory)
    {
        File theDir = new File(directory);
        boolean directoryExists = theDir.exists();
        if(!directoryExists)
            directoryExists = theDir.mkdir();

        if (directoryExists)
            commandLinesParams.directory = directory;

        return directoryExists;
    }
}
