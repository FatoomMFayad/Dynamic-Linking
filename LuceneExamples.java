package de.tudarmstadt.ukp.wikipedia;


import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.search.PhraseQuery;

import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * @author pdx
 *
 */
public class LuceneExamples {

	public static final int TITLE_DIFFERENCE_THRSHOLD = 2;

	static int occurance = 0;
	public static void main(String args[]) throws WikiInitializationException{
        DatabaseConfiguration dbConfig = new DatabaseConfiguration();
		dbConfig.setHost("localhost");
		dbConfig.setDatabase("arwiki");
		dbConfig.setUser("root");
		dbConfig.setPassword("root");
		dbConfig.setLanguage(Language.arabic);
		
        Wikipedia wiki = new Wikipedia(dbConfig);
		try {
			Set<Page> pages = search("مجلس التعاون الخليجي", wiki);
			for(Page p: pages){
				System.out.println(p.getTitle());
			}
		} catch (NumberFormatException | WikiApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void indexDirectory() {		
		 //Apache Lucene Indexing Directory .txt files     
	     try {	
		 //indexing directory	
	        DatabaseConfiguration dbConfig = new DatabaseConfiguration();
			dbConfig.setHost("localhost");
			dbConfig.setDatabase("arwiki");
			dbConfig.setUser("root");
			dbConfig.setPassword("root");
			dbConfig.setLanguage(Language.arabic);
			
	        Wikipedia wiki = new Wikipedia(dbConfig);
	 
	     Path path = Paths.get("C:/PageTitles2");
		 Directory directory = FSDirectory.open(path);
		 IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());		
		 IndexWriter indexWriter = new IndexWriter(directory, config);
		 indexWriter.deleteAll();
		 //Iterator<Page> iter = wiki.getPages().iterator();
		 Iterator<Page> iter = wiki.getPages().iterator();
		     while(iter.hasNext()) {
		    	 try{
		    	 	Page page = iter.next();
		            System.out.println("indexed " + page.getPageId());		        
					Document doc = new Document();
					doc.add(new TextField("path", ""+page.getPageId(), Store.YES));
					if(!page.getTitle().getEntity().contains("Discussion")|| !page.getTitle().getEntity().contains("("))
						doc.add(new TextField("PageTitle", page.getTitle().getEntity(), Store.YES));
					indexWriter.addDocument(doc);
		    	 }catch(Exception e){
		    		 e.printStackTrace();
		    	 }
		     }	 		     
		     indexWriter.close();		    
		     directory.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}					
	}
	
	public static Set<Page> search(String text, Wikipedia wiki) throws NumberFormatException, WikiApiException {	
		//Apache Lucene searching text inside .txt files
			Set<String> pages = new HashSet<String>();

	     //String page = "";
	     //String[] p;
		try {					
				Path path = Paths.get("C:/PageTitles2");
				Directory directory = FSDirectory.open(path);		
				IndexReader indexReader =  DirectoryReader.open(directory);
				IndexSearcher indexSearcher = new IndexSearcher(indexReader);
				QueryParser queryParser = new QueryParser("PageTitle",  new StandardAnalyzer());
				queryParser.setDefaultOperator(QueryParser.Operator.AND);
				queryParser.setPhraseSlop(0);
				//QueryBuilder query = queryParser.createPhraseQuery("PageTitle", text);//.parse(text);
				Query query = queryParser.parse(text);
				TopDocs topDocs = indexSearcher.search(query,indexReader.numDocs());
		  //      System.out.println("totalHits " + topDocs.totalHits);
		        occurance = topDocs.totalHits;
		        int docNum = 0;
		       // String[] pages = null;
				for (ScoreDoc scoreDoc : topDocs.scoreDocs) {			
				    Document document = indexSearcher.doc(scoreDoc.doc);
				    docNum ++;
				    //System.out.println("path #" + docNum + ": "+ document.get("path"));
				    //page += document.get("path") + " ";
				    pages.add(document.get("path"));
				  //  System.out.println("Title\n " + document.get("PageTilte"));
				 //   System.out.println("************************************************************");
			   
			}
			//System.out.println(p);
		//	String[]
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		Set<Page> allPages = new HashSet<Page>();
		for(String pa: pages){
			Page wp = wiki.getPage(Integer.parseInt(pa));
			if(wp != null && wp.getTitle().getEntity().trim().split(" ").length  <= 4 && (wp.getTitle().getEntity().split(" ").length-text.split(" ").length)<=TITLE_DIFFERENCE_THRSHOLD && !wp.getTitle().getEntity().contains("Discussion") && !wp.getTitle().toString().contains("توضيح"))
				allPages.add(wp);
			//allPages.add(wiki.getPage(Integer.parseInt(pa)));

		}
		//Set<String> outLinks = getOutlinks("3366");
		//System.out.println(allPages.size());
		return allPages;	
		
	}
	public static int getAllPages(String pageId){
		String page = "";
		String[] p;
		//List<String> outLink = new ArrayList();
		try {	
			Path path = Paths.get("C:/pageTitles");
			Directory directory = FSDirectory.open(path);		
			IndexReader indexReader =  DirectoryReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			
		    TermQuery query = new TermQuery(new Term("path", pageId));
			TopDocs topDocs = indexSearcher.search(query,indexReader.numDocs());
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {			
			    Document document = indexSearcher.doc(scoreDoc.doc);
			    page  += document.get("path")+" ";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		p = page.split("\\,|\\[|\\]|\\s+");
		Set<String> pages = new HashSet<String>(Arrays.asList(p));
		//Set<String> outLinks = getOutlinks("3366");
		return pages.size();	
	}

	public static void searchPages(String text) {	
		//Apache Lucene searching text inside .txt files
		try {	
			Path path = Paths.get("C:/pageTitles2");
			Directory directory = FSDirectory.open(path);		
			IndexReader indexReader =  DirectoryReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			QueryParser queryParser = new QueryParser("PageTitle",  new StandardAnalyzer());
			queryParser.setDefaultOperator(QueryParser.Operator.AND);
			queryParser.setPhraseSlop(0);
			Query query = queryParser.parse(text);
			TopDocs topDocs = indexSearcher.search(query,indexReader.numDocs());
	        System.out.println("totalHits " + topDocs.totalHits);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {			
			    Document document = indexSearcher.doc(scoreDoc.doc);
			    System.out.println("PageID " + document.get("path"));
			    System.out.println("Title " + document.get("PageTitle"));
			    
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}				
	}
	public static int searchContent(String text) {	
		//Apache Lucene searching text inside .txt files
		int total = 0;
		try {	
			Path path = Paths.get("C:/pageIndexes");
			Directory directory = FSDirectory.open(path);		
			IndexReader indexReader =  DirectoryReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			QueryParser queryParser = new QueryParser("contents",  new StandardAnalyzer());
			queryParser.setDefaultOperator(QueryParser.Operator.AND);
			queryParser.setPhraseSlop(0);
			Query query = queryParser.parse(text);
			TopDocs topDocs = indexSearcher.search(query,indexReader.numDocs());
	       // System.out.println("totalHits " + topDocs.totalHits);
	        total = topDocs.totalHits;
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {			
			    Document document = indexSearcher.doc(scoreDoc.doc);
			  //  System.out.println("path " + document.get("path"));
			  //  System.out.println("content " + document.get("contents"));			    
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
		return total;
	}
  }
