package com.smart.rinoiot.common.network;

import java.io.IOException;

public class ApiException extends IOException {
    public ApiException(String msg) {
        super(msg);
    }
}
