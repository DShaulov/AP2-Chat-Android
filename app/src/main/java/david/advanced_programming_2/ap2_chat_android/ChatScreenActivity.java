package david.advanced_programming_2.ap2_chat_android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatScreenActivity extends AppCompatActivity {
    private List<MessageModel> messages;
    private String contactId;
    private String contactServer;
    private String contactName;
    private RecyclerView messagesRecyclerView;
    private MessagesRecyclerViewAdapter adapter;
    private Button chatSendBtn;
    private EditText chatInputEditText;
    private TextView contactNameTextView;
    private ImageButton optionsBtn;
    private SharedPreferences preferences;
    private MessagesViewModel messageData;

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO remove if unnecessary
            /*MessageModel newMessage = new MessageModel(
                    intent.getExtras().getString("body"),
                    intent.getExtras().getString("time"),
                    false,
                    intent.getExtras().getString("from"),
                    intent.getExtras().getString("to")
            );
            messages.add(newMessage);
            adapter.notifyDataSetChanged();
            messagesRecyclerView.scrollToPosition(messages.size() - 1);*/
            fetchMessages(contactId);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        Bundle bundle = getIntent().getBundleExtra("Bundle");
        messages = (ArrayList<MessageModel>) bundle.getSerializable("messages");
        contactId = bundle.getString("contactId");
        contactServer = bundle.getString("contactServer");
        contactName = bundle.getString("contactName");

        optionsBtn = findViewById(R.id.optionsBtn);
        optionsBtn.setOnClickListener(view -> startOptionsActivity());
        chatSendBtn = findViewById(R.id.chatSendBtn);
        chatInputEditText = findViewById(R.id.chatInputEditText);
        contactNameTextView = findViewById(R.id.contactNameTextView);

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        adapter = new MessagesRecyclerViewAdapter(this, messages);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(adapter);
        preferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        chatSendBtn.setOnClickListener(view -> handleMessageSend());

        messagesRecyclerView.scrollToPosition(messages.size() - 1);

        contactNameTextView.setText(contactName);

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((messageReceiver),
                new IntentFilter("MessageData")
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    private void startOptionsActivity() {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }
    private void handleMessageSend() {
        String messageContent = chatInputEditText.getText().toString();
        if (messageContent.equals("")) {
            return;
        }
        Retrofit retrofit = createRetrofit(preferences.getString("server",""));
        WebAPI webApi = retrofit.create(WebAPI.class);
        String fullToken = "Bearer " + preferences.getString("token","");
        Call<ResponseBody> call = webApi.postMessage(fullToken, contactId, messageContent);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.println(Log.ERROR,"RETRO", "Request unsuccessful" + response.code());
                }
                fetchMessages(contactId);
                messageTransfer(contactId, messageContent);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
            }
        });
        chatInputEditText.setText("");
        hideKeyboard(this);
    }

    private void fetchMessages(String contactId) {
        Retrofit retrofit = createRetrofit(preferences.getString("server",""));
        WebAPI webApi = retrofit.create(WebAPI.class);
        String fullToken = "Bearer " + preferences.getString("token","");
        Call<List<MessageModel>> call = webApi.getMessages(fullToken, contactId);

        call.enqueue(new Callback<List<MessageModel>>() {
            @Override
            public void onResponse(Call<List<MessageModel>> call, Response<List<MessageModel>> response) {
                if (!response.isSuccessful()) {
                    Log.println(Log.ERROR,"RETRO", "Request unsuccessful" + response.code());
                }
                List<MessageModel> allMessages = response.body();
                messages = allMessages;
                adapter.updateMessagesList(allMessages);
                messagesRecyclerView.scrollToPosition(messages.size() - 1);
            }
            @Override
            public void onFailure(Call<List<MessageModel>> call, Throwable t) {
                Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
            }
        });

    }

    private void messageTransfer(String contactId, String messageContent) {
        Retrofit retrofit = createRetrofit(contactServer);
        WebAPI webApi = retrofit.create(WebAPI.class);
        Call<Void> call = webApi.transferMessage(preferences.getString("currentUser", ""), contactId, messageContent);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.println(Log.ERROR,"RETRO", "Request unsuccessful" + response.code());
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
            }
        });
    }

    private void addMessageToLiveData(String contactId, String messageContent) {
        String content = messageContent;
        String created = parseTime();
        String from = preferences.getString("currentUser","");
        String to = contactId;
        MessageModel newMessage = new MessageModel(content, created, true, from, to);
    }

    private String parseTime() {
        return "placeholder";
    }
    Retrofit createRetrofit(String serverUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

}
