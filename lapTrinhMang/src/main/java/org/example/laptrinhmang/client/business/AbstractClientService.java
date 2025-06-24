package org.example.laptrinhmang.client.business;

import org.example.laptrinhmang.client.network.ServerCommunicationConnector;

/**
 * Lớp trừu tượng chung cho tất cả các Client Service,
 * định nghĩa các thuộc tính và phương thức cơ bản để tương tác với server.
 */
public abstract class AbstractClientService { // THÊM 'abstract' để chỉ ra đây là lớp trừu tượng

    protected ServerCommunicationConnector connector; // KHAI BÁO BIẾN 'connector'

    /**
     * Hàm tạo của AbstractClientService.
     * Các lớp con sẽ gọi hàm tạo này để thiết lập ServerCommunicationConnector.
     * @param connector Đối tượng ServerCommunicationConnector để giao tiếp với server.
     */
    public AbstractClientService(ServerCommunicationConnector connector) {
        this.connector = connector; // GÁN GIÁ TRỊ CHO 'connector'
    }
}