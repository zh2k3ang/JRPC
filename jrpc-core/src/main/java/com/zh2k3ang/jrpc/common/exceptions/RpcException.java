package com.zh2k3ang.jrpc.common.exceptions;

import com.zh2k3ang.jrpc.common.enums.RpcErrorMessageEnum;

public class RpcException extends RuntimeException {
    public RpcException(RpcErrorMessageEnum error) {
        super(error.getMessage());
    }
    public RpcException(RpcErrorMessageEnum error, String detail) {
        super(error.getMessage()+":"+detail);
    }
}
