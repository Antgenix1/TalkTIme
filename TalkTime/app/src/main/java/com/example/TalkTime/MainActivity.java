package com.example.TalkTime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TalkTime.chat.CreateNewChat;
import com.example.TalkTime.messages.MessagesAdapter;
import com.example.TalkTime.messages.MessagesList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// TODO possible improvement: swipe to delete a chat
public class MainActivity extends AppCompatActivity {
    public static final String FIREBASE_URL = "https://chat-app-test-17377-default-rtdb.europe-west1.firebasedatabase.app/";

    private final List<MessagesList> messagesLists = new ArrayList<>();
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(FIREBASE_URL);
    private String mobile;
    private String name;
    private String email;
    private int unseenMessages = 0;
    private String lastMessage = "";
    private String chatKey = "";
    private boolean dataSet = false;
    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView addBtn = findViewById(R.id.add_btn);

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        //get Intent Data from Register.class activity
        mobile = getIntent().getStringExtra("mobile");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");

        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set adapter to recyclerview
        messagesAdapter = new MessagesAdapter(messagesLists, MainActivity.this);
        messagesRecyclerView.setAdapter(messagesAdapter);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.dismiss();

        // get Data from realtime Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesLists.clear();
                unseenMessages = 0;
                lastMessage = "";
                chatKey = "";

                for (DataSnapshot dataSnapshot : snapshot.child("users").getChildren()) {

                    final String mobileNumber = dataSnapshot.getKey();

                    dataSet = false;

                    if (!mobileNumber.equals(mobile)) {
                        final String name = dataSnapshot.child("name").getValue(String.class);

                        // Access the chat data
                        databaseReference.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {

                            // callback if the data in chat changes
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                int getChatCounts = (int) snapshot.getChildrenCount();

                                if (getChatCounts > 0) {

                                    //loop iterates through chat records
                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {


                                        final String getKey = dataSnapshot1.getKey();
                                        chatKey = getKey;

                                        // checks if the chat has the required children
                                        if (dataSnapshot1.hasChild("user_1") && dataSnapshot1.hasChild("user_2") && dataSnapshot1.hasChild("messages")) {
                                            final String getUserOne = dataSnapshot1.child("user_1").getValue(String.class);
                                            final String getUserTwo = dataSnapshot1.child("user_2").getValue(String.class);

                                            //checks if the current user is involved in the chat
                                            if (getUserOne.equals(mobileNumber) && getUserTwo.equals(mobile) || getUserOne.equals(mobile) && getUserTwo.equals(mobileNumber)) {
                                                for (DataSnapshot chatDataSnapshot : dataSnapshot1.child("messages").getChildren()) {

                                                    // retrieves key of the message and stores the message
                                                    final long getMessageKey = Long.parseLong(chatDataSnapshot.getKey());
                                                    final long getLastSeenMessage = Long.parseLong(MemoryData.getLastMsgTS(MainActivity.this, getKey));

                                                    // show the your last message on the home screen
                                                    lastMessage = chatDataSnapshot.child("msg").getValue(String.class);
                                                    if (getMessageKey > getLastSeenMessage) {
                                                        unseenMessages++;
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }

                                if (!dataSet) {
                                    dataSet = true;
                                    MessagesList messagesList = new MessagesList(name, mobileNumber, lastMessage, unseenMessages, chatKey);
                                    messagesLists.add(messagesList);
                                    messagesAdapter.updateData(messagesLists);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateNewChat.class);

                startActivity(intent);
            }
        });
        // TODO optional logout btn
    }
}