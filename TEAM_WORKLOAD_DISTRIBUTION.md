# Team Workload Distribution - Multi-Client Chat Application

## 📋 Project Overview
A multi-client TCP chat server system demonstrating core network programming concepts including socket communication, serialization, I/O streams, multi-threading, and message broadcasting.

**Project Duration**: 4 weeks  
**Total Team Members**: 5  
**Programming Language**: Java  
**Total Lines of Code**: ~495 lines (balanced distribution)

---

## 👥 Team Member Assignments

### **Member 1: TCP Socket Connection & Lifecycle Management** 🔌
**Primary Network Concept**: TCP Socket Programming & Connection State Management

#### Assigned Files:
- `ChatClient.java` (Partial - Connection methods)
  - Lines: 1-61, 77-92, 179-215

#### Specific Responsibilities:

**1. Constructor & Initialization**
```java
public class ChatClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5555;
    private Socket socket;
    private volatile boolean connected;
    
    public ChatClient(String username) {
        this.username = username;
        this.connected = false;
    }
}
```

**2. Connection Establishment**
```java
public boolean connect() {
    try {
        // Create TCP socket connection to server
        socket = new Socket(SERVER_HOST, SERVER_PORT);
        
        // Call Member 3's output stream initialization
        // Call Member 5's input stream initialization
        
        connected = true;
        
        // Send JOIN message
        Message joinMessage = new Message(Message.MessageType.JOIN, username, "");
        sendMessage(joinMessage); // Member 3's method
        
        // Start receive thread
        startReceiveThread(); // Member 5's method
        
        return true;
        
    } catch (IOException e) {
        if (listener != null) {
            listener.onConnectionError("Failed to connect to server: " + e.getMessage());
        }
        return false;
    }
}
```

**3. Disconnection Logic**
```java
public void disconnect() {
    if (connected) {
        connected = false;
        
        // Send LEAVE message
        try {
            Message leaveMessage = new Message(Message.MessageType.LEAVE, username, "");
            if (out != null) {
                out.writeObject(leaveMessage);
                out.flush();
            }
        } catch (IOException e) {
            // Ignore errors during disconnect
        }
        
        // Close resources
        cleanup();
    }
}
```

**4. Resource Cleanup**
```java
private void cleanup() {
    try {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    } catch (IOException e) {
        System.err.println("Error closing connection: " + e.getMessage());
    }
}
```

**5. Utility Methods**
```java
public boolean isConnected() {
    return connected;
}

public String getUsername() {
    return username;
}

public void setMessageListener(MessageListener listener) {
    this.listener = listener;
}
```

#### Deliverables:
- ✅ Complete socket connection/disconnection logic
- 📝 **Documentation**: `MEMBER1_SOCKET_CONNECTION.md`
  - TCP three-way handshake explanation
  - Connection state diagram (disconnected → connecting → connected → disconnected)
  - Socket lifecycle flowchart
  - Error handling strategies
  - TCP vs UDP comparison
- 🧪 **Test Cases**:
  - Test 1: Successful connection to server
  - Test 2: Connection to non-existent server (timeout)
  - Test 3: Disconnect while connected
  - Test 4: Multiple connect/disconnect cycles
  - Test 5: Resource cleanup verification

#### Estimated Workload:
- **Lines of Code**: ~90 lines
- **Complexity**: Medium
- **Time Required**: 15 hours
- **Dependencies**: None (can start immediately)

#### Learning Outcomes:
✅ TCP socket creation and binding  
✅ Connection lifecycle management  
✅ State management (connected flag)  
✅ Network error handling patterns  
✅ Resource cleanup and memory management  

---

### **Member 2: Message Protocol & Object Serialization** 📦
**Primary Network Concept**: Application-Layer Protocol Design & Java Serialization

#### Assigned Files:
- `Message.java` (Complete file - ~92 lines)

#### Specific Responsibilities:

**1. Message Class Structure**
```java
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum MessageType {
        JOIN,       // Client joining notification
        LEAVE,      // Client leaving notification
        MESSAGE,    // Regular chat message
        ERROR       // Error notification
    }
    
    private MessageType type;
    private String username;
    private String content;
    private LocalDateTime timestamp;
}
```

