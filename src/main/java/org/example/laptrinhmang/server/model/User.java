package org.example.laptrinhmang.server.model;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String passwordHash;
    private String fullname;
    private String gender;
    private String dob;

    public User(String username, String passwordHash, String fullname, String gender, String dob) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullname = fullname;
        this.gender = gender;
        this.dob = dob;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullname() { return fullname; }
    public String getGender() { return gender; }
    public String getDob() { return dob; }
}