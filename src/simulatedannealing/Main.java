package simulatedannealing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.SingleSelectionModel;

import com.google.gson.Gson;


public class Main {
  public static int numPart = 10;
  public static List<String> listString = new ArrayList<String>();
  public static int percentTrashold = 5;
  public static int temperature = 30000;
  public static int kVariable=1;

  public static class Review {
    public String reviewerID;
    public String asin;
    public String reviewerName;
    public int helpful[];
    public String reviewText;
    public int unixReviewTime;
    public String reviewTime;
  }

  public static void main(String args[]) throws IOException {
	  Calendar cal = Calendar.getInstance();
      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
      System.out.println( sdf.format(cal.getTime()));
	BufferedReader fr = new BufferedReader(
	    new FileReader(new File("/home/ubuntu/Downloads/file1.json")));
//		new FileReader(new File("/home/ubuntu/Downloads/shakespeare.txt")));
	List<Integer> partSizeList = new ArrayList<Integer>();
	List<Hashtable<String, Integer>> hsPart =
	    new ArrayList<Hashtable<String, Integer>>();
	Hashtable<String, Integer> hsSize = new Hashtable<String, Integer>();
	Hashtable<String, Integer> hsNewKeys = new Hashtable<String, Integer>();
	List<Integer> percentSaw = new ArrayList<Integer>();
	percentSaw.add(0);
	for (int i = 0; i < numPart; i++) {
	  Hashtable<String, Integer> hstb = new Hashtable<String, Integer>();
	  hsPart.add(hstb);
	  int size = 0;
	  partSizeList.add(size);
	
	}
    while (fr.ready()) {
      Gson gson = new Gson();
      Review review = gson.fromJson(fr.readLine(), Review.class);
      StringTokenizer tk = new StringTokenizer(review.reviewText);
//    	StringTokenizer tk = new StringTokenizer(fr.readLine());
    	while (tk.hasMoreTokens()) {
        String nextToken = tk.nextToken();
        listString.add(nextToken);
      }
    }
    fr.close();
    //Hashtable<String, Integer> hsPart = new Hashtable<String, Integer>();
    //Hashtable<String, Integer> hsSize = new Hashtable<String, Integer>();
    HashSet<Integer> valuesRead = new HashSet<Integer>();

    int stop = listString.size()*percentTrashold/100;
//    temperature = listString.size()*1*percentTrashold/15000;
  //  System.out.println("temp = "+temperature);
    for (int i = 0; i < listString.size() ; i++) {
    	int percent=100*i/listString.size();
    	String key3 = listString.get(i);
    	int place = Math.abs(key3.hashCode() % numPart);
    	if (!hsPart.get(place).containsKey(key3) && !hsNewKeys.containsKey(key3)) {
    		
    		int partitionSent = place;
    	  //        int partitionSent = 0;
    		for (int k = 0; k < partSizeList.size(); k++) {
    			if (partSizeList.get(k) < partSizeList.get(partitionSent)) {
    				partitionSent = k;
    			}
    		}
    		if(percent<=60 && !percentSaw.contains(percent)){
    			hsSize.put(key3, 1);
        		hsNewKeys.put(key3, partitionSent);
    		} else {
    			partSizeList.set(partitionSent,partSizeList.get(partitionSent)+1);
    		}
    		
    	} else {
    		if(hsPart.get(place).containsKey(key3)){
    			int partition = hsPart.get(place).get(key3);
    			partSizeList.set(partition, partSizeList.get(partition) + 1);
    		} else if(percent<=60 && !percentSaw.contains(percent)){
    	        hsSize.put(key3, hsSize.get(key3)+1);
    		} else {
    			int partition = hsNewKeys.get(key3);
    			partSizeList.set(partition, partSizeList.get(partition) + 1);
    		}
      }
      if(percent%percentTrashold==0 && percent<=60 && !percentSaw.contains(percent)){
    	  percentSaw.add(percent);
    	  List<Partition> myList = new ArrayList<Partition>();
    	  System.out.println("======");
  		System.out.println(percent);


    	  BeamSearch simuA = new BeamSearch();
//          myList = simuA.beamSearch(hsNewKeys,hsSize,partSizeList,numPart, temperature,kVariable);
          SBS sbs = new SBS();
         myList = sbs.beamSearch(hsNewKeys,hsSize,partSizeList ,numPart,temperature,kVariable);
//          SimulatedAnnealing simuA = new SimulatedAnnealing();
//          myList = simuA.simulatedAnnealing(hsNewKeys,hsSize,partSizeList ,numPart , temperature);
          partSizeList = new ArrayList<Integer>();
//          hsPart = new Hashtable<String, Integer>();
          System.out.println("------");
          System.out.println(percent);
          int sum=0;
          
          for(int k=0 ; k<myList.size() ; k++){
          	partSizeList.add(myList.get(k).size);
          	sum+=myList.get(k).size;
          	for (String keyIt : myList.get(k).htKeySize) {
          		if(keyIt!=null){
      				place = Math.abs(keyIt.hashCode() % numPart);
                    hsPart.get(place).put(keyIt, k);
  				}
          	}
          }
         // System.out.println(sum+" "+i);
          hsNewKeys= new Hashtable<String, Integer>();
          hsSize = new Hashtable<String, Integer>();
          
      }
      
    }
    int min =0;
    int max=0;
    for(int i=0 ; i<partSizeList.size() ; i++){
      if(partSizeList.get(i)>partSizeList.get(max)){
        max=i;
      } else if (partSizeList.get(i)<partSizeList.get(min)){
        min=i;
      }
    }
    int dif=partSizeList.get(max)-partSizeList.get(min);
    float percent = 100*dif * 1.00f / partSizeList.get(max) * 1.00f;
 //   System.out.println("total de chaves: " + listString.size());
    System.out.println(
        "Maior partição: " + max + " de tamanho: " + partSizeList.get(max));
    System.out.println(
        "Menor partição: " + min + " de tamanho: " + partSizeList.get(min));
    System.out.println("Diferença: " + dif + " totalizando: " + percent + "%");
    cal = Calendar.getInstance();
    sdf = new SimpleDateFormat("HH:mm:ss");
    System.out.println( sdf.format(cal.getTime()));
    int sum=0;
    for(int i=0 ; i<partSizeList.size() ; i++){
    	sum+=partSizeList.get(i);
    }
    System.out.println(sum+" "+listString.size());
  }
}

