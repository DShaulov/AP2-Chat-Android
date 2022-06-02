package david.advanced_programming_2.ap2_chat_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {
    private String currentUser;
    private String token;
    private ArrayList<ContactModel> contacts;
    private HashMap<String, ArrayList<MessageModel>> messages;
    private Button toRegisterBtn;
    private EditText usernameField;
    private EditText passwordField;
    private Button loginBtn;
    private TextView errorTextView;
    private ImageButton optionsBtn;
    private AppDB db;
    private MessageDao messageDao;
    private ContactDao contactDao;
    private SharedPreferences preferences;
    private String firebaseToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get firebase token for push notifications
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, instanceIdResult -> {
            firebaseToken = instanceIdResult.getToken();
        });


        db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "ChatDB")
                .allowMainThreadQueries()
                .build();
        messageDao = db.messageDao();
        contactDao = db.contactDao();

        toRegisterBtn = findViewById(R.id.toRegisterBtn);
        loginBtn = findViewById(R.id.loginBtn);
        optionsBtn = findViewById(R.id.optionsBtn);
        usernameField = findViewById(R.id.loginUsernameField);
        passwordField = findViewById(R.id.loginPasswordField);
        errorTextView = findViewById(R.id.loginErrorTextView);
        token = "";
        contacts = new ArrayList<>();
        messages = new HashMap<String, ArrayList<MessageModel>>();

        preferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currentUser", "");
        editor.putString("token", "");
        editor.putString("server", "http://10.0.2.2:5201/");
        editor.commit();

        fetchContacts();
        fetchMessages();

        toRegisterBtn.setOnClickListener(view -> startRegisterActivity());
        optionsBtn.setOnClickListener(view -> startOptionsActivity());
        loginBtn.setOnClickListener(view -> {
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();
            // VALIDATION TODO
            token = validateUser(username, password);
            //
            if (token.equals("")) {
                errorTextView.setText(R.string.loginValidityError);
                return;
            }
            SharedPreferences.Editor onClickEditor = preferences.edit();
            onClickEditor.putString("currentUser", username);
            onClickEditor.putString("token", token);
            onClickEditor.commit();
            startContactsActivity();
        });


    }

    private void startRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    private void startContactsActivity() {
        Intent intent = new Intent(this, ContactListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contactsList", (Serializable) contacts);
        bundle.putSerializable("allMessages", (Serializable) messages);
        intent.putExtra("Bundle", bundle);
        startActivity(intent);
    }
    private void startOptionsActivity() {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }


    // TODO
    private String validateUser(String username, String password) {
        Retrofit retrofit = createRetrofit();
        WebAPI webApi = retrofit.create(WebAPI.class);
        Call<String> call = webApi.authenticateUser(username, password);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    Log.println(Log.ERROR, "RETRO", "UNSUCCESSFUL " + response.code());
                    Toast.makeText(getApplicationContext(), "Code: " + response.code(), Toast.LENGTH_SHORT);
                    return;
                }
                String tokenString = response.body();
                // Case where password and username do not match
                if (tokenString.equals("Invalid")) {
                    Log.println(Log.ERROR, "RETRO", tokenString);

                    Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT);
                }
                Log.println(Log.ERROR, "RETRO", tokenString);
                Toast.makeText(getApplicationContext(), tokenString, Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.println(Log.ERROR, "RETRO", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
        return "PLACEHOLDER";
    }

    // TODO
    private void fetchContacts() {
        // PLACEHOLDER
        ContactModel dennisContactModel = new ContactModel("mac", "Mac",
                "localhost:3000", "Goddamn Bitch",
                "24-04-22", "mac");
        ContactModel charlieContactModel = new ContactModel("charlie", "Charlie",
                "localhost:3000", "Fight MilK!",
                "24-04-22", "mac");
        //
        contacts.add(dennisContactModel);
        contacts.add(charlieContactModel);
    }

    Retrofit createRetrofit() {
        String serverUrl = preferences.getString("server","");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.0.2.2:7201/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }


    //TODO
    private void fetchMessages() {
        ArrayList<MessageModel> messagesWithCharlie = new ArrayList<>();
        ArrayList<MessageModel> messagesWithMac = new ArrayList<>();

        MessageModel message1 = new MessageModel("My elbows are massive", "24-04-2022",
                false, "mac", "frank");
        MessageModel message2 = new MessageModel("Goddamn you mac", "24-04-2022",
                true, "frank", "mac");
        MessageModel message3 = new MessageModel("Do you think a pirate lives in there?", "24-04-2022",
                false, "charlie", "frank");
        MessageModel message4 = new MessageModel("Botched toe!", "24-04-2022",
                true, "frank", "charlie");
        messagesWithMac.add(message1);
        messagesWithMac.add(message2);
        messagesWithCharlie.add(message3);
        messagesWithCharlie.add(message4);
        messages.put("charlie", messagesWithCharlie);
        messages.put("mac", messagesWithMac);
    }
}