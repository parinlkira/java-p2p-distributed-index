package com.p2p;

/**
 * Demo application demonstrating P2P system with distributed index
 */
public class P2PDemo {
    
    public static void main(String[] args) {
        printHeader("P2P DISTRIBUTED INDEX SYSTEM DEMO");
        
        // Start registry server
        RegistryServer registry = new RegistryServer(9000);
        Thread registryThread = new Thread(registry);
        registryThread.start();
        
        sleep(1000);
        
        try {
            // Create and start multiple peers
            printSection("STEP 1: Creating and Starting Peers");
            
            Peer peer1 = new Peer("Peer-1", "localhost", 9001);
            Peer peer2 = new Peer("Peer-2", "localhost", 9002);
            Peer peer3 = new Peer("Peer-3", "localhost", 9003);
            
            peer1.start();
            peer2.start();
            peer3.start();
            
            sleep(1000);
            
            // Register peers with registry
            printSection("STEP 2: Registering Peers with Registry Server");
            
            peer1.registerWithRegistry("localhost", 9000);
            peer2.registerWithRegistry("localhost", 9000);
            peer3.registerWithRegistry("localhost", 9000);
            
            sleep(1000);
            registry.displayPeers();
            
            // Populate local indexes
            printSection("STEP 3: Populating Local Indexes");
            
            System.out.println("\n📝 Peer-1 adding local data:");
            peer1.putLocal("name", "Alice");
            peer1.putLocal("city", "New York");
            peer1.putLocal("role", "Developer");
            
            System.out.println("\n📝 Peer-2 adding local data:");
            peer2.putLocal("name", "Bob");
            peer2.putLocal("city", "San Francisco");
            peer2.putLocal("language", "Java");
            
            System.out.println("\n📝 Peer-3 adding local data:");
            peer3.putLocal("name", "Charlie");
            peer3.putLocal("city", "Seattle");
            peer3.putLocal("project", "P2P System");
            
            sleep(500);
            
            // Display all local indexes
            printSection("STEP 4: Displaying All Local Indexes");
            
            peer1.displayLocalIndex();
            peer2.displayLocalIndex();
            peer3.displayLocalIndex();
            
            // Demonstrate peer-to-peer communication
            printSection("STEP 5: Peer-to-Peer Data Exchange");
            
            System.out.println("\n🔄 Peer-1 sending data to Peer-2:");
            peer1.putRemote("localhost", 9002, "message", "Hello from Peer-1");
            
            System.out.println("\n🔄 Peer-2 sending data to Peer-3:");
            peer2.putRemote("localhost", 9003, "status", "Active");
            
            System.out.println("\n🔄 Peer-3 sending data to Peer-1:");
            peer3.putRemote("localhost", 9001, "timestamp", "2025-11-03");
            
            sleep(500);
            
            // Display updated indexes
            printSection("STEP 6: Indexes After Data Exchange");
            
            peer1.displayLocalIndex();
            peer2.displayLocalIndex();
            peer3.displayLocalIndex();
            
            // Demonstrate remote queries
            printSection("STEP 7: Remote Data Queries");
            
            System.out.println("\n🔍 Peer-1 querying 'language' from Peer-2:");
            String language = peer1.getRemote("localhost", 9002, "language");
            System.out.println("   Result: " + (language != null ? language : "NOT FOUND"));
            
            System.out.println("\n🔍 Peer-2 querying 'project' from Peer-3:");
            String project = peer2.getRemote("localhost", 9003, "project");
            System.out.println("   Result: " + (project != null ? project : "NOT FOUND"));
            
            System.out.println("\n🔍 Peer-3 querying 'role' from Peer-1:");
            String role = peer3.getRemote("localhost", 9001, "role");
            System.out.println("   Result: " + (role != null ? role : "NOT FOUND"));
            
            sleep(500);
            
            // Demonstrate concurrent operations
            printSection("STEP 8: Concurrent Operations Test");
            
            System.out.println("\n⚡ Multiple peers accessing Peer-1 simultaneously:");
            
            Thread t1 = new Thread(() -> {
                peer2.putRemote("localhost", 9001, "concurrent-1", "From Peer-2");
            });
            
            Thread t2 = new Thread(() -> {
                peer3.putRemote("localhost", 9001, "concurrent-2", "From Peer-3");
            });
            
            t1.start();
            t2.start();
            
            t1.join();
            t2.join();
            
            sleep(500);
            
            System.out.println("\n📊 Peer-1 index after concurrent writes:");
            peer1.displayLocalIndex();
            
            // Summary
            printSection("STEP 9: System Summary");
            
            System.out.println("\n📈 Index Sizes:");
            System.out.println("   Peer-1: " + peer1.getIndex().size() + " entries");
            System.out.println("   Peer-2: " + peer2.getIndex().size() + " entries");
            System.out.println("   Peer-3: " + peer3.getIndex().size() + " entries");
            
            System.out.println("\n✅ Demonstration completed successfully!");
            
            registry.displayPeers();
            
            // Cleanup
            printSection("STEP 10: Shutting Down System");
            
            System.out.println("\n🛑 Stopping all peers...");
            peer1.stop();
            peer2.stop();
            peer3.stop();
            
            System.out.println("🛑 Stopping registry server...");
            registry.shutdown();
            
            sleep(1000);
            
            printHeader("DEMO FINISHED");
            
        } catch (Exception e) {
            System.err.println("\n❌ Error during demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private static void printHeader(String title) {
        System.out.println("\n");
        System.out.println("╔" + "═".repeat(60) + "╗");
        System.out.println("║" + centerText(title, 60) + "║");
        System.out.println("╚" + "═".repeat(60) + "╝");
        System.out.println();
    }
    
    private static void printSection(String title) {
        System.out.println("\n");
        System.out.println("┌" + "─".repeat(60) + "┐");
        System.out.println("│ " + title + " ".repeat(60 - title.length() - 1) + "│");
        System.out.println("└" + "─".repeat(60) + "┘");
    }
    
    private static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }
}
