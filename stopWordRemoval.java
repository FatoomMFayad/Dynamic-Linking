import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class stopWordRemoval {
	/*public static void main(String args[]){
		stopWordRemoval removal = new stopWordRemoval();
		removal.removeStopWords(text);
	}*/
	public List<String> removeStopWords(List<String> text) {
		List<String> stopWords =new ArrayList<String>();
		text = new ArrayList<String>();
		try {
			File f = new File("E:\\Master\\Thesis\\Software\\EclipseWorkspace\\de.tudarmstadt.ukp.wikipedia\\stopwords_ar.txt");
			//FileReader fr = new FileReader(f);
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String temp = "";
			
			while((temp = br.readLine())!= null){
				stopWords.add(temp);
				
			}
			/*for(String t:text){
				for(String s:stopWords){
					if(t.equals(s))
						text.remove(t);
				}
			}*/
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
	}

}
