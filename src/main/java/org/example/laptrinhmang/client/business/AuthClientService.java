// src/main/java/org/example/laptrinhmang/client/service/AuthClientService.java
package org.example.laptrinhmang.client.business;

import org.example.laptrinhmang.client.network.ServerCommunicationConnector;
import org.example.laptrinhmang.common.dto.LoginRequestDTO;
import org.example.laptrinhmang.common.dto.RequestMessage;
import org.example.laptrinhmang.common.dto.ResponseMessage;
import org.example.laptrinhmang.common.dto.UserDTO;
import org.example.laptrinhmang.common.util.Constants;

public class AuthClientService {
    private ServerCommunicationConnector connector;

    public AuthClientService(ServerCommunicationConnector connector) {
        this.connector = connector;
    }

    public ResponseMessage login(String username, String password) {
        LoginRequestDTO loginRequest = new LoginRequestDTO(username, password);
        RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_LOGIN, loginRequest);
        return connector.sendRequest(request);
    }

    public ResponseMessage register(UserDTO userDTO) {
        RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_REGISTER, userDTO);
        return connector.sendRequest(request);
    }
}