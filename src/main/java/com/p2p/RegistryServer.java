package com.p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Registry server for peer discovery and management
 */
public class RegistryServer implements Runnable {
    private final int port;
    private final ConcurrentHashMap<String, PeerInfo> registeredPeers;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private volatile boolean running = false;

    public RegistryServer(int port) {
        this.port = port;
        this.registeredPeers = new ConcurrentHashMap<>();
        this.threadPool = Executors.newFixedThreadPool(10);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("🌐 Registry Server started on port " + port);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.execute(new RegistryHandler(clientSocket));
                } catch (SocketException e) {
                    if (!running) {
                        break;
                    }
                    throw e;
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("❌ Registry Server error: " + e.getMessage());
            }
        }
    }

    public void shutdown() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            threadPool.shutdown();
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
            System.out.println("🛑 Registry Server stopped");
        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Error shutting down registry: " + e.getMessage());
            threadPool.shutdownNow();
        }
    }

    /**
     * Handles registry requests
     */
    private class RegistryHandler implements Runnable {
        private final Socket socket;

        public RegistryHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String request = in.readLine();
                if (request != null) {
                    String response = processRequest(request);
                    out.println(response);
                }

            } catch (IOException e) {
                System.err.println("❌ Registry error handling request: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        /**
         * Process registry requests
         * REGISTER|peerId|host|port - Register a peer
         * LIST_PEERS - Get list of all registered peers
         * UNREGISTER|peerId - Unregister a peer
         */
        private String processRequest(String request) {
            try {
                String[] parts = request.split("\\|");
                String command = parts[0].toUpperCase();

                switch (command) {
                    case "REGISTER":
                        if (parts.length >= 4) {
                            String peerId = parts[1];
                            String host = parts[2];
                            int port = Integer.parseInt(parts[3]);
                            
                            PeerInfo peerInfo = new PeerInfo(peerId, host, port);
                            registeredPeers.put(peerId, peerInfo);
                            
                            System.out.println("✓ Registered peer: " + peerInfo);
                            return "OK|REGISTERED|" + peerId;
                        }
                        return "ERROR|Invalid REGISTER format";

                    case "LIST_PEERS":
                        StringBuilder sb = new StringBuilder("OK|PEERS|");
                        registeredPeers.values().forEach(peer -> 
                            sb.append(peer.getPeerId()).append(":")
                              .append(peer.getHost()).append(":")
                              .append(peer.getPort()).append(",")
                        );
                        return sb.toString();

                    case "UNREGISTER":
                        if (parts.length >= 2) {
                            String peerId = parts[1];
                            PeerInfo removed = registeredPeers.remove(peerId);
                            if (removed != null) {
                                System.out.println("✓ Unregistered peer: " + removed);
                                return "OK|UNREGISTERED|" + peerId;
                            }
                            return "NOT_FOUND|" + peerId;
                        }
                        return "ERROR|Invalid UNREGISTER format";

                    case "COUNT":
                        return "OK|COUNT|" + registeredPeers.size();

                    default:
                        return "ERROR|Unknown command: " + command;
                }
            } catch (Exception e) {
                return "ERROR|" + e.getMessage();
            }
        }
    }

    /**
     * Display all registered peers
     */
    public void displayPeers() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🌐 Registered Peers");
        System.out.println("=".repeat(50));
        if (registeredPeers.isEmpty()) {
            System.out.println("  (none)");
        } else {
            registeredPeers.values().forEach(peer -> 
                System.out.println("  " + peer)
            );
        }
        System.out.println("Total peers: " + registeredPeers.size());
        System.out.println("=".repeat(50) + "\n");
    }
}
