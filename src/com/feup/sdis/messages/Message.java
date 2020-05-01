package com.feup.sdis.messages;

import java.io.Serializable;

public abstract class Message implements Serializable {
    public abstract void handle();
}