**2. Constructor & Initialization**
```java
public Message(MessageType type, String username, String content) {
    this.type = type;
    this.username = username;
    this.content = content;
    this.timestamp = LocalDateTime.now();
}
```

**3. Getter Methods**
```java
public MessageType getType() { return type; }
public String getUsername() { return username; }
public String getContent() { return content; }
public LocalDateTime getTimestamp() { return timestamp; }

public String getFormattedTimestamp() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    return timestamp.format(formatter);
}
```

**4. Formatted Output**
```java
public String getFormattedMessage() {
    String timeStr = getFormattedTimestamp();
    
    switch (type) {
        case JOIN:
            return String.format("[%s] *** %s joined the chat ***", timeStr, username);
        case LEAVE:
            return String.format("[%s] *** %s left the chat ***", timeStr, username);
        case MESSAGE:
            return String.format("[%s] %s: %s", timeStr, username, content);
        case ERROR:
            return String.format("[%s] ERROR: %s", timeStr, content);
        default:
            return String.format("[%s] %s", timeStr, content);
    }
}
```

**5. Override toString()**
```java
@Override
public String toString() {
    return String.format("Message{type=%s, username='%s', content='%s', timestamp=%s}", 
                       type, username, content, timestamp);
}
```

#### Deliverables:
- ✅ Complete `Message.java` implementation
- 📝 **Documentation**: `MEMBER2_MESSAGE_PROTOCOL.md`
  - Message format specification
  - Protocol flow diagrams (JOIN → MESSAGE → LEAVE)
  - Serialization mechanism explanation
  - serialVersionUID importance
  - Message type usage scenarios
  - Protocol versioning strategies
- 🧪 **Test Cases**:
  - Test 1: Create and serialize each message type
  - Test 2: Deserialize messages correctly
  - Test 3: Timestamp formatting accuracy
  - Test 4: getFormattedMessage() for all types
  - Test 5: Serialization compatibility testing

