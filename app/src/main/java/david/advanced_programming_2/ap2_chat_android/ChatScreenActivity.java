package david.advanced_programming_2.ap2_chat_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatScreenActivity extends AppCompatActivity {
    private RecyclerView messagesRecyclerView;
    private MessagesRecyclerViewAdapter adapter;
    private Button chatSendBtn;
    private EditText chatInputEditText;
    private ImageButton optionsBtn;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        Bundle bundle = getIntent().getBundleExtra("Bundle");
        ArrayList<MessageModel> messages = (ArrayList<MessageModel>) bundle.getSerializable("messages");

        optionsBtn = findViewById(R.id.optionsBtn);
        optionsBtn.setOnClickListener(view -> startOptionsActivity());
        chatSendBtn = findViewById(R.id.chatSendBtn);
        chatInputEditText = findViewById(R.id.chatInputEditText);
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        adapter = new MessagesRecyclerViewAdapter(this, messages);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(adapter);
        preferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        chatSendBtn.setOnClickListener(view -> handleMessageSend());
    }

    private void startOptionsActivity() {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }
    private void handleMessageSend() {
        String messageContent = chatInputEditText.getText().toString();
    }
    Retrofit createRetrofit() {
        String serverUrl = preferences.getString("server","");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.0.2.2:7201/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
