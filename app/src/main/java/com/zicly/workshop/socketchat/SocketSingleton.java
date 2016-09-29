package com.zicly.workshop.socketchat;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Gillu on 9/28/2016.
 */

public class SocketSingleton {
    private static SocketSingleton instance = new SocketSingleton();

    private Socket mSocket;
    private String CHAT_SERVER_URL = "http://chat.socket.io";

    public static SocketSingleton getInstance() {
        return instance;
    }

    private SocketSingleton() {
        try {
            mSocket = IO.socket(CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
