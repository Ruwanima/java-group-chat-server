# System Architecture Diagram

## Overall Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        CHAT SERVER                              │
│  ┌───────────────────────────────────────────────────────────┐ │
│  │  ChatServer.java                                          │ │
│  │  - ServerSocket (Port 5555)                               │ │
│  │  - ConcurrentHashMap<String, ClientHandler>               │ │
│  │  - ExecutorService (Thread Pool)                          │ │
│  │  - broadcastMessage()                                     │ │
│  └────────────────────┬──────────────────────────────────────┘ │
│                       │                                         │
│         ┌─────────────┼─────────────┬──────────────┐           │
│         │             │             │              │           │
│  ┌──────▼──────┐ ┌───▼──────┐ ┌───▼──────┐ ┌────▼─────┐      │
│  │ClientHandler│ │ClientHan-│ │ClientHan-│ │ClientHan-│      │
│  │  (Thread 1) │ │(Thread 2)│ │(Thread 3)│ │(Thread N)│      │
│  │  - Socket   │ │ - Socket │ │ - Socket │ │ - Socket │      │
│  │  - I/O Str. │ │ - I/O St.│ │ - I/O St.│ │ - I/O St.│      │
│  └──────┬──────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘      │
└─────────┼─────────────┼────────────┼────────────┼────────────┘
          │             │            │            │
          │ TCP         │ TCP        │ TCP        │ TCP
          │ Sockets     │ Sockets    │ Sockets    │ Sockets
          │             │            │            │
┌─────────▼─────────────▼────────────▼────────────▼────────────┐
│                      CLIENTS                                  │
├───────────────┬───────────────┬───────────────┬───────────────┤
│  Client 1     │  Client 2     │  Client 3     │  Client N     │
│ ┌───────────┐ │ ┌───────────┐ │ ┌───────────┐ │ ┌───────────┐ │
│ │ChatClient │ │ │ChatClient │ │ │ChatClient │ │ │ChatClient │ │
│ │ Backend   │ │ │ Backend   │ │ │ Backend   │ │ │ Backend   │ │
│ │-Socket    │ │ │-Socket    │ │ │-Socket    │ │ │-Socket    │ │
│ │-I/O Str.  │ │ │-I/O Str.  │ │ │-I/O Str.  │ │ │-I/O Str.  │ │
│ │-Listener  │ │ │-Listener  │ │ │-Listener  │ │ │-Listener  │ │
│ └─────┬─────┘ │ └─────┬─────┘ │ └─────┬─────┘ │ └─────┬─────┘ │
│       │       │       │       │       │       │       │       │
│ ┌─────▼─────┐ │ ┌─────▼─────┐ │ ┌─────▼─────┐ │ ┌─────▼─────┐ │
│ │ChatClient │ │ │ChatClient │ │ │ChatClient │ │ │ChatClient │ │
│ │    GUI    │ │ │    GUI    │ │ │    GUI    │ │ │    GUI    │ │
│ │ (Swing)   │ │ │ (Swing)   │ │ │ (Swing)   │ │ │ (Swing)   │ │
│ └───────────┘ │ └───────────┘ │ └───────────┘ │ └───────────┘ │
└───────────────┴───────────────┴───────────────┴───────────────┘
```

## Message Flow

### 1. Client Joins
```
ChatClientGUI → ChatClient → Server → All ClientHandlers → All Clients
    [JOIN]         [JOIN]     [JOIN]        [JOIN]           [JOIN]
```

### 2. Client Sends Message
```
ChatClientGUI → ChatClient → Server → All ClientHandlers → All Clients
  [Type "Hi!"]   [MESSAGE]  [Broadcast]   [MESSAGE]        [Display]
```

### 3. Client Leaves
```
ChatClientGUI → ChatClient → Server → All ClientHandlers → All Clients
  [Disconnect]    [LEAVE]    [LEAVE]       [LEAVE]         [Notify]
```

## Class Relationships

```
Message (Serializable)
    ├─ MessageType enum (JOIN, LEAVE, MESSAGE, ERROR)
    ├─ username: String
    ├─ content: String
    └─ timestamp: LocalDateTime

