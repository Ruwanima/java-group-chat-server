# Testing Guide

## Comprehensive Test Scenarios

### Test 1: Basic Server Startup
**Objective**: Verify server starts and listens correctly

**Steps**:
1. Run `java ChatServer`
2. Verify output shows:
   ```
   =================================
     Chat Server Started
     Listening on port: 5555
   =================================
   ```

**Expected**: Server runs without errors

---

### Test 2: Single Client Connection
**Objective**: Test basic client-server connection

**Steps**:
1. Start server
2. Run `java ChatClientGUI`
3. Enter username "Alice"
4. Click "Connect"

**Expected**:
- Client shows "Connected" status (green)
- Server console shows: "Client joined: Alice"
- Notification area shows: "[time] *** Alice joined the chat ***"

---

### Test 3: Multiple Client Connections
**Objective**: Test concurrent client connections

**Steps**:
1. Start server
2. Launch 3 client GUIs
3. Connect as "Alice", "Bob", and "Charlie"

**Expected**:
- All clients connect successfully
- Each client sees join notifications for other users
- Server console shows active client count

**Server Console Output**:
```
Client joined: Alice
Active clients: 1
Client joined: Bob
Active clients: 2
Client joined: Charlie
Active clients: 3
```

---

### Test 4: Message Broadcasting
**Objective**: Verify messages reach all clients

**Steps**:
1. Connect 3 clients (Alice, Bob, Charlie)
2. Alice sends: "Hello everyone!"
3. Bob sends: "Hi Alice!"
4. Charlie sends: "Hey there!"

**Expected**:
- All messages appear in all clients' chat areas
- Messages show correct timestamp and username
- Format: `[HH:mm:ss] username: message`

**All Clients Should See**:
```
[10:30:15] Alice: Hello everyone!
[10:30:20] Bob: Hi Alice!
[10:30:25] Charlie: Hey there!
```

---

### Test 5: Duplicate Username Prevention
**Objective**: Test username uniqueness validation

**Steps**:
1. Connect first client as "Alice"
2. Connect second client as "Alice"

**Expected**:
- Second client receives error dialog: "Username 'Alice' is already taken"
- Second client is disconnected
- First client remains connected

---

### Test 6: Client Disconnection
**Objective**: Test graceful disconnect and notifications

**Steps**:
1. Connect 3 clients (Alice, Bob, Charlie)
2. Click "Disconnect" button on Bob's client

**Expected**:
- Bob disconnects successfully
- Alice and Charlie see: "[time] *** Bob left the chat ***"
- Server console shows: "Client disconnected: Bob"
- Server shows: "Active clients: 2"

---

### Test 7: Real-time Message Delivery
**Objective**: Test message latency and ordering

**Steps**:
1. Connect 3 clients
2. Send messages rapidly from different clients
3. Observe message order

**Expected**:
- Messages appear in chronological order
- No message loss
- Minimal latency (< 1 second)

---

### Test 8: Server Shutdown
**Objective**: Test graceful server shutdown

**Steps**:
1. Connect multiple clients
2. Press Ctrl+C on server console
3. Observe client behavior

**Expected**:
- Server shows "Shutting down server..."
- All clients receive disconnect notification
- All client GUIs show connection lost
- Server terminates cleanly

---

### Test 9: Empty Message Prevention
**Objective**: Test input validation

**Steps**:
1. Connect a client
2. Try to send empty message (just spaces)
3. Try to send message with only whitespace

**Expected**:
- Empty messages are not sent
- Message field clears after send
- No error messages

---

### Test 10: Long Message Handling
**Objective**: Test message size limits

**Steps**:
1. Connect a client
2. Send a very long message (500+ characters)

**Expected**:
- Message is sent and received successfully
- Text wraps correctly in chat area
- No truncation or errors

---

### Test 11: Special Characters
**Objective**: Test character encoding

**Steps**:
1. Connect a client
2. Send messages with:
   - Emojis: "Hello 😊"
   - Special chars: "Test @#$%^&*()"
   - Unicode: "こんにちは"

**Expected**:
- All characters display correctly
- No encoding errors

---

### Test 12: Rapid Connection/Disconnection
**Objective**: Test server stability under stress

**Steps**:
1. Start server
2. Rapidly connect and disconnect 5 clients
3. Repeat 3 times

**Expected**:
- Server remains stable
- No exceptions or errors
- Correct client count maintained

---

### Test 13: GUI Responsiveness
**Objective**: Test UI remains responsive

**Steps**:
1. Connect client
2. Receive 100+ messages
3. Try to send new message
4. Try to disconnect

**Expected**:
- GUI remains responsive
- No freezing or lag
- Scroll works smoothly
- Buttons remain clickable

---

### Test 14: Multiple Client Windows
**Objective**: Test multiple instances on same machine

**Steps**:
1. Run `run-client.bat` 5 times
2. Connect all with different usernames
3. Send messages from each

**Expected**:
- All windows operate independently
- No interference between instances
- All messages broadcast correctly

