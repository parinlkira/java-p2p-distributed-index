
package com.p2p;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Distributed Index implementation using thread-safe ConcurrentHashMap.
 * Stores key-value pairs and supports concurrent access.
 */
public class DistributedIndex {
    private final ConcurrentHashMap<String, String> index;
    private final String peerId;

    public DistributedIndex(String peerId) {
        this.index = new ConcurrentHashMap<>();
        this.peerId = peerId;
    }

    /**
     * Insert a key-value pair into the index
     */
    public void put(String key, String value) {
        index.put(key, value);
        System.out.println("✓ [" + peerId + "] Inserted: " + key + " = " + value);
    }

    /**
     * Query a value by key
     */
    public String get(String key) {
        return index.get(key);
    }

    /**
     * Delete a key-value pair
     */
    public String remove(String key) {
        String removed = index.remove(key);
        if (removed != null) {
            System.out.println("✓ [" + peerId + "] Removed: " + key);
        }
        return removed;
    }

    /**
     * Check if key exists
     */
    public boolean containsKey(String key) {
        return index.containsKey(key);
    }

    /**
     * Get all keys
     */
    public Set<String> getAllKeys() {
        return index.keySet();
    }

    /**
     * Get the size of the index
     */
    public int size() {
        return index.size();
    }

    /**
     * Get a copy of all entries
     */
    public Map<String, String> getAllEntries() {
        return new ConcurrentHashMap<>(index);
    }

    /**
     * Display the entire index
     */
    public void displayIndex() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 Index for Peer: " + peerId);
        System.out.println("=".repeat(50));
        if (index.isEmpty()) {
            System.out.println("  (empty)");
        } else {
            index.forEach((key, value) -> 
                System.out.println("  " + key + " → " + value)
            );
        }
        System.out.println("Total entries: " + index.size());
        System.out.println("=".repeat(50) + "\n");
    }
}
