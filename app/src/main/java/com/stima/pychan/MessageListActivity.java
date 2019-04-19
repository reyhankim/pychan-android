package com.stima.pychan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import static com.stima.pychan.Message.StringMatching;

public class MessageListActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private ArrayList<Message> messageList;
    private User user;

    private Button sendButton;
    private EditText userChatInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        user = new User("TESTUSER");
        messageList = new ArrayList<Message>(2);
        messageList.add(new Message("Aku Pychan", new User("Pychan")));
        messageList.add(new Message("Halo, Pychan!", user));

        sendButton = findViewById(R.id.button_chatbox_send);
        userChatInput = findViewById(R.id.edittext_chatbox);

        // SETUP RECYCLERVIEW
        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mMessageRecycler.setLayoutManager(layoutManager);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mMessageRecycler.getContext(), layoutManager.getOrientation());
//        mMessageRecycler.addItemDecoration(dividerItemDecoration);
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setAdapter(mMessageAdapter);

        setOnClick();
    }

    private void setOnClick(){

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = userChatInput.getText().toString();
                messageList.add(new Message(content, user));
                userChatInput.getText().clear();
                mMessageAdapter.notifyDataSetChanged();
                messageList.add(new Message(StringMatching(content), new User("Pychan")));
            }
        });

    }


}
