package hyperatl.cgs;

import java.util.*;

public class DecisionTreeInner extends DecisionTree {

	private final int agentId;
	
	private final int numberOfStratgies;
	
	private final List<DecisionTree> childs;
	
	
	public DecisionTreeInner(int agentId, int numberOfStrategies, List<DecisionTree> childs) {
		this.agentId = agentId;
		this.numberOfStratgies = numberOfStrategies;
		this.childs = childs;
		
		assert(childs.size() == numberOfStrategies);
	}


	@Override
	public int lookUp(int[] strategyprofile) {
		return childs.get(strategyprofile[agentId]).lookUp(strategyprofile);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + agentId;
		result = prime * result + ((childs == null) ? 0 : childs.hashCode());
		result = prime * result + numberOfStratgies;
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
		DecisionTreeInner other = (DecisionTreeInner) obj;
		if (agentId != other.agentId)
			return false;
		if (childs == null) {
			if (other.childs != null)
				return false;
		} else if (!childs.equals(other.childs))
			return false;
		if (numberOfStratgies != other.numberOfStratgies)
			return false;
		return true;
	}


	@Override
	public Set<Integer> computeSuccessorStates(Map<Integer, Integer> partialProfile) {
		
		if(partialProfile.containsKey(agentId)) {
			// The agent is included in the partial Assignment
			
			int strategy = partialProfile.get(agentId);
			
			return childs.get(strategy).computeSuccessorStates(partialProfile);
			
		} else {
			// Compute Union of all Subtrees
			HashSet<Integer> set = new HashSet<>();
			
			for(DecisionTree t : childs) {			
				set.addAll(t.computeSuccessorStates(partialProfile));
			}
			
			return set;
		}
	}

	@Override
	public int lookUp(Map<Integer, Integer> moveVector) {
		return childs.get(moveVector.get(agentId)).lookUp(moveVector);
	}

	@Override
	public DecisionTree remapIds(Map<Integer, Integer> mapping) {
		
		List<DecisionTree> mappedChilds = new ArrayList<>();
		
		
		for(DecisionTree t : childs) {
			mappedChilds.add(t.remapIds(mapping));
		}
		
		return new DecisionTreeInner(agentId, numberOfStratgies, mappedChilds);
	}
}
