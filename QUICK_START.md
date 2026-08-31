# Quick Start Guide

## Run the Demo in 3 Commands

```bash
cd /Users/parindalwadi/Desktop/github/p2p-distributed-system
./compile.sh
./run.sh
```

## What You'll See

The demo will automatically:

1. ✅ Start a registry server on port 9000
2. ✅ Create 3 peers on ports 9001, 9002, 9003
3. ✅ Register all peers with the registry
4. ✅ Populate local indexes with sample data
5. ✅ Exchange data between peers
6. ✅ Perform remote queries across the network
7. ✅ Demonstrate concurrent operations
8. ✅ Display system statistics
9. ✅ Gracefully shut down all components

## Expected Runtime

The demo runs for approximately **15-20 seconds** and produces highly formatted, user-friendly output with emojis, boxes, and clear step-by-step information.

## Key Features Demonstrated

- 🚀 **Concurrent Server**: Multiple clients connecting simultaneously
- 📊 **Distributed Index**: Data spread across multiple peers
- 🌐 **Registry Server**: Central discovery service
- 🔄 **P2P Exchange**: Direct peer-to-peer communication
- ⚡ **Thread Safety**: Concurrent operations without conflicts

## Manual Compilation (if needed)

```bash
mkdir -p out
javac -d out src/main/java/com/p2p/*.java
cd out
java com.p2p.P2PDemo
```

## Verify Success

Look for these indicators:
- ✓ Green checkmarks for successful operations
- 📊 Index displays showing distributed data
- 🔗 Connection events for peer communication
- ✅ "Demonstration completed successfully!" message

## Troubleshooting

**Ports already in use?**
- Edit `P2PDemo.java` and change port numbers (9000-9003)

**Compilation errors?**
- Ensure Java 8+ is installed: `java -version`
- Check file permissions: `chmod +x compile.sh run.sh`

**Permission denied?**
- Run: `chmod +x compile.sh run.sh`

Enjoy exploring the P2P Distributed Index System! 🎉
