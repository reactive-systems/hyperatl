package hyperatl.pg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PGStateSimple {

	private final List<Integer> systemStates;
	
	private final int automatonState;


	public int getAutomatonState() {
		return automatonState;
	}

	public PGStateSimple(List<Integer> systemStates, int automatonState) {
		this.systemStates = systemStates;
		this.automatonState = automatonState;
	}

	public PGStatePartialChoice convert() {
		List<Map<Integer, Integer>> emptyMoveVectorList = new ArrayList<>(systemStates.size());
		
		for(int i = 0; i < systemStates.size(); i++) {
			emptyMoveVectorList.add(new HashMap<>());
		}

		return new PGStatePartialChoice(systemStates, emptyMoveVectorList, automatonState, 0, true);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + automatonState;
		result = prime * result + ((systemStates == null) ? 0 : systemStates.hashCode());
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
		PGStateSimple other = (PGStateSimple) obj;
		if (automatonState != other.automatonState)
			return false;
		if (systemStates == null) {
			if (other.systemStates != null)
				return false;
		} else if (!systemStates.equals(other.systemStates))
			return false;
		return true;
	}
	
	
	
}
