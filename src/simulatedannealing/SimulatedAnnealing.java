package simulatedannealing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SimulatedAnnealing{

	/*entrada:
	 * hsTableNewKeys: hashtable com as chaves ainda não particionadas
	 * hsTableSize: hashtable com o tamanho de todas as chaves já particionadas
	 * partSizes: Lista com o tamanho de todas as partições
	 * partitions: número de partições
	 * temperature: quantidade de passos a serem executados
	*/
	public List<Partition> simulatedAnnealing(Hashtable<String, Integer> hsNewKeys,Hashtable<String, Integer> hsTableKeySizes, List<Integer> partSizes,
		      int partitions, int temperature){
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
					resposta.get(destino).size+=hsTableKeySizes.get(key);
					resposta.get(destino).htKeySize.add(key);
				}
		//	}
			
		//executa o passo até a temperatura zerar
			while(temperature>0){
				int biggerPart=0;
				int smallerPart=0;
				List<Integer> sizePart = new ArrayList<Integer>();
				for(int i=0 ; i<resposta.size() ; i++){
					sizePart.add(resposta.get(i).size);
					if(resposta.get(i).size>resposta.get(biggerPart).size){
						biggerPart=i;
					} else if(resposta.get(i).size<resposta.get(smallerPart).size){
						smallerPart=i;
					}
				}
				Random rand = new Random();
				int numPart1 = biggerPart;
				int numPart2 = smallerPart;
				int dif1=sizePart.get(numPart1)-sizePart.get(numPart2);
				if(resposta.get(numPart1).htKeySize.size()>0){
					int numEle1 = rand.nextInt(resposta.get(numPart1).htKeySize.size());
					String stringEle1 = new ArrayList<String>(resposta.get(numPart1).htKeySize).get(numEle1);
					int sizeEle1 = hsTableKeySizes.get(stringEle1);
					sizePart.set(numPart1, sizePart.get(numPart1)-sizeEle1);
					sizePart.set(numPart2, sizePart.get(numPart2)+sizeEle1);					
					int dif2=0;
					for(int i=0 ; i<resposta.size() ; i++){
						if(sizePart.get(i)>sizePart.get(biggerPart)){
							biggerPart=i;
						} else if(sizePart.get(i)<sizePart.get(smallerPart)){
							smallerPart=i;
						}
					}
					dif2=sizePart.get(biggerPart)-sizePart.get(smallerPart);
					if(dif2<dif1){
						resposta.get(numPart1).htKeySize.remove(stringEle1);
						resposta.get(numPart1).size-=sizeEle1;
						resposta.get(numPart2).htKeySize.add(stringEle1);
						resposta.get(numPart2).size+=sizeEle1;
					} else {
						double alfa=Math.exp((dif2 * 1.00d - dif1 * 1.00d) / temperature * 1.00d);
						if(alfa>rand.nextDouble()){
							resposta.get(numPart1).htKeySize.remove(stringEle1);
							resposta.get(numPart1).size-=sizeEle1;
							resposta.get(numPart2).htKeySize.add(stringEle1);
							resposta.get(numPart2).size+=sizeEle1;
						}
					}					
				}								
				temperature--;
			}
		return resposta;
	}
}