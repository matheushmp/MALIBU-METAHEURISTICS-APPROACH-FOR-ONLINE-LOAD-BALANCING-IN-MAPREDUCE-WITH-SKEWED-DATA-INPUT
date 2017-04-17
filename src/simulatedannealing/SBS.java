package simulatedannealing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class SBS {

	public Backtracker passo(List<Partition> partitionList, Hashtable<String, Integer> hsTableKeySizes,int partitions, Backtracker oldbt, int numBt) {
		Backtracker bt = new Backtracker();
		bt.origin=partitionList;
		bt.oldbt = oldbt;
		bt.numBt=numBt;
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
	  public List<Partition> advanceLP(Backtracker bt, List<Partition> partList){
		  bt.newbt=null; 
		  while(bt.oldbt!=null){
			  bt.oldbt.newbt=bt;
			  bt=bt.oldbt;
		  }
		  while(bt.newbt!=null){
			  if(!partList.get(bt.newPart).htKeySize.contains(bt.key) && partList.get(bt.oldPart).htKeySize.contains(bt.key)){
				  partList.get(bt.newPart).size+=bt.keySize;
				  partList.get(bt.oldPart).size-=bt.keySize;
				  partList.get(bt.newPart).htKeySize.add(bt.key);
				  partList.get(bt.oldPart).htKeySize.remove(bt.key);			  
			  }
			  bt=bt.newbt;
		  }
		  if(!partList.get(bt.newPart).htKeySize.contains(bt.key) && partList.get(bt.oldPart).htKeySize.contains(bt.key)){
			  partList.get(bt.newPart).size+=bt.keySize;
			  partList.get(bt.oldPart).size-=bt.keySize;
			  partList.get(bt.newPart).htKeySize.add(bt.key);
			  partList.get(bt.oldPart).htKeySize.remove(bt.key);			  
		  }
		  
		  return partList;
	  }
	  public List<Partition> createLP(Backtracker btnew,Backtracker btold,List<Partition> partList) {
		  boolean ancestral=false;
		  btnew.newbt=null;
		  while ((btnew.oldbt!=null || btold.oldbt!=null) &&!ancestral){
			  if(btnew.numBt>btold.numBt){
				  if(btnew.oldbt==btold){
					  ancestral=true;
				  }
				  btnew.oldbt.newbt=btnew;
				  btnew=btnew.oldbt;
			  } else if(btnew.numBt<btold.numBt){
				  if(btold.oldbt==btnew){
					  ancestral=true;
				  }
				  if(partList.get(btold.newPart).htKeySize.contains(btold.key) && !partList.get(btold.oldPart).htKeySize.contains(btold.key)){
					  partList.get(btold.newPart).size-=btold.keySize;
					  partList.get(btold.oldPart).size+=btold.keySize;
					  partList.get(btold.newPart).htKeySize.remove(btold.key);
					  partList.get(btold.oldPart).htKeySize.add(btold.key);			  
				  }
				  btold=btold.oldbt;
			  } else {
				  if(btnew.oldbt==btold.oldbt){
					  ancestral=true;
				  }
				  btnew.oldbt.newbt=btnew;
				  btnew=btnew.oldbt;
				  if(partList.get(btold.newPart).htKeySize.contains(btold.key) && !partList.get(btold.oldPart).htKeySize.contains(btold.key)){
					  partList.get(btold.newPart).size-=btold.keySize;
					  partList.get(btold.oldPart).size+=btold.keySize;
					  partList.get(btold.newPart).htKeySize.remove(btold.key);
					  partList.get(btold.oldPart).htKeySize.add(btold.key);			  
				  }
				  btold=btold.oldbt;
			  }
		  }
		  if(partList.get(btold.newPart).htKeySize.contains(btold.key) && !partList.get(btold.oldPart).htKeySize.contains(btold.key)){
			  partList.get(btold.newPart).size-=btold.keySize;
			  partList.get(btold.oldPart).size+=btold.keySize;
			  partList.get(btold.newPart).htKeySize.remove(btold.key);
			  partList.get(btold.oldPart).htKeySize.add(btold.key);			  
		  }
		  while(btnew.newbt!=null){
			  if(!partList.get(btnew.newPart).htKeySize.contains(btnew.key) && partList.get(btnew.oldPart).htKeySize.contains(btnew.key)){
				  partList.get(btnew.newPart).size+=btnew.keySize;
				  partList.get(btnew.oldPart).size-=btnew.keySize;
				  partList.get(btnew.newPart).htKeySize.add(btnew.key);
				  partList.get(btnew.oldPart).htKeySize.remove(btnew.key);			  
			  }
			  btnew=btnew.newbt;
		  }
		  if(!partList.get(btnew.newPart).htKeySize.contains(btnew.key) && partList.get(btnew.oldPart).htKeySize.contains(btnew.key)){
			  partList.get(btnew.newPart).size+=btnew.keySize;
			  partList.get(btnew.oldPart).size-=btnew.keySize;
			  partList.get(btnew.newPart).htKeySize.add(btnew.key);
			  partList.get(btnew.oldPart).htKeySize.remove(btnew.key);			  
		  }
		  return partList;

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
			
			//cria 2k objetos iniciais que serão clonados do objeto original
			List<Backtracker> possibleSolutions = new ArrayList<Backtracker>();
			List<List<Partition>> solutionList = new ArrayList<List<Partition>>(); 
			for(int i=0 ; i<kVariable*2 ; i++){
				List<Partition> listPart = new ArrayList<Partition>();
				for(int j=0 ; j<resposta.size() ; j++){
					Partition part = new Partition();
					part.size = resposta.get(j).size;
					part.htKeySize = (HashSet<String>) resposta.get(j).htKeySize.clone();
					listPart.add(part);
				}
				solutionList.add(listPart);
				possibleSolutions.add(passo(resposta,hsTableKeySizes,partitions, null,0));
			}
			resposta=null;
			//executa o passo até a temperatura zerar
			while(temperature>0){
//				if(temperature%100==0){
//					System.out.println(temperature);
//				}
				int accepted=0;
				List<Backtracker> acceptedSolutions = new ArrayList<Backtracker>();
				List<List<Partition>> objList = new ArrayList<List<Partition>>(); 
				int difSum=0;
				for(int i=0 ; i< possibleSolutions.size() ; i++){
					difSum+=possibleSolutions.get(i).dif;
				}
				int chance=difSum*possibleSolutions.size();
				for(int i=0 ; i<possibleSolutions.size() ; i++){
					chance-=possibleSolutions.get(i).dif;
				}
				Random rand = new Random();
				while(accepted!=kVariable){
					if(chance>0){
						double prob = rand.nextInt(chance);
						int found=0;
						while(prob>difSum-possibleSolutions.get(found).dif){
							prob-=difSum-possibleSolutions.get(found).dif;
							found+=1;	
						}
						chance=chance+possibleSolutions.get(found).dif-difSum;
						accepted+=1;
						acceptedSolutions.add(possibleSolutions.get(found));
						objList.add(solutionList.get(found));
						solutionList.remove(found);
						possibleSolutions.remove(found);
					} else {
						accepted+=1;
						acceptedSolutions.add(possibleSolutions.get(0));
						possibleSolutions.remove(0);
						objList.add(solutionList.get(0));
						solutionList.remove(0);
					}
				}
				for(int i=0 ; i<kVariable ; i++){
					objList.add(createLP(acceptedSolutions.get(i),possibleSolutions.get(i),solutionList.get(i)));
					acceptedSolutions.add(passo(objList.get(kVariable+i), hsTableKeySizes,partitions,acceptedSolutions.get(i),acceptedSolutions.get(i).numBt+1));
				}
				possibleSolutions=acceptedSolutions;
				solutionList=objList;
				temperature--;							
			}
			
		int bestSol=0;
		for(int i=0 ; i<possibleSolutions.size() ; i++){
			if(possibleSolutions.get(i).dif<possibleSolutions.get(bestSol).dif){
				bestSol=i;
			}
		}
		List<Partition> partList = solutionList.get(bestSol);
		Backtracker bt = possibleSolutions.get(bestSol);
		partList.get(bt.oldPart).size-=bt.keySize;
		partList.get(bt.newPart).size+=bt.keySize;
		partList.get(bt.oldPart).htKeySize.remove(bt.key);
		partList.get(bt.newPart).htKeySize.add(bt.key);
		return partList;
	}
}
//public class SBS {
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
//				int difSum=0;
//				for(int i=0 ; i< possibleSolutions.size() ; i++){
//					difSum+=calcDif(possibleSolutions.get(i));
//				}
//				int chance=difSum*possibleSolutions.size();
//				for(int i=0 ; i<possibleSolutions.size() ; i++){
//					chance-=calcDif(possibleSolutions.get(i));
//				}
//				Random rand = new Random();
//				while(accepted!=kVariable){					
//					if(chance>0){
//						double prob = rand.nextInt(chance);
//						int found=0;
//						while(prob>difSum-calcDif(possibleSolutions.get(found))){
//							prob-=difSum-calcDif(possibleSolutions.get(found));
//							found+=1;
//							
//						}
//						chance=chance+calcDif(possibleSolutions.get(found))-difSum;
//						accepted+=1;
//						acceptedSolutions.add(possibleSolutions.get(found));
//						acceptedSolutions.add(passo(possibleSolutions.get(found), partitions, hsTableKeySizes));
//						possibleSolutions.remove(found);
//					}else{
//						accepted+=1;
//						acceptedSolutions.add(possibleSolutions.get(0));
//						acceptedSolutions.add(passo(possibleSolutions.get(0), partitions, hsTableKeySizes));
//						possibleSolutions.remove(0);
//					}
//				}
//				possibleSolutions=acceptedSolutions;
////				for(int i=0 ; i<kVariable ; i++){
////					possibleSolutions.remove(kVariable);
////					possibleSolutions.add(passo(possibleSolutions.get(i), partitions));
////				}
//				
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