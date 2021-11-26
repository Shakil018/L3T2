package CLIENT;

import SERVER.ClientHandler;
import SERVER.ServerMain;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.lang.Math.min;

public class ServerHandler extends Thread {

    Socket socket;

    private int studentId;

    ServerHandler(Socket socket) {
        this.socket = socket;

    }

    @Override
    public void run() {

//        ObjectInputStream ois = null;
//        ObjectOutputStream oos = null;

        DataOutputStream dos = null;
        DataInputStream dis = null;


        try {
//            oos = new ObjectOutputStream(socket.getOutputStream());
//            ois = new ObjectInputStream(socket.getInputStream());

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());


        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] byteInServer = new byte[1000];

        try {
            while(true){
                int bytesRead2 = 0;
                try {
                    bytesRead2 = dis.read(byteInServer, 0, byteInServer.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(bytesRead2 <=0 ) continue;

                String fromServer = new String(byteInServer, 0, bytesRead2, Charset.forName("UTF-8"));

//                String fromServer = (String) ois.readObject();

                String[] fromServerSplit = fromServer.split("#");

                if(fromServerSplit[0].equalsIgnoreCase("login")){
                    if(fromServerSplit[1].equalsIgnoreCase("success")){
                        System.out.println("Login successful");
                        ClientMain.isLoggedIn = true;

                    }
                    else{
                        System.out.println("Login failed, already logged in using " + fromServerSplit[2]);
                        ClientMain.studentId = -1;
                        ClientMain.isLoggedIn = false;
                        if(socket != null){
                            socket.close();
                        }
                        break;
                    }

                    System.out.println("Connection established");
                    System.out.println("Remote port: " + socket.getPort());
                    System.out.println("Local port: " + socket.getLocalPort());

                }
                else if(fromServerSplit[0].equalsIgnoreCase("showActiveList")){
                    System.out.println("Active Students: ");

//                    bytesRead2 = dis.read(byteInServer, 0, byteInServer.length);
//                    if(bytesRead2 <=0 ) break;
//                    fromServer2 = new String(byteInServer, 0, bytesRead2, Charset.forName("UTF-8"));
//
//                    String[] allInfo = fromServer2.split("#");

                    for(int i = 1; i < fromServerSplit.length; i++){
                        System.out.println(fromServerSplit[i]);
                    }


                }
                else if(fromServerSplit[0].equalsIgnoreCase("showAllList")){
                    System.out.println("All Students: ");

//                    bytesRead2 = dis.read(byteInServer, 0, byteInServer.length);
//                    if(bytesRead2 <=0 ) break;
//                    fromServer2 = new String(byteInServer, 0, bytesRead2, Charset.forName("UTF-8"));
//
//                    String[] allInfo = fromServer2.split("#");

                    for(int i = 1; i < fromServerSplit.length; i++){
                        System.out.println(fromServerSplit[i]);
                    }

                }
                else if(fromServerSplit[0].equalsIgnoreCase("requestFile")){

                    if(fromServerSplit[1].equalsIgnoreCase("Successful")){
                        System.out.println("File request sent successfully, reqeust id: " + fromServerSplit[2]);
                    }
                    else{
                        System.out.println("File request failed");
                    }

                }
                else if(fromServerSplit[0].equalsIgnoreCase("showMyFiles")){

                    //cmd = fileId + "#" + fileName + "#" + fileType;


                    System.out.println("My Files: ");
                    System.out.println("File Name ( File type ) - File ID");


//                    bytesRead2 = dis.read(byteInServer, 0, byteInServer.length);
//                    if(bytesRead2 <=0 ) break;
//                    fromServer2 = new String(byteInServer, 0, bytesRead2, Charset.forName("UTF-8"));
//
//                    String[] allInfo = fromServer2.split("#");


                    for(int i = 1; i < fromServerSplit.length; i++){

                        String[] record = fromServerSplit[i].split("\\*\\*");

                        System.out.println(record[1] + " (" + record[2] + " ) - " + record[0]);
                    }

                }
                else if(fromServerSplit[0].equalsIgnoreCase("showAllFiles")){

                    //cmd = fileId + "#" + fileName + "#" + uploaderId;


                    System.out.println("All Files: ");
                    System.out.println("FileName ( File ID ) - Uploader");

                    for(int i = 1; i < fromServerSplit.length; i++){

                        String[] record = fromServerSplit[i].split("\\*\\*");

                        System.out.println(record[1] + " (" + record[0] + " ) - " + record[2]);
                    }

                }
                else if(fromServerSplit[0].equalsIgnoreCase("viewUnreadMessage")){

                    //cmd = reqId + "#" + fileName + "#" + description + "#" + requesterId;

                    System.out.println("Unread messages: ");

                    String requesterId;
                    for(int i = 1; i < fromServerSplit.length; i++){

                        if(fromServerSplit[i].equalsIgnoreCase("No")){
                            System.out.println("No new messages");
                            break;
                        }

                        String[] record = fromServerSplit[i].split("\\*\\*");
                        requesterId = record[3];

                        if(Integer.parseInt(requesterId) == ClientMain.studentId){
                            System.out.println("Update : " + record[4]);
                        }

                        System.out.println("Request ID: " + record[0]);
                        System.out.println("Requester ID: " + record[3]);
                        System.out.println("File Name: " + record[1]);
                        System.out.println("Description: " + record[2] + "\n");

                    }

                }
                else if(fromServerSplit[0].equalsIgnoreCase("viewAllMessage")){

                    //cmd = reqId + "#" + fileName + "#" + description + "#" + requesterId;

                    System.out.println("All messages: ");

                    for(int i = 1; i < fromServerSplit.length; i++){

                        if(fromServerSplit[i].equalsIgnoreCase("No")){
                            System.out.println("No message");
                            break;
                        }

                        String[] record = fromServerSplit[i].split("\\*\\*");
                        System.out.println("Request ID: " + record[0]);
                        System.out.println("Requester ID: " + record[3]);
                        System.out.println("File Name: " + record[1]);
                        System.out.println("Description: " + record[2] + "\n");

                    }

                }
                else if(fromServerSplit[0].equalsIgnoreCase("logout")){

                    if(fromServerSplit[1].equalsIgnoreCase("successful")){
                        System.out.println("Logged out successfully");
                        ClientMain.isLoggedIn = false;
                        if(socket != null){
                            socket.close();
                        }

                        break;
                    }
                }
            }

            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
