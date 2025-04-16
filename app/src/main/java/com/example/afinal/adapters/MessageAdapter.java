package com.example.afinal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.R;
import com.example.afinal.models.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private List<Message> messages;
    
    public MessageAdapter() {
        this.messages = new ArrayList<>();
    }
    
    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }
    
    public void removeMessage(Message message) {
        int position = messages.indexOf(message);
        if (position >= 0) {
            messages.remove(position);
            notifyItemRemoved(position);
        }
    }
    
    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Message.TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_assistant_message, parent, false);
            return new AssistantMessageViewHolder(view);
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        
        if (holder instanceof UserMessageViewHolder) {
            UserMessageViewHolder userHolder = (UserMessageViewHolder) holder;
            userHolder.messageText.setText(message.getContent());
            userHolder.timeText.setText(message.getTimestamp());
        } else if (holder instanceof AssistantMessageViewHolder) {
            AssistantMessageViewHolder assistantHolder = (AssistantMessageViewHolder) holder;
            assistantHolder.messageText.setText(message.getContent());
            assistantHolder.timeText.setText(message.getTimestamp());
        }
    }
    
    @Override
    public int getItemCount() {
        return messages.size();
    }
    
    // ViewHolder for user messages
    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        
        UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.timeText);
        }
    }
    
    // ViewHolder for assistant messages
    static class AssistantMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, sourceText;
        
        AssistantMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.timeText);
            sourceText = itemView.findViewById(R.id.sourceText);
        }
    }
} 