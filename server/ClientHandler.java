package sample.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private ServerChat serverChat;

    public ClientHandler(Socket socket, ServerChat serverChat) {

        try {
            this.socket = socket;
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            this.serverChat = serverChat;

            new Thread(() -> {
                try {
                while (true) {
                    String str = in.readUTF();
                    if(str.equals("/end")){
                        break;
                    }
                    System.out.println("Client sent " + str);
                    serverChat.broadcastMsg(str);
                }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                    System.out.println("Клиент отключился");
                        disconnect();
                    }
            }).start();
        } catch (IOException e) {
            disconnect();
            e.printStackTrace();

        }

    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMessage() {
        try {
            return in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void disconnect(){
        serverChat.removeClient(this);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
