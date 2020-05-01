package com.feup.sdis.messages;

import java.io.Serializable;

import com.feup.sdis.chord.Connection;

public abstract class Message implements Serializable {
    public abstract Message handle();
    public abstract Connection getConnection();
}