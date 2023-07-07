package com.example.TalkTime.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TalkTime.MemoryData;
import com.example.TalkTime.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatWindowActivity extends AppCompatActivity {

    private final List<ChatMessage> chatMessages = new ArrayList<>();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://chat-app-test-17377-default-rtdb.europe-west1.firebasedatabase.app/");
    String userMobile = "";
    private String chatId;
    private RecyclerView chattingRecyclerView;
    private ChatAdapter chatAdapter;
    private boolean loadingFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Views initialization
        final ImageView backBtn = findViewById(R.id.back_btn);
        final TextView nameTV = findViewById(R.id.name);
        final EditText messageEditText = findViewById(R.id.messageEditTxt);
        final ImageView sendBtn = findViewById(R.id.sendBtn);

        chattingRecyclerView = findViewById(R.id.chattingRecyclerView);

        //get data from message adapter class
        final String name = getIntent().getStringExtra("name");
        chatId = getIntent().getStringExtra("chat_key");
        final String mobile = getIntent().getStringExtra("mobile");

        //get user mobile from memory
        userMobile = MemoryData.getData(ChatWindowActivity.this);

        nameTV.setText(name);

        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(ChatWindowActivity.this));

        chatAdapter = new ChatAdapter(chatMessages, ChatWindowActivity.this);
        chattingRecyclerView.setAdapter(chatAdapter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild("chat")) {

                    if (snapshot.child("chat").child(chatId).hasChild("messages")) {

                        chatMessages.clear();

                        for (DataSnapshot messagesSnapshot : snapshot.child("chat").child(chatId).child("messages").getChildren()) {

                            if (messagesSnapshot.hasChild("msg") && messagesSnapshot.hasChild("mobile")) {
                                final String messagesTimestamp = messagesSnapshot.getKey(); //timestamp is also used as key/id for messages
                                final String mobile = messagesSnapshot.child("mobile").getValue(String.class);
                                final String msg = messagesSnapshot.child("msg").getValue(String.class);

                                long timestampMillis = Long.parseLong(messagesTimestamp);
                                Timestamp timestamp = new Timestamp(timestampMillis);
                                Date date = new Date(timestamp.getTime());
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
                                ChatMessage chatList = new ChatMessage(mobile, name, msg, simpleDateFormat.format(date), simpleTimeFormat.format(date));
                                chatMessages.add(chatList);

                                if (loadingFirstTime || Long.parseLong(messagesTimestamp) > Long.parseLong(MemoryData.getLastMsgTS(ChatWindowActivity.this, chatId))) {
                                    loadingFirstTime = false;
                                    MemoryData.saveLastMsgTS(messagesTimestamp, chatId, ChatWindowActivity.this);

                                    chatAdapter.updateChatList(chatMessages);

                                    chattingRecyclerView.scrollToPosition(chatMessages.size() - 1);
                                }
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //TODO add validation with toaster

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String txtMessage = messageEditText.getText().toString();

                //get current timestamps, to use as message id
                final String currentTimestamp = String.valueOf(System.currentTimeMillis());

                if (txtMessage.isEmpty()) {
                    Toast.makeText(ChatWindowActivity.this, "All Fields Required!!!", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.child("chat").child(chatId).child("user_1").setValue(userMobile);
                    databaseReference.child("chat").child(chatId).child("user_2").setValue(mobile);
                    databaseReference.child("chat").child(chatId).child("messages").child(currentTimestamp).child("msg").setValue(txtMessage);
                    databaseReference.child("chat").child(chatId).child("messages").child(currentTimestamp).child("mobile").setValue(userMobile);

                    //clear edit text
                    messageEditText.setText("");
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}