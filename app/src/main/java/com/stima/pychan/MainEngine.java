//Nama File : Engine.java
//Nama Kelompok : Py-Chan
//Topik : Tugas Besar 3 Strategi Algoritma

import java.util.*;
import java.util.regex.Pattern;
import java.io.*;

public class MainEngine{
    
    public static void main(String[] args){
        String namaFilePertanyaan = "DataPertanyaan.txt";
        String namaFileSynonym = "DataSynonym.txt";
        String namaFileStopWords = "DataStopWords.txt";
        
        ArrayList<String> dataPertanyaan = new ArrayList<String>();
        ArrayList<String> dataSynonym = new ArrayList<String>();
        ArrayList<String> dataStopWords = new ArrayList<String>();
        
        getDataFromFile(namaFilePertanyaan, dataPertanyaan);    //Mengambil data pertanyaan beserta jawaban
        getDataFromFile(namaFileSynonym, dataSynonym);          //Mengambil data synonym
        getDataFromFile(namaFileStopWords, dataStopWords);      //Mengambil data stopwords
        
        Scanner input = new Scanner(System.in); //input user
        final float kecocokan = (float) 0.9;
        
        //Intro kalimat chatbot
        System.out.println("CHATBOT PY-CHAN");
        System.out.println("BOT : Halo, Apakah ada yang bisa saya bantu ?");
        System.out.print("You : ");
        
        String question = null;
        question = input.nextLine();
        question = FormattingQuestion(dataStopWords, dataSynonym, question);
        
        while(!question.equals("exit")){
            String[] temp;
            boolean found = false;
            int i;
            int result;
            
            //Pengecekan langsung satu String dengan algoritma KMP
            i=0;
            while(i<dataPertanyaan.size() && !found){
                temp = dataPertanyaan.get(i).split("\\?");
                result = KMP(temp[0].toLowerCase(),question);
                if(result==-1){ //Pertanyaan tidak ditemukan
                    i++;
                }else{ //Pertanyaan ditemukan
                    System.out.println("BOT :" + temp[1]);
                    found = true;
                }
            }
            
            if(!found){
                //Pengecekan per-substring dengan algoritma BM
                i=0;
                float pembilang;
                float pembagi;
                while(i<dataPertanyaan.size() && !found){
                    String[] subkata = question.split(" ");
                    
                    temp = dataPertanyaan.get(i).split("\\?");
                    pembilang=0;
                    pembagi = temp[0].length()-subkata.length+1;
                    // System.out.println(temp[0] + " " + subkata.length);
                    
                    for(int j=0; j<subkata.length;j++){
                        result = BM(temp[0].toLowerCase(),subkata[j]);
                        
                        if(result!=-1){
                            pembilang += subkata[j].length();
                        }
                    }
                    
                    if((pembilang/pembagi)>kecocokan){    //presentase kecocokan string
                        System.out.println("BOT :" + temp[1]);
                        found = true;
                    }else{
                        i++;
                    }
                }
            }
            if(!found){ //Pertanyaan tidak ditemukan baik dicari dari kalimat maupun per-kata
                System.out.println("BOT : Maaf aku tidak mengerti apa maksud kamu");
            }
            
            System.out.print("You : ");
            question = input.nextLine();
            question = FormattingQuestion(dataStopWords, dataSynonym, question);
        } //input == "exit"
        System.out.println("Good bye, see you later.");     //Penutup        
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

        for(int i=0;i<stopWords.size();i++){
            if(cekRegex(question, stopWords.get(i))){
                question = question.replaceAll(" " + stopWords.get(i) + " ", " ");  //Menghapus stopwords pada question
            }
        }

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