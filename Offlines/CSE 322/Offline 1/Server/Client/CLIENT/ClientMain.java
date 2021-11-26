package CLIENT;

import SERVER.ServerMain;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;


public class ClientMain{

    public static int socketPort = 13000, upDownSocketPort = 12000, studentId = -1;
    public static String server = "127.0.0.1";

    public static List<String> tasklist = new ArrayList<>();

    public static Boolean isLoggedIn = false;


    public static void main(String[] args) throws IOException, ClassNotFoundException {


        FileOutputStream fos = null;
        Socket socket = null;

        DataOutputStream dos = null;
        DataInputStream dis = null;

        Scanner scanner = new Scanner(System.in);

        String input= "", fileName;
        byte[] byteInServer = new byte[1000];

        System.out.println("1. Login");
        System.out.println("2. Show Active Users");
        System.out.println("3. Show All Users");
        System.out.println("4. Request File");
        System.out.println("5. Serve Request");
        System.out.println("6. Show My Files");
        System.out.println("7. Show All Public Files");
        System.out.println("8. View Unread Messages");
        System.out.println("9. View All Requests");
        System.out.println("10. Download File");
        System.out.println("11. Upload File");
        System.out.println("12. Logout");
        System.out.println("13. Exit");

        int choice;

        while(true) {

            input = scanner.nextLine();
            if(input.equalsIgnoreCase("")) continue;

            char[] charData = input.toCharArray();

            boolean isInvalidInput = false;
            for(int i = 0; i < charData.length; i++){
                if(charData[i] > '9' || charData[i] < '0'){
                    System.out.println("Enter valid input");
                    isInvalidInput = true;
                }
            }
            if(isInvalidInput) continue;

            choice = Integer.parseInt(input);

            if(choice == 1){

                if(isLoggedIn){
                    System.out.println("Please Logout First");
                    continue;
                }

                socket = new Socket("localhost", socketPort);

                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());

//                oos  = new ObjectOutputStream(socket.getOutputStream());
                //ois = new ObjectInputStream(socket.getInputStream());

                (new ServerHandler(socket)).start();

                // "login#studentId"
                System.out.printf("Enter Student Id : ");
                input = scanner.nextLine();
                String[] inputs = input.split(" ");
                ClientMain.studentId = Integer.parseInt(input);

                String cmd = "login" + "#" + input;
                dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                dos.flush();
//                oos.writeObject(cmd);
                System.out.println("sent " + cmd);



            }
            else if(choice == 2){
                if(!isLoggedIn){
                    System.out.println("Please Login First");
                    continue;
                }
                String cmd = "showActiveList";
                dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                dos.flush();

//                oos.writeObject(cmd);


            }
            else if(choice == 3){
                if(!isLoggedIn){
                    System.out.println("Please Login First");
                    continue;
                }
                String cmd = "showAllList";

                dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                dos.flush();

//                oos.writeObject(cmd);
            }
            else if(choice == 4){
                if(!isLoggedIn){
                    System.out.println("Please Login First");
                    continue;
                }

                System.out.println("Enter FileName: ");
                fileName = scanner.nextLine();
                System.out.println("Enter Description: ");
                String description = scanner.nextLine();

                String cmd = "requestFile#" + fileName + "#" + description;
                dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                dos.flush();

//                oos.writeObject(cmd);

            }
            else if(choice == 5){
                if(!isLoggedIn){
                    System.out.println("Please Login First");
                    continue;
                }

                System.out.println("Enter Request Id: ");

                String requestId = scanner.nextLine();

//                String cmd = "serveRequest#" + requestId;
//                dos.write(cmd.getBytes(Charset.forName("UTF-8")));

                System.out.println("Enter File Name: ");
                fileName = scanner.nextLine();

                File file = new File("./ToUpload/" + fileName);

                // "serveRequest#requestId#fileSize#fileName#

                String requestCmd = "serveRequest#" + requestId + "#" + file.length() + "#" + fileName + "#" + ClientMain.studentId;
                Socket uploadSocket = new Socket("localhost", upDownSocketPort);

                FileHandler.fileName = fileName;
                FileHandler.requestCmd = requestCmd;

                (new FileHandler(uploadSocket)).start();


            }
            else if(choice == 6){
                if(!isLoggedIn){
                    System.out.println("Please Login First");
                    continue;
                }

                String cmd = "showMyFiles#";
                dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                dos.flush();

//                oos.writeObject(cmd);

            }

            else if(choice == 7){
                if(!isLoggedIn){
                    System.out.println("Please Login First");
                    continue;
                }
                String cmd = "showAllFiles#";
                dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                dos.flush();

//                oos.writeObject(cmd);

            }
            else if(choice == 8){
                if(!isLoggedIn){
                    System.out.println("Please Login First");
                    continue;
                }
                String cmd = "viewUnreadMessage";
                dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                dos.flush();

//                oos.writeObject(cmd);

            }
            else if(choice == 9){
                if(!isLoggedIn){
                    System.out.println("Please Login First");
                    continue;
                }
                String cmd = "viewAllMessage";
                dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                dos.flush();

//                oos.writeObject(cmd);

            }
            else if(choice == 10){

                if(!isLoggedIn){
                    System.out.println("Please Login First");
                    continue;
                }

                System.out.println("Enter Source Student No.: ");
                int uploaderId = Integer.parseInt(scanner.nextLine());
                System.out.println("Enter fileName: ");
                fileName = scanner.nextLine();

                Socket downloadSocket = new Socket("localhost", upDownSocketPort);

                FileHandler.fileName = fileName;
                FileHandler.uploaderId = uploaderId;

                FileHandler.requestCmd = "download#" + fileName + "#" + uploaderId + "#" + ClientMain.studentId;

                (new FileHandler(downloadSocket)).start();


            }
            else if(choice == 11){

                if(!isLoggedIn){
                    System.out.println("Please Login First");
                    continue;
                }

                System.out.println("Enter File Name: ");
                fileName = scanner.nextLine();

                System.out.println("Enter File Type: ");
                System.out.println("1. Public");
                System.out.println("2. Private");

                int choice2 = Integer.parseInt(scanner.nextLine());
                String fileType;

                if(choice2 == 1){
                    fileType = "public";
                }
                else{
                    fileType = "private";
                }

                File file = new File("./ToUpload/" + fileName);

                String requestCmd = "upload#" + fileName + "#" + fileType + "#" + (int)file.length() + "#" + ClientMain.studentId;
                Socket uploadSocket = new Socket("localhost", upDownSocketPort);

                FileHandler.fileName = fileName;
                FileHandler.requestCmd = requestCmd;
                FileHandler.fileSize = (int) file.length();

                (new FileHandler(uploadSocket)).start();

            }
            else if(choice == 12){
                if(!isLoggedIn){
                    System.out.println("Please Login First");
                    continue;
                }

                String cmd = "logout";

                dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                dos.flush();

//                oos.writeObject(cmd);


            }
            else if(choice == 13){

                if(socket != null){
                    socket.close();
                }
                System.out.println("Exiting");
                break;
            }
            else{
                System.out.println("Invalid Command");
            }
        }
    }
}



