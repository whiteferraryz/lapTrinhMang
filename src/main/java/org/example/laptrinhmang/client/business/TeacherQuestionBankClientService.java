// src/main/java/org/example/laptrinhmang/client/service/TeacherQuestionBankClientService.java
package org.example.laptrinhmang.client.business;

import org.example.laptrinhmang.client.network.ServerCommunicationConnector;
import org.example.laptrinhmang.common.dto.QuestionDTO;
import org.example.laptrinhmang.common.dto.QuestionListResponseDTO;
import org.example.laptrinhmang.common.dto.RequestMessage;
import org.example.laptrinhmang.common.dto.ResponseMessage;
import org.example.laptrinhmang.common.dto.SubjectDTO;
import org.example.laptrinhmang.common.dto.SubjectListResponseDTO;
import org.example.laptrinhmang.common.util.Constants;

import java.util.Collections;
import java.util.List;

public class TeacherQuestionBankClientService {
    private ServerCommunicationConnector connector;

    public TeacherQuestionBankClientService(ServerCommunicationConnector connector) {
        this.connector = connector;
    }

    public List<QuestionDTO> getAllQuestions() {
        RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_GET_ALL_QUESTIONS, null);
        ResponseMessage response = connector.sendRequest(request);
        if (Constants.RESPONSE_STATUS_SUCCESS.equals(response.getStatus()) && response instanceof QuestionListResponseDTO) {
            return ((QuestionListResponseDTO) response).getQuestions();
        }
        return Collections.emptyList();
    }

    public List<SubjectDTO> getAllSubjects() {
        RequestMessage request = new RequestMessage(Constants.REQUEST_TYPE_GET_ALL_SUBJECTS, null);
        ResponseMessage response = connector.sendRequest(request);
        if (Constants.RESPONSE_STATUS_SUCCESS.equals(response.getStatus()) && response instanceof SubjectListResponseDTO) {
            return ((SubjectListResponseDTO) response).getSubjects();
        }
        return Collections.emptyList();
    }
}