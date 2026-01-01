# Project File Overview

## 📁 Source Files (.java)

### Core Components

#### **Message.java**
- **Purpose**: Serializable message model class
- **Key Features**:
  - `MessageType` enum (JOIN, LEAVE, MESSAGE, ERROR)
  - Username, content, timestamp fields
  - Formatted output methods
  - Serialization support for network transmission
- **Lines**: ~92
- **Dependencies**: None

#### **ChatServer.java**
- **Purpose**: Main server application
- **Key Features**:
  - ServerSocket on port 5555
  - ConcurrentHashMap for thread-safe client storage
  - ExecutorService thread pool
  - Message broadcasting to all clients
  - Graceful shutdown handling
- **Lines**: ~169
- **Dependencies**: ClientHandler, Message
- **Entry Point**: `main()` method

#### **ClientHandler.java**
- **Purpose**: Handles individual client connections
- **Key Features**:
  - Runs in separate thread (implements Runnable)
  - ObjectInputStream/ObjectOutputStream for I/O
  - Message processing and routing
  - Synchronized send operations
  - Cleanup and disconnect handling
- **Lines**: ~155
- **Dependencies**: ChatServer, Message
- **Thread**: One instance per connected client

#### **ChatClient.java**
- **Purpose**: Client-side networking logic
- **Key Features**:
  - Socket connection management
  - MessageListener interface for GUI notification
  - Background thread for receiving messages
  - Thread-safe message sending
  - Connection error handling
- **Lines**: ~210
- **Dependencies**: Message
- **Pattern**: Observer (listener pattern)

#### **ChatClientGUI.java**
- **Purpose**: Swing-based graphical user interface
- **Key Features**:
  - Username input and connection controls
  - Dual panel display (chat + notifications)
  - Message input with send button
  - Real-time updates using SwingUtilities
  - Status indicator
  - MessageListener implementation
- **Lines**: ~340
- **Dependencies**: ChatClient, Message
- **Entry Point**: `main()` method
- **Thread**: Event Dispatch Thread (EDT)

---

## 📄 Documentation Files (.md)

### **README.md**
- **Purpose**: Main project documentation
- **Contains**:
  - Project overview and features
  - Architecture description
  - Getting started guide
  - Compilation and running instructions
  - Technical details
  - Learning objectives
  - Future enhancements
- **Audience**: Developers and users

### **QUICKSTART.md**
- **Purpose**: Fast setup guide
- **Contains**:
  - 3-step setup process
  - Usage instructions
  - Testing guide
  - Troubleshooting tips
  - Features to try
- **Audience**: New users

### **ARCHITECTURE.md**
- **Purpose**: Technical architecture documentation
- **Contains**:
  - System architecture diagrams
  - Message flow diagrams
  - Class relationships
  - Thread model
  - Concurrency mechanisms
  - Design patterns used
- **Audience**: Developers and architects

### **TESTING.md**
- **Purpose**: Comprehensive testing guide
- **Contains**:
  - 20 detailed test scenarios
  - Expected behaviors
  - Performance metrics
  - Bug report template
  - Test automation script
  - Test checklist
- **Audience**: QA and developers

### **FILESTRUCTURE.md** (this file)
- **Purpose**: Project file reference
- **Contains**:
  - File descriptions
  - Key features per file
  - Dependencies
  - Usage information
- **Audience**: Developers

---

## 🔧 Utility Files (.bat)

### **compile.bat**
- **Purpose**: Compile all Java source files
- **Usage**: Double-click or run in terminal
- **Does**:
  - Checks for Java compiler (javac)
  - Compiles all .java files
  - Reports success/failure
- **Output**: .class files

### **run-server.bat**
- **Purpose**: Start the chat server
- **Usage**: Double-click or run in terminal
- **Does**:
  - Launches ChatServer
  - Displays server status
- **Note**: Keep window open while clients connect

### **run-client.bat**
- **Purpose**: Start a chat client GUI
- **Usage**: Double-click multiple times for multiple clients
- **Does**:
  - Launches ChatClientGUI in new window
  - Can be run multiple times simultaneously
- **Note**: Uses `start` command for separate windows

---

## 📦 Compiled Files (.class)

Generated after compilation:

### Primary Classes
- `ChatServer.class` - Server application
- `ChatClient.class` - Client backend logic
- `ChatClientGUI.class` - GUI application
- `ClientHandler.class` - Client connection handler
- `Message.class` - Message data model

### Inner/Anonymous Classes
- `Message$MessageType.class` - Message type enum
- `Message$1.class` - Anonymous class in Message
- `ChatClient$MessageListener.class` - Listener interface
- `ClientHandler$1.class` - Anonymous class in ClientHandler
- `ChatClientGUI$1.class` - Window adapter
- `ChatClientGUI$2.class` - Anonymous class for events

