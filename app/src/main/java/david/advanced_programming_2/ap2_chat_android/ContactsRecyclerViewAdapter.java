package david.advanced_programming_2.ap2_chat_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.MyViewHolder> {
    Context context;
    private AppDB db;
    private MessageDao messageDao;
    List<ContactModel> contacts;
    HashMap<String, ArrayList<MessageModel>> messages;
    SharedPreferences preferences;
    public ContactsRecyclerViewAdapter(Context context, ArrayList<ContactModel> contacts,
                                       HashMap<String, ArrayList<MessageModel>> messages, SharedPreferences preferences,
                                       AppDB db, MessageDao messageDao) {
        this.contacts = contacts;
        this.context = context;
        this.messages = messages;
        this.preferences = preferences;
        this.db = db;
        this.messageDao = messageDao;
    }
    @NonNull
    @Override
    public ContactsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new ContactsRecyclerViewAdapter.MyViewHolder(view);
    }

    public void updateContactList(List<ContactModel> updatedContacts) {
        contacts.clear();
        contacts = updatedContacts;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsRecyclerViewAdapter.MyViewHolder holder, int position) {
        int lastPosition = holder.getAdapterPosition();
        holder.contactName.setText(contacts.get(position).getName());
        holder.contactLastDate.setText(contacts.get(position).getLastdate().replace("T", " "));
        holder.contactLastMessage.setText(contacts.get(position).getLast());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contactId = contacts.get(lastPosition).getId();
                String contactServer = contacts.get(lastPosition).getServer();
                String contactName = contacts.get(lastPosition).getName();
                fetchMessages(contactId, contactServer, contactName, view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView contactImage;
        TextView  contactName, contactLastMessage, contactLastDate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            contactImage = itemView.findViewById(R.id.contactImageViewRecycler);
            contactLastMessage = itemView.findViewById(R.id.contactLastMessageView);
            contactLastDate = itemView.findViewById(R.id.contactLastDateView);
            contactName = itemView.findViewById(R.id.contactNameView);
        }
    }

    private void fetchMessages(String contactId, String contactServer, String contactName, View view) {
        Retrofit retrofit = createRetrofit();
        WebAPI webApi = retrofit.create(WebAPI.class);
        String fullToken = "Bearer " + preferences.getString("token","");
        Call<List<MessageModel>> call = webApi.getMessages(fullToken, contactId);

        call.enqueue(new Callback<List<MessageModel>>() {
            @Override
            public void onResponse(Call<List<MessageModel>> call, Response<List<MessageModel>> response) {
                if (!response.isSuccessful()) {
                    Log.println(Log.ERROR,"RETRO", "Request unsuccessful" + response.code());
                }
                List<MessageModel> allMessages = response.body();
                // Insert messages into database
                for (MessageModel messageModel : allMessages) {
                    messageDao.insert(messageModel);
                }
                Intent intent = new Intent(view.getContext(), ChatScreenActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("messages", (Serializable) allMessages);
                bundle.putString("contactId", contactId);
                bundle.putString("contactServer", contactServer);
                bundle.putString("contactName", contactName);
                intent.putExtra("Bundle", bundle);
                context.startActivity(intent);
            }

            @Override
            public void onFailure(Call<List<MessageModel>> call, Throwable t) {
                Log.println(Log.ERROR,"RETRO", "Request Failed: " + t.getMessage());
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
