
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


public class NGram {

    public static List<String> ngrams(int max, String val) {
        List<String> out = new ArrayList<String>(1000);
        String[] words = val.split(" ");
        for (int i = 0; i < words.length - max + 1; i++) {
            out.add(makeString(words, i,  max));
        }
        return out;
    }

    public static String makeString(String[] words, int start, int length) {
        StringBuilder tmp= new StringBuilder(100);
        for (int i = start; i < start + length; i++) {
            tmp.append(words[i]).append(" ");
        }
        return tmp.substring(0, tmp.length() - 1);
    }

    public static List<String> reduceNgrams(List<String> in, int size) {
        if (1 < size) {
            List<String> working = reduceByOne(in);
            in.addAll(working);
            for (int i = size -2 ; i > 0; i--) {
                working = reduceByOne(working);
                in.addAll(working);
            }
        }
        return in;
    }

    public static List<String> reduceByOne(List<String> in) {
        List<String> out = new ArrayList<String>(in.size());
        int end;
        for (String s : in) {
            end = s.lastIndexOf(" ");
            out.add(s.substring(0, -1 == end ? s.length() : end));  
        }
        //the last one will always reduce twice - words 0, n-1 are in the loop this catches the words 1, n
        String s = in.get(in.size() -1);
        out.add(s.substring(s.indexOf(" ")+1));
        return out;
    }

    public static void main(String[] args) {
        long start;
        start = System.currentTimeMillis();
        List<String> ngrams = ngrams(3, "Your text goes here, actual mileage may vary");
        reduceNgrams(ngrams, 3);
        List<String> grams = ngrams(3,"‏الدوري الإيطالي لاتسيو يتغلب ضيفه انتر ميلان بهدفين نظيفين الأسبوع المسابقة");
        for(String s: grams){
        	System.out.println(System.currentTimeMillis() - start);
        	System.out.println(s);
        }
    }
}