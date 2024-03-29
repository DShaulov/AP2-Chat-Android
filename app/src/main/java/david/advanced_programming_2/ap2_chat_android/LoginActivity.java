package david.advanced_programming_2.ap2_chat_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
    private ProgressBar progressBar;
    private String firebaseToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        progressBar = findViewById(R.id.loginProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        contacts = new ArrayList<>();
        messages = new HashMap<String, List<MessageModel>>();
        firebaseToken = "";

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
            progressBar.setVisibility(View.VISIBLE);
            validateUser(username, password, firebaseToken);
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, instanceIdResult -> {
            firebaseToken = instanceIdResult.getToken();
        });

        // Case where user just registered
        Bundle bundle = getIntent().getBundleExtra("Bundle");
        if (bundle != null) {
            String username = bundle.getString("username", "");
            String password = bundle.getString("password", "");
            String fireToken = bundle.getString("firebaseToken", "");
            if (!username.equals("") && !password.equals("")) {
                progressBar.setVisibility(View.VISIBLE);
                validateUser(username, password, fireToken);
            }
        }
    }

    private void startRegisterActivity() {
        errorTextView.setText("");
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
    private void validateUser(String username, String password, String fireToken) {
        Retrofit retrofit = createRetrofit();
        WebAPI webApi = retrofit.create(WebAPI.class);
        Call<ResponseModel> call = webApi.authenticateUser(username, password, fireToken);
        SharedPreferences defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (!response.isSuccessful()) {
                    Log.println(Log.ERROR,"RETRO", "Request unsuccessful" + response.code());
                    progressBar.setVisibility(View.INVISIBLE);
                }
                String tokenString = response.body().getValue();
                if (tokenString.equals("Invalid")) {
                    errorTextView.setText("Username and password do not match");
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    // Update user preferences
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
                errorTextView.setText("*Server not responding");
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void fetchContacts() {
        //
        Retrofit retrofit = createRetrofit();
        WebAPI webApi = retrofit.create(WebAPI.class);
        String fullToken = "Bearer " + preferences.getString("token","");
        Call<List<ContactModel>> call = webApi.getContacts(fullToken);

        call.enqueue(new Callback<List<ContactModel>>() {
            @Override
            public void onResponse(Call<List<ContactModel>> call, Response<List<ContactModel>> response) {
                if (!response.isSuccessful()) {
                    Log.println(Log.ERROR,"RETRO", "Request unsuccessful" + response.code());
                    progressBar.setVisibility(View.INVISIBLE);
                }
                List<ContactModel> allContacts = response.body();
                contacts = allContacts;
                for (ContactModel contact : contacts) {
                    contactDao.insert(contact);
                }
                progressBar.setVisibility(View.INVISIBLE);
                startContactsActivity();
            }
            @Override
            public void onFailure(Call<List<ContactModel>> call, Throwable t) {
                Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
                errorTextView.setText("*Server not responding");
                progressBar.setVisibility(View.INVISIBLE);
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