import java.io.*;
import java.net.Socket;

/**
 * ChatClient handles the client-side networking logic.
 * Manages connection to the server, sending and receiving messages.
 * Implements a listener pattern to notify the GUI of incoming messages.
 */
public class ChatClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5555;
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;
    private MessageListener listener;
    private volatile boolean connected;
    private Thread receiveThread;
    
    /**
     * Interface for receiving message notifications
     * Allows GUI to be updated when messages arrive
     */
    public interface MessageListener {
        void onMessageReceived(Message message);
        void onConnectionError(String error);
        void onDisconnected();
    }
    
    /**
     * Constructor for ChatClient
     * @param username The username for this client
     */
    public ChatClient(String username) {
        this.username = username;
        this.connected = false;
    }
    
    /**
     * Sets the message listener for receiving notifications
     * @param listener The MessageListener implementation (usually the GUI)
     */
    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }
    
    /**
     * Connects to the chat server
     * @return true if connection successful, false otherwise
     */
    public boolean connect() {
        try {
            // Create socket connection to server
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            
            // Initialize I/O streams (ObjectOutputStream first)
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            
            connected = true;
            
            // Send JOIN message to server
            Message joinMessage = new Message(Message.MessageType.JOIN, username, "");
            sendMessage(joinMessage);
            
            // Start thread to receive messages from server
            startReceiveThread();
            
            return true;
            
        } catch (IOException e) {
            if (listener != null) {
                listener.onConnectionError("Failed to connect to server: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Starts a background thread to receive messages from the server
     */
    private void startReceiveThread() {
        receiveThread = new Thread(() -> {
            try {
                while (connected) {
                    try {
                        // Read incoming message
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
        
        receiveThread.setDaemon(true);
        receiveThread.start();
    }
    
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
    
    /**
     * Sends a message object to the server
     * Synchronized to prevent concurrent write access
     * @param message The message to send
     */
    private synchronized void sendMessage(Message message) {
        try {
            if (out != null && connected) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            if (listener != null) {
                listener.onConnectionError("Failed to send message: " + e.getMessage());
            }
            disconnect();
        }
    }
    
    /**
     * Disconnects from the server
     */
    public void disconnect() {
        if (connected) {
            connected = false;
            
            // Send LEAVE message to server
            try {
                Message leaveMessage = new Message(Message.MessageType.LEAVE, username, "");
                if (out != null) {
                    out.writeObject(leaveMessage);
                    out.flush();
                }
            } catch (IOException e) {
                // Ignore errors during disconnect
            }
            
            // Close streams and socket
            cleanup();
        }
    }
    
    /**
     * Closes all resources
     */
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
    
    /**
     * Checks if the client is connected to the server
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Gets the username of this client
     * @return The client's username
     */
    public String getUsername() {
        return username;
    }
}
