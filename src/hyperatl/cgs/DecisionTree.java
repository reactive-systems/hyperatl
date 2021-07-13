package hyperatl.cgs;

import java.util.Map;
import java.util.Set;

public abstract class DecisionTree {
	public abstract int lookUp(int[] strategyprofile);
	
	public abstract int lookUp(Map<Integer, Integer> moveVector);
	
	
	public abstract DecisionTree remapIds(Map<Integer, Integer> mapping);
	
	public abstract Set<Integer> computeSuccessorStates(Map<Integer, Integer> partialProfile);
	
	public abstract boolean equals(Object obj);
	public abstract int hashCode();
	
}
