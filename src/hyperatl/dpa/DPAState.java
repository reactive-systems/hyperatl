package hyperatl.dpa;

public class DPAState {
	private final int stateId;

	private final String info;
	
	private int colour;
	
	private final BinaryTransitionTree transitionTree;

	public DPAState(int stateID, String info, int colour, BinaryTransitionTree transitionTree) {
		this.stateId = stateID;
		this.info = info;
		this.colour = colour;
		this.transitionTree = transitionTree;
	}

	public int getStateId() {
		return stateId;
	}

	public String getInfo() {
		return info;
	}

	public int getColour() {
		return colour;
	}

	public void setColour(int colour) {
		this.colour = colour;
	}

	public BinaryTransitionTree getTransitionTree() {
		return transitionTree;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + colour;
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		result = prime * result + stateId;
		result = prime * result + ((transitionTree == null) ? 0 : transitionTree.hashCode());
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
		DPAState other = (DPAState) obj;
		if (colour != other.colour)
			return false;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		if (stateId != other.stateId)
			return false;
		if (transitionTree == null) {
			if (other.transitionTree != null)
				return false;
		} else if (!transitionTree.equals(other.transitionTree))
			return false;
		return true;
	}
	
}
