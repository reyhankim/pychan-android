package com.stima.pychan;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Pattern;

import static com.stima.pychan.MessageListActivity.getDataPertanyaan;
import static com.stima.pychan.MessageListActivity.getDataSynonym;
import static com.stima.pychan.MessageListActivity.getDataStopWords;

public class Message {
    private String message;
    private User sender;
    private Date createdAt;

    public Message(String message, User sender) {
        setMessage(message);
        setSender(sender);
        setCreatedAt();
    }

    String getMessage() {
        return this.message;
    }

    User getSender() {
        return this.sender;
    }

    Date getCreatedAt() {
        return this.createdAt;
    }


    void setMessage(String message) {
        this.message = message;
    }

    void setSender(User sender) {
        this.sender = sender;
    }

    void setCreatedAt() {
        this.createdAt = Calendar.getInstance().getTime();
    }

    public static String StringMatching(String message){
        String question = message;
        ArrayList<String> dataPertanyaan = getDataPertanyaan();
        ArrayList<String> dataSynonym = getDataSynonym();
        ArrayList<String> dataStopWords = getDataStopWords();

        ArrayList<String> alternate = new ArrayList<String>();

        question = FormattingString(dataStopWords, dataSynonym, question);

        final float matchPercentage = (float) 0.9;
        float persentasekecocokan;
        String[] temp;
        boolean found = false;
        int i;
        int result;
        String out = null;

        //Pengecekan langsung satu String dengan algoritma KMP
        i=0;
        while(i<dataPertanyaan.size() && !found){
            temp = dataPertanyaan.get(i).split("\\?\\W");
            result = KMP(question, FormattingString(dataStopWords, dataSynonym, temp[0]));
            if(result==-1){ //Pertanyaan tidak ditemukan
                i++;
            }else{ //Pertanyaan ditemukan
                out = temp[1];
                found = true;
            }
        }
        
        if(!found){
            //Pengecekan per-substring dengan algoritma BM
            i=0;
            float pembilang;    //counter jumlah huruf yang match
            float pembagi;      //counter jumlah huruf dalam kalimat
            while(i<dataPertanyaan.size() && !found){
                String[] subkata = question.split(" ");
                
                temp = dataPertanyaan.get(i).split("\\?\\W");
                pembilang=0;
                pembagi = temp[0].length()-subkata.length+1;
                
                for(int j=0; j<subkata.length;j++){
                    result = BM(FormattingString(dataStopWords, dataSynonym, temp[0]),subkata[j]);
                    
                    if(result!=-1){
                        pembilang += subkata[j].length();
                    }
                }

                persentasekecocokan = pembilang/pembagi;

                if((persentasekecocokan)>=matchPercentage){    //presentase kecocokan string
                    out = temp[1];
                    found = true;
                }else{
                    if((persentasekecocokan)>=0.5){
                        alternate.add(temp[0]);
                    }
                    i++;
                }
            }
        }

        if(!found){ //Pertanyaan tidak ditemukan baik dicari dari kalimat maupun per-kata
            if(alternate.size()!=0){
                out = "Apakah yang Anda maksud : \n";

                for(int k=0;k<alternate.size();k++){
                    if(k==alternate.size()-1){
                        out = out + "- " + alternate.get(k);
                    }else{
                        out = out + "- " + alternate.get(k) + "\n";
                    }

                }

                for(String item : alternate){
                    out = out + "- " + item + "\n";
                }
            }else{
                out = "I don't understand, senpai~ Please try a different sentence. >w<";
            }
        }
        return out;
    }



    //Fungsi yang mengembalikan string hasil konversi pertanyaan dari user menjadi pernyataan untuk dilakukan pencarian pada list pertanyaan
    public static String FormattingString(ArrayList<String> stopWords, ArrayList<String> synonym, String kata){
        kata = kata.replace("\\W?\\?","");     //Menghapus tanda ? pada format pencarian
        kata = kata.toLowerCase();

        //Menghapus stopwords pada input pertanyaan dengan pencarian kata stopwords menggunakan regex
        for(int i=0;i<stopWords.size();i++){
            if(cekRegex(kata, stopWords.get(i))){
                kata = kata.replaceAll("\\W" + stopWords.get(i) + "\\W", " ");  //Menghapus stopwords pada question
            }
        }

        //Menghapus synonym pada input pertanyaan dengan pencarian kata synonym menggunakan regex
        for(int i=0;i<synonym.size();i++){
            String[] temp = synonym.get(i).split("\\=");
            if(cekRegex(kata, temp[0])){
                kata = kata.replaceAll(temp[0], temp[1]);
            }
        }

        return kata;
    }

    //Algoritma Pencocokan String Knuth-Morris-Pratt (KMP Algorithm)
    public static int KMP (String kataInput, String pattern){
        int n = kataInput.length();
        int m = pattern.length();

        int fail[] = computeFail(pattern);

        int i = 0;
        int j = 0;

        while(i<n){
            if(pattern.charAt(j) == kataInput.charAt(i)){
                if(j==m-1){
                    return i-m+1;
                }
                i++;
                j++;
            }else if(j>0){
                j = fail[j-1];
            }else{
                i++;
            }
        }

        return -1;
    }
    
    //Fungsi bagian dari Algoritma KMP
    public static int[] computeFail(String pattern){

        int fail[] = new int[pattern.length()];
        fail[0] = 0;

        int m = pattern.length();
        int i = 1;
        int j = 0;

        while(i<m){
            if(pattern.charAt(j)==pattern.charAt(i)){
                fail[i] = j+1;
                i++;
                j++;
            }else if(j>0){
                j = fail[j-1];
            }else{
                fail[i] = 0;
                i++;
            }
        }
        return fail;
    }

    //Algoritma Pencocokan String Boyer-Moore (BM Algorithm)
    public static int BM(String kataInput, String pattern){
        int last[] = buildLast(pattern);
        int n = kataInput.length();
        int m = pattern.length();
        int i = m-1;

        if(i>n-1){
            return -1;
        }else{
            int j = m-1;
            do{
                if(pattern.charAt(j)==kataInput.charAt(i)){
                    if(j==0){
                        return i;
                    }else{
                        i--;
                        j--;
                    }
                }else{
                    int lo = last[kataInput.charAt(i)];
                    i = i+m-Math.min(j, 1+lo);
                    j = m-1;
                }
            }while(i<=n-1);

            return -1;
        }
    }

    //Fungsi bagian dari Algoritma BM
    public static int[] buildLast(String pattern){

        int last[] = new int[128];

        for(int i=0;i<128;i++){
            last[i] = -1;
        }

        for(int i=0;i<pattern.length();i++){
            last[pattern.charAt(i)] = i;
        }

        return last;
    }

    public static boolean cekRegex(String kataInput, String pattern){
        boolean cek;
        String regex = "\\W?" + pattern + "\\W?";

        cek = Pattern.matches(regex, kataInput);

        return cek;
    }


}