---

## 📊 File Statistics

### Source Code
```
File               Lines   Classes   Methods   Complexity
---------------------------------------------------------
Message.java         92      2         8         Low
ChatServer.java     169      1        10         Medium
ClientHandler.java  155      2        10         Medium
ChatClient.java     210      2        14         Medium
ChatClientGUI.java  340      3        23         High
---------------------------------------------------------
TOTAL               966     10        65         Medium
```

### Documentation
```
File                  Lines   Purpose
----------------------------------------------
README.md              390    Main documentation
QUICKSTART.md           85    Quick setup guide
ARCHITECTURE.md        250    Technical details
TESTING.md             420    Test scenarios
FILESTRUCTURE.md       300    File reference (this)
----------------------------------------------
TOTAL                 1445    Comprehensive docs
```

---

## 🔄 Dependencies Graph

```
Message.java (Foundation - No dependencies)
    ↑
    ├── ClientHandler.java → ChatServer.java
    │       ↑                     ↑
    │       └─────────┬───────────┘
    │                 │
    └── ChatClient.java
            ↑
            │
    ChatClientGUI.java
```

**Compilation Order**:
1. Message.java (no dependencies)
2. ChatServer.java (depends on Message)
3. ClientHandler.java (depends on Message, ChatServer)
4. ChatClient.java (depends on Message)
5. ChatClientGUI.java (depends on Message, ChatClient)

**Note**: `javac *.java` handles dependencies automatically

---

## 📝 File Purposes Summary

### **For Running the System**
- `ChatServer.java` - Server entry point
- `ChatClientGUI.java` - Client entry point
- `compile.bat` - Build system
- `run-server.bat` - Start server
- `run-client.bat` - Start client

### **For Understanding the Code**
- `README.md` - Overall understanding
- `ARCHITECTURE.md` - Technical deep-dive
- `QUICKSTART.md` - Fast start

### **For Testing**
- `TESTING.md` - Test procedures
- Multiple client instances via `run-client.bat`

### **For Learning**
- All source files contain detailed comments
- Documentation files explain concepts
- Architecture diagrams show relationships

---

## 🎯 Key Files for Different Tasks

### **Want to modify server behavior?**
→ Edit `ChatServer.java` and `ClientHandler.java`

### **Want to change the GUI?**
→ Edit `ChatClientGUI.java`

### **Want to modify networking logic?**
→ Edit `ChatClient.java`

### **Want to change message format?**
→ Edit `Message.java`

### **Want to add features?**
→ Start with `ARCHITECTURE.md` to understand design
→ Modify relevant source files
→ Update `TESTING.md` with new test cases

---

## 💡 Best Practices

### When Modifying
1. **Read documentation first** (ARCHITECTURE.md)
2. **Understand thread safety** (ConcurrentHashMap, synchronized)
3. **Test thoroughly** (use TESTING.md scenarios)
4. **Update documentation** (keep docs in sync)
5. **Maintain code comments** (explain non-obvious logic)

### When Extending
1. **Follow existing patterns** (listener, serialization)
2. **Preserve thread safety** (synchronized, volatile)
3. **Handle errors gracefully** (try-catch, cleanup)
4. **Update relevant docs** (README.md, ARCHITECTURE.md)
5. **Add test scenarios** (TESTING.md)

---

## 📌 Quick Reference

### Compile Everything
```bash
javac *.java
```

### Run Server
```bash
java ChatServer
```

### Run Client
```bash
java ChatClientGUI
```

### Clean Build (PowerShell)
```powershell
Remove-Item *.class
javac *.java
```

### Check Compilation
```powershell
dir *.class
```

---

## 🔍 File Locations

All files are in: `c:\Users\ASUS\Desktop\Net\`

```
Net/
├── Message.java              (Core model)
├── ChatServer.java           (Server logic)
├── ClientHandler.java        (Client handler)
├── ChatClient.java           (Client backend)
├── ChatClientGUI.java        (Client GUI)
├── compile.bat               (Build script)
├── run-server.bat            (Server launcher)
├── run-client.bat            (Client launcher)
├── README.md                 (Main docs)
├── QUICKSTART.md             (Setup guide)
├── ARCHITECTURE.md           (Technical docs)
├── TESTING.md                (Test guide)
└── FILESTRUCTURE.md          (This file)
```

---

**Last Updated**: October 30, 2025  
**Total Files**: 13 (5 .java, 5 .md, 3 .bat)  
**Total Lines of Code**: ~966  
**Total Documentation Lines**: ~1445  
**Language**: Java 8+  
**Framework**: Java Swing (GUI)  
**Dependencies**: None (standard Java library only)
