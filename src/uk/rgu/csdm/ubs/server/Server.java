package uk.rgu.csdm.ubs.server;

import uk.rgu.csdm.ubs.count.Processor;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static boolean doProcess = false;

    public static void startListening()
    {

    }
    public static void startListening(final int port)
    {
        Runnable r = () -> {
            listen(port);
        };
        Thread t = new Thread(r);
        t.start();
    }

    private static void listen(int port)
    {
        try
        {
            ServerSocket serverSocket = new ServerSocket(port, 5);
            while(true)
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
                if(doProcess)
                {
                    Processor.getInstance().add(received, port);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