ChatServer
    ├─ ServerSocket serverSocket
    ├─ ConcurrentHashMap<String, ClientHandler> clients
    ├─ ExecutorService threadPool
    ├─ + start()
    ├─ + addClient(username, handler)
    ├─ + removeClient(username)
    └─ + broadcastMessage(message)

ClientHandler implements Runnable
    ├─ Socket clientSocket
    ├─ ChatServer server
    ├─ ObjectOutputStream out
    ├─ ObjectInputStream in
    ├─ String username
    ├─ + run()
    ├─ + sendMessage(message)
    └─ + stop()

ChatClient
    ├─ Socket socket
    ├─ ObjectOutputStream out
    ├─ ObjectInputStream in
    ├─ String username
    ├─ MessageListener listener
    ├─ + connect()
    ├─ + sendChatMessage(content)
    └─ + disconnect()
    
    └─ MessageListener interface
        ├─ onMessageReceived(message)
        ├─ onConnectionError(error)
        └─ onDisconnected()

ChatClientGUI extends JFrame implements MessageListener
    ├─ JTextArea chatArea
    ├─ JTextArea notificationArea
    ├─ JTextField messageField
    ├─ JButton sendButton
    ├─ ChatClient client
    ├─ + onMessageReceived(message)
    ├─ + onConnectionError(error)
    └─ + onDisconnected()
```

## Thread Model

```
Main Thread (Server)
    │
    ├─ Accept Connection Loop
    │   └─ Creates new ClientHandler for each connection
    │
    └─ Thread Pool (ExecutorService)
        ├─ ClientHandler Thread 1
        ├─ ClientHandler Thread 2
        ├─ ClientHandler Thread 3
        └─ ClientHandler Thread N

Main Thread (Client)
    │
    ├─ GUI Event Dispatch Thread (EDT)
    │   ├─ Handles all Swing UI events
    │   ├─ Button clicks
    │   └─ Text input
    │
    └─ Background Receive Thread
        └─ Continuously reads messages from server
        └─ Notifies GUI via SwingUtilities.invokeLater()
```

## Concurrency & Thread Safety

### Server Side
```
ConcurrentHashMap
    ├─ Thread-safe iteration
    ├─ Safe concurrent reads/writes
    └─ No external synchronization needed

synchronized methods
    ├─ ClientHandler.sendMessage() (stream access)
    └─ ChatClient.sendMessage() (stream access)

volatile flags
    └─ running flag for thread coordination
```

### Client Side
```
SwingUtilities.invokeLater()
    ├─ Ensures GUI updates on EDT
    ├─ Thread-safe component updates
    └─ Prevents concurrency issues

Daemon thread
    └─ Background receive thread
    └─ Automatically stops when main thread exits
```

## Data Flow

```
1. Serialization (Client → Server)
   ChatClientGUI input → Message object → ObjectOutputStream → 
   Network → ObjectInputStream → Server

2. Broadcasting (Server → All Clients)
   Server receives message → Iterate ConcurrentHashMap → 
   Send to each ClientHandler → ObjectOutputStream → 
   Network → Client ObjectInputStream → GUI display

3. Thread Communication (Client)
   Receive Thread → listener.onMessageReceived() → 
   SwingUtilities.invokeLater() → EDT → GUI update
```

## Port Configuration

```
Server: Port 5555 (configurable in ChatServer.java)
Clients: Connect to localhost:5555

To change port:
1. Modify PORT constant in ChatServer.java
2. Modify SERVER_PORT constant in ChatClient.java
3. Recompile both files
```

## Key Design Patterns Used

1. **Thread-per-Client Pattern**: Each client gets dedicated thread
2. **Observer/Listener Pattern**: GUI observes client events
3. **Producer-Consumer**: Clients produce, server consumes and redistributes
4. **Object Pool**: ExecutorService manages thread pool
5. **Serialization**: Object transmission over network
6. **MVC-like**: Separation of GUI, client logic, and data model

---

This architecture ensures:
- ✓ Scalability (thread pool)
- ✓ Thread safety (concurrent collections, synchronized methods)
- ✓ Responsiveness (separate threads for I/O)
- ✓ Maintainability (modular design)
- ✓ Reliability (error handling, cleanup)
