package david.advanced_programming_2.ap2_chat_android;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    private Button toLoginBtn;
    private Button registerBtn;
    private Button uploadImageBtn;
    private EditText usernameField;
    private EditText passwordField;
    private EditText passwordConfirmField;
    private EditText nameField;
    private TextView errorTextView;
    private ImageButton optionsBtn;
    private SharedPreferences preferences;
    private String firebaseToken;
    private ProgressBar progressBar;
    private Bitmap profileImageBitmap;
    private int SELECT_PICTURE_CODE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toLoginBtn = (Button) findViewById(R.id.toLoginBtn);
        registerBtn = (Button) findViewById(R.id.registerScreenRegisterBtn);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);
        optionsBtn = findViewById(R.id.optionsBtn);
        usernameField = findViewById(R.id.registerUsernameField);
        passwordField = findViewById(R.id.registerPasswordField);
        passwordConfirmField = findViewById(R.id.confirmPasswordEditText);
        nameField = findViewById(R.id.registerNameField);
        errorTextView = findViewById(R.id.registerErrorTextView);
        preferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        progressBar = findViewById(R.id.registerProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        profileImageBitmap = null;
        SELECT_PICTURE_CODE = 200;

        toLoginBtn.setOnClickListener(view -> startLoginActivity());
        registerBtn.setOnClickListener(view -> handleRegister());
        optionsBtn.setOnClickListener(view -> startOptionsActivity());
        uploadImageBtn.setOnClickListener(view -> chooseImage());

        // Get firebase token for push notifications
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(RegisterActivity.this, instanceIdResult -> {
            firebaseToken = instanceIdResult.getToken();
        });
    }
    private void chooseImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchActivity.launch(i);
    }

    ActivityResultLauncher<Intent> launchActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                Bitmap selectedImageBitmap;
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    uploadImageBtn.setBackgroundColor(getResources().getColor(R.color.green));
                    profileImageBitmap = selectedImageBitmap;
                    int a = 5;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    });


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
        String confirmedPassword = passwordConfirmField.getText().toString();
        if (!password.equals(confirmedPassword)) {
            errorTextView.setText("*Passwords do not match");
            return;
        }
        errorTextView.setText("");
        if (!inputIsValid(password)) {
            errorTextView.setText(R.string.registerValidityError);
            return;
        }
        if (username.equals("") || displayName.equals("")) {
            errorTextView.setText("*Fields cannot be empty");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
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
                    progressBar.setVisibility(View.INVISIBLE);
                }
                String responseString = response.body().getValue();
                if (responseString.equals("EXISTS")) {
                    errorTextView.setText("*Username already taken");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }
                // Register the user with the API
                registerUser(username, password, displayName);
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
                errorTextView.setText("*Server not responding");
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void registerUser(String username, String password, String displayName) {
        Retrofit retrofit = createRetrofit();
        WebAPI webApi = retrofit.create(WebAPI.class);
        String serverURL = preferences.getString("server", "");
        Call<ResponseModel> call = webApi.registerUser(username, password, displayName, serverURL, firebaseToken);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (!response.isSuccessful()) {
                    Log.println(Log.ERROR,"RETRO", "Request unsuccessful" + response.code());
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    progressBar.setVisibility(View.INVISIBLE);
                    login(username, password);
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
                progressBar.setVisibility(View.INVISIBLE);
                errorTextView.setText("*Server not responding");
            }
        });

    }
    private void login(String username, String password) {
        Intent intent = new Intent(this, LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("username", username);
        bundle.putSerializable("password", password);
        bundle.putSerializable("firebaseToken", firebaseToken);
        intent.putExtra("Bundle", bundle);
        startActivity(intent);
    }

}
