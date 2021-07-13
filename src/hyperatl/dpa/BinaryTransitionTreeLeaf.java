package hyperatl.dpa;

public class BinaryTransitionTreeLeaf extends BinaryTransitionTree {
	
	int stateId;
	
	public BinaryTransitionTreeLeaf(int stateId) {
		this.stateId = stateId;
	}

	@Override
	public int lookUp(boolean[] atomicPropositions) {
		return stateId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		BinaryTransitionTreeLeaf other = (BinaryTransitionTreeLeaf) obj;
		if (stateId != other.stateId)
			return false;
		return true;
	}
}