#### Estimated Workload:
- **Lines of Code**: ~92 lines
- **Complexity**: Low-Medium
- **Time Required**: 12 hours
- **Dependencies**: None (can start immediately - PRIORITY #1)

#### Learning Outcomes:
✅ Java Serializable interface  
✅ Application-layer protocol design  
✅ Object serialization/deserialization  
✅ Enum types for protocol states  
✅ Data marshalling concepts  
✅ Protocol versioning (serialVersionUID)  

---

### **Member 3: ObjectOutputStream & Message Sending** 📤
**Primary Network Concept**: Output Streams & Synchronous Write Operations

#### Assigned Files:
- `ChatClient.java` (Partial - Send methods)
  - Lines: 67-70 (stream initialization), 144-177

#### Specific Responsibilities:

**1. Output Stream Initialization**
```java
// In connect() method (called by Member 1)
// IMPORTANT: ObjectOutputStream MUST be created BEFORE ObjectInputStream

out = new ObjectOutputStream(socket.getOutputStream());
out.flush(); // Must flush to send header
```

**2. Public Send Method**
```java
/**
 * Sends a chat message to the server
 * @param content The message content to send
 */
public void sendChatMessage(String content) {
    if (connected && content != null && !content.trim().isEmpty()) {
        Message message = new Message(Message.MessageType.MESSAGE, username, content);
        sendMessage(message);
    }
}
```

**3. Core Send Logic (Synchronized)**
```java
/**
 * Sends a message object to the server
 * Synchronized to prevent concurrent write access
 * @param message The message to send
 */
private synchronized void sendMessage(Message message) {
    try {
        if (out != null && connected) {
            out.writeObject(message);
            out.flush(); // Ensure immediate transmission
        }
    } catch (IOException e) {
        if (listener != null) {
            listener.onConnectionError("Failed to send message: " + e.getMessage());
        }
        disconnect(); // Member 1's method
    }
}
```

**4. Integration Points**
```java
// Used by Member 1 in connect():
Message joinMessage = new Message(Message.MessageType.JOIN, username, "");
sendMessage(joinMessage);

// Used by Member 1 in disconnect():
Message leaveMessage = new Message(Message.MessageType.LEAVE, username, "");
sendMessage(leaveMessage);

// Used by GUI:
chatClient.sendChatMessage("Hello everyone!");
```

#### Deliverables:
- ✅ Output stream initialization code
- ✅ Synchronized message sending methods
- 📝 **Documentation**: `MEMBER3_OUTPUT_STREAM.md`
  - ObjectOutputStream API explanation
  - Stream initialization order (why output before input)
  - flush() importance and behavior
  - Synchronization necessity (prevent concurrent writes)
  - writeObject() mechanism
  - Buffer management concepts
- 🧪 **Test Cases**:
  - Test 1: Send single message successfully
  - Test 2: Send multiple messages rapidly
  - Test 3: Concurrent send from multiple threads
  - Test 4: Send during disconnection (error handling)
  - Test 5: Verify flush() behavior with Wireshark/tcpdump

#### Estimated Workload:
- **Lines of Code**: ~45 lines
- **Complexity**: Medium
- **Time Required**: 12 hours
- **Dependencies**: Member 1 (socket), Member 2 (Message class)

#### Learning Outcomes:
✅ ObjectOutputStream usage  
✅ Stream buffering and flushing  
✅ Synchronized methods for thread safety  
✅ Write operation blocking behavior  
✅ Stream initialization order importance  

---

### **Member 4: Server Architecture & Multi-Threading** 🖥️
**Primary Network Concept**: ServerSocket, Thread-Per-Client Model & Broadcasting

#### Assigned Files:
- `ChatServer.java` (Complete file - ~169 lines)
- `ClientHandler.java` (Complete file - ~155 lines)

#### Specific Responsibilities:

**1. Server Initialization (ChatServer.java)**
```java
public class ChatServer {
    private static final int PORT = 5555;
    private static final int MAX_CLIENTS = 50;
    
    private ServerSocket serverSocket;
    private ConcurrentHashMap<String, ClientHandler> clients;
    private ExecutorService threadPool;
    private volatile boolean running;
    
    public ChatServer() {
        clients = new ConcurrentHashMap<>();
        threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        running = false;
    }
}
```

**2. Server Accept Loop**
```java
public void start() {
    try {
        serverSocket = new ServerSocket(PORT);
        running = true;
        
        System.out.println("Chat Server Started on port: " + PORT);
        
        // Main server loop - accepts client connections
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection from: " + 
                                 clientSocket.getInetAddress().getHostAddress());
                
                // Create handler and execute in thread pool
                ClientHandler handler = new ClientHandler(clientSocket, this);
                threadPool.execute(handler);
                
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error accepting client: " + e.getMessage());
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Error starting server: " + e.getMessage());
    }
}
```

**3. Client Management (Thread-Safe)**
```java
public void addClient(String username, ClientHandler handler) {
    if (clients.containsKey(username)) {
        // Username already taken
        Message errorMsg = new Message(Message.MessageType.ERROR, 
                                      "Server", 
                                      "Username '" + username + "' is already taken");
        handler.sendMessage(errorMsg);
        handler.stop();
        return;
    }
    
    clients.put(username, handler);
    System.out.println("Active clients: " + clients.size());
}

public void removeClient(String username) {
    if (username != null) {
        clients.remove(username);
        System.out.println("Active clients: " + clients.size());
    }
}
```

**4. Broadcasting Logic**
```java
public void broadcastMessage(Message message) {
    // ConcurrentHashMap allows safe iteration
    for (ClientHandler handler : clients.values()) {
        handler.sendMessage(message);
    }
}
```

**5. ClientHandler (Separate Thread per Client)**
```java
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ChatServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;
    private volatile boolean running;
    
    @Override
    public void run() {
        try {
            // Initialize I/O streams
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
            
            // Message receive loop
            while (running) {
                Message message = (Message) in.readObject();
                if (message != null) {
                    handleMessage(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // Handle disconnect
        } finally {
            cleanup();
        }
    }
    
    private void handleMessage(Message message) {
        switch (message.getType()) {
            case JOIN:
                username = message.getUsername();
                server.addClient(username, this);
                server.broadcastMessage(message);
                break;
            case MESSAGE:
                server.broadcastMessage(message);
                break;
            case LEAVE:
                running = false;
                break;
        }
    }
    
    public synchronized void sendMessage(Message message) {
        try {
            if (out != null) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            cleanup();
        }
    }
}
```

**6. Graceful Shutdown**
```java
public void stop() {
    running = false;
    
    // Disconnect all clients
    for (ClientHandler handler : clients.values()) {
        handler.stop();
    }
    clients.clear();
    
    // Shutdown thread pool
    threadPool.shutdown();
    
    // Close server socket
    try {
        if (serverSocket != null) {
            serverSocket.close();
        }
    } catch (IOException e) {
        System.err.println("Error closing server: " + e.getMessage());
    }
}
```

#### Deliverables:
- ✅ Complete `ChatServer.java` implementation
- ✅ Complete `ClientHandler.java` implementation
- 📝 **Documentation**: `MEMBER4_SERVER_MULTITHREADING.md`
  - ServerSocket API and accept() loop
  - Thread-per-client architecture diagram
  - ExecutorService thread pool explanation
  - ConcurrentHashMap for thread safety
  - Broadcasting algorithm and complexity
  - Synchronized methods necessity
  - Server lifecycle diagram
- 🧪 **Test Cases**:
  - Test 1: Server accepts single client
  - Test 2: Server handles 10 concurrent clients
  - Test 3: Username uniqueness validation
  - Test 4: Message broadcasting to all clients
  - Test 5: Client disconnect handling
  - Test 6: Server graceful shutdown
  - Test 7: Load testing (50+ clients)

#### Estimated Workload:
- **Lines of Code**: ~324 lines (169 + 155)
- **Complexity**: High
- **Time Required**: 20 hours
- **Dependencies**: Member 2 (Message class)

#### Learning Outcomes:
✅ ServerSocket and accept() blocking call  
✅ Thread-per-client concurrency model  
✅ ExecutorService and thread pools  
✅ ConcurrentHashMap for thread-safe collections  
✅ Synchronized methods for shared resources  
✅ Broadcasting algorithms  
✅ Graceful shutdown procedures  

---

### **Member 5: ObjectInputStream & Message Receiving** 📥
**Primary Network Concept**: Input Streams & Asynchronous Read Operations

#### Assigned Files:
- `ChatClient.java` (Partial - Receive methods)
  - Lines: 28-32 (interface), 70 (stream init), 94-143

#### Specific Responsibilities:

**1. MessageListener Interface**
```java
/**
 * Interface for receiving message notifications
 * Allows GUI to be updated when messages arrive
 */
public interface MessageListener {
    void onMessageReceived(Message message);
    void onConnectionError(String error);
    void onDisconnected();
}
```

**2. Input Stream Initialization**
```java
// In connect() method (called by Member 1, after Member 3's output stream)
// IMPORTANT: ObjectInputStream MUST be created AFTER ObjectOutputStream

in = new ObjectInputStream(socket.getInputStream());
```

**3. Asynchronous Receive Thread**
```java
/**
 * Starts a background thread to receive messages from the server
 */
private void startReceiveThread() {
    receiveThread = new Thread(() -> {
        try {
            while (connected) {
                try {
                    // Read incoming message (BLOCKING CALL)
                    Message message = (Message) in.readObject();
                    
                    if (message != null && listener != null) {
                        // Notify listener (GUI) of new message
                        listener.onMessageReceived(message);
                        
                        // Check for error messages (e.g., duplicate username)
                        if (message.getType() == Message.MessageType.ERROR) {
                            disconnect();
                            break;
                        }
                    }
                    
                } catch (ClassNotFoundException e) {
                    if (listener != null) {
                        listener.onConnectionError("Unknown message type received");
                    }
                    break;
                } catch (EOFException e) {
                    // Server closed connection
                    break;
                }
            }
        } catch (IOException e) {
            if (connected && listener != null) {
                listener.onConnectionError("Connection error: " + e.getMessage());
            }
        } finally {
            if (listener != null) {
                listener.onDisconnected();
            }
        }
    });
    
    receiveThread.setDaemon(true); // Dies when main thread exits
    receiveThread.start();
}
```

**4. Integration with GUI**
```java
// GUI implements MessageListener interface
public class ChatClientGUI implements ChatClient.MessageListener {
    
    @Override
    public void onMessageReceived(Message message) {
        // Update GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            switch (message.getType()) {
                case JOIN:
                case LEAVE:
                    notificationArea.append(message.getFormattedMessage() + "\n");
                    break;
                case MESSAGE:
                    chatArea.append(message.getFormattedMessage() + "\n");
                    break;
                case ERROR:
                    showError(message.getContent());
                    break;
            }
        });
    }
    
    @Override
    public void onConnectionError(String error) {
        SwingUtilities.invokeLater(() -> {
            showError(error);
        });
    }
    
    @Override
    public void onDisconnected() {
        SwingUtilities.invokeLater(() -> {
            updateConnectionStatus(false);
        });
    }
}
```

#### Deliverables:
- ✅ Input stream initialization code
- ✅ Asynchronous receive thread implementation
- ✅ MessageListener interface definition
- 📝 **Documentation**: `MEMBER5_INPUT_STREAM.md`
  - ObjectInputStream API explanation
  - Blocking I/O and readObject() behavior
  - Asynchronous vs synchronous I/O
  - Daemon threads explanation
  - Observer/Listener pattern architecture
  - Callback mechanism and thread safety
  - EOFException and ClassNotFoundException handling
- 🧪 **Test Cases**:
  - Test 1: Receive single message successfully
  - Test 2: Receive 100 messages rapidly (no loss)
  - Test 3: Receive while sending (concurrent I/O)
  - Test 4: Handle server disconnect (EOFException)
  - Test 5: Handle corrupted message (ClassNotFoundException)
  - Test 6: Verify listener callbacks invoked correctly

#### Estimated Workload:
- **Lines of Code**: ~80 lines
- **Complexity**: Medium-High
- **Time Required**: 15 hours
- **Dependencies**: Member 1 (socket, connected), Member 2 (Message class)

#### Learning Outcomes:
✅ ObjectInputStream usage  
✅ Blocking I/O operations (readObject())  
✅ Asynchronous communication patterns  
✅ Multi-threading for non-blocking I/O  
✅ Observer/Listener design pattern  
✅ Daemon threads concept  
✅ Exception handling (EOF, ClassNotFound)  
✅ Callback mechanisms  

---

## 📅 Project Timeline (4 Weeks)

### **Week 1: Foundation & Core Components**
**Goal**: Complete independent components

| Member | Tasks | Deliverables |
|--------|-------|--------------|
| **Member 1** | Study TCP sockets, implement connect/disconnect | Socket connection methods, initial tests |
| **Member 2** | Complete `Message.java` ✅ **PRIORITY** | Full Message class, serialization tests |
| **Member 3** | Study ObjectOutputStream, wait for Message class | Research and design document |
| **Member 4** | Start `ChatServer.java` skeleton | Basic server structure |
| **Member 5** | Study ObjectInputStream, observer pattern | Research and design document |

**Team Meeting**: Friday - Review Message class (Member 2)

---

### **Week 2: Implementation**
**Goal**: Complete individual components

| Member | Tasks | Deliverables |
|--------|-------|--------------|
| **Member 1** | Complete all connection methods in `ChatClient.java` | Tested connection logic |
| **Member 2** | Write documentation, help others | `MEMBER2_MESSAGE_PROTOCOL.md` |
| **Member 3** | Implement output stream & send methods | Send functionality, tests |
| **Member 4** | Complete `ChatServer.java` + `ClientHandler.java` | Full server implementation |
| **Member 5** | Implement input stream & receive thread | Receive functionality, tests |

**Team Meeting**: Wednesday - Integration planning, Friday - Code review

---

### **Week 3: Integration & Testing**
**Goal**: Merge components and test system

**Monday-Tuesday**: Code Integration
- Members 1, 3, 5 merge `ChatClient.java` (use Git branches)
- Member 4 integrates with Message class
- Resolve merge conflicts

**Wednesday-Friday**: System Testing
- Run server + multiple clients
- Test all scenarios from TESTING.md
- Fix bugs and edge cases
- Performance testing (load, latency)

**Team Meetings**: Daily standups (15 min)

---

### **Week 4: Documentation & Presentation**
**Goal**: Complete documentation and prepare demo

**Monday-Wednesday**: Documentation
- Each member completes their documentation file
- Create team presentation slides
- Record demo video

**Thursday**: Final Testing & Polish
- End-to-end testing
- Code cleanup and comments
- README updates

**Friday**: Presentation & Submission
- Team presentation/demo
- Submit all deliverables

---

## 🔗 Integration Strategy

### **Dependency Graph**
```
Message.java (Member 2) ← MUST BE COMPLETED FIRST
    ↓
    ├── ChatServer.java (Member 4)
    │   └── ClientHandler.java (Member 4)
    │
    └── ChatClient.java
        ├── Connection (Member 1) ← Foundation
        ├── Output Stream (Member 3) ← Depends on Member 1
        └── Input Stream (Member 5) ← Depends on Member 1
```

### **Git Branch Strategy**
```bash
main (protected)
├── member1-connection
├── member2-message (merge first!)
├── member3-output
├── member4-server
└── member5-input

# Merge order:
1. member2-message → main
2. member4-server → main
3. member1-connection → main
4. member3-output → member1-connection → main
5. member5-input → member1-connection → main
```

### **Integration Points**

**Member 1 ↔ Member 3**
```java
// Member 1 calls Member 3's code:
out = new ObjectOutputStream(socket.getOutputStream()); // Member 3
sendMessage(joinMessage); // Member 3
```

**Member 1 ↔ Member 5**
```java
// Member 1 calls Member 5's code:
in = new ObjectInputStream(socket.getInputStream()); // Member 5
startReceiveThread(); // Member 5
```

**Member 3 ↔ Member 2**
```java
// Member 3 uses Member 2's class:
Message message = new Message(MessageType.MESSAGE, username, content);
out.writeObject(message);
```

**Member 5 ↔ Member 2**
```java
// Member 5 uses Member 2's class:
Message message = (Message) in.readObject();
```

**Member 4 ↔ Member 2**
```java
// Member 4 uses Member 2's class:
Message message = (Message) in.readObject();
server.broadcastMessage(message);
```

---

## 📊 Workload Balance

| Member | Component | Lines of Code | Complexity | Hours | Start Date |
|--------|-----------|---------------|------------|-------|------------|
| Member 1 | Socket Connection | ~90 | Medium | 15h | Week 1 |
| Member 2 | Message Protocol | ~92 | Low-Medium | 12h | Week 1 ✅ |
| Member 3 | Output Stream | ~45 | Medium | 12h | Week 2 |
| Member 4 | Server + Handler | ~324 | High | 20h | Week 1-2 |
| Member 5 | Input Stream | ~80 | Medium-High | 15h | Week 2 |
| **TOTAL** | | **~631** | | **74h** | |

**Average per member**: 14.8 hours (balanced workload)

---

## 📝 Documentation Requirements

### Each Member Must Submit:

**1. Technical Documentation (10-15 pages)**
- Network concept explanation with diagrams
- Code implementation walkthrough
- API documentation (JavaDoc style)
- Design decisions and rationale
- Integration points with other members

**2. Testing Report (5-7 pages)**
- Test cases designed (minimum 5)
- Test results with screenshots
- Edge cases identified and tested
- Performance observations
- Bugs found and fixed

**3. Source Code**
- Well-commented code
- JavaDoc comments for all public methods
- Inline comments for complex logic
- Follow Java naming conventions

**4. Personal Reflection (2-3 pages)**
- What you learned
- Challenges faced and solutions
- How your component fits in the system
- Suggestions for improvement

---

## 🧪 Testing Strategy

### **Unit Testing (Individual)**
Each member tests their component independently:

**Member 1**: Socket connection tests
```java
// Test 1: Successful connection
// Test 2: Connection to invalid host
// Test 3: Disconnect while connected
```

**Member 2**: Message serialization tests
```java
// Test 1: Serialize/deserialize each message type
// Test 2: Timestamp formatting
// Test 3: getFormattedMessage() output
```

**Member 3**: Send operation tests
```java
// Test 1: Send single message
// Test 2: Concurrent send from multiple threads
// Test 3: Send during disconnect
```

**Member 4**: Server tests
```java
// Test 1: Accept multiple clients
// Test 2: Broadcasting to all clients
// Test 3: Graceful shutdown
```

**Member 5**: Receive operation tests
```java
// Test 1: Receive messages
// Test 2: Listener callbacks
// Test 3: Handle server disconnect
```

---

### **Integration Testing (Team)**
Test complete system functionality:

**Test 1: Basic Chat Flow**
1. Start server (Member 4)
2. Connect 2 clients (Members 1, 3, 5)
3. Send messages (Member 3)
4. Verify reception (Member 5)
5. Verify message format (Member 2)

**Test 2: Multiple Clients**
1. Start server
2. Connect 5 clients with different usernames
3. Each client sends 10 messages
4. Verify all receive all messages

**Test 3: Error Handling**
1. Test duplicate username
2. Test server disconnect
3. Test invalid message format

**Test 4: Performance**
1. Connect 20 clients
2. Send 100 messages/second
3. Measure latency and throughput

---

## ✅ Completion Checklist

### **Week 1 Checklist**
- [ ] Member 2: `Message.java` complete and tested
- [ ] Member 4: `ChatServer.java` skeleton created
- [ ] Member 1: Connection methods implemented
- [ ] All: Research and design documents submitted
- [ ] Team: Integration plan finalized

### **Week 2 Checklist**
- [ ] Member 1: All connection methods complete
- [ ] Member 3: Send functionality complete
- [ ] Member 4: Server + ClientHandler complete
- [ ] Member 5: Receive functionality complete
- [ ] All: Unit tests passed

### **Week 3 Checklist**
- [ ] `ChatClient.java` merged successfully
- [ ] Server integrates with Message class
- [ ] Full system compiles without errors
- [ ] Basic chat functionality working
- [ ] All integration tests passed
- [ ] Bug fixes completed

### **Week 4 Checklist**
- [ ] All documentation complete
- [ ] Presentation slides ready
- [ ] Demo video recorded
- [ ] Code commented and cleaned
- [ ] README updated
- [ ] Final submission prepared

---

## 🎯 Success Criteria

The project is successful when:

✅ **Functionality**
- [ ] Server accepts 10+ concurrent clients
- [ ] Messages broadcast to all clients correctly
- [ ] Join/leave notifications work
- [ ] Duplicate usernames rejected
- [ ] Graceful disconnect handling

✅ **Code Quality**
- [ ] All code compiles without warnings
- [ ] Proper error handling throughout
- [ ] Thread-safe operations verified
- [ ] Resource cleanup confirmed
- [ ] Code follows Java conventions

✅ **Documentation**
- [ ] Each member submitted complete docs
- [ ] Network concepts clearly explained
- [ ] Code well-commented
- [ ] Integration points documented
- [ ] Test results recorded

✅ **Demonstration**
- [ ] Live demo with 5+ clients
- [ ] All features demonstrated
- [ ] No crashes or errors
- [ ] Performance acceptable
- [ ] Team can explain architecture

---

## 📞 Communication Plan

### **Team Meetings**
- **Weekly Team Meeting**: Every Friday 2:00 PM (1 hour)
- **Daily Standups**: Week 3 only, 9:00 AM (15 minutes)
- **Integration Sessions**: Week 3, as needed

### **Communication Channels**
- **Team Chat**: [Discord/Slack/WhatsApp]
- **Code Repository**: [GitHub/GitLab URL]
- **Document Sharing**: [Google Drive/Dropbox]
- **Issue Tracking**: [GitHub Issues/Jira]

### **Response Time Expectations**
- Urgent issues: Within 2 hours
- Code review requests: Within 24 hours
- General questions: Within 24 hours
- Weekend support: Best effort

---

## 🆘 Troubleshooting Guide

### **Common Issues**

**Issue: "Message class not found"**
- **Cause**: Member 2's code not merged
- **Solution**: Merge member2-message branch first

**Issue: "Socket already in use"**
- **Cause**: Previous server instance still running
- **Solution**: Kill process on port 5555
```bash
# Windows
netstat -ano | findstr :5555
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :5555
kill -9 <PID>
```

**Issue: "Stream initialization error"**
- **Cause**: Wrong order (input before output)
- **Solution**: Always create ObjectOutputStream first

**Issue: "Merge conflicts in ChatClient.java"**
- **Cause**: Multiple members editing same file
- **Solution**: Follow merge order (Member 1 → 3 → 5)

**Issue: "GUI not updating"**
- **Cause**: Not using SwingUtilities.invokeLater()
- **Solution**: Wrap GUI updates in invokeLater()

---

## 📚 Learning Resources

### **For All Members**
- Java Socket Programming Tutorial: [Oracle Docs](https://docs.oracle.com/javase/tutorial/networking/)
- TCP/IP Illustrated (Book)
- Computer Networking: A Top-Down Approach (Book)

### **Member 1: Sockets**
- Java Socket API Documentation
- TCP connection lifecycle
- Network programming best practices

### **Member 2: Serialization**
- Java Serialization Specification
- Protocol design patterns
- Data marshalling techniques

### **Member 3: Output Streams**
- ObjectOutputStream JavaDoc
- Stream buffering concepts
- Thread synchronization

### **Member 4: Multi-threading**
- Java Concurrency in Practice (Book)
- ExecutorService guide
- Thread-safe collections

### **Member 5: Input Streams**
- ObjectInputStream JavaDoc
- Asynchronous I/O patterns
- Observer pattern tutorial

---

## 🏆 Grading Breakdown (Suggested)

| Component | Weight | Criteria |
|-----------|--------|----------|
| **Code Implementation** | 40% | Functionality, quality, error handling |
| **Documentation** | 25% | Completeness, clarity, technical accuracy |
| **Testing** | 15% | Test coverage, edge cases, results |
| **Integration** | 10% | Successful merge, teamwork |
| **Presentation** | 10% | Demo quality, explanation, Q&A |

---

## 📧 Contact Information

| Member | Component | Email | Phone |
|--------|-----------|-------|-------|
| Member 1 | Socket Connection | member1@email.com | (XXX) XXX-XXXX |
| Member 2 | Message Protocol | member2@email.com | (XXX) XXX-XXXX |
| Member 3 | Output Stream | member3@email.com | (XXX) XXX-XXXX |
| Member 4 | Server | member4@email.com | (XXX) XXX-XXXX |
| Member 5 | Input Stream | member5@email.com | (XXX) XXX-XXXX |

**Team Lead**: [Name]  
**Project Coordinator**: [Name]  
**Submission Deadline**: [Date]

---

## 🎓 Final Notes

This workload distribution ensures:
- ✅ **Equal workload** (~12-20 hours per member)
- ✅ **Clear boundaries** (minimal code overlap)
- ✅ **Real networking concepts** (each member learns distinct topics)
- ✅ **Independent work** (can work in parallel)
- ✅ **Clear integration points** (well-defined interfaces)
- ✅ **Comprehensive learning** (covers full network programming stack)

**Remember**: Communication is key! Don't hesitate to ask teammates for help or clarification. Good luck! 🚀

---

**Document Version**: 1.0  
**Last Updated**: November 6, 2025  
**Project Type**: Academic Team Project (Network Programming)
