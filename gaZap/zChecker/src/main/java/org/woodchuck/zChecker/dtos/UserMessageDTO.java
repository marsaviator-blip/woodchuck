package org.woodchuck.zChecker.dtos;

import java.time.LocalDateTime;

public class UserMessageDTO {
    private String prompt;
    private boolean isUser;
    private boolean isNote;
    private LocalDateTime timestamp;
    private String userId;
    private Long id;

    // Default Constructor (Required for Jackson JSON deserialization)
    public UserMessageDTO() {}

    // Getters and Setters
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public boolean isUser() { return isUser; }
    public void setUser(boolean user) { this.isUser = user; }

    public boolean isNote() { return isNote; }
    public void setNote(boolean note) { this.isNote = note; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
