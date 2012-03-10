package pagerank;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/*
 * description:	implemented PageRank on undirected, weighted graph
 * Reference:	http://en.wikipedia.org/wiki/Pagerank
 * developed by JIA Xun
 * website:	jia1546.is-programmer.com
 * E-mail:	jia1546@163.com
 */

public class PageRank {
	private Map<String, ArrayList<MapEntry>> map = null;
	private List<PageRankNode> rankedList = null;
	
	public PageRank()
	{
		map = new HashMap<String, ArrayList<MapEntry>>();
	}
	
	/*
	 * construct linked list representation of graph using hash map
	 * this version uses file to initialize
	 */
	public void initializeMap(String addr)
	{
		BufferedReader inputStream = null;
		String line = null;
		try{
			inputStream = new BufferedReader(new FileReader(addr));
			line = inputStream.readLine();
			
			String node1 = null, node2 = null;
			double edgeWeight = 0;
			while(line != null)
			{
				line = line.trim();
			
				//too complex, another simple method to handle this
				//int FirstPosition = line.indexOf('\t');
				//int LastPosition = line.lastIndexOf('\t');
				String entries[] = line.split("\t");
				
				node1 = entries[0];
				node2 = entries[1];
				edgeWeight = Double.parseDouble(entries[2]);
				
				this.addEntry(node1, node2, edgeWeight);
				this.addEntry(node2, node1, edgeWeight);
				
				line = inputStream.readLine();
			}
			inputStream.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		System.out.println("initialize map from file finished...");
	}
	
	
	/*
	 * undirected graph, add entry (node1, <node2, weight>), (node2, <node1, weight>)
	 */
	public void addEntry(String node1, String node2, double edgeWeight)
	{
		MapEntry mapEntry = new MapEntry(node2, edgeWeight);
		if(this.map.containsKey(node1))
		{
			if(! map.get(node1).contains(mapEntry))
			{
				this.map.get(node1).add(mapEntry);
			}	
		}
		else
		{
			ArrayList<MapEntry> list = new ArrayList<MapEntry>();
			list.add(mapEntry);
			this.map.put(node1, list);
		}	
	}
	
	public void rank(int iterations, double dampingFactor)
	{	
        HashMap<String, Double> lastRanking = new HashMap<String, Double>();
        HashMap<String, Double> nextRanking = new HashMap<String, Double>();
        
        Double startRank = 1.0 / map.size();
        
        for (String key : map.keySet()) 
        {
            lastRanking.put(key, startRank);
        }
        
        double dampingFactorComplement = 1.0 - dampingFactor;
        
        for (int times = 0; times < iterations; times++)
        {
        	for (String key : map.keySet()) 
            {
            	double totalWeight = 0;
               	for(MapEntry entry : map.get(key))
               		totalWeight += (entry.getWeight() * lastRanking.get(entry.getIdentifier()) / this.map.get(entry.getIdentifier()).size());
                	
                   Double nextRank = dampingFactorComplement
                           + (dampingFactor * totalWeight);

                   nextRanking.put(key, nextRank);
            }
        	lastRanking = nextRanking;
        }
        
        System.out.println(iterations + " times iteration finished...");
        
        rankedList = PageRankVector(lastRanking);
       
	}
	
	public void saveRankedResults(String writeAddr)
	{
		File file = new File(writeAddr);
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(file);
			for(PageRankNode node : rankedList)
			{
				writer.println(node.getIdentifier() + "\t" + node.getRank());
			}
			System.out.println("save ranked results finished...");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * show pagerank results in pretty presentation
	 */
	public void showResults(int topK)
	{
        System.out.println("--------------------------------------");
        System.out.println("     node     |          rank         ");
        System.out.println("--------------------------------------");
        
        int startIndex = 0;
        for(int i=0; i< topK; i++)
        {
        	String key = rankedList.get(startIndex).getIdentifier();
        	
        	while(key.startsWith("N"))	//this is a pattern node
        	{
        		startIndex ++;
        		if(startIndex > rankedList.size())
        		{
        			System.out.println("number of T nodes < top-K");
        			System.exit(0);
        		}
        		key = rankedList.get(startIndex).getIdentifier();
        	}
  
        	//double rank = lastRanking.get(key);
        	double rank = rankedList.get(startIndex).getRank();
        	System.out.println("     "+ key + "    |" + "     " + rank + "     ");
        	
        	startIndex ++;
        }		
	}
	
	
	/*
	 * get ranked results using Collections's sort method
	 */
    public List<PageRankNode> PageRankVector(final HashMap<String, Double> LastRanking) 
    {
    	List<PageRankNode> nodeList = new LinkedList<PageRankNode>();
        for (String identifier : LastRanking.keySet())
        {
            PageRankNode node
                    = new PageRankNode(identifier, LastRanking.get(identifier));
            nodeList.add(node);
        }
        Collections.sort(nodeList);
        return nodeList;
    }		
	
	public static void main(String[] args) {
	    long startTime = System.currentTimeMillis();
	    
	    /*
	     * @readAddr:	set the path  to read graph file
	     * @topK:	present to user the top k results
	     * @iteration	number of iterations to compute pageRank, usually 10 iterations leads to convergence
	     * @writeAddr	set the path to save ranked results
	     */
		String readAddr = "/tfacts_result.txt";
		String writeAddr = "D://rankedResults.txt";
		int iterations = 10;
		double DumpingFactor = 0.85;
		int topK = 10;
				
		PageRank pagerank = new PageRank();
		pagerank.initializeMap(readAddr);
		pagerank.rank(iterations, DumpingFactor);
		pagerank.showResults(topK);
		
		//sava ranked results in text form
		pagerank.saveRankedResults(writeAddr);
		
		long endTime = System.currentTimeMillis();
		long time = endTime - startTime;
		System.out.println("program runs " + time + "ms");
	}
}
