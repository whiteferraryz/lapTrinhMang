package org.example.laptrinhmang.client;
import org.example.laptrinhmang.server.model.User;
import org.example.laptrinhmang.client.view.ClientView;

import java.io.*;
import java.net.Socket;

public class ClientMain {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 1234);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            User user = ClientView.getUserInput();
            oos.writeObject(user);

            String response = (String) ois.readObject();
            System.out.println("Server Response: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
