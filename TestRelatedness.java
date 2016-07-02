package de.tudarmstadt.ukp.wikipedia;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiTitleParsingException;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.LuceneExamples;

public class TestRelatedness {
	
	public static HashMap<String, Double> relatednessHistory = new HashMap<String, Double>();
	static int newSize = 3095367;
	public static void main(String[] args) {
		try{	
			DatabaseConfiguration dbConfig = new DatabaseConfiguration();
			dbConfig.setHost("localhost");
			dbConfig.setDatabase("arwiki");
			dbConfig.setUser("root");
			dbConfig.setPassword("root");
			dbConfig.setLanguage(Language.arabic);
			Wikipedia wiki = new Wikipedia(dbConfig); 
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static int chooseAnchor(Set<Page> pages, List<Page> others) throws WikiTitleParsingException{
		HashMap<Integer, Double> pageRelatedness = new HashMap<Integer, Double>();
		double Relatedness=0.0;
		int selected = 0;
		if(pages.size() >= 1){
			for(Page page:pages){
			if(others.size() > 0)
				Relatedness = calculateAvgRelatedness(page,others);
		if(Relatedness != 0.0)
					pageRelatedness.put(page.getPageId(), Relatedness);

				
			}
		
			LinkedHashMap<Integer, Double> sortedMap = sortHashMapByValuesD(pageRelatedness);

			if(sortedMap.size() > 0)
				selected = (int) sortedMap.keySet().toArray()[sortedMap.size()-1];	
		}

		return selected;
	}
	public static double calculateAvgRelatedness(Page pa, List<Page> pages) throws WikiTitleParsingException{
		int i=0;
		double avgRelatedness = 0;
		double result = 0.0;
		for(Page p: pages){
			double r = 0;
			String key = Math.max(pa.getPageId(), p.getPageId())+","+Math.min(pa.getPageId(), p.getPageId());
			if(relatednessHistory.containsKey(key)){
				r = relatednessHistory.get(key);
			}else{
				r = calculateRelatedness(pa, p);
				relatednessHistory.put(key, r);
			}
			if(r != 0 ){
			avgRelatedness = avgRelatedness +r; 
			i++;
			}
		}
		if (i != 0){
			result = avgRelatedness/i;
		}
		else{
			result = 0.0;
		}
		return result;
	}
	
	public static double calculateRelatedness(Page pa, Page pb) throws WikiTitleParsingException{

			Set<Integer> paInLinkSet = pa.getInlinkIDs();
			Set<Integer> pbInLinkSet = pb.getInlinkIDs();
	    	int A = paInLinkSet.size();
	    	int B =  pbInLinkSet.size();
	    	int min = Math.min(A,B);
	    	int max = Math.max(A,B);
	    	double bottom = Math.log(newSize) - Math.log(min);
	    	int AB = 0;
	    	for(int p1: pbInLinkSet){
	    		for(int p2: paInLinkSet){
	    			if(p1 == p2){
	    				AB++;
	    			}
	    		}
	    	} 	
	    	double upper = Math.log(max) - Math.log(AB);
	    	double relatedness = 0.0;
	    	if (upper != 0 && bottom != 0 && AB != 0){
	    		relatedness= 1.0 - Math.abs(upper/bottom);
	    	}
	    	return relatedness;
	}

	public static double calculateProbability(Page p) throws WikiTitleParsingException{
		int occurance = LuceneExamples.searchContent(p.getTitle().toString());
	     int anchors = p.getNumberOfInlinks() ;
	     double Probability = 0.0;
	     if (occurance != 0) 
	    		 Probability = (double)anchors/ (double)occurance;
	    
	     return Probability;
	}
	public static LinkedHashMap<Integer, Double> sortHashMapByValuesD(HashMap passedMap) {
		   List<Integer> mapKeys = new ArrayList<Integer>(passedMap.keySet());
		   List<Double> mapValues = new ArrayList<Double>(passedMap.values());
		   Collections.sort(mapValues);
		   Collections.sort(mapKeys);

		   LinkedHashMap<Integer, Double>  sortedMap = new LinkedHashMap<Integer, Double> ();

		   Iterator<Double> valueIt = mapValues.iterator();
		   while (valueIt.hasNext()) {
		       Object val = valueIt.next();
		       Iterator<Integer> keyIt = mapKeys.iterator();

		       while (keyIt.hasNext()) {
		           Object key = keyIt.next();
		           String comp1 = passedMap.get(key).toString();
		           String comp2 = val.toString();

		           if (comp1.equals(comp2)){
		               passedMap.remove(key);
		               mapKeys.remove(key);
		               sortedMap.put((Integer)key, (Double)val);
		               break;
		           }

		       }

		   }
		   return sortedMap;
		}
	public static boolean filter(Page p, List<Page> others) throws WikiTitleParsingException{
		boolean result = false;
		double Beta = 0.3;
		double alpha = 0.7;
		double threshold = 0.3;

		double F = (alpha * calculateAvgRelatedness(p, others))+ (Beta * calculateProbability(p));
		if (F >= threshold ){
			result = true;
		}
		return result;
			
	}
}
