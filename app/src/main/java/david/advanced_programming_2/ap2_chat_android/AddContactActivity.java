package david.advanced_programming_2.ap2_chat_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class AddContactActivity extends AppCompatActivity {
    private AppDB db;
    private ContactDao contactDao;
    private EditText contactName, contactUsername, contactServer;
    private Button addContactBtn;
    private ImageButton optionsBtn;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        contactName = findViewById(R.id.contactNameEditText);
        contactUsername = findViewById(R.id.contactUsernameEditText);
        contactServer = findViewById(R.id.contactServerEditText);
        addContactBtn = findViewById(R.id.addContactBtn);
        optionsBtn = findViewById(R.id.optionsBtn);

        db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "ChatDB")
                .allowMainThreadQueries()
                .build();
        contactDao = db.contactDao();

        preferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        optionsBtn.setOnClickListener(view -> startOptionsActivity());
        addContactBtn.setOnClickListener(view -> handleAddContact());
    }
    private void startOptionsActivity() {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }
    // TODO
    private void handleAddContact() {
        String contactNameString = contactName.getText().toString();
        String contactUsernameString = contactUsername.getText().toString();
        String contactServerString = contactServer.getText().toString();
        String currentUser =  preferences.getString("currentUser", "");

        ContactModel newContact = new ContactModel(contactUsernameString, contactNameString, contactServerString,
        "", "", currentUser);

        contactDao.insert(newContact);

        // TODO
        if (isOnMyServer(contactServerString)) {
            if (checkContactExists(contactUsernameString)) {

            }
            else {

            }
        }
        else {

        }



    }
    // TODO
    private boolean checkContactExists(String contactUsername) {
        return true;
    }
    // TODO
    private boolean isOnMyServer(String server) {
        return true;
    }


}
