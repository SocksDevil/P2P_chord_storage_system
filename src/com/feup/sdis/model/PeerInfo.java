package com.feup.sdis.model;

import com.feup.sdis.chord.SocketAddress;

import java.io.Serializable;
import java.util.Objects;

public class PeerInfo implements Serializable {
    private final SocketAddress address;
    private final int chunkSize;

    public PeerInfo(SocketAddress address, int chunkSize) {
        this.address = address;
        this.chunkSize = chunkSize;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeerInfo peerInfo = (PeerInfo) o;
        return chunkSize == peerInfo.chunkSize &&
                address.equals(peerInfo.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, chunkSize);
    }
}
