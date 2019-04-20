package com.stima.pychan;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.stima.pychan.Message.StringMatching;

public class MessageListActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private ArrayList<Message> messageList;
    private User user;
    private User pychanUser;

    private Button sendButton;
    private EditText userChatInput;

    private static ArrayList<String> dataPertanyaan;
    private static ArrayList<String> dataSynonym;
    private static ArrayList<String> dataStopWords;

    private static String namaFilePertanyaan = "raw/datapertanyaan.txt";
    private static String namaFileSynonym = "raw/datasynonym.txt";
    private static String namaFileStopWords = "raw/datastopwords.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataPertanyaan = getDataFromFile(getApplicationContext(), R.raw.datapertanyaan);
        dataStopWords = getDataFromFile(getApplicationContext(), R.raw.datastopwords);
        dataSynonym = getDataFromFile(getApplicationContext(), R.raw.datasynonym);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        user = new User("kamu");
        pychanUser = new User("Pychan");

        messageList = new ArrayList<Message>();
        messageList.add(new Message("Halo, Aku Pychan!", pychanUser));
        messageList.add(new Message("Aku adalah chatbot yang suka menjawab rasa penasaran dan keisenganmu!", pychanUser));
        messageList.add(new Message("Eh, sebelumnya, kenalan dulu, dong! Nama kamu siapa?", pychanUser));

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
                final String content = userChatInput.getText().toString();
                if (content.trim().length() > 0) {
                    if (messageList.size() > 3) {
                        messageList.add(new Message(content, user));
                        userChatInput.getText().clear();
                        mMessageAdapter.notifyDataSetChanged();
                        new Thread(new Runnable() {
                            public void run() {
                                messageList.add(new Message(StringMatching(content), new User("Pychan")));
                            }
                        }).start();
                        mMessageAdapter.notifyDataSetChanged();
                    } else {
//                        content = content.toLowerCase();
//                        //Menghapus stopwords pada input pertanyaan dengan pencarian kata stopwords menggunakan regex
//                        for(int i=0;i<getDataStopWords().size();i++){
//                            if(cekRegex(content, getDataStopWords().get(i))){
//                                content = content.replaceAll("\\W" + getDataStopWords().get(i) + "\\W", " ");  //Menghapus stopwords pada question
//                            }
//                        }
                        messageList.add(new Message(content, user));
                        userChatInput.getText().clear();
                        user.setNickname(content);
                        mMessageAdapter.notifyDataSetChanged();
                        messageList.add(new Message("Halo, " + user.getNickname() + "! Salam kenal!", pychanUser));
                        messageList.add(new Message("Kalau ada pertanyaan, langsung tanya aja, ya!", pychanUser));
                    }
                }

//                if (getDataPertanyaan().get(0) != null) {
//                    Toast toast = Toast.makeText(getApplicationContext(), getDataPertanyaan().get(0), Toast.LENGTH_LONG);
//                    toast.show();
//                } else {
//                    Toast toast = Toast.makeText(getApplicationContext(), "datapertanyaan Kosong", Toast.LENGTH_LONG);
//                    toast.show();
//                }
            }
        });
    }

    //Prosedur pembacaan file dan memindahkan ke memori agar data dari file tersimpan
    public static ArrayList<String> getDataFromFile(Context ctx, int resId){

        ArrayList<String> data = new ArrayList<String>();

//        try{
//            final File file = new File(namaFile);
//
//            if(file.exists()){
//                FileInputStream fis = new FileInputStream(file);
//                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
////            DataInputStream in = new DataInputStream(fis);
//                String temp = br.readLine();
//
//                while(temp!=null){
//                    data.add(temp);
//                    temp = br.readLine();
//                }
//            }

            InputStream inputStream = ctx.getResources().openRawResource(resId);

            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);

            String temp = null;
            StringBuilder text = new StringBuilder();
            try{
                while((temp = buffreader.readLine()) != null){
                    data.add(temp);
            }
            System.out.println("BERHASIL");
//            in.close();
        }catch(Exception e){
            System.out.println("Gagal membaca file " + resId);
//            System.exit(1);
            e.printStackTrace();
        }

        return data;
    }

    public static ArrayList<String> getDataPertanyaan(){
        return dataPertanyaan;
    }

    public static ArrayList<String> getDataSynonym(){
        return dataSynonym;
    }

    public static ArrayList<String> getDataStopWords(){
        return dataStopWords;
    }
}
