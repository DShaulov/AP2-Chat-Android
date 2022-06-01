package david.advanced_programming_2.ap2_chat_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class AddContactActivity extends AppCompatActivity {
    private EditText contactName, contactUsername, contactServer;
    private Button addContactBtn;
    private ImageButton optionsBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        contactName = findViewById(R.id.contactNameEditText);
        contactUsername = findViewById(R.id.contactUsernameEditText);
        contactServer = findViewById(R.id.contactServerEditText);
        addContactBtn = findViewById(R.id.addContactBtn);
        optionsBtn = findViewById(R.id.optionsBtn);

        optionsBtn.setOnClickListener(view -> startOptionsActivity());
    }
    private void startOptionsActivity() {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }
}
