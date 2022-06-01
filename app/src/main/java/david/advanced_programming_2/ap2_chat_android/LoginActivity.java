package david.advanced_programming_2.ap2_chat_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
    SharedPreferences preferences;

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
        token = "";
        contacts = new ArrayList<>();
        messages = new HashMap<String, ArrayList<MessageModel>>();

        preferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currentUser", "");
        editor.putString("token", "");
        editor.putString("server", "https://localhost:7201");
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