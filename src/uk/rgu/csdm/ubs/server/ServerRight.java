package uk.rgu.csdm.ubs.server;

import uk.rgu.csdm.ubs.count.Processor;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerRight {

    public static void startListening()
    {
        Runnable r = () -> {
            listen();
        };
        Thread t = new Thread(r);
        t.start();
    }

    private static void listen()
    {
        try
        {
            ServerSocket serverSocket = new ServerSocket(4344, 5);
            while(true)
            {
                try
                {
                    Socket socket = serverSocket.accept();
                    InputStream is = socket.getInputStream();
                    byte[] lenBytes = new byte[4];
                    is.read(lenBytes, 0, 4);
                    int len = (((lenBytes[3] & 0xff) << 24) | ((lenBytes[2] & 0xff) << 16) |
                            ((lenBytes[1] & 0xff) << 8) | (lenBytes[0] & 0xff));
                    byte[] receivedBytes = new byte[len];
                    is.read(receivedBytes, 0, len);
                    String received = new String(receivedBytes, 0, len);
                    Processor.getInstance().addRight(received);
                    is.close();
                    socket.close();
                }
                catch(SocketTimeoutException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
