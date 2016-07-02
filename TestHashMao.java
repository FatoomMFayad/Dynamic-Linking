package de.tudarmstadt.ukp.wikipedia;

import java.util.HashMap;

public class TestHashMao {

	public static void main(String args[]){
		HashMap<Pair, Double> relatednessMap = new HashMap<Pair, Double>();
		Pair p1 = new Pair(2,3);
		relatednessMap.put(p1, 0.7d);
		Pair p2 = new Pair(2,3);
		relatednessMap.put(p2, 0.5d);
		System.out.println(relatednessMap.size());
		
	}
}

class Pair{
	public int id1;
	public int id2;
	
	public Pair(int id1, int id2){
		this.id1 = id1;
		this.id2 = id2;
		
	}
	@Override
	public boolean equals(Object obj) {
		Pair p2 = (Pair) obj;
		if((p2.id1 == this.id1) && (p2.id2== this.id2))
			return true;
		else if((p2.id1 == this.id2) && (p2.id2== this.id1))
			return true;
		else
			return false;
				
	}

}
