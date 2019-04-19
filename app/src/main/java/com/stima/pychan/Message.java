package com.stima.pychan;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Message {
    private String message;
    private User sender;
    private Date createdAt;

    private static ArrayList<String> dataPertanyaan;
    private static ArrayList<String> dataSynonym;
    private static ArrayList<String> dataStopWords;

    static {
        String namaFilePertanyaan = "DataPertanyaan.txt";
        String namaFileSynonym = "DataSynonym.txt";
        String namaFileStopWords = "DataStopWords.txt";

        dataPertanyaan = getDataFromFile(namaFilePertanyaan);
        dataStopWords = getDataFromFile(namaFileStopWords);
        dataPertanyaan = getDataFromFile(namaFilePertanyaan);
    }

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

    public static ArrayList<String> getDataPertanyaan(){
        return dataPertanyaan;
    }

    public static ArrayList<String> getDataSynonym(){
        return dataSynonym;
    }

    public static ArrayList<String> getDataStopWords(){
        return dataStopWords;
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
        question = FormattingString(dataStopWords, dataSynonym, question);

        final float matchPercentage = (float) 0.9;
        String[] temp;
        boolean found = false;
        int i;
        int result;
        String out = null;

        //Pengecekan langsung satu String dengan algoritma KMP
        i=0;
        while(i<dataPertanyaan.size() && !found){
            temp = dataPertanyaan.get(i).split("\\?");
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
                
                temp = dataPertanyaan.get(i).split("\\?");
                pembilang=0;
                pembagi = temp[0].length()-subkata.length+1;
                
                for(int j=0; j<subkata.length;j++){
                    result = BM(FormattingString(dataStopWords, dataSynonym, temp[0]),subkata[j]);
                    
                    if(result!=-1){
                        pembilang += subkata[j].length();
                    }
                }
                
                if((pembilang/pembagi)>=matchPercentage){    //presentase kecocokan string
                    out = temp[1];
                    found = true;
                }else{
                    i++;
                }
            }
        }

        if(!found){ //Pertanyaan tidak ditemukan baik dicari dari kalimat maupun per-kata
            out = "I don't understand, senpai~ Please try a different sentence. >w<";
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

    //Prosedur pembacaan file dan memindahkan ke memori agar data dari file tersimpan
    public static ArrayList<String> getDataFromFile(String namaFile){

        ArrayList<String> data = new ArrayList<String>();

        try{
            FileInputStream fis = new FileInputStream(namaFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String temp = null;
            temp = br.readLine();

            while(temp!=null){
                data.add(temp);
                temp = br.readLine();
            }

            in.close();
        }catch(IOException e){
            // System.out.println("Gagal membaca file " + namaFile);
//            System.exit(1);
            e.printStackTrace();
        }

        return data;
    }
}
