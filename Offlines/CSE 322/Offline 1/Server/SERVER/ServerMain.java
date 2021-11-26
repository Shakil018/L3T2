package SERVER;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.*;


public class ServerMain {

    public static int MAX_BUFFER_SIZE = 471661786, OCCUPIED_BUFFER = 0 ,MIN_CHUNK_SIZE = 1000, MAX_CHUNK_SIZE = 2000;
    public static int fileID = 1000;
    public static int requestId = 100;

    public static int getFileID(){
        return fileID++;
    }
    public static int getRequestId() { return requestId++; }

    public static HashMap<Integer, String > allStudents = new HashMap<>();
    public static HashMap<Integer, String> activeStudents = new HashMap<>();
    public static HashMap<Integer, MyFile> allFiles = new HashMap<>();
    public static HashMap<Integer, Request> allRequests = new HashMap<>();
    public static HashMap<Integer, HashMap<Integer, Request> > allChatBox = new HashMap<>();
    public static Queue<Integer> uploadQueue = new PriorityQueue<>();



    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket welcomeSocket = new ServerSocket(13000);

        (new ServerFileMain()).start();

        while(true) {
            System.out.println("Waiting for connection...");
            Socket socket = welcomeSocket.accept();

            Thread worker = new ClientHandler(socket);
            worker.start();

            System.out.println("Connection established");

        }

    }
}
