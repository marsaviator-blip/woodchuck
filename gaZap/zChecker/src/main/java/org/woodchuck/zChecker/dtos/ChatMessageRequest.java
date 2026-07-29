package org.woodchuck.zChecker.dtos;

import java.util.Date;

public class ChatMessageRequest {
    private String prompt;
    private boolean isUser;
    private boolean isNote;
    private Date timestamp;
    private String userId;
    private Long id;

    // Getters and Setters
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    
    public boolean getIsUser() { return isUser; }
    public void setIsUser(boolean isUser) { this.isUser = isUser; }
    
    public boolean getIsNote() { return isNote; }
    public void setIsNote(boolean isNote) { this.isNote = isNote; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
