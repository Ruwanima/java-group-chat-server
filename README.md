# Multi-Client Chat Server System

A comprehensive multi-client chat application built in Java, featuring TCP socket programming, multi-threading, concurrency management, and a Java Swing GUI.

## 📋 Features

### Server-Side
- **TCP Socket Server**: Uses `ServerSocket` to listen on port 5555
- **Multi-threaded Architecture**: Each client handled by a separate `ClientHandler` thread
- **Thread-Safe Operations**: Uses `ConcurrentHashMap` for client management
- **Thread Pool**: Managed with `ExecutorService` for efficient resource utilization
- **Message Broadcasting**: Real-time message distribution to all connected clients
- **Protocol Support**: Structured message protocol with JOIN, LEAVE, MESSAGE, and ERROR types
- **Username Validation**: Prevents duplicate usernames
- **Graceful Shutdown**: Proper cleanup of resources and client notifications

### Client-Side
- **TCP Socket Connection**: Connects to server using Java sockets
- **Object Serialization**: Messages transmitted as serializable Java objects
- **Asynchronous Message Receiving**: Background thread for non-blocking message reception
- **Listener Pattern**: Decoupled communication between network layer and GUI
- **Auto-reconnect Capability**: Handles connection errors gracefully

### GUI (Java Swing)
- **User-Friendly Interface**: Clean, intuitive design
- **Username Entry**: Simple login/join functionality
- **Dual Display Areas**: 
  - Chat messages panel (left)
  - Notifications panel (right) for join/leave events
- **Message Input**: Text field with send button and Enter key support
- **Real-Time Updates**: Immediate display of incoming messages
- **Status Indicator**: Visual connection status feedback
- **Auto-Scroll**: Automatically scrolls to latest messages
- **Thread-Safe GUI Updates**: Uses `SwingUtilities.invokeLater()` for EDT compliance

## 🏗️ Architecture

### Class Structure

```
Message.java          - Serializable message object with type, username, content, timestamp
ChatServer.java       - Main server with ServerSocket, client management, broadcasting
ClientHandler.java    - Thread-per-client handler for I/O operations
ChatClient.java       - Client networking logic with MessageListener interface
ChatClientGUI.java    - Swing GUI implementing MessageListener
```

### Message Protocol

The system uses a custom protocol based on `Message` objects with four types:
- **JOIN**: Client joining notification
- **LEAVE**: Client leaving notification
- **MESSAGE**: Regular chat message
- **ERROR**: Error notifications (e.g., duplicate username)

Each message includes:
- Message type
- Username
- Content
- Timestamp (automatically generated)

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Terminal/Command Prompt
- Text editor or IDE (optional)

### Compilation

Navigate to the project directory and compile all Java files:

```bash
# Windows (PowerShell/CMD)
javac *.java

# Linux/Mac
javac *.java
```

This will compile all `.java` files and generate `.class` files.

### Running the Application

#### Step 1: Start the Server

Open a terminal and run:

```bash
java ChatServer
```

You should see:
```
=================================
  Chat Server Started
  Listening on port: 5555
=================================
```

#### Step 2: Start Client(s)

Open **separate terminals** for each client and run:

```bash
java ChatClientGUI
```

This launches the GUI window for each client.

#### Step 3: Connect and Chat

For each client:
1. Enter a unique username in the "Username" field
2. Click "Connect" or press Enter
3. Once connected, type messages in the bottom text field
4. Click "Send" or press Enter to send messages
5. View chat messages in the left panel
6. View join/leave notifications in the right panel

### Testing Multiple Clients

To fully demonstrate the system:
1. Start the server (one terminal)
2. Launch multiple client GUIs (3-5 recommended)
3. Connect each client with different usernames
4. Send messages from different clients
5. Observe real-time broadcasting to all connected clients
6. Disconnect clients and observe leave notifications

## 📸 User Interface

### Client GUI Layout

