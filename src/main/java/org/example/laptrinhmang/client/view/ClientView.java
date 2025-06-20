package org.example.laptrinhmang.client.view;
import org.example.laptrinhmang.server.model.User;

import java.util.Scanner;

public class ClientView {
    public static User getUserInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("Full Name: ");
        String fullname = scanner.nextLine();

        System.out.print("Gender (Male/Female/Other): ");
        String gender = scanner.nextLine();

        System.out.print("Date of Birth (yyyy-mm-dd): ");
        String dob = scanner.nextLine();

        return new User(username, password, fullname, gender, dob);
    }
}