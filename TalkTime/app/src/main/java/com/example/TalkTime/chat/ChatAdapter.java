package com.example.TalkTime.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TalkTime.MemoryData;
import com.example.TalkTime.R;

import java.util.List;


// is responsible for binding the chat messages to the RecyclerView
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private final Context context;
    private final String userMobile;
    private List<ChatMessage> chatLists;

    public ChatAdapter(List<ChatMessage> chatLists, Context context) {
        this.chatLists = chatLists;
        this.context = context;
        this.userMobile = MemoryData.getData(context);
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the layout for each chat message item
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {
        ChatMessage list2 = chatLists.get(position);

        if (list2.getMobile().equals(userMobile)) {
            // If the message is from the current user
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.oppoLayout.setVisibility(View.GONE);

            holder.myMessage.setText(list2.getMessage());
            holder.myTime.setText(list2.getDate() + " " + list2.getTime());
            holder.myUsername.setText(list2.getName());
        } else {
            // If the message is from the other user
            holder.myLayout.setVisibility(View.GONE);
            holder.oppoLayout.setVisibility(View.VISIBLE);

            holder.oppoMessage.setText(list2.getMessage());
            holder.oppoTime.setText(list2.getDate() + " " + list2.getTime());
            holder.oppoUsername.setText(list2.getName());
        }
    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    public void updateChatList(List<ChatMessage> chatLists) {
        this.chatLists = chatLists;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout oppoLayout;
        private final LinearLayout myLayout;
        private final TextView oppoMessage;
        private final TextView myMessage;
        private final TextView oppoTime;
        private final TextView myTime;
        private final TextView oppoUsername;
        private final TextView myUsername;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            oppoLayout = itemView.findViewById(R.id.oppoLayout);
            myLayout = itemView.findViewById(R.id.myLayout);
            oppoMessage = itemView.findViewById(R.id.oppoMessage);
            myMessage = itemView.findViewById(R.id.myMessage);
            myTime = itemView.findViewById(R.id.myMsgTime);
            oppoTime = itemView.findViewById(R.id.oppoMsgTime);
            oppoUsername = itemView.findViewById(R.id.oppoName);
            myUsername = itemView.findViewById(R.id.myName);
        }
    }
}
