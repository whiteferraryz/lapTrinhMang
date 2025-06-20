package org.example.laptrinhmang.server;
import org.example.laptrinhmang.server.controller.RegisterController;
import org.example.laptrinhmang.server.model.User;
import org.example.laptrinhmang.server.view.ServerView;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            ServerView.log("Server is running on port 1234...");

            while (true) {
                Socket socket = serverSocket.accept();
                ServerView.log("Client connected: " + socket.getInetAddress());

                new Thread(() -> {
                    try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                         ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {

                        User user = (User) ois.readObject();
                        RegisterController controller = new RegisterController();
                        String result = controller.register(user);

                        oos.writeObject(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
