package com.stima.pychan;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Message {
    private String message;
    private User sender;
    private long createdAt;
    private final float matchPercentage;
    private ArrayList<String> dataPertanyaan;
    private ArrayList<String> dataSynonym;
    private ArrayList<String> dataStopWords;
    private final String namaFilePertanyaan = "DataPertanyaan.txt";
    private final String namaFileSynonym = "DataSynonym.txt";
    private final String namaFileStopWords = "DataStopWords.txt";

    public Message(String message, User sender, long createdAt) {
        setMessage(message);
        setSender(sender);
        setCreatedAt(createdAt);
        this.matchPercentage = (float) 0.9;
        this.dataPertanyaan = new ArrayList<String>();
        this.dataSynonym = new ArrayList<String>();
        this.dataStopWords = new ArrayList<String>();

        getDataFromFile(namaFilePertanyaan, this.dataPertanyaan);    //Mengambil data pertanyaan beserta jawaban
        getDataFromFile(namaFileSynonym, this.dataSynonym);          //Mengambil data synonym
        getDataFromFile(namaFileStopWords, this.dataStopWords);      //Mengambil data stopwords
        
    }

    public float getPercentage(){
        return this.matchPercentage;
    }

    public String getMessage() {
        return this.message;
    }

    public User getSender() {
        return this.sender;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public ArrayList<String> getDataPertanyaan(){
        return this.dataPertanyaan;
    }

    public ArrayList<String> getDataSynonym(){
        return this.dataSynonym;
    }

    public ArrayList<String> getDataStopWords(){
        return this.dataStopWords;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String StringMatching(String message){
        String question = message;
        question = FormattingQuestion(this.dataStopWords, this.dataSynonym, question);

        String[] temp;
        boolean found = false;
        int i;
        int result;
        String out;

        //Pengecekan langsung satu String dengan algoritma KMP
        i=0;
        while(i<this.dataPertanyaan.size() && !found){
            temp = this.dataPertanyaan.get(i).split("\\?");
            result = KMP(temp[0].toLowerCase(),question);
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
                
                temp = this.dataPertanyaan.get(i).split("\\?");
                pembilang=0;
                pembagi = temp[0].length()-subkata.length+1;
                
                for(int j=0; j<subkata.length;j++){
                    result = BM(temp[0].toLowerCase(),subkata[j]);
                    
                    if(result!=-1){
                        pembilang += subkata[j].length();
                    }
                }
                
                if((pembilang/pembagi)>=this.matchPercentage){    //presentase kecocokan string
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

    //Prosedur pembacaan file dan memindahkan ke memori agar data dari file tersimpan
    public static void getDataFromFile(String namaFile, ArrayList<String> data){

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
            
    }

    //Fungsi yang mengembalikan string hasil konversi pertanyaan dari user menjadi pernyataan untuk dilakukan pencarian pada list pertanyaan
    public static String FormattingQuestion(ArrayList<String> stopWords, ArrayList<String> synonym, String question){
        question = question.replace(" ?",""); //Penanganan kasus pertanyaan dengan tanda spasi sebelum ?
        question = question.replace("?", ""); //Penanganan kasus pertanyaan dengan tanpa tanda spasi sebelum ?
        question = question.toLowerCase();    //Menghapus tanda ? pada format pencarian

        //Menghapus stopwords pada input pertanyaan dengan pencarian kata stopwords menggunakan regex
        for(int i=0;i<stopWords.size();i++){
            if(cekRegex(question, stopWords.get(i))){
                question = question.replaceAll(" " + stopWords.get(i) + " ", " ");  //Menghapus stopwords pada question
            }
        }

        //Menghapus synonym pada input pertanyaan dengan pencarian kata synonym menggunakan regex
        for(int i=0;i<synonym.size();i++){
            String[] temp = synonym.get(i).split("\\=");
            if(cekRegex(question, temp[0])){
                question = question.replaceAll(temp[0], temp[1]);
            }
        }

        return question;
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
        String regex = ".*" + pattern + ".*";

        cek = Pattern.matches(regex, kataInput);

        return cek;
    }
}
