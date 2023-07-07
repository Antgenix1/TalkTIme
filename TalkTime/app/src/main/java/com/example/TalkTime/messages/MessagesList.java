package com.example.TalkTime.messages;

public class MessagesList {

    private final String name;
    private final String mobile;
    private final String lastMessage;
    private final String chatKey;

    private final int unseenMessages;


    public MessagesList(String name, String mobile, String lastMessage, int unseenMessages, String chatKey) {
        this.name = name;
        this.mobile = mobile;
        this.lastMessage = lastMessage;
        this.unseenMessages = unseenMessages;
        this.chatKey = chatKey;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public String getChatKey() {
        return chatKey;
    }
}
