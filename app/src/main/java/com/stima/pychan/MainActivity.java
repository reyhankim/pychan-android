package com.stima.pychan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    String username = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeToMessageListActivity();
    }

    private void changeToMessageListActivity(){
        Intent nextActivity = new Intent(this, MessageListActivity.class);
        nextActivity.putExtra("dataPertanyaan", username);
        startActivity(nextActivity);
    }
}
