package com.feup.sdis.messages.responses.chord;

import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.Response;

public class NotifyResponse extends Response{

    public NotifyResponse(Status status) {
        super(status);
    }

    @Override
    public String toString(){
        
        return "res: CHD_NOTIFY " + "STATUS: " + this.getStatus();
    }

}
