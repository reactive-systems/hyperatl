package hyperatl.cgs;


import java.util.Arrays;
import java.util.List;


public class CGSState {

	private final int stateId;

	private final String info;

	// Boolean Vector that indicates which APs are true
	private final boolean[] atomicPropositions;

	// Number of stratgies for each agent
	private final List<Integer> numberOfStratgies;

	// Successor States given as a DT
	private DecisionTree decisionTree;

	public DecisionTree getDecisionTree() {
		return decisionTree;
	}

	public void setDecisionTree(DecisionTree decisionTree) {
		this.decisionTree = decisionTree;
	}

	public CGSState(int stateId, String info, boolean[] atomicPropositions, List<Integer> numberOfStratgies)
	{
		this.stateId = stateId;
		this.info = info;
		this.atomicPropositions = atomicPropositions;
		this.numberOfStratgies = numberOfStratgies;
	}

	public int getStateId()
	{
		return stateId;
	}

	public String getInfo()
	{
		return info;
	}

	public boolean[] getAtomicProps()
	{
		return atomicPropositions;
	}

	public List<Integer> getNumberOfStratgies()
	{
		return numberOfStratgies;
	}

	@Override
	public String toString()
	{
		return stateId +
				" " +
				(info == null ? "" : info+" ");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atomicPropositions == null) ? 0 : Arrays.hashCode(atomicPropositions));
		result = prime * result + ((decisionTree == null) ? 0 : decisionTree.hashCode());
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		result = prime * result + ((numberOfStratgies == null) ? 0 : numberOfStratgies.hashCode());
		result = prime * result + stateId;
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
		CGSState other = (CGSState) obj;
		if (atomicPropositions == null) {
			if (other.atomicPropositions != null)
				return false;
		} else if (!Arrays.equals(atomicPropositions, other.atomicPropositions))
			return false;
		if (decisionTree == null) {
			if (other.decisionTree != null)
				return false;
		} else if (!decisionTree.equals(other.decisionTree))
			return false;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		if (numberOfStratgies == null) {
			if (other.numberOfStratgies != null)
				return false;
		} else if (!numberOfStratgies.equals(other.numberOfStratgies))
			return false;
		return stateId == other.stateId;
	}
	
}
