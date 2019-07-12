package com.example.stavros_melidoniotis.valekarta;

public interface SmsListener {
    /**
     * To call this method when new message received and send back
     *
     @param message Message
     */
    void messageReceived(String message);
}
