package com.zicly.workshop.socketchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText name;
    private Button joinButton;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText)findViewById(R.id.nickNameEditText);
        joinButton = (Button) findViewById(R.id.joinButtton);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        mSocket = SocketSingleton.getInstance().getSocket();
        mSocket.connect();
        mSocket.on("login", onLogin);
    }

    private void attemptLogin() {
        String nickName = name.getText().toString();
        mSocket.emit("add user", nickName);
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //User have successfully logged in.
            Log.i(TAG, "ongInSuccess");
            Intent intent = new Intent(LoginActivity.this, ChatWindow.class);
            intent.putExtra("username", name.getText().toString());
            startActivity(intent);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("login", onLogin);
    }
}
