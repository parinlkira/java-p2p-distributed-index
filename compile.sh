#!/bin/bash

echo "Compiling P2P Distributed Index System..."

# Create output directory
mkdir -p out

# Compile all Java files with proper source path
javac -d out -sourcepath src/main/java src/main/java/com/p2p/*.java

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful!"
    echo ""
    echo "To run the demo:"
    echo "  ./run.sh"
    echo ""
    echo "Or manually:"
    echo "  java -cp out com.p2p.P2PDemo"
else
    echo "✗ Compilation failed!"
    exit 1
fi
