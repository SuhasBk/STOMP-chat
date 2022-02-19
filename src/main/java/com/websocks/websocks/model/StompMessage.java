package com.websocks.websocks.model;

import java.util.List;

public class StompMessage {
    
    private String message;
    private String id;
    private Integer count;
    private List<String> users;
    private String filename;

    public StompMessage(String id, String message, Integer count, List<String> users) {
        this.id = id;
        this.message = message;
        this.count = count;
        this.users = users;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Integer getCount() {
        return count;
    }
    public void setCount(Integer count) {
        this.count = count;
    }
    public List<String> getUsers() {
        return users;
    }
    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
