package david.advanced_programming_2.ap2_chat_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
    private Button toLoginBtn;
    private Button registerBtn;
    private EditText usernameField;
    private EditText passwordField;
    private EditText nameField;
    private TextView errorTextView;
    private ImageButton optionsBtn;

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

        toLoginBtn.setOnClickListener(view -> startLoginActivity());
        registerBtn.setOnClickListener(view -> handleRegister());
        optionsBtn.setOnClickListener(view -> startOptionsActivity());

    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    private void startOptionsActivity() {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }
    private void handleRegister() {
        errorTextView.setText("");
        if (!inputIsValid()) {
            errorTextView.setText(R.string.registerValidityError);
            return;
        }
    }
    private boolean inputIsValid() {
        String password = passwordField.getText().toString();
        boolean atLeastOneChar = password.matches(".*[a-zA-Z]+.*");
        boolean atLeastOneNumber = password.matches(".*\\d.*");
        if (!atLeastOneChar || !atLeastOneNumber) {
            return false;
        }
        return true;
    }
}
