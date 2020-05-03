package com.feup.sdis.messages.requests;

import java.io.Serializable;

import com.feup.sdis.chord.SocketAddress;
import com.feup.sdis.messages.responses.Response;

public abstract class Request implements Serializable {
    public abstract Response handle();
    public abstract SocketAddress getConnection();
}