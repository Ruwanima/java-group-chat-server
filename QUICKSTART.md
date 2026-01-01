# Quick Start Guide

## Simple 3-Step Setup

### Step 1: Compile
Double-click `compile.bat` or run in terminal:
```bash
compile.bat
```

### Step 2: Start Server
Double-click `run-server.bat` or run in terminal:
```bash
run-server.bat
```
Keep this window open!

### Step 3: Start Clients
Double-click `run-client.bat` multiple times (3-5 times recommended) or run:
```bash
run-client.bat
```

Each click opens a new chat client window.

## Using the Chat

1. **Enter Username**: Type a unique name (e.g., Alice, Bob, Charlie)
2. **Click Connect**: Or press Enter
3. **Start Chatting**: Type messages and click Send or press Enter
4. **Watch Both Panels**: 
   - Left panel shows chat messages
   - Right panel shows who joins/leaves

## Testing the System

1. Open server (1 terminal)
2. Open 3+ clients (3+ GUI windows)
3. Connect each with different usernames
4. Send messages from different clients
5. Watch real-time message broadcasting
6. Disconnect a client and see the leave notification

## Manual Commands (Alternative)

If batch files don't work:

```bash
# Compile
javac *.java

# Run server
java ChatServer

# Run client (in separate terminal)
java ChatClientGUI
```

## Troubleshooting

**"javac not found"**
- Install JDK and add to PATH

**"Connection refused"**
- Start server first, then clients

**"Username already taken"**
- Use a different username for each client

**Port already in use**
- Close previous server instance

## Features to Try

✓ Multiple clients chatting simultaneously  
✓ Real-time message broadcasting  
✓ Join/leave notifications  
✓ Timestamp on all messages  
✓ Error handling (duplicate usernames)  
✓ Clean disconnect  

Enjoy chatting! 🎉
