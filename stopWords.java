import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import de.tudarmstadt.ukp.wikipedia.api.Page;

import java.util.ArrayList;
import java.util.Arrays;

public class stopWords {
	public static List<String> stopwords =stopWords.ReadStopWords();
	public static Set<String> stopWordSet = new HashSet<String>(stopwords);
	public static boolean isStopword(String word) {
		if(word.length() < 2) return true;
		if(word.charAt(0) >= '0' && word.charAt(0) <= '9') return true; //remove numbers, "25th", etc
		if(stopWordSet.contains(word)) return true;
		else return false;	
	}
	public static String removeStopWords(String string) {
		String result = "";
		/*String[] words = string.split("\\s+");
		for(String word : words) {
			if(isStopword(word)){
				words.re
			}
			result += (word+" ");
		}
		return result;*/
		String[] words = string.split("\\s+");
		for(int i = 0; i < words.length; i++){
			if(isStopword(words[i]))
				words[i] = "";
			result += (words[i]+" ");
		}
		return result;
	}
	public static List<String> ReadStopWords() {
		List<String> stopWordsList =new ArrayList<String>();
		try {
			File f = new File("E:\\Master\\Thesis\\Software\\EclipseWorkspace\\de.tudarmstadt.ukp.wikipedia\\stopwords_ar.txt");
			//FileReader fr = new FileReader(f);
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
			BufferedReader br = new BufferedReader(isr);
			Scanner sc = new Scanner(br);
			String temp = "";
			
			while(sc.hasNextLine()){
				stopWordsList.add(sc.nextLine());
			}	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stopWordsList;
	}
	public static void main(String args[]){
		
		List<String> stopList = stopWords.ReadStopWords();
		for(int i = 0; i< stopList.size(); i++){
			System.out.println(stopList.get(i));
		}
		String clear = stopWords.removeStopWords("برشلونة يفوز على ريال مدريد");
		/*Iterator iter = stopWordSet.iterator();
	    	while(iter.hasNext()){
  	    		System.out.println(iter.next());
  	    	}*/
		System.out.println(clear);
		System.out.println(isStopword("على"));
	}
}