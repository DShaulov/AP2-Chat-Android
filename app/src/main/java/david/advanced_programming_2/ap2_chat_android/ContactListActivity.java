package david.advanced_programming_2.ap2_chat_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactListActivity extends AppCompatActivity {
    ArrayList<ContactModel> contacts;
    HashMap<String, ArrayList<MessageModel>> messages;
    private FloatingActionButton floatingAddContactBtn;
    private ImageButton optionsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Bundle bundle = getIntent().getBundleExtra("Bundle");
        ArrayList<ContactModel> contactList = (ArrayList<ContactModel>) bundle.getSerializable("contactsList");
        HashMap<String, ArrayList<MessageModel>> allMessages = (HashMap<String, ArrayList<MessageModel>>) bundle.getSerializable("allMessages");
        contacts = contactList;
        messages = allMessages;
        optionsBtn = findViewById(R.id.optionsBtn);
        floatingAddContactBtn = findViewById(R.id.floatingAddContactBtn);

        optionsBtn.setOnClickListener(view -> startOptionsActivity());
        floatingAddContactBtn.setOnClickListener(view -> startAddContactActivity());

        RecyclerView recyclerView = findViewById(R.id.contactsRecyclerView);
        ContactsRecyclerViewAdapter adapter = new ContactsRecyclerViewAdapter(this, contacts, messages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void startOptionsActivity() {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }
    private void startAddContactActivity() {
        Intent intent = new Intent(this, AddContactActivity.class);
        startActivity(intent);
    }


}
