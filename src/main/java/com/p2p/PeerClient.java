package com.p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client component for connecting to other peers
 */
public class PeerClient {
    private final String peerId;

    public PeerClient(String peerId) {
        this.peerId = peerId;
    }

    /**
     * Send a request to another peer and get response
     */
    public String sendRequest(String host, int port, String request) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(request);
            String response = in.readLine();
            
            System.out.println("📤 [" + peerId + "] Sent to " + host + ":" + port + " → " + request);
            System.out.println("📥 [" + peerId + "] Received response → " + response);
            
            return response;

        } catch (IOException e) {
            String error = "ERROR|Connection failed: " + e.getMessage();
            System.err.println("❌ [" + peerId + "] " + error);
            return error;
        }
    }

    /**
     * Put a key-value pair to a remote peer
     */
    public boolean put(String host, int port, String key, String value) {
        String request = "PUT|" + key + "|" + value;
        String response = sendRequest(host, port, request);
        return response != null && response.startsWith("OK");
    }

    /**
     * Get a value from a remote peer
     */
    public String get(String host, int port, String key) {
        String request = "GET|" + key;
        String response = sendRequest(host, port, request);
        
        if (response != null && response.startsWith("OK|GET")) {
            String[] parts = response.split("\\|");
            if (parts.length >= 4) {
                return parts[3];
            }
        }
        return null;
    }

    /**
     * Remove a key from a remote peer
     */
    public boolean remove(String host, int port, String key) {
        String request = "REMOVE|" + key;
        String response = sendRequest(host, port, request);
        return response != null && response.startsWith("OK");
    }

    /**
     * Get the size of a remote peer's index
     */
    public int getSize(String host, int port) {
        String request = "SIZE";
        String response = sendRequest(host, port, request);
        
        if (response != null && response.startsWith("OK|SIZE")) {
            String[] parts = response.split("\\|");
            if (parts.length >= 3) {
                return Integer.parseInt(parts[2]);
            }
        }
        return -1;
    }

    /**
     * Register with the registry server
     */
    public boolean registerWithRegistry(String registryHost, int registryPort, 
                                       String myPeerId, String myHost, int myPort) {
        String request = "REGISTER|" + myPeerId + "|" + myHost + "|" + myPort;
        String response = sendRequest(registryHost, registryPort, request);
        return response != null && response.startsWith("OK");
    }

    /**
     * Get list of peers from registry
     */
    public String getPeerList(String registryHost, int registryPort) {
        String request = "LIST_PEERS";
        return sendRequest(registryHost, registryPort, request);
    }
}
