package hyperatl.dpa;

public class BinaryTransitionTreeInner extends BinaryTransitionTree {

	int apIndex;
	
	BinaryTransitionTree left;
	BinaryTransitionTree right;
	
	public BinaryTransitionTreeInner(int apIndex, BinaryTransitionTree left, BinaryTransitionTree right) {
		this.apIndex = apIndex;
		this.left = left;
		this.right = right;
	}

	@Override
	public int lookUp(boolean[] atomicPropositions) {
		if(!atomicPropositions[apIndex]) {
			return left.lookUp(atomicPropositions);
		} else {
			return right.lookUp(atomicPropositions);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + apIndex;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
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
		BinaryTransitionTreeInner other = (BinaryTransitionTreeInner) obj;
		if (apIndex != other.apIndex)
			return false;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
}
