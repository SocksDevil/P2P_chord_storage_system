package com.feup.sdis.messages.responses.chord;

import com.feup.sdis.messages.Status;
import com.feup.sdis.messages.responses.Response;

public class PingResponse extends Response{

    public PingResponse(Status status) {
        super(status);
    }

    @Override
    public String toString(){
        
        return "res: CHD_PING " + "STATUS: " + this.getStatus();
    }

}
