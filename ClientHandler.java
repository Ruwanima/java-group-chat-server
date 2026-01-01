import java.io.*;
import java.net.Socket;

/**
 * ClientHandler manages communication with a single connected client.
 * Each ClientHandler runs in a separate thread to handle concurrent client connections.
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ChatServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;
    private volatile boolean running;
    
    /**
     * Constructor for ClientHandler
     * @param socket The client's socket connection
     * @param server Reference to the main ChatServer for broadcasting
     */
    public ClientHandler(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.server = server;
        this.running = true;
    }
    
    /**
     * Main execution method for the client handler thread
     * Handles the message receiving loop and cleanup
     */
    @Override
    public void run() {
        try {
            // Initialize I/O streams (ObjectOutputStream must be created first)
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
            
            // Receive and process messages from the client
            while (running) {
                try {
                    Message message = (Message) in.readObject();
                    
                    if (message != null) {
                        handleMessage(message);
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Error: Unknown message class received");
                    break;
                } catch (EOFException e) {
                    // Client disconnected
                    break;
                }
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Error handling client " + username + ": " + e.getMessage());
            }
        } finally {
            cleanup();
        }
    }
    
    /**
     * Processes received messages based on their type
     * @param message The message to process
     */
    private void handleMessage(Message message) {
        switch (message.getType()) {
            case JOIN:
                // Register the client's username
                username = message.getUsername();
                server.addClient(username, this);
                System.out.println("Client joined: " + username);
                
                // Broadcast join notification to all clients
                server.broadcastMessage(message);
                break;
                
            case MESSAGE:
                // Broadcast regular chat message to all clients
                System.out.println(message.getFormattedMessage());
                server.broadcastMessage(message);
                break;
                
            case LEAVE:
                // Client is leaving
                running = false;
                break;
                
            default:
                System.err.println("Unknown message type received");
        }
    }
    
    /**
     * Sends a message to this client
     * Synchronized to prevent concurrent write access to the output stream
     * @param message The message to send
     */
    public synchronized void sendMessage(Message message) {
        try {
            if (out != null && running) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Error sending message to " + username + ": " + e.getMessage());
            cleanup();
        }
    }
    
    /**
     * Gets the username of this client
     * @return The client's username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Closes the connection and cleans up resources
     * Thread-safe cleanup method
     */
    private void cleanup() {
        running = false;
        
        // Remove client from server's client list
        if (username != null) {
            server.removeClient(username);
            
            // Notify other clients that this user left
            Message leaveMessage = new Message(Message.MessageType.LEAVE, username, "");
            server.broadcastMessage(leaveMessage);
            
            System.out.println("Client disconnected: " + username);
        }
        
        // Close streams and socket
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing client connection: " + e.getMessage());
        }
    }
    
    /**
     * Gracefully stops this client handler
     */
    public void stop() {
        running = false;
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error stopping client handler: " + e.getMessage());
        }
    }
}
