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
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class LuceneExamples2 {
	
	public static void main(String[] args) {
		//indexDirectory();
		//search("350539");
		//System.out.println();
		printOutlinks("3366");
		search("3366");
		/*System.out.println(getInlinksNo("3366"));
		System.out.println("*******************************");*/
		/*printOutlinks("3366");
		System.out.println(getOutlinksNo("3366"));
		System.out.println("*******************************");
		/*Set<String>  inLinks = getInlinks("3366");
		for(String link:inLinks){
			System.out.println(link);
		}
		System.out.println(inLinks.size());
		System.out.println("*******************************");
		Set<String> outLinks = getOutlinks("3366");
		for(String link:outLinks){
			System.out.println(link);
		}
		System.out.println(outLinks.size());
		System.out.println("*******************************");*/
		//search("برشلونة");
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
		     Path path = Paths.get("C:/linkIndexestmp1");
		     Directory directory = FSDirectory.open(path);
			 IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());		
			 IndexWriter indexWriter = new IndexWriter(directory, config);
			 indexWriter.deleteAll();
			 //Iterator<Page> iter = wiki.getPages().iterator();
			// Iterator<Page> iter = wiki.getPages().iterator();
			 Iterator<Page> iter = wiki.getArticles().iterator();
			     while(iter.hasNext()) {
			    	 try{
			    	 	Page page = iter.next();
			            System.out.println("indexed " + page.getPageId());		        
						Document doc = new Document();
						if(page.getNumberOfInlinks() != 0 && page.getNumberOfOutlinks() !=0){
						doc.add(new TextField("path", ""+page.getPageId(), Store.YES));
						doc.add(new TextField("inlinks", page.getInlinkIDs()+" ", Store.YES));
						doc.add(new TextField("outlinks", page.getOutlinkIDs()+" ", Store.YES));
						doc.add(new TextField("numInlinks",page.getNumberOfInlinks()+" ", Store.YES));
						doc.add(new TextField("numOutlinks", page.getNumberOfOutlinks()+" ", Store.YES));
						}						
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
			/* List<String> pages = new ArrayList<String>();
			 
			 for(int j = 0; j < 10000; j++){
				 try{
				 pages.add(wiki.getPage(j).getPageId()+"");
				 }catch(Exception e){
				 e.printStackTrace();
				 j++;
			 }
			 }

			 int i=0;
			     for (String page : pages) {
			    	 System.out.println(page.toString());
						
			    	 	Document doc = new Document();
						doc.add(new TextField("path", page.toString(), Store.YES));
						i++;
						doc.add(new TextField("inlinks", wiki.getPage(page.toString()).getInlinkIDs()+" ", Store.YES));
						doc.add(new TextField("outlinks", wiki.getPage(page.toString()).getOutlinkIDs()+" ", Store.YES));
						doc.add(new TextField("numInlinks", wiki.getPage(page.toString()).getNumberOfInlinks()+" ", Store.YES));
						doc.add(new TextField("numOutlinks", wiki.getPage(page.toString()).getNumberOfOutlinks()+" ", Store.YES));
						System.out.println("id : "+page.toString());
						System.out.println("inlinks: "+wiki.getPage(page.toString()).getInlinkIDs());
						indexWriter.addDocument(doc);
						
			     }	 		  
			     /*indexWriter.close();		    
			     directory.close();*/
		/*} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}*/					
	}
	
	public static void printInlinks(String pageId){
		try {	
			Path path = Paths.get("C:/linkIndexestmp1");
			Directory directory = FSDirectory.open(path);		
			IndexReader indexReader =  DirectoryReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			
		    TermQuery query = new TermQuery(new Term("path", pageId));
			TopDocs topDocs = indexSearcher.search(query,indexReader.numDocs());
	        System.out.println("totalHits " + topDocs.totalHits);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {			
			    Document document = indexSearcher.doc(scoreDoc.doc);
			    System.out.println("path " + document.get("path"));
			    System.out.println(document.get("inlinks"));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static Set<String> getInlinks(String pageId){
		String inLinks = "";
		String[] in;
		
		try {	
			Path path = Paths.get("C:/linkIndexestmp1");
			Directory directory = FSDirectory.open(path);		
			IndexReader indexReader =  DirectoryReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			
		    TermQuery query = new TermQuery(new Term("path", pageId));
			TopDocs topDocs = indexSearcher.search(query,indexReader.numDocs());
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {			
			    Document document = indexSearcher.doc(scoreDoc.doc);
			    inLinks  = document.get("inlinks");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		in = inLinks.split("\\, |\\[|\\]");
		/*for(String s:in){
			s.trim();
		}*/
		Set<String> inLink = new HashSet<String>(Arrays.asList(in));
		return inLink;	
		
	}
	public static String getInlinksNo(String pageId){
		String inLinksNo = "";
		try {	
			Path path = Paths.get("C:/linkIndexestmp1");
			Directory directory = FSDirectory.open(path);		
			IndexReader indexReader =  DirectoryReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		    TermQuery query = new TermQuery(new Term("path", pageId));
			TopDocs topDocs = indexSearcher.search(query,indexReader.numDocs());
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {			
			    Document document = indexSearcher.doc(scoreDoc.doc);
			    inLinksNo  = document.get("numInlinks");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return inLinksNo;
	}
	public static void printOutlinks(String pageId){
		try {	
			Path path = Paths.get("C:/linkIndexestmp1");
			Directory directory = FSDirectory.open(path);		
			IndexReader indexReader =  DirectoryReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			
		    TermQuery query = new TermQuery(new Term("path", pageId));
			TopDocs topDocs = indexSearcher.search(query,indexReader.numDocs());
	        System.out.println("totalHits " + topDocs.totalHits);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {			
			    Document document = indexSearcher.doc(scoreDoc.doc);
			    System.out.println("path " + document.get("path"));
			    System.out.println("outlinks " + document.get("outlinks"));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static List<String> getOutlinks(String pageId){
		String outLinks = "";
		String[] out;
		//List<String> outLink = new ArrayList();
		try {	
			Path path = Paths.get("C:/linkIndexestmp1");
			Directory directory = FSDirectory.open(path);		
			IndexReader indexReader =  DirectoryReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			
		    TermQuery query = new TermQuery(new Term("path", pageId));
			TopDocs topDocs = indexSearcher.search(query,indexReader.numDocs());
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {			
			    Document document = indexSearcher.doc(scoreDoc.doc);
			    outLinks  = document.get("outlinks");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		out = outLinks.split("\\,|\\[|\\]|\\s+");
		/*for(String s:out){
			s.trim();
		}*/
		//Set<String> outLink = new HashSet<String>(Arrays.asList(out));
		List<String> outLink = new ArrayList<String>(Arrays.asList(out));
		return outLink;	
	}
	public static String getOutlinksNo(String pageId){
		String outLinksNo = "";
		try {	
			Path path = Paths.get("C:/linkIndexestmp1");
			Directory directory = FSDirectory.open(path);		
			IndexReader indexReader =  DirectoryReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			
		    TermQuery query = new TermQuery(new Term("path", pageId));
			TopDocs topDocs = indexSearcher.search(query,indexReader.numDocs());
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {			
			    Document document = indexSearcher.doc(scoreDoc.doc);
			    outLinksNo  = document.get("numOutlinks");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return outLinksNo;
	}
	
public static void search(String text) {	
		//Apache Lucene searching text inside .txt files
		try {	
			Path path = Paths.get("C:/pageTiltes2");
			Directory directory = FSDirectory.open(path);		
			IndexReader indexReader =  DirectoryReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			QueryParser queryParser = new QueryParser("contents",  new StandardAnalyzer());  
			Query query = queryParser.parse(text);
			TopDocs topDocs = indexSearcher.search(query,10);
	        System.out.println("totalHits " + topDocs.totalHits);
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {			
			    Document document = indexSearcher.doc(scoreDoc.doc);
			    System.out.println("path " + document.get("path"));
			    System.out.println("content " + document.get("contents"));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}				
	}
  }
