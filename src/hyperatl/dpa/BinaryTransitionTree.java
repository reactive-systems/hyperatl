package hyperatl.dpa;

public abstract class BinaryTransitionTree {

	public abstract int lookUp(boolean[] atomicPropositions);
	
	public abstract boolean equals(Object obj);
	public abstract int hashCode();
	
}
