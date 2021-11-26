package CLIENT;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

import static java.lang.Math.min;

public class FileHandler extends Thread{
    Socket socket;

    public static int fileSize, chunkSize, fileId, uploaderId;
    public static String fileName, requestCmd;

    FileHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {

        byte[] byteInServer = new byte[1000];

        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dos.write(requestCmd.getBytes(Charset.forName("UTF-8")));
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("requ : " + requestCmd);

        int bytesRead2 = 0;

        while(true){
            System.out.println("before reading in file handler");
            try {
                bytesRead2 = dis.read(byteInServer, 0, byteInServer.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("after reading in file handler");

            String fromServer = new String(byteInServer, 0, bytesRead2, Charset.forName("UTF-8"));
            String[] fromServerSplit = fromServer.split("#");

            System.out.println("server sent: " + fromServer);

            if (fromServerSplit[0].equalsIgnoreCase("waiting")) {
                System.out.println("Server buffer full, waiting in queue");
            }


            else if (fromServerSplit[0].equalsIgnoreCase("DownloadFile")) {

                FileOutputStream fos = null;
                BufferedOutputStream bos = null;

                // fromserver = "#downloadFile#fileSize#chunkSize#fileId";

                fileSize = Integer.parseInt(fromServerSplit[1]);
                chunkSize = Integer.parseInt(fromServerSplit[2]);
                fileId = Integer.parseInt(fromServerSplit[3]);

                try {
                    File file = new File("./Downloaded/MyFiles/" + fileName);
                    //file.createNewFile();

                    fos = new FileOutputStream("./Downloaded/MyFiles/" + fileName);
                    bos = new BufferedOutputStream(fos);

                    byte[] myByteArray = new byte[fileSize];
                    byte[] tempArray = new byte[chunkSize];

                    System.out.println("file Size: " + fileSize + ", chunkSize : " + chunkSize);

                    int current = 0, cnt = 0, bytesRead = 0, receiveSize;
                    for (; (cnt*chunkSize) <= fileSize; cnt++) {

                        receiveSize = min(chunkSize, fileSize - current);
                        bytesRead = dis.read(myByteArray, current, receiveSize);

                        System.arraycopy(myByteArray, current, tempArray, 0, bytesRead);

                        String msg = new String(tempArray);
                        //System.out.println("server sent: " + msg);

                        if (bytesRead > 0) current += bytesRead;
                        //System.out.println("inside for loop, bytesRead: " + bytesRead + ", current : " + current);

                    }

                    bytesRead = dis.read(byteInServer, 0, byteInServer.length);
                    String message = new String(byteInServer, 0, bytesRead, Charset.forName("UTF-8"));

                    if (message.equalsIgnoreCase("downSuccess")) {
                        System.out.println("file downloaded successfully. fileId: " + fileId + ", port : " + socket.getPort());
                        bos.write(myByteArray, 0, current);
                        bos.flush();
                        System.out.println("file " + fileName + " downloaded( " + current + " bytes read)");
                    }
                    else if (message.equalsIgnoreCase("downFailed")) {
                        System.out.println("file downloading FAILED. fileId: " + fileId + ", port : " + socket.getPort());
                        this.socket.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("File received fully");
                break;

            }
            else if (fromServerSplit[0].equalsIgnoreCase("UploadFile")) {

                //fromServer = "uploadFile#chunkSize#fileId";

                chunkSize = Integer.parseInt(fromServerSplit[1]);
                fileId = Integer.parseInt(fromServerSplit[2]);

                System.out.println("You are sending file to server");
                System.out.println("server sent. chunksize:  " + chunkSize + ", fileId: " + fileId);

                try {
                    FileInputStream fis = new FileInputStream("./ToUpload/" + fileName);
                    BufferedInputStream bis = new BufferedInputStream(fis);

                    File file = new File("./ToUpload/" + fileName);

                    byte[] myByteArray = new byte[fileSize];
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    bis.read(myByteArray, 0, myByteArray.length);

                    int current = 0, cnt = 0, sendSize = chunkSize;
                    boolean isSent = true, isTimeOut = false;


                    this.socket.setSoTimeout(10000);
                    for (; (cnt ) * chunkSize <= fileSize; cnt++, current += sendSize) {

                        System.out.println("inside for");

                        sendSize = min(chunkSize, fileSize - current);
                        dos.write(myByteArray, current, sendSize);
                        dos.flush();

                        System.out.println("after sending");

                        //System.out.println("before while");

                        long start = System.currentTimeMillis(), finish, elapsedTime;

                        while(true){

                            System.out.println("inside while");
                            int temp = dis.read(byteInServer, 0, byteInServer.length);
                            String temp2 = new String(byteInServer, 0, temp, Charset.forName("UTF-8"));

                            finish = System.currentTimeMillis();
                            elapsedTime = (int)(finish - start)/1000;
                            if(elapsedTime > 10){
                                System.out.println("Timeout Error");
                                isTimeOut = true;
                                break;
                            }
                            if(temp2.equalsIgnoreCase("ackUpload")) {
                                //System.out.println("acknowledgement : " + String.valueOf(cnt + 1));
                                break;
                            }
                        }

                        System.out.println("while complete");
                    }

                    if(!isTimeOut)
                    {
                        dos.flush();

                        String cmd = "upComplete";
                        dos.write(cmd.getBytes(Charset.forName("UTF-8")));

                        System.out.println("Up completed. waiting for server response");

                        while(true){
                            int bytesRead = dis.read(byteInServer, 0, byteInServer.length);
                            String message = new String(byteInServer, 0, bytesRead, Charset.forName("UTF-8"));

                            System.out.println("server sent: " + message);

                            if (message.equalsIgnoreCase("upSuccess")) {
                                System.out.println("Upload successful");
                                break;
                            } else if (message.equalsIgnoreCase("upFailed")) {
                                System.out.println("Upload Failed");
                                break;
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch(SocketTimeoutException e){
                    System.out.println("Socket timed out");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }
}
