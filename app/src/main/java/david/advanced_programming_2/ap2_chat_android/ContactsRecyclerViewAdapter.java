package david.advanced_programming_2.ap2_chat_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<ContactModel> contacts;
    HashMap<String, ArrayList<MessageModel>> messages;
    public ContactsRecyclerViewAdapter(Context context, ArrayList<ContactModel> contacts, HashMap<String, ArrayList<MessageModel>> messages) {
        this.contacts = contacts;
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ContactsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new ContactsRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsRecyclerViewAdapter.MyViewHolder holder, int position) {
        int lastPosition = holder.getAdapterPosition();
        holder.contactName.setText(contacts.get(position).getName());
        holder.contactLastDate.setText(contacts.get(position).getLastDate());
        holder.contactLastMessage.setText(contacts.get(position).getLast());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contactId = contacts.get(lastPosition).getId();
                Intent intent = new Intent(view.getContext(), ChatScreenActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("messages", (Serializable) messages.get(contactId));
                intent.putExtra("Bundle", bundle);
                context.startActivity(intent);
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
}
