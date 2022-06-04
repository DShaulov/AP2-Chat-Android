package david.advanced_programming_2.ap2_chat_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    private Button toLoginBtn;
    private Button registerBtn;
    private EditText usernameField;
    private EditText passwordField;
    private EditText nameField;
    private TextView errorTextView;
    private ImageButton optionsBtn;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toLoginBtn = (Button) findViewById(R.id.toLoginBtn);
        registerBtn = (Button) findViewById(R.id.registerScreenRegisterBtn);
        optionsBtn = findViewById(R.id.optionsBtn);
        usernameField = findViewById(R.id.registerUsernameField);
        passwordField = findViewById(R.id.registerPasswordField);
        nameField = findViewById(R.id.registerNameField);
        errorTextView = findViewById(R.id.registerErrorTextView);
        preferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);


        toLoginBtn.setOnClickListener(view -> startLoginActivity());
        registerBtn.setOnClickListener(view -> handleRegister());
        optionsBtn.setOnClickListener(view -> startOptionsActivity());

    }

    private void startLoginActivity() {
        finish();
    }
    private void startOptionsActivity() {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }
    private void handleRegister() {
        String username = usernameField.getText().toString();
        String displayName = nameField.getText().toString();
        String password = passwordField.getText().toString();
        errorTextView.setText("");
        if (!inputIsValid(password)) {
            errorTextView.setText(R.string.registerValidityError);
            return;
        }
        if (username.equals("") || displayName.equals("")) {
            errorTextView.setText("*Fields cannot be empty");
            return;
        }
        // Check that username is not taken and register the user
        checkAndRegister(username, password, displayName);

    }
    private boolean inputIsValid(String password) {
        boolean atLeastOneChar = password.matches(".*[a-zA-Z]+.*");
        boolean atLeastOneNumber = password.matches(".*\\d.*");
        if (!atLeastOneChar || !atLeastOneNumber) {
            return false;
        }

        return true;
    }
    Retrofit createRetrofit() {
        String serverUrl = preferences.getString("server","");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
    private void checkAndRegister(String username, String password, String displayName) {
        Retrofit retrofit = createRetrofit();
        WebAPI webApi = retrofit.create(WebAPI.class);
        Call<ResponseModel> call = webApi.checkUserExists(username);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (!response.isSuccessful()) {
                    Log.println(Log.ERROR,"RETRO", "Request unsuccessful" + response.code());
                }
                String responseString = response.body().getValue();
                if (responseString.equals("EXISTS")) {
                    errorTextView.setText("*Username already taken");
                    return;
                }
                // Register the user with the API
                registerUser(username, password, displayName);
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
            }
        });
    }

    private void registerUser(String username, String password, String displayName) {
        Retrofit retrofit = createRetrofit();
        WebAPI webApi = retrofit.create(WebAPI.class);
        String serverURL = preferences.getString("server", "");
        Call<ResponseModel> call = webApi.registerUser(username, password, displayName, serverURL);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (!response.isSuccessful()) {
                    Log.println(Log.ERROR,"RETRO", "Request unsuccessful" + response.code());
                }
                else {
                    login(username, password);
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
            }
        });

    }
    private void login(String username, String password) {
        Intent intent = new Intent(this, LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("username", username);
        bundle.putSerializable("password", password);
        intent.putExtra("Bundle", bundle);
        startActivity(intent);
    }

}
