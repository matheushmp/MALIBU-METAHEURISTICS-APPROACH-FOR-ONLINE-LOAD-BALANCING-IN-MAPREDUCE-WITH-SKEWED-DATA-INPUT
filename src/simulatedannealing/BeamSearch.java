package simulatedannealing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;


public class BeamSearch {	
	public Backtracker passo(List<Partition> partitionList, Hashtable<String, Integer> hsTableKeySizes, int partitions) {
		Backtracker bt = new Backtracker();
		bt.origin=partitionList;
		int biggerPart=0;
		int smallerPart=0;
		for(int i=0 ; i<partitions ; i++){
			if(partitionList.get(i).size>partitionList.get(biggerPart).size && partitionList.get(i).htKeySize.size()>0){
				biggerPart=i;
			} else if(partitionList.get(i).size<partitionList.get(smallerPart).size){
				smallerPart=i;
			}
		}
		Random rand = new Random();
		bt.oldPart = biggerPart;//rand.nextInt(candidata.size());
		bt.newPart = smallerPart;//rand.nextInt(candidata.size());
		if(partitionList.get(bt.oldPart).htKeySize.size()>0){
			Partition part1 = partitionList.get(bt.oldPart);
			Partition part2 = partitionList.get(bt.newPart);
			bt.key=null;
			while(bt.key==null){
				int numEle1 = rand.nextInt(part1.htKeySize.size());
				bt.key = new ArrayList<String>(part1.htKeySize).get(numEle1);
			}
			bt.keySize = hsTableKeySizes.get(bt.key);
			int Mt=partitionList.get(bt.oldPart).size-bt.keySize;
			int mt=partitionList.get(bt.newPart).size+bt.keySize;
			for(int i=0 ; i<partitions ; i++){
				int size =partitionList.get(i).size;
				if(i==bt.oldPart){
					size-=bt.keySize;
				} else if(i==bt.newPart){
					size+=bt.keySize;
				}
				if(size>Mt){
					Mt=size;
				}else if(size<mt){
					mt=size;
				}
			}
			bt.dif=Mt-mt;
		}
		return bt;		
	  }

	  public List<Partition> createLP(Backtracker bt) {
		  
		  List<Partition> LP = new ArrayList<Partition>();
		  for(int i=0 ; i<bt.origin.size() ; i++){
			  Partition part = new Partition();
			  part.size=bt.origin.get(i).size;
			  part.htKeySize=bt.origin.get(i).htKeySize;
			  LP.add(part);
		  }
		  Partition part1 = LP.get(bt.oldPart);
		  Partition part2 = LP.get(bt.newPart);
		  part1.size-=bt.keySize;
		  part2.size+=bt.keySize;
		  part1.htKeySize=(HashSet<String>) part1.htKeySize.clone();
		  part2.htKeySize=(HashSet<String>) part2.htKeySize.clone();
		  part1.htKeySize.remove(bt.key);
		  part2.htKeySize.add(bt.key);
		  LP.set(bt.oldPart,part1);
		  LP.set(bt.newPart,part2);		  
		  return LP;

	  }