//        String sentence;
//        String fromServer;
//
//        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
//
//        Socket clientSocket = new Socket("hostname", 6789);
//
//        //DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
//
//        PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream());
//        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//
//        System.out.println("1. Login");
//        System.out.println("2. Exit");
//        System.out.println("Enter you choice: ");
//
//
//        sentence = userInput.readLine();
//
//        int choice = Integer.parseInt(sentence);
//
//        if(choice == 1){
//            fromServer = inFromServer.readLine();
//            System.out.println(fromServer);
//
//            while(true){
//
//                sentence = userInput.readLine();
//                outToServer.println(sentence);
//
//                fromServer = inFromServer.readLine();
//                System.out.println(fromServer);
//
//            }
//        }
//
//
//        //outToServer.writeBytes(sentence + '\n');
//
//        System.out.println("Connection closed");
//        clientSocket.close();
//










//public class ClientMain extends Application {
//
//    @Override
//    public void start(Stage primaryStage) throws Exception{
//
//
//
//
//        try{
//            Parent root = FXMLLoader.load(getClass().getResource("/CLIENT/Login.fxml"));
//            Scene scene = new Scene(root, 400, 400);
//            //scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//
//            primaryStage.setTitle("Moodle");
//            primaryStage.setScene(scene);
//            primaryStage.show();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
