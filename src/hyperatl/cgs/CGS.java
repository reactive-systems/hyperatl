package hyperatl.cgs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CGS {

	// Maps the stateid to the state
	private Map<Integer, CGSState> indexToState;

	// Number of states in this CGS
	private int numberOfStates;
	
	private int numberOfAgents;
	
	private int initialState;

	// Ordered List of all atomic propositions. Used to compute the alignment with the APs in the formula
	private List<String> atomicPropositions;
	
	// Map each agent to the level of informedness, i.e., the order in which thy choose their move
	private Map<Integer, Integer> agentLevelMap;
	
	private String name;
	
	
	public CGS() {
		indexToState = new HashMap<>();
	}

	public CGSState getState(int stateId) {
		return indexToState.get(stateId);
	}

	public void setIndexToState(Map<Integer, CGSState> indexToState) {
		this.indexToState = indexToState;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAtomicPropositions() {
		return atomicPropositions;
	}

	public void setAtomicPropositions(List<String> atomicPropositions) {
		this.atomicPropositions = atomicPropositions;
	}

	public int getInitialState() {
		return initialState;
	}

	public void setInitialState(int initialState) {
		this.initialState = initialState;
	}


	public void addState(CGSState state) {
		int stateId = state.getStateId();
		if (indexToState.containsKey(stateId)) {
			throw new UnsupportedOperationException("Duplicate state (id = "+stateId+")");
		}
		indexToState.put(stateId, state);
	}

	public Map<Integer, Integer> getAgentLevelMap() {
		return agentLevelMap;
	}

	public void setAgentLevelMap(Map<Integer, Integer> agentLevelMap) {
		this.agentLevelMap = agentLevelMap;
	}

	public int getNumberOfStates() {
		return numberOfStates;
	}

	public void setNumberOfStates(int numberOfStates) {
		this.numberOfStates = numberOfStates;
	}

	public int getNumberOfAgents() {
		return numberOfAgents;
	}

	public void setNumberOfAgents(int numberOfAgents) {
		this.numberOfAgents = numberOfAgents;
	}

	public Map<Integer, CGSState> getIndexToState() {
		return indexToState;
	}

}
