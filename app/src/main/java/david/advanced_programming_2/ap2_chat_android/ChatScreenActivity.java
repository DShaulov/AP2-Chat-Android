package david.advanced_programming_2.ap2_chat_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatScreenActivity extends AppCompatActivity {
    private String contactId;
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
        contactId = bundle.getString("contactId");

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
        if (messageContent.equals("")) {
            return;
        }
        Retrofit retrofit = createRetrofit();
        WebAPI webApi = retrofit.create(WebAPI.class);
        String fullToken = "Bearer " + preferences.getString("token","");
        Call<ResponseBody> call = webApi.postMessage(fullToken, contactId, messageContent);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.println(Log.ERROR,"RETRO", "Request unsuccessful" + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
            }
        });
        chatInputEditText.setText("");
        hideKeyboard(this);
    }
    Retrofit createRetrofit() {
        String serverUrl = preferences.getString("server","");
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
