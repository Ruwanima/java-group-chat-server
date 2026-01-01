import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Message class represents a chat message exchanged between clients and server.
 * Implements Serializable for object transmission over network streams.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Message types for protocol communication
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
    
    /**
     * Constructor for creating a new message
     * @param type The type of message (JOIN, LEAVE, MESSAGE, ERROR)
     * @param username The username of the sender
     * @param content The message content
     */
    public Message(MessageType type, String username, String content) {
        this.type = type;
        this.username = username;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters
    public MessageType getType() {
        return type;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getContent() {
        return content;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Returns formatted timestamp string
     * @return Timestamp in HH:mm:ss format
     */
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return timestamp.format(formatter);
    }
    
    /**
     * Returns a formatted string representation of the message for display
     * @return Formatted message string
     */
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
    
    @Override
    public String toString() {
        return String.format("Message{type=%s, username='%s', content='%s', timestamp=%s}", 
                           type, username, content, timestamp);
    }
}
