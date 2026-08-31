package com.p2p;

import java.io.Serializable;

/**
 * Represents information about a peer in the network
 */
public class PeerInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String peerId;
    private final String host;
    private final int port;

    public PeerInfo(String peerId, String host, int port) {
        this.peerId = peerId;
        this.host = host;
        this.port = port;
    }

    public String getPeerId() {
        return peerId;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return peerId + "@" + host + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeerInfo peerInfo = (PeerInfo) o;
        return port == peerInfo.port && 
               peerId.equals(peerInfo.peerId) && 
               host.equals(peerInfo.host);
    }

    @Override
    public int hashCode() {
        return peerId.hashCode();
    }
}
