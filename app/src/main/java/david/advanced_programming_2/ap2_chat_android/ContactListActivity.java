package david.advanced_programming_2.ap2_chat_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContactListActivity extends AppCompatActivity {
    ArrayList<ContactModel> contacts;
    private AppDB db;
    private MessageDao messageDao;
    HashMap<String, ArrayList<MessageModel>> messages;
    private ContactsRecyclerViewAdapter adapter;
    SharedPreferences preferences;
    private FloatingActionButton floatingAddContactBtn;
    private ImageButton optionsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "ChatDB")
                .allowMainThreadQueries()
                .build();
        messageDao = db.messageDao();

        Bundle bundle = getIntent().getBundleExtra("Bundle");
        ArrayList<ContactModel> contactList = (ArrayList<ContactModel>) bundle.getSerializable("contactsList");
        HashMap<String, ArrayList<MessageModel>> allMessages = (HashMap<String, ArrayList<MessageModel>>) bundle.getSerializable("allMessages");
        contacts = contactList;
        messages = allMessages;
        optionsBtn = findViewById(R.id.optionsBtn);
        floatingAddContactBtn = findViewById(R.id.floatingAddContactBtn);
        preferences = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);


        optionsBtn.setOnClickListener(view -> startOptionsActivity());
        floatingAddContactBtn.setOnClickListener(view -> startAddContactActivity());

        RecyclerView recyclerView = findViewById(R.id.contactsRecyclerView);
        adapter = new ContactsRecyclerViewAdapter(this, contacts, messages, preferences, db, messageDao);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchContacts();
    }

    private void fetchContacts() {
        Retrofit retrofit = createRetrofit(preferences.getString("server",""));
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
                adapter.updateContactList(allContacts);
            }
            @Override
            public void onFailure(Call<List<ContactModel>> call, Throwable t) {
                Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
            }
        });
    }

    private void startOptionsActivity() {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }
    private void startAddContactActivity() {
        Intent intent = new Intent(this, AddContactActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contactsList", (Serializable) contacts);
        intent.putExtra("Bundle", bundle);
        startActivity(intent);
    }

    private Retrofit createRetrofit(String serverUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
