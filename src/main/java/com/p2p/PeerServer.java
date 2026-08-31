package com.p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Concurrent server that handles multiple client connections
 */
public class PeerServer implements Runnable {
    private final String peerId;
    private final int port;
    private final DistributedIndex index;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private volatile boolean running = false;

    public PeerServer(String peerId, int port, DistributedIndex index) {
        this.peerId = peerId;
        this.port = port;
        this.index = index;
        this.threadPool = Executors.newFixedThreadPool(10); // Pool of 10 threads
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("🚀 [" + peerId + "] Server started on port " + port);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.execute(new ClientHandler(clientSocket));
                } catch (SocketException e) {
                    if (!running) {
                        break; // Normal shutdown
                    }
                    throw e;
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("❌ [" + peerId + "] Server error: " + e.getMessage());
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
            System.out.println("🛑 [" + peerId + "] Server stopped");
        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Error shutting down server: " + e.getMessage());
            threadPool.shutdownNow();
        }
    }

    /**
     * Handles individual client connections
     */
    private class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String clientAddress = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
                System.out.println("🔗 [" + peerId + "] Client connected: " + clientAddress);

                String request;
                while ((request = in.readLine()) != null) {
                    if (request.equalsIgnoreCase("EXIT")) {
                        break;
                    }
                    
                    String response = processRequest(request);
                    out.println(response);
                }

                System.out.println("👋 [" + peerId + "] Client disconnected: " + clientAddress);

            } catch (IOException e) {
                System.err.println("❌ [" + peerId + "] Error handling client: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        /**
         * Process client requests
         * Protocol: COMMAND|KEY|VALUE
         */
        private String processRequest(String request) {
            try {
                String[] parts = request.split("\\|", -1);
                String command = parts[0].toUpperCase();

                switch (command) {
                    case "PUT":
                        if (parts.length >= 3) {
                            index.put(parts[1], parts[2]);
                            return "OK|PUT|" + parts[1];
                        }
                        return "ERROR|Invalid PUT format";

                    case "GET":
                        if (parts.length >= 2) {
                            String value = index.get(parts[1]);
                            if (value != null) {
                                return "OK|GET|" + parts[1] + "|" + value;
                            }
                            return "NOT_FOUND|" + parts[1];
                        }
                        return "ERROR|Invalid GET format";

                    case "REMOVE":
                        if (parts.length >= 2) {
                            String removed = index.remove(parts[1]);
                            if (removed != null) {
                                return "OK|REMOVE|" + parts[1];
                            }
                            return "NOT_FOUND|" + parts[1];
                        }
                        return "ERROR|Invalid REMOVE format";

                    case "LIST":
                        StringBuilder sb = new StringBuilder("OK|LIST|");
                        index.getAllKeys().forEach(key -> sb.append(key).append(","));
                        return sb.toString();

                    case "SIZE":
                        return "OK|SIZE|" + index.size();

                    default:
                        return "ERROR|Unknown command: " + command;
                }
            } catch (Exception e) {
                return "ERROR|" + e.getMessage();
            }
        }
    }
}
