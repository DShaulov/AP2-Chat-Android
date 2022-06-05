package david.advanced_programming_2.ap2_chat_android;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MESSAGE_TO = 1;
    private static final int MESSAGE_FROM = 2;

    Context context;
    List<MessageModel> messages;
    public MessagesRecyclerViewAdapter(Context context, List<MessageModel> messages) {
        this.messages = messages;
        this.context = context;
    }
    public void updateMessagesList(List<MessageModel> updatedMessages) {
        this.messages.clear();
        this.messages = updatedMessages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MESSAGE_TO) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_to, parent, false);
            return new MessageToHolder(view);
        } else if (viewType == MESSAGE_FROM) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_from, parent, false);
            return new MessageFromHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message =  messages.get(position);
        if (message.isSent()) {
            return MESSAGE_TO;
        } else {
            return MESSAGE_FROM;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageModel message = messages.get(position);
        int viewType = holder.getItemViewType();
        if (viewType == MESSAGE_TO) {
            ((MessageToHolder) holder).bind(message);
        }
        else {
            ((MessageFromHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private class MessageToHolder extends RecyclerView.ViewHolder {
        TextView messageView, timeView;

        MessageToHolder(View itemView) {
            super(itemView);

            messageView = (TextView) itemView.findViewById(R.id.messageTo);
            timeView = (TextView) itemView.findViewById(R.id.messageTimeStampTo);
        }

        void bind(MessageModel message) {
            messageView.setText(message.getContent());
            timeView.setText(message.getCreated());
        }
    }

    private class MessageFromHolder extends RecyclerView.ViewHolder {
        TextView messageView, timeView;

        MessageFromHolder(View itemView) {
            super(itemView);
            messageView = itemView.findViewById(R.id.messageFrom);
            timeView =  itemView.findViewById(R.id.messageTomeStampFrom);
        }

        void bind(MessageModel message) {
            messageView.setText(message.getContent());
            timeView.setText(message.getCreated());
        }
    }
}
