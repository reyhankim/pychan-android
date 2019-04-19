package com.stima.pychan;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MessageListActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private ArrayList<Message> messageList;

    private final String namaFilePertanyaan = "DataPertanyaan.txt";
    private final String namaFileSynonym = "DataSynonym.txt";
    private final String namaFileStopWords = "DataStopWords.txt";
    private ArrayList<String> dataPertanyaan = getDataFromFile(namaFilePertanyaan);
    private ArrayList<String> dataSynonym = getDataFromFile(namaFileSynonym);
    private ArrayList<String> dataStopWords = getDataFromFile(namaFileStopWords);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    //Prosedur pembacaan file dan memindahkan ke memori agar data dari file tersimpan
    public static ArrayList<String> getDataFromFile(String namaFile){

        ArrayList<String> data = new ArrayList<String>();

        try{
            BufferedReader br = new BufferedReader(new FileReader(namaFile));
            String temp = null;
            temp = br.readLine();

            while(temp!=null){
                data.add(temp);
                temp = br.readLine();
            }

        }catch(FileNotFoundException e){
            System.out.println("Tidak dapat membuka file " + namaFile);
            System.exit(1);
        }catch(IOException e){
            System.out.println("Gagal membaca file " + namaFile);
            System.exit(1);
        }

        return data;
    }
}
