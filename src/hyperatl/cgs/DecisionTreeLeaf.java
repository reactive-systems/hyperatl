package hyperatl.cgs;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DecisionTreeLeaf extends DecisionTree {

	private final int stateID;
	
	public DecisionTreeLeaf(int stateID) {
		this.stateID = stateID;
	}
	
	@Override
	public int lookUp(int[] strategyprofile) {
		
		return stateID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + stateID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DecisionTreeLeaf other = (DecisionTreeLeaf) obj;
		if (stateID != other.stateID)
			return false;
		return true;
	}

	@Override
	public Set<Integer> computeSuccessorStates(Map<Integer, Integer> partialProfile) {
		HashSet<Integer> set = new HashSet<>();
		set.add(stateID);
		return set;
	}

	@Override
	public int lookUp(Map<Integer, Integer> moveVector) {
		return stateID;
	}

	@Override
	public DecisionTree remapIds(Map<Integer, Integer> mapping) {
		return new DecisionTreeLeaf(mapping.get(stateID));
		
	}
	
	
	
}
