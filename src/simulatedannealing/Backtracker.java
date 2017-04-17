package simulatedannealing;

import java.util.List;

public class Backtracker {
	List<Partition> origin;
	String key;
	int keySize;
	int oldPart;
	int newPart;
	int dif;
	Backtracker oldbt;
	Backtracker newbt;
	int numBt;
}
