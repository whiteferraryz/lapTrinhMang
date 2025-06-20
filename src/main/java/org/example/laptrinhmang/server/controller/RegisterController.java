package org.example.laptrinhmang.server.controller;

import org.example.laptrinhmang.server.model.User;
import org.example.laptrinhmang.server.model.UserDAO;

public class RegisterController {
    private final UserDAO userDAO;

    public RegisterController() {
        userDAO = new UserDAO();
    }

    public String register(User user) {
        boolean success = userDAO.registerUser(user);
        return success ? "REGISTER_SUCCESS" : "REGISTER_FAILED";
    }
}