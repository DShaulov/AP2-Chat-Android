package david.advanced_programming_2.ap2_chat_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatScreenActivity extends AppCompatActivity {
    private RecyclerView messagesRecyclerView;
    private MessagesRecyclerViewAdapter adapter;
    private Button chatSendBtn;
    private EditText chatInputEditText;
    private ImageButton optionsBtn;

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

        chatSendBtn.setOnClickListener(view -> handleMessageSend());
    }

    private void startOptionsActivity() {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }
    private void handleMessageSend() {
        String messageContent = chatInputEditText.getText().toString();
    }

}
