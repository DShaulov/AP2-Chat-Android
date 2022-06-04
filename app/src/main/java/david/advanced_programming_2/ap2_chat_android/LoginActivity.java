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
    private List<ContactModel> contacts;
    private HashMap<String, List<MessageModel>> messages;
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
        contacts = new ArrayList<>();
        messages = new HashMap<String, List<MessageModel>>();

        preferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currentUser", "");
        editor.putString("token", "");
        editor.putString("server", "http://10.0.2.2:5201");
        editor.commit();


        toRegisterBtn.setOnClickListener(view -> startRegisterActivity());
        optionsBtn.setOnClickListener(view -> startOptionsActivity());
        loginBtn.setOnClickListener(view -> {
            errorTextView.setText("");
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();
            if (password.equals("") || username.equals("")) {
                errorTextView.setText("*Fields cannot be empty");
                return;
            }
            validateUser(username, password);
        });

        // Case where user just registered
        Bundle bundle = getIntent().getBundleExtra("Bundle");
        if (bundle != null) {
            String username = bundle.getString("username", "");
            String password = bundle.getString("password", "");
            if (!username.equals("") && !password.equals("")) {
                validateUser(username, password);
            }
        }
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
    private void validateUser(String username, String password) {
        Retrofit retrofit = createRetrofit();
        WebAPI webApi = retrofit.create(WebAPI.class);
        Call<ResponseModel> call = webApi.authenticateUser(username, password);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (!response.isSuccessful()) {
                    Log.println(Log.ERROR,"RETRO", "Request unsuccessful" + response.code());
                }
                String tokenString = response.body().getValue();
                if (tokenString.equals("Invalid")) {
                    errorTextView.setText("Username and password do not match");
                }
                else {
                    SharedPreferences.Editor onClickEditor = preferences.edit();
                    onClickEditor.putString("currentUser", username);
                    onClickEditor.putString("token", tokenString);
                    onClickEditor.commit();
                    fetchContacts();
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
            }
        });
    }

    private void fetchContacts() {
        Retrofit retrofit = createRetrofit();
        WebAPI webApi = retrofit.create(WebAPI.class);
        String fullToken = "Bearer " + preferences.getString("token","");
        Call<List<ContactModel>> call = webApi.getContacts(fullToken);

        call.enqueue(new Callback<List<ContactModel>>() {
            @Override
            public void onResponse(Call<List<ContactModel>> call, Response<List<ContactModel>> response) {
                if (!response.isSuccessful()) {
                    Log.println(Log.ERROR,"RETRO", "Request unsuccessful" + response.code());
                }
                List<ContactModel> allContacts = response.body();
                contacts = allContacts;
                startContactsActivity();
            }
            @Override
            public void onFailure(Call<List<ContactModel>> call, Throwable t) {
                Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
            }
        });
    }
    Retrofit createRetrofit() {
        String serverUrl = preferences.getString("server","");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }



}