---

### Test 15: Network Interruption
**Objective**: Test error handling

**Steps**:
1. Connect client to server
2. Stop server forcefully (kill process)
3. Observe client behavior

**Expected**:
- Client detects disconnection
- Shows "Connection to server lost" notification
- Status changes to "Not connected"
- No crash or hang

---

### Test 16: Timestamp Accuracy
**Objective**: Verify message timestamps

**Steps**:
1. Connect two clients
2. Send messages
3. Check timestamps

**Expected**:
- Timestamps reflect message send time
- Format: HH:mm:ss
- Chronological order maintained

---

### Test 17: Auto-Scroll Functionality
**Objective**: Test automatic scrolling

**Steps**:
1. Connect client
2. Receive 50+ messages
3. Scroll up manually
4. Receive new message

**Expected**:
- Auto-scrolls to latest message
- Scroll bar at bottom
- Latest messages always visible

---

### Test 18: Username with Special Characters
**Objective**: Test username validation

**Steps**:
1. Try usernames: "Alice123", "Bob_Smith", "Test User"

**Expected**:
- All valid usernames accepted
- Display correctly in messages

---

### Test 19: Concurrent Message Sending
**Objective**: Test message handling under load

**Steps**:
1. Connect 5 clients
2. All clients send messages simultaneously
3. Repeat 10 times

**Expected**:
- All messages delivered
- No message loss
- Correct order maintained
- No server errors

---

### Test 20: GUI State Consistency
**Objective**: Verify UI state management

**Steps**:
1. Before connection:
   - Username field: enabled
   - Message field: disabled
   - Send button: disabled
   - Connect button: "Connect"
2. After connection:
   - Username field: disabled
   - Message field: enabled
   - Send button: enabled
   - Connect button: "Disconnect"

**Expected**:
- State transitions work correctly
- No inconsistent states

---

## Performance Metrics

### Expected Performance
- Message latency: < 100ms (local)
- Server capacity: 50 concurrent clients
- Message throughput: 1000+ messages/second
- Memory usage: < 100MB for 10 clients
- CPU usage: < 10% idle, < 30% active

### Load Testing
```bash
# Simulate 10 clients
for i in {1..10}; do
    java ChatClientGUI &
done
```

---

## Bug Report Template

If you find issues, report using this format:

```
**Bug Title**: Brief description

**Environment**:
- OS: Windows/Linux/Mac
- Java Version: (run `java -version`)
- Number of clients: X

**Steps to Reproduce**:
1. Step one
2. Step two
3. Step three

**Expected Behavior**:
What should happen

**Actual Behavior**:
What actually happened

**Error Messages**:
Any console output or error dialogs

**Screenshots**:
If applicable
```

---

## Automated Testing Script (PowerShell)

```powershell
# Save as test-chat-system.ps1
Write-Host "=== Chat System Test Suite ===" -ForegroundColor Green

# Test 1: Compilation
Write-Host "`nTest 1: Compilation" -ForegroundColor Yellow
javac *.java
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Compilation successful" -ForegroundColor Green
} else {
    Write-Host "✗ Compilation failed" -ForegroundColor Red
    exit 1
}

# Test 2: Check class files
Write-Host "`nTest 2: Class Files" -ForegroundColor Yellow
$required = @("ChatServer.class", "ChatClient.class", 
              "ChatClientGUI.class", "Message.class", 
              "ClientHandler.class")
$missing = @()
foreach ($file in $required) {
    if (!(Test-Path $file)) {
        $missing += $file
    }
}
if ($missing.Count -eq 0) {
    Write-Host "✓ All class files present" -ForegroundColor Green
} else {
    Write-Host "✗ Missing: $($missing -join ', ')" -ForegroundColor Red
    exit 1
}

Write-Host "`n=== All Tests Passed ===" -ForegroundColor Green
Write-Host "Ready to run the chat system!"
```

Run with:
```powershell
powershell -ExecutionPolicy Bypass -File test-chat-system.ps1
```

---

## Test Checklist

Use this checklist for complete testing:

- [ ] Server starts successfully
- [ ] Single client connects
- [ ] Multiple clients connect simultaneously
- [ ] Messages broadcast to all clients
- [ ] Duplicate username rejected
- [ ] Client disconnects gracefully
- [ ] Server shutdown is clean
- [ ] Empty messages prevented
- [ ] Special characters handled
- [ ] Long messages work correctly
- [ ] GUI remains responsive
- [ ] Timestamps are accurate
- [ ] Auto-scroll functions properly
- [ ] Network errors handled gracefully
- [ ] Join/leave notifications appear
- [ ] Status indicator updates correctly
- [ ] Multiple instances work on same machine
- [ ] Concurrent sending works
- [ ] GUI state consistency maintained
- [ ] Memory usage acceptable

---

**Note**: For production deployment, additional tests would include:
- Security testing (injection, authentication)
- Stress testing (1000+ concurrent users)
- Network latency simulation
- Memory leak detection
- Cross-platform testing
