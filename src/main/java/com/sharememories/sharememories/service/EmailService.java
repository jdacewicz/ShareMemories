package com.sharememories.sharememories.service;

public interface EmailService {

    void sendMessage(String from, String to, String subject, String text);
}
