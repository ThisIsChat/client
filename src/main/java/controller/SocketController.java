package сontroller;

import java.nio.channels.SocketChannel;
import client.CommandLinesParams;
import java.net.InetSocketAddress;
import java.util.logging.Logger;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.TimerTask;
import java.util.Timer;
import msg_processor.*;

public class SocketController extends TimerTask
{
    private InetSocketAddress inetSocketAddress;
    private SocketChannel socketChannel;
    private ByteBuffer receive;
    private HandlerMsg handler;
    private ByteBuffer send;
    private Logger logger;
    private Timer timer;

    {
        inetSocketAddress = new InetSocketAddress(CommandLinesParams.getServerAddress(), CommandLinesParams.getServerPort());
        logger = Logger.getLogger(SocketController.class.getName());
        receive = ByteBuffer.allocate(8192);
        send = ByteBuffer.allocate(8192);
        timer = new Timer();
    }

    public SocketController(HandlerMsg handler)
    {
        this.handler = handler;
        timer.schedule(this, 10, 10);
    }

    /**
     * Остановить работу
     */
    public void stop()
    {
        timer.cancel();
        timer.purge();
        try
        {
            if(socketChannel != null && socketChannel.isOpen())
                socketChannel.close();
        }
        catch (IOException e)
        {
            logger.info("error release");
        }
    }

    /**
     * Добавить сообщение в очередь на отправку
     * @param msg
     */
    public void addMsgToQueue(final GenericPack msg)
    {
        synchronized (send)
        {
            msg.serializeTo(send);
        }
    }

    /**
     * Обработчик таймера
     */
    @Override
    public void run()
    {
        if(!connect())
            return;
        sendAllFromSendBuffer();
        readAllFromReceiveBuffer();
    }

    /**
     * Проверка подключения к серверу
     * @return
     */
    private boolean connect()
    {
        if(socketChannel == null)
        {
            try
            {
                socketChannel = SocketChannel.open(inetSocketAddress);
                socketChannel.configureBlocking(false);
            }
            catch (IOException e)
            {
                logger.info("error connect 1");
                return false;
            }
        }

        if(!socketChannel.isConnected())
        {
            try
            {
                socketChannel = SocketChannel.open(inetSocketAddress);
            }
            catch (IOException e)
            {
                logger.info("error connect 2");
                return false;
            }
        }
        return true;
    }

    /**
     * Чтение пакетов из буфера получения данных
     */
    private void readAllFromReceiveBuffer()
    {
        while (true)
        {
            try
            {
                int readCount = socketChannel.read(receive);

                if (readCount == 0)
                {
                    break;
                } else if (readCount == -1) // Произошло отключение
                {
                    socketChannel = null;
                    break;
                }
            }
            catch (IOException e)
            {
                socketChannel = null;
                logger.info("exception while read from socket");
                break;
            }

            int bufferPosition = receive.position();
            receive.flip();

            GenericPack decoded = Decoder.decodeGenericPack(receive);
            if(decoded == null)
            {
                receive.position(bufferPosition);
                logger.info("can not decode");
                break;
            }

            handler.handleInputMessage(decoded);
            receive.compact();
            receive.position(0);
        }
    }

    /**
     * Передача всех пакетов из буфера исходящих сообщений
     */
    private void sendAllFromSendBuffer()
    {
        synchronized (send)
        {
            if(send.position() > 0)
            {
                int startPosition = send.position();

                send.rewind();
                try
                {
                    int bytesWritten = 0;
                    bytesWritten = socketChannel.write(send.slice(0, startPosition));
                    logger.info("written " + bytesWritten + " bytes");
                }
                catch (IOException e)
                {
                    logger.info("error write");
                    send.position(startPosition);
                }
            }
        }
    }
}
