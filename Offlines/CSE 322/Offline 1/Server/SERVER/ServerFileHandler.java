package SERVER;

import CLIENT.ClientMain;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static java.lang.Math.min;

public class ServerFileHandler extends Thread{

    Socket socket;
    private int studentId;

    ServerFileHandler(Socket socket)
    {
        this.socket = socket;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    @Override
    public void run() {

        int bytesRead = 0;
        byte[] byteServer = new byte[1000];
        String received;

        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            bytesRead = dis.read(byteServer, 0, byteServer.length);
            received = new String(byteServer, 0, bytesRead, Charset.forName("UTF-8"));

            System.out.println("In server file handler");

            String[] receivedSplit = received.split("#");
            System.out.println("received from client : " + received);

            if (receivedSplit[0].equalsIgnoreCase("download")) {

                System.out.println("client handler : download cmd received");

                // "download#fileName#uploaderId#StudentId"

                String fileName = receivedSplit[1];
                int uploaderId = Integer.parseInt(receivedSplit[2]);
                File newFile = new File("./Inventory/" + uploaderId + "/" + fileName);
                int fileSize = (int) newFile.length();
                int tempStd = Integer.parseInt(receivedSplit[3]);
                this.setStudentId(tempStd);


                System.out.println("fileName: " + fileName);

                int chunkSize = ServerMain.MAX_CHUNK_SIZE;
                int fileId = -1;

                for (Integer tempFileId : ServerMain.allFiles.keySet()) {

                    MyFile file = ServerMain.allFiles.get(tempFileId);
                    if (file.getFileName().equalsIgnoreCase(fileName) && file.getUploaderId() == uploaderId) {
                        fileId = tempFileId;
                    }
                }

                //        max buffer er kaj ekhane
                String cmd = "downloadFile#" + fileSize + "#" + chunkSize + "#" + fileId;
                dos.write(cmd.getBytes(Charset.forName("UTF-8")));

                File myFile = new File("./Inventory/" + uploaderId + "/" + fileName);

                byte[] myByteArray = new byte[fileSize];
                FileInputStream fis = new FileInputStream(myFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                bis.read(myByteArray, 0, myByteArray.length);
                //os = socket.getOutputStream();

                //System.out.println("Sending " + fileName + ", size: " + myByteArray.length + " bytes");
                //oos.writeObject("DownloadFile#" + myByteArray.length);
                //oos.flush();

                int current = 0, sendSize = chunkSize;
                boolean isSent = true;
                do {
                    if (ServerMain.activeStudents.get(studentId) == null) {
                        isSent = false;
                        cmd = "downFailed";
                        dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                        break;
                    }

                    sendSize = min(chunkSize, fileSize - current);
                    dos.write(myByteArray, current, sendSize);
                    dos.flush();
                    current += sendSize;
                    System.out.println("sent : " + sendSize + " bytes");

                }
                while (current < fileSize);

                //os.write(myByteArray, 0, myByteArray.length);
                //os.flush();
                if (isSent) {
                    cmd = "downSuccess";
                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                    System.out.println("File: " + fileId + " download completed");

                }

                fis.close();
                bis.close();

            } else if (receivedSplit[0].equalsIgnoreCase("upload") || receivedSplit[0].equalsIgnoreCase("serveRequest")) {

                String fileName, fileType = null;
                int fileSize, requestId = -1, requesterId = -1;

                int chunkSize = (int) (Math.random() * (ServerMain.MAX_CHUNK_SIZE - ServerMain.MIN_CHUNK_SIZE + 1) + ServerMain.MIN_CHUNK_SIZE);
                int fileID = ServerMain.getFileID();

                if (receivedSplit[0].equalsIgnoreCase("upload")) {

                    // "upload#fileName#fileType#fileSize#studentId"

                    fileName = receivedSplit[1];
                    fileType = receivedSplit[2];
                    fileSize = Integer.parseInt(receivedSplit[3]);
                    int tempStd = Integer.parseInt(receivedSplit[4]);
                    this.setStudentId(tempStd);

                } else {
                    // "serveRequest#requestId#fileSize#fileName#studentId

                    requestId = Integer.parseInt(receivedSplit[1]);
                    Request req = ServerMain.allRequests.get(requestId);

                    fileName = req.getFileName();
                    fileType = "public";
                    requesterId = req.getRequesterID();
                    fileSize = Integer.parseInt(receivedSplit[2]);

                    String fileName2 = receivedSplit[3];
                    int tempStd = Integer.parseInt(receivedSplit[4]);
                    this.setStudentId(tempStd);

                    System.out.println("server fileName:" + fileName + ", client fileName: " + fileName2);

                }



                if(ServerMain.MAX_BUFFER_SIZE - ServerMain.OCCUPIED_BUFFER - fileSize < 0){
                    ServerMain.uploadQueue.add(fileID);

                    String cmd = "waiting#" + fileID;
                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                    dos.flush();
                    int head;
                    while(true){
                        Thread.sleep(10);
                        if(ServerMain.MAX_BUFFER_SIZE - ServerMain.OCCUPIED_BUFFER - fileSize >= 0){
                            head = ServerMain.uploadQueue.peek();
                            if(head == fileID){
                                ServerMain.uploadQueue.poll();
                                break;
                            }

                        }
                    }
                }

                ServerMain.OCCUPIED_BUFFER += fileSize;

                String cmd = "uploadFile#" + chunkSize + "#" + fileID;
                dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                dos.flush();


                System.out.println("Upload fileName: " + fileName + ", filesize : " + fileSize);

                File file = new File("./Inventory/" + studentId + "/" + fileName);
                //file.createNewFile();

                FileOutputStream fos = new FileOutputStream("./Inventory/" + studentId + "/" + fileName);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                int current = 0;

                int tempSize = min(chunkSize, fileSize);
                byte[] myByteArray = new byte[fileSize];
                byte[] tempArray = new byte[tempSize];

                //InputStream is = socket.getInputStream();

                //bytesRead = is.read(myByteArray, 0, chunkSize);
                //current = bytesRead;

                boolean isReceived = false, isTimeOut = false;

                int receiveSize = chunkSize, cnt = 1;

                System.out.println("before do while");

                do {
                    receiveSize = min(chunkSize, fileSize - current);
                    bytesRead = dis.read(tempArray, 0, receiveSize);
                    String message = new String(tempArray, StandardCharsets.UTF_8);

                    System.out.println("bytesRead: " + bytesRead);

                    if (message.equalsIgnoreCase("timeout")) {
                        System.out.println("timeout on fileId: " + fileID + ", port : " + socket.getPort());
                        // delete file content
                        isTimeOut = true;
                        break;
                    }
                    if (message.equalsIgnoreCase("upComplete")) {
                        System.out.println("got upload complete from client");
                        break;
                    }
                    //System.out.println("client sent: " + message);

                    System.arraycopy(tempArray, 0, myByteArray, current, bytesRead);
                    cmd = "ackUpload";
                    dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                    dos.flush();

                    //bytesRead = is.read(myByteArray, current, chunkSize);
                    //if(myByteArray[current])

                    if (bytesRead > 0) current += bytesRead;
                    //System.out.println("inside while loop, bytesRead: " + bytesRead + "ack: " + cnt++);
                } while (bytesRead > 0);


                if(!isTimeOut){

                    while (true) {
                        System.out.println("not timeout while loop");
                        int temp = dis.read(byteServer, 0, byteServer.length);
                        String temp2 = new String(byteServer, 0, temp, Charset.forName("UTF-8"));
                        if (temp2.equalsIgnoreCase("upComplete")) {
                            System.out.println("got upload complete from client");
                            break;
                        }
                    }

                    if (current == fileSize) {
                        cmd = "upSuccess";
                        dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                        System.out.println("upload successful");

                        isReceived = true;
                        bos.write(myByteArray, 0, current);
                        bos.flush();

                        MyFile newFile = new MyFile(fileName, fileType, this.studentId);
                        ServerMain.allFiles.put(fileID, newFile);

                    } else {
                        cmd = "upFailed";
                        dos.write(cmd.getBytes(Charset.forName("UTF-8")));
                        System.out.println("upload failed");
                        socket.close();
                    }

                    if (receivedSplit[0].equalsIgnoreCase("serveRequest") && isReceived) {

                        HashMap<Integer, Request> chatBox = ServerMain.allChatBox.get(requesterId);

                        Request req = ServerMain.allRequests.get(requestId);
                        req.setStatusUpdate(studentId + " uploaded your requested file, fileID: " + fileID);
                        chatBox.put(requestId, req);
                    }
                    System.out.println("file " + fileName + " uploaded (" + current + ")bytes");
                }

                ServerMain.OCCUPIED_BUFFER -= fileSize;
                System.out.println("next : " + ServerMain.uploadQueue.peek());


                fos.close();
                bos.close();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

