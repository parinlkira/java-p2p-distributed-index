# P2P Distributed Index System

A peer-to-peer system with distributed index implementation in Java, featuring concurrent server-client communication and distributed data management.

## Features

✅ **Concurrent Server Processing**
- Each peer runs a server that can handle multiple client connections simultaneously
- Uses thread pool (10 threads) for efficient concurrent request handling

✅ **Distributed Index Management**
- Thread-safe distributed index using ConcurrentHashMap
- Support for PUT, GET, REMOVE operations across the network
- Each peer maintains its own local index

✅ **Peer Discovery**
- Centralized registry server for peer registration and discovery
- Peers can register/unregister dynamically

✅ **P2P Communication**
- Direct peer-to-peer data exchange
- Remote index operations between peers
- Support for concurrent operations from multiple peers

## Architecture

The system consists of the following components:

1. **DistributedIndex**: Thread-safe key-value store for each peer
2. **PeerServer**: Concurrent server handling multiple client connections
3. **PeerClient**: Client for connecting to other peers
4. **RegistryServer**: Central registry for peer discovery
5. **Peer**: Main class combining server and client functionality
6. **P2PDemo**: Demonstration application

## Project Structure

```
p2p-distributed-system/
├── src/main/java/com/p2p/
│   ├── DistributedIndex.java    # Distributed key-value index
│   ├── Peer.java                # Main peer class
│   ├── PeerClient.java          # Client component
│   ├── PeerInfo.java            # Peer metadata
│   ├── PeerServer.java          # Concurrent server component
│   ├── RegistryServer.java      # Registry for peer discovery
│   └── P2PDemo.java             # Demo application
├── compile.sh                    # Compilation script
├── run.sh                        # Run script
└── README.md                     # This file
```

## Communication Protocol

The system uses a simple text-based protocol:

- `PUT|key|value` - Insert/update a key-value pair
- `GET|key` - Retrieve value for a key
- `REMOVE|key` - Delete a key-value pair
- `LIST` - List all keys
- `SIZE` - Get index size
- `REGISTER|peerId|host|port` - Register with registry
- `LIST_PEERS` - Get list of registered peers

## How to Build and Run

### Build
```bash
chmod +x compile.sh run.sh
./compile.sh
```

### Run
```bash
./run.sh
```

Or manually:
```bash
cd out
java com.p2p.P2PDemo
```

## Demo Workflow

The demo application demonstrates:

1. **Registry Server Startup**: Starts a central registry on port 9000
2. **Peer Creation**: Creates 3 peers on ports 9001, 9002, 9003
3. **Peer Registration**: All peers register with the registry
4. **Local Index Population**: Each peer adds data to its local index
5. **P2P Data Exchange**: Peers send data to each other
6. **Remote Queries**: Peers query data from remote peers
7. **Concurrent Operations**: Multiple peers access one peer simultaneously
8. **System Summary**: Display statistics and final state
9. **Graceful Shutdown**: All components shut down cleanly

## Example Output

The demo produces user-friendly, formatted output with:
- 🚀 Server startup notifications
- 🔗 Client connection events
- ✓ Successful operations
- 📊 Index displays with clear formatting
- 📤/📥 Request/response visualization
- ⚡ Concurrent operation indicators
- ❌ Error messages when applicable

## Key Capabilities Demonstrated

### 1. Concurrent Server
- Multiple clients can connect to a peer simultaneously
- Thread pool manages concurrent requests efficiently
- Each client connection is handled in a separate thread

### 2. Distributed Index
- Each peer maintains its own index
- Thread-safe operations using ConcurrentHashMap
- Data can be distributed across multiple peers

### 3. Well-Known Server
- Registry server acts as a well-known endpoint
- Peers register themselves at startup
- Other peers can discover available peers

### 4. Data Exchange
- Peers can send data to other peers
- Remote index operations (PUT, GET, REMOVE)
- Query data across the distributed network

## Requirements

- Java 8 or higher
- Unix-like environment (Linux, macOS) for shell scripts
- Windows users can compile and run manually using the commands shown above

## Technical Details

- **Port Range**: 9000-9003 (registry + 3 peers)
- **Thread Pool Size**: 10 threads per peer server
- **Default Host**: localhost
- **Concurrency**: Full thread-safe implementation
- **Shutdown**: Graceful shutdown with proper resource cleanup

## Author

Created as a demonstration of P2P systems with distributed index capabilities.
