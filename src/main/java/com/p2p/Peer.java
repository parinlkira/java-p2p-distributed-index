package com.p2p;

/**
 * Main Peer class that combines server and client functionality
 */
public class Peer {
    private final String peerId;
    private final String host;
    private final int port;
    private final DistributedIndex index;
    private final PeerServer server;
    private final PeerClient client;
    private Thread serverThread;

    public Peer(String peerId, String host, int port) {
        this.peerId = peerId;
        this.host = host;
        this.port = port;
        this.index = new DistributedIndex(peerId);
        this.server = new PeerServer(peerId, port, index);
        this.client = new PeerClient(peerId);
    }

    /**
     * Start the peer (both server and client)
     */
    public void start() {
        serverThread = new Thread(server);
        serverThread.start();
        
        // Wait a bit for server to start
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Stop the peer
     */
    public void stop() {
        server.shutdown();
        if (serverThread != null) {
            try {
                serverThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Register with a registry server
     */
    public boolean registerWithRegistry(String registryHost, int registryPort) {
        return client.registerWithRegistry(registryHost, registryPort, peerId, host, port);
    }

    /**
     * Add data to local index
     */
    public void putLocal(String key, String value) {
        index.put(key, value);
    }

    /**
     * Get data from local index
     */
    public String getLocal(String key) {
        return index.get(key);
    }

    /**
     * Send data to remote peer
     */
    public boolean putRemote(String remoteHost, int remotePort, String key, String value) {
        return client.put(remoteHost, remotePort, key, value);
    }

    /**
     * Get data from remote peer
     */
    public String getRemote(String remoteHost, int remotePort, String key) {
        return client.get(remoteHost, remotePort, key);
    }

    /**
     * Display local index
     */
    public void displayLocalIndex() {
        index.displayIndex();
    }

    /**
     * Get peer information
     */
    public PeerInfo getPeerInfo() {
        return new PeerInfo(peerId, host, port);
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

    public PeerClient getClient() {
        return client;
    }

    public DistributedIndex getIndex() {
        return index;
    }
}
