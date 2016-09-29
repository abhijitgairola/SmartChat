package com.zicly.workshop.socketchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatWindow extends AppCompatActivity {

    private EditText messageInput;
    private ImageButton sendChat;
    private TextView chatWindow;
    private String mUsername;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        this.mUsername = getIntent().getStringExtra("username");

        chatWindow = (TextView) findViewById(R.id.chatWindow);
        sendChat = (ImageButton) findViewById(R.id.send_button);
        messageInput = (EditText) findViewById(R.id.message_input);

        sendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString();
                sendMessage(message);
                addMessage(mUsername, message);
                messageInput.setText("");
            }
        });

        mSocket = SocketSingleton.getInstance().getSocket();
        mSocket.on("new message", onNewMessage);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }
                    addMessage(username, message);
                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }
                    addUserJoined(username, numUsers);
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }
                    addUserLeft(username, numUsers);
                }
            });
        }
    };

    private void addMessage(String username, String message) {
        String currentContent = chatWindow.getText().toString();
        currentContent += "\n<"+username+"> : "+ message;
        chatWindow.setText(currentContent);
    }

    private void addUserJoined(String username, int numUsers) {
        String currentContent = chatWindow.getText().toString();
        currentContent += "\n"+username+" JOINED, Total users = "+ numUsers;
        chatWindow.setText(currentContent);
    }

    private void addUserLeft(String username, int numUsers) {
        String currentContent = chatWindow.getText().toString();
        currentContent += "\n"+username+" LEFT, Total users = "+ numUsers;
        chatWindow.setText(currentContent);
    }

    private void sendMessage(String message) {
        mSocket.emit("new message", message);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSocket.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
    }
}