```
┌─────────────────────────────────────────────────────────┐
│  Connection                                             │
│  Username: [________] [Connect]  ● Connected           │
├──────────────────────┬──────────────────────────────────┤
│  Chat Messages       │  Notifications                   │
│                      │                                  │
│  [10:30:15] Alice:   │  [10:30:10] *** Alice joined    │
│  Hello everyone!     │  the chat ***                   │
│                      │                                  │
│  [10:30:20] Bob:     │  [10:30:18] *** Bob joined      │
│  Hi Alice!           │  the chat ***                   │
│                      │                                  │
│                      │                                  │
├──────────────────────┴──────────────────────────────────┤
│  Send Message                                           │
│  [Type your message here...          ] [Send]          │
└─────────────────────────────────────────────────────────┘
```

## 🔧 Technical Details

### Thread Safety
- `ConcurrentHashMap` for client list management
- `synchronized` methods for stream I/O operations
- `volatile` flags for thread coordination
- `SwingUtilities.invokeLater()` for GUI updates

### Concurrency
- Thread pool (`ExecutorService`) for client handlers
- Daemon thread for message receiving
- Proper thread shutdown and cleanup

### Error Handling
- Connection error notifications
- Duplicate username detection
- Graceful disconnect handling
- Socket timeout and EOF handling

### I/O Streams
- `ObjectOutputStream` and `ObjectInputStream` for serialization
- Proper stream initialization order (output before input)
- Stream flushing after writes
- Resource cleanup in finally blocks

## 📝 Key Implementation Highlights

### Server Broadcasting
```java
public void broadcastMessage(Message message) {
    for (ClientHandler handler : clients.values()) {
        handler.sendMessage(message);
    }
}
```

### Client Message Receiving
```java
private void startReceiveThread() {
    receiveThread = new Thread(() -> {
        while (connected) {
            Message message = (Message) in.readObject();
            listener.onMessageReceived(message);
        }
    });
    receiveThread.setDaemon(true);
    receiveThread.start();
}
```

### GUI Thread-Safe Updates
```java
@Override
public void onMessageReceived(Message message) {
    SwingUtilities.invokeLater(() -> {
        addChatMessage(message.getFormattedMessage());
    });
}
```

## 🛠️ Troubleshooting

### "Address already in use"
- The server port (5555) is already in use
- Kill the existing process or wait a moment before restarting

### "Connection refused"
- Ensure the server is running before starting clients
- Check that the server is listening on port 5555
- Verify localhost connectivity

### GUI not updating
- Check that messages are being received (server console)
- Ensure SwingUtilities.invokeLater() is used for GUI updates

### Username already taken
- Each client must use a unique username
- Change the username and reconnect

## 📚 Learning Objectives Demonstrated

✅ **TCP Socket Programming**: ServerSocket and Socket usage  
✅ **Multi-threading**: Thread-per-client model with ExecutorService  
✅ **Concurrency**: ConcurrentHashMap, synchronized methods, volatile flags  
✅ **I/O Streams**: ObjectInputStream/ObjectOutputStream with serialization  
✅ **Java Swing**: Event-driven GUI programming  
✅ **Design Patterns**: Observer/Listener pattern for GUI updates  
✅ **Thread Safety**: EDT compliance, synchronized I/O operations  
✅ **Error Handling**: Network errors, resource cleanup, graceful shutdown  
✅ **Protocol Design**: Custom message protocol with type system  

## 🎯 Future Enhancements

Possible extensions:
- Private messaging between users
- File transfer capability
- Message history persistence
- User authentication
- Encryption (SSL/TLS)
- Emoji support
- User list display
- Message search functionality
- Configurable server host/port
- Chat rooms/channels

## 👥 Authors

Created as a comprehensive demonstration of Java networking, concurrency, and GUI programming concepts.

## 📄 License

This project is created for educational purposes.

---

**Note**: This is a demonstration project showcasing fundamental Java networking and concurrency concepts. For production use, additional security measures, error handling, and scalability considerations would be necessary.
