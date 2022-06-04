package david.advanced_programming_2.ap2_chat_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddContactActivity extends AppCompatActivity {
    private AppDB db;
    private ContactDao contactDao;
    private ArrayList<ContactModel> contacts;
    private EditText contactName, contactUsername, contactServer;
    private Button addContactBtn;
    private ImageButton optionsBtn;
    private TextView errorTextView;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Bundle bundle = getIntent().getBundleExtra("Bundle");
        ArrayList<ContactModel> contactList = (ArrayList<ContactModel>) bundle.getSerializable("contactsList");
        contacts = contactList;
        contactName = findViewById(R.id.contactNameEditText);
        contactUsername = findViewById(R.id.contactUsernameEditText);
        contactServer = findViewById(R.id.contactServerEditText);
        addContactBtn = findViewById(R.id.addContactBtn);
        optionsBtn = findViewById(R.id.optionsBtn);
        errorTextView = findViewById(R.id.addContactErrorTextView);

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
        errorTextView.setText("");
        String contactNameString = contactName.getText().toString();
        String contactUsernameString = contactUsername.getText().toString();
        String contactServerString = contactServer.getText().toString();
        String currentUser =  preferences.getString("currentUser", "");
        // Check that user is not already a contact
        for (ContactModel contact: contacts) {
            if (contact.getId().equals(contactUsernameString)) {
                errorTextView.setText("* " + contact.getId() +  " is already a contact");
                return;
            }
        }
        // If contact on my server, check if exists and send invite
        // If contact not on my server, just send invite
        checkAndAddContact(contactUsernameString, contactNameString, contactServerString, currentUser);
    }
    // TODO
    private void checkAndAddContact(String contactId, String name, String server, String whose) {
        // If the contact is on my server, check that he exists
        if (isOnMyServer(server)) {
            Retrofit retrofit = createRetrofit(preferences.getString("server",""));
            WebAPI webApi = retrofit.create(WebAPI.class);
            Call<ResponseModel> call = webApi.checkUserExists(contactId);

            call.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    if (!response.isSuccessful()) {
                        Log.println(Log.ERROR,"RETRO", "Request unsuccessful" + response.code());
                    }
                    String responseString = response.body().getValue();
                    if (responseString.equals("EXISTS")) {
                        addContactInvitation(contactId, name, server ,whose);
                    }
                    else {
                        errorTextView.setText("*Contact does not exist on server");
                    }
                }
                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
                }
            });
        }
        // If contact not on my server, add him on my server and send invitation
        else {
            addContactInvitation(contactId, name, server ,whose);
        }
    }
    // Adds contact on contact server
    private void addContactInvitation(String contactId, String name, String server, String whose) {
        Retrofit retrofit = createRetrofit(server);
        WebAPI webApi = retrofit.create(WebAPI.class);
        String fullToken = "Bearer " + preferences.getString("token","");
        Call<Void> call = webApi.inviteContact(fullToken, whose, contactId, server);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.println(Log.ERROR,"RETRO", "Request unsuccessful" + response.code());
                    errorTextView.setText("Request unsuccessful");
                }
                else {
                    addContactMyServer(contactId, name, server ,whose);
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
                errorTextView.setText("*Contact server not responding");
            }
        });
    }
    // Adds contact on my server
    private void addContactMyServer(String contactId, String name, String server, String whose) {
        Retrofit retrofit = createRetrofit(preferences.getString("server",""));
        WebAPI webApi = retrofit.create(WebAPI.class);
        String fullToken = "Bearer " + preferences.getString("token","");
        Call<Void> call = webApi.addContact(fullToken, contactId, name, server);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.println(Log.ERROR,"RETRO", "Request unsuccessful" + response.code());
                    errorTextView.setText("*Request unsuccessful");
                }
                else {
                    finish();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
                errorTextView.setText("*Server not responding");
            }
        });
    }

    private boolean isOnMyServer(String server) {
        String myServer = preferences.getString("server", "");
        if (server.equals(myServer)) {
            return true;
        }
        return false;
    }
    Retrofit createRetrofit(String serverUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }


}
