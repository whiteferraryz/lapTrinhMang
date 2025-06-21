// src/main/java/org/example/laptrinhmang/common/protocol/Request.java
package org.example.laptrinhmang.common.protocol;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    private Object payload;

    public Request(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}