	  public List<Partition> beamSearch(Hashtable<String, Integer> hsNewKeys, Hashtable<String, Integer> hsTableKeySizes,
			  List<Integer> partSizes,int partitions, int temperature, int kVariable){
		//	inicializa a Lista resposta
		List<Partition> resposta = new ArrayList<Partition>();
			for(int i=0 ; i<partitions ; i++){
				Partition part = new Partition();
				part.size=partSizes.get(i);
				resposta.add(part);
				
			}
		//preparar a lista resposta com a solução inicial:
		//	for(Hashtable<String, Integer> hstb : hsTableKeys){
		//		for(String key: hstb.keySet()){
			for(String key: hsNewKeys.keySet()){
					int destino = hsNewKeys.get(key);
					Partition partModificada = resposta.get(destino);
					partModificada.size+=hsTableKeySizes.get(key);
					partModificada.htKeySize.add(key);
					resposta.remove(destino);
					resposta.add(destino, partModificada);
				}
		//	}
			
		//executa o passo até a temperatura zerar
			List<Backtracker> possibleSolutions = new ArrayList<Backtracker>();
			for(int i=0 ; i<kVariable*2 ; i++){
				possibleSolutions.add(passo(resposta,hsTableKeySizes,partitions));
			}
			resposta=null;
			System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa");
			while(temperature>0){
				if(temperature%1000==0){
					System.out.println(temperature);
				}
				int accepted=0;
				List<Backtracker> acceptedSolutions = new ArrayList<Backtracker>();
				while(accepted!=kVariable){
					int found=0;
					for(int i=0 ; i<possibleSolutions.size() ; i++){
						if(possibleSolutions.get(i).dif<possibleSolutions.get(found).dif){
							found=i;
						}
					}
					accepted+=1;
					acceptedSolutions.add(possibleSolutions.get(found));
					acceptedSolutions.add(passo(createLP(possibleSolutions.get(found)), hsTableKeySizes,partitions));
					possibleSolutions.remove(found);
				}
				possibleSolutions=acceptedSolutions;				
				temperature--;
			}
			
		int bestSol=0;
		for(int i=0 ; i<possibleSolutions.size() ; i++){
			if(possibleSolutions.get(i).dif<possibleSolutions.get(bestSol).dif){
				bestSol=i;
			}
		}
		return createLP(possibleSolutions.get(bestSol));
	}
}
//public class BeamSearch {
//
//	public List<Partition> passo(List<Partition> partitionList, int partitions, Hashtable<String, Integer> hsTableKeySizes) {
//		List<Partition> candidata = new ArrayList<Partition>();
//		int biggerPart=0;
//		int smallerPart=0;
//		for(int i=0 ; i<partitionList.size() ; i++){
//			Partition part = new Partition();
//			part.size=partitionList.get(i).size;
//			part.htKeySize= partitionList.get(i).htKeySize;
//			candidata.add(part);
//			if(part.size>partitionList.get(biggerPart).size){
//				biggerPart=i;
//			} else if(part.size<partitionList.get(smallerPart).size){
//				smallerPart=i;
//			}
//		}
//		Random rand = new Random();
//		int numPart1 = biggerPart;//rand.nextInt(candidata.size());
//		int numPart2 = smallerPart;//rand.nextInt(candidata.size());
//		while(numPart2==numPart1){
//			numPart2 = rand.nextInt(candidata.size());
//		}
//		if(candidata.get(numPart1).htKeySize.size()>0){
//			Partition part1 = candidata.get(numPart1);
//			Partition part2 = candidata.get(numPart2);
//			part1.htKeySize = (HashSet<String>) part1.htKeySize.clone();
//			part2.htKeySize = (HashSet<String>) part2.htKeySize.clone();
//			int chance = rand.nextInt(5);
//			int numEle1 = rand.nextInt(part1.htKeySize.size());
//			String stringEle1 = new ArrayList<String>(part1.htKeySize).get(numEle1);
//			int sizeEle1 = hsTableKeySizes.get(stringEle1);
//			part1.size-=sizeEle1;
//			part1.htKeySize.remove(stringEle1);
//			if(chance>1 && part2.htKeySize.size()!=0){
//				int numEle2 = rand.nextInt(part2.htKeySize.size());
//				String stringEle2 = new ArrayList<String>(part2.htKeySize).get(numEle2);
//				int sizeEle2 = hsTableKeySizes.get(stringEle2);
//				part2.size-=sizeEle2;
//				part2.htKeySize.remove(stringEle2);
//				part1.size+=sizeEle2;
//				part1.htKeySize.add(stringEle2);
//			}	
//			part2.size+=sizeEle1;
//			part2.htKeySize.add(stringEle1);
//			candidata.remove(numPart2);
//			candidata.add(numPart2,part2);
//			candidata.remove(numPart1);
//			candidata.add(numPart1,part1);
//			
//		}
//		return candidata;		
//	  }
//
//	  public int calcDif(List<Partition> partitionList) {
//	    int Dif = 0;
//	    for (int i = 0; i < partitionList.size() - 1; i++) {
//	      for (int j = i + 1; j < partitionList.size(); j++) {
//	        if (partitionList.get(i).size > partitionList.get(j).size) {
//	          Dif = Dif + partitionList.get(i).size - partitionList.get(j).size;
//	        } else {
//	          Dif = Dif - partitionList.get(i).size + partitionList.get(j).size;
//	        }
//	      }
//	    }
//	    return Dif;
//
//	  }
//
//	  public List<Partition> beamSearch(Hashtable<String, Integer> hsNewKeys/*List<Hashtable<String, Integer>> hsTableKeys*/,
//			  Hashtable<String, Integer> hsTableKeySizes, List<Integer> partSizes,
//		      int partitions, int temperature, int kVariable){
//		//	inicializa a Lista resposta
//		List<Partition> resposta = new ArrayList<Partition>();
//			for(int i=0 ; i<partitions ; i++){
//				Partition part = new Partition();
//				part.size=partSizes.get(i);
//				resposta.add(part);
//				
//			}
//		//preparar a lista resposta com a solução inicial:
//		//	for(Hashtable<String, Integer> hstb : hsTableKeys){
//		//		for(String key: hstb.keySet()){
//			for(String key: hsNewKeys.keySet()){
//					int destino = hsNewKeys.get(key);
//					Partition partModificada = resposta.get(destino);
//					partModificada.size+=hsTableKeySizes.get(key);
//					partModificada.htKeySize.add(key);
//					resposta.remove(destino);
//					resposta.add(destino, partModificada);
//				}
//		//	}
//			
//		//executa o passo até a temperatura zerar
//			List<List<Partition>> possibleSolutions = new ArrayList<List<Partition>>();
//			for(int i=0 ; i<kVariable*2 ; i++){
//				possibleSolutions.add(passo(resposta, partitions,hsTableKeySizes));
//			}
//			resposta=null;
//			while(temperature>0){
//				int accepted=0;
//				List<List<Partition>> acceptedSolutions = new ArrayList<List<Partition>>();
//				while(accepted!=kVariable){
//					int found=0;
//					for(int i=0 ; i<possibleSolutions.size() ; i++){
//						if(calcDif(possibleSolutions.get(i))<calcDif(possibleSolutions.get(found))){
//							found=i;
//						}
//					}
//					accepted+=1;
//					acceptedSolutions.add(possibleSolutions.get(found));
//					acceptedSolutions.add(passo(possibleSolutions.get(found), partitions, hsTableKeySizes));
//					possibleSolutions.remove(found);
//				}
//				possibleSolutions=acceptedSolutions;				
//				temperature--;
//			}
//			
//		int bestSol=0;
//		for(int i=0 ; i<possibleSolutions.size() ; i++){
//			if(calcDif(possibleSolutions.get(bestSol))>calcDif(possibleSolutions.get(i))){
//				bestSol=i;
//			}
//		}
//		return possibleSolutions.get(bestSol);
//	}
//}


