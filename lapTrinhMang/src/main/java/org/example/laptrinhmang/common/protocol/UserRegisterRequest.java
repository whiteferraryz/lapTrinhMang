// src/main/java/org/example/laptrinhmang/common/protocol/UserRegisterRequest.java
package org.example.laptrinhmang.common.protocol;

import java.io.Serializable;

public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String familyName;
    private String firstName;
    private String email;
    private int roleId;

    public UserRegisterRequest(String username, String password, String familyName, String firstName, String email, int roleId) {
        this.username = username;
        this.password = password;
        this.familyName = familyName;
        this.firstName = firstName;
        this.email = email;
        this.roleId = roleId;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFamilyName() { return familyName; }
    public String getFirstName() { return firstName; }
    public String getEmail() { return email; }
    public int getRoleId() { return roleId; }
}