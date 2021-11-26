package SERVER;

import CLIENT.FileHandler;
import javafx.beans.property.adapter.ReadOnlyJavaBeanProperty;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import static java.lang.Math.min;

public class ClientHandler extends Thread{

    Socket socket;
    FileInputStream fis;
    BufferedInputStream bis;
    OutputStream os;

    private int studentId;
    public HashMap<Integer, Request> chatBox = new HashMap<>();

    ClientHandler(Socket socket){
        this.socket = socket;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void run(){
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

//            ObjectOutputStream oos = new ObjectOutputStream(this.socket.getOutputStream());
//            ObjectInputStream ois = new ObjectInputStream(this.socket.getInputStream());

//            BufferedReader clientInput = new BufferedReader(new InputStreamReader(this.dis));
//            PrintWriter sendToClient = new PrintWriter(this.dos, true);
            //received = dis.readUTF();

            int bytesRead = 0;
            byte[] byteServer = new byte[1000];
            String received;

            while (true) {
                bytesRead = dis.read(byteServer, 0, byteServer.length);
                received = new String(byteServer, 0, bytesRead, Charset.forName("UTF-8"));

//                received = (String)ois.readObject();

                String[] receivedSplit = received.split("#");
                System.out.println("received from client : " + received);

                int stdId;
                if(receivedSplit[0].equalsIgnoreCase("login")){
                    stdId = Integer.parseInt(receivedSplit[1]);

                    boolean isLoggedIn = false;
                    if(ServerMain.activeStudents.get(stdId) != null){
                        isLoggedIn = true;
                        int existingInstance =  Integer.parseInt(ServerMain.activeStudents.get(stdId));
                        String cmd = "login#failed#" + existingInstance;
//                        oos.writeObject(cmd);
                        dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                        dos.flush();
                        break;
                    }
                    else{
                        String cmd = "login#success#";
//                        oos.writeObject(cmd);

                        dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                        dos.flush();

                        this.setStudentId(stdId);

                        ServerMain.activeStudents.put(stdId, String.valueOf(socket.getPort()));
                        if(ServerMain.allStudents.get(stdId) == null){
                            ServerMain.allStudents.put(stdId, String.valueOf(socket.getPort()));
                            new File("./Inventory/" + stdId).mkdirs();
                            ServerMain.allChatBox.put(stdId, new HashMap<>());
                        }
                        this.chatBox = ServerMain.allChatBox.get(stdId);
                    }
                }

                else if (receivedSplit[0].equalsIgnoreCase("showActiveList")) {
                    String cmd = "showActiveList#";
//                    oos.writeObject(cmd);

//                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
//                    dos.flush();


                    for (int activeStudent : ServerMain.activeStudents.keySet()) {
                        cmd += activeStudent + "#";
//                        oos.writeObject(cmd);
                    }
                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                    dos.flush();
//                    cmd = "DoneActiveList";
//                    oos.writeObject(cmd);

//                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
//                    dos.flush();


                } else if (receivedSplit[0].equalsIgnoreCase("showAllList")) {
                    String cmd = "showAllList#";
//                    oos.writeObject(cmd);

//                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
//                    dos.flush();

                    for (int tempStudentId : ServerMain.allStudents.keySet()) {
                        cmd += tempStudentId + "#";
//                        oos.writeObject(cmd);

                    }
                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                    dos.flush();

//                    cmd = "DoneAllList";
//                    oos.writeObject(cmd);

//                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
//                    dos.flush();


                }
                else if (receivedSplit[0].equalsIgnoreCase("requestFile")) {

                    String fileName = receivedSplit[1];
                    String description = receivedSplit[2];
                    Request newreq = new Request(fileName, description, getStudentId());

                    int requestId = ServerMain.getRequestId();
                    ServerMain.allRequests.put(requestId, newreq);

                    for (HashMap<Integer, Request> chatBox : ServerMain.allChatBox.values()) {
                        if(chatBox != this.chatBox){
                            chatBox.put(requestId, newreq);
                        }
                    }

                    String cmd = "requestFile#Successful#" + requestId;
//                    oos.writeObject(cmd);

                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                    dos.flush();
                }

                else if (receivedSplit[0].equalsIgnoreCase("showMyFiles")) {

                    String cmd = "showMyFiles#";
//                    oos.writeObject(cmd);

//                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
//                    dos.flush();

//                    printWriter.println(cmd);

                    for (int fileId : ServerMain.allFiles.keySet()) {
                        MyFile file = ServerMain.allFiles.get(fileId);
                        String fileName = file.getFileName();
                        String fileType = file.getFileType();
                        int uploaderId = file.getUploaderId();

                        if (uploaderId != getStudentId()) continue;

                        cmd += fileId + "**" + fileName + "**" + fileType + "#";
//                        oos.writeObject(cmd);


                    }
                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                    dos.flush();

//                    cmd = "DoneMyFiles";
//                    oos.writeObject(cmd);

//                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
//                    dos.flush();

                }

                else if (receivedSplit[0].equalsIgnoreCase("showAllFiles")) {

                    String cmd = "showAllFiles#";
//                    oos.writeObject(cmd);

//                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
//                    dos.flush();

                    for (int fileId : ServerMain.allFiles.keySet()) {
                        MyFile file = ServerMain.allFiles.get(fileId);
                        String fileName = file.getFileName();
                        String fileType = file.getFileType();
                        int uploaderId = file.getUploaderId();
                        System.out.println("fileName: " + fileName + ", uploader: " + uploaderId);

                        if (fileType.equalsIgnoreCase("private")) continue;
                        cmd += fileId + "**" + fileName + "**" + uploaderId + "#";
                        System.out.println(cmd);
//                        oos.writeObject(cmd);

                    }

                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                    dos.flush();


//                    cmd = "DoneAllFiles";
//                    oos.writeObject(cmd);

//                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
//                    dos.flush();

                }
                else if (receivedSplit[0].equalsIgnoreCase("viewUnreadMessage")) {

                    String cmd = "viewUnreadMessage#";
//                    oos.writeObject(cmd);

//                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
//                    dos.flush();

                    for (int reqId : chatBox.keySet()) {
                        Request req = chatBox.get(reqId);
                        String fileName = req.getFileName();
                        String description = req.getDescription();
                        int requesterId = req.getRequesterID();

                        cmd += reqId + "**" + fileName + "**" + description + "**" + requesterId + "**" + req.getStatusUpdate() +"#";
//                        oos.writeObject(cmd);

                    }

                    if(cmd.equalsIgnoreCase("viewUnreadMessage#")){
                        cmd += "No";
                    }

                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                    dos.flush();

//                    cmd = "DoneUnreadMessage";
//                    oos.writeObject(cmd);
//                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
//                    dos.flush();

                    this.chatBox.clear();

                }
                else if (receivedSplit[0].equalsIgnoreCase("viewAllMessage")) {

                    String cmd = "viewAllMessage#";
//                    oos.writeObject(cmd);

//                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
//                    dos.flush();

                    for (int reqId : ServerMain.allRequests.keySet()) {
                        Request req = ServerMain.allRequests.get(reqId);
                        String fileName = req.getFileName();
                        String description = req.getDescription();
                        int requesterId = req.getRequesterID();

                        cmd += reqId + "**" + fileName + "**" + description + "**" + requesterId + "#";
//                        oos.writeObject(cmd);

                    }
                    if(cmd.equalsIgnoreCase("viewAllMessage#")){
                        cmd += "No";
                    }

                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                    dos.flush();

//                    cmd = "DoneAllMessage";
//                    oos.writeObject(cmd);
//                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
//                    dos.flush();
                }
                else if(receivedSplit[0].equalsIgnoreCase("logout")){
                    String cmd = "logout#successful";
//                    oos.writeObject(cmd);

                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                    dos.flush();

                    Thread.sleep(1000);
                    socket.close();
                    ServerMain.activeStudents.remove(getStudentId());

                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
