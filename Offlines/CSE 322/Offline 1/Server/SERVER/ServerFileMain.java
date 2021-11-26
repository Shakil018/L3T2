package SERVER;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class ServerFileMain extends Thread{


    @Override
    public void run(){
        ServerSocket upDownSocket = null;
        try {
            upDownSocket = new ServerSocket(12000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            while(true){
                Socket clientSocket = upDownSocket.accept();

                (new ServerFileHandler(clientSocket)).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
