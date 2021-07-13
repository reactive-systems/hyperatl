package hyperatl.pg;

import hyperatl.cgs.CGS;
import hyperatl.cgs.CGSState;
import hyperatl.dpa.DPA;
import hyperatl.dpa.DPAState;

import java.util.*;

public class PGStatePartialChoice {

	// The actual state in each of the n-copies
	private final List<Integer> systemStateList;
	
	private final List<Map<Integer, Integer>> partialMoveVectorList;
	
	private final int automatonState;
	
	private final int level;

	private final boolean agentChoice;
	
	public PGStatePartialChoice(List<Integer> systemStateList, List<Map<Integer, Integer>> partialMoveVectorList,
			int automatonState,int level, boolean agentChoice) {
		this.systemStateList = systemStateList;
		this.partialMoveVectorList = partialMoveVectorList;
		this.automatonState = automatonState;
		this.level = level;
		this.agentChoice = agentChoice;
	}

	public int getColour(DPA dpa) {
		return dpa.getState(automatonState).getColour();
	}
	
	private int computeAutomatonSuccessorState(List<CGS> cgsList, DPA dpa, List<Map<Integer, Integer>> apAlignment) {
		// First get the current state
		DPAState dpastate = dpa.getState(automatonState);

		boolean[] bset = new boolean[dpa.getAtomicPropositions().size()];
		
		for(int index = 0; index < systemStateList.size(); index++) {
			CGS cgs = cgsList.get(index);
			
			// Get the local state in the CGS
			CGSState state = cgs.getState(systemStateList.get(index));

			// Get the alignment for the current copy
			Map<Integer, Integer> map = apAlignment.get(index);
			
			for(int apIndex = 0; apIndex < cgs.getAtomicPropositions().size(); apIndex++) {
				if(map.containsKey(apIndex)) {
					// The AP is relevant for the automaton
					
					// Get the mapped Index
					int mappedIndex = map.get(apIndex);
					
					bset[mappedIndex] = state.getAtomicProps()[apIndex];
				}
			}
		}
		return dpastate.getTransitionTree().lookUp(bset);
	}

	public int getLevel() {
		return level;
	}

	public boolean isAgentChoice() {
		return agentChoice;
	}

	// Checks if all partial move vectors are global, i.e., all agents have a chosen a move
	public boolean allChoicesFixed(List<CGS> cgsList) {
		for(int index = 0; index < partialMoveVectorList.size(); index++) {
			if(partialMoveVectorList.get(index).keySet().size() != cgsList.get(index).getNumberOfAgents()) {
				return false;
			}
		}
		
		return true;
	}
	
	public PGStateSimple successorState(List<CGS> cgsList, DPA dpa, List<Map<Integer, Integer>> apAlignment){
		
		// Only possible if all choices are already fixed
		assert(allChoicesFixed(cgsList));
		
		int automatonSuc = computeAutomatonSuccessorState(cgsList, dpa, apAlignment);
		
		
		List<Integer> successorStates = new ArrayList<>(systemStateList.size());
		
		for(int index = 0; index < partialMoveVectorList.size(); index++) {
			
			// Get the current state in the curent system
			CGS cgs = cgsList.get(index);
			CGSState state = cgs.getState(systemStateList.get(index));
			
			
			int succState = state.getDecisionTree().lookUp(partialMoveVectorList.get(index));
			successorStates.add(succState);
			
		}
		// Construct and return new simple state, the autaomaton does not progress
		return new PGStateSimple(successorStates, automatonSuc);
	}

	
	public static Set<Map<Integer, Integer>> computeIndividualNewPartialMoveVectors(CGS cgs, int stateId, List<Integer> agentList, Map<Integer, Integer> partialMoveVector) {

		CGSState state = cgs.getState(stateId);

		Set<Map<Integer, Integer>> possibleMoveVectors = new HashSet<>();

		// For all agents in agentList, define all possible strategies
		int[] ranges = new int[agentList.size()];
		
		for(int i = 0; i < agentList.size(); i++) {
			ranges[i] = state.getNumberOfStratgies().get(agentList.get(i));
		}
		
		// Build all possible combinations
		Set<List<Integer>> combinations = cartesianProductRange(ranges);

		for(List<Integer> p : combinations) {
			// Copy existing assignment
			Map<Integer, Integer> c = new HashMap<>(partialMoveVector);
			
			//Update value for agents in agentList
			for(int i = 0; i < agentList.size(); i++) {
				c.put(agentList.get(i), p.get(i));
			}
			possibleMoveVectors.add(c);
		}
		
		return possibleMoveVectors;
	}
	
	
	public List<PGStatePartialChoice> computeSuccessors(List<CGS> cgsList, List<List<Integer>> agents){
		
		List<Set<Map<Integer, Integer>>> individualMoveVectorExtensions = new ArrayList<>(cgsList.size());
		
		for(int index = 0; index < cgsList.size(); index++) {
			Set<Map<Integer, Integer>> pm = computeIndividualNewPartialMoveVectors(cgsList.get(index), systemStateList.get(index), agents.get(index), partialMoveVectorList.get(index));
			individualMoveVectorExtensions.add(pm);
		}
		
		Set<List<Map<Integer, Integer>>> combinations = cartesianProduct(individualMoveVectorExtensions);
		
		List<PGStatePartialChoice> l = new ArrayList<>(combinations.size());
		for(List<Map<Integer, Integer>> partialVector : combinations) {
			PGStatePartialChoice newState;
			if(!agentChoice) {
				// Not the choice of agents so increase level
				newState = new PGStatePartialChoice(systemStateList, partialVector, automatonState, level + 1, !agentChoice);
			} else {
				newState = new PGStatePartialChoice(systemStateList, partialVector, automatonState, level, !agentChoice);
			}
						
			l.add(newState);
		}
		
		return l;
	}

	public static Set<List<Integer>> cartesianProductRange(int... ranges) {
		if(ranges.length == 0) {
			Set<List<Integer>> empty = new HashSet<>();
			empty.add(new ArrayList<>());
			return empty;
		}
		
		return _cartesianProductRange(0, ranges);
	}
	
	private static Set<List<Integer>> _cartesianProductRange(int index, int... ranges) {
	    Set<List<Integer>> ret = new HashSet<>();
	    if (index == ranges.length) {
	        ret.add(new ArrayList<>());
	    } else {
	        for (int i = 0; i < ranges[index]; i++) {
	            for (List<Integer> set : _cartesianProductRange(index+1, ranges)) {
	            	// Add object at the front of the list
	                set.add(0, i);
	                ret.add(set);
	            }
	        }
	    }
	    return ret;
	}

	public static <T> Set<List<T>> cartesianProduct(List<Set<T>> sets) {
	    if (sets.size() < 1)
	        throw new IllegalArgumentException(
	                "Can't have a product of fewer than one set (got " +
	                sets.size() + ")");

	    return _cartesianProduct(0, sets);
	}

	private static <T> Set<List<T>> _cartesianProduct(int index, List<Set<T>> sets) {
	    Set<List<T>> ret = new HashSet<>();
	    if (index == sets.size()) {
	        ret.add(new ArrayList<>(sets.size()));
	    } else {
	        for (T obj : sets.get(index)) {
	            for (List<T> set : _cartesianProduct(index+1, sets)) {
	                set.add(0, obj);
	                ret.add(set);
	            }
	        }
	    }
	    return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + automatonState;
		result = prime * result + ((partialMoveVectorList == null) ? 0 : partialMoveVectorList.hashCode());
		result = prime * result + ((systemStateList == null) ? 0 : systemStateList.hashCode());
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
		PGStatePartialChoice other = (PGStatePartialChoice) obj;
		if (automatonState != other.automatonState)
			return false;
		if (partialMoveVectorList == null) {
			if (other.partialMoveVectorList != null)
				return false;
		} else if (!partialMoveVectorList.equals(other.partialMoveVectorList))
			return false;
		if (systemStateList == null) {
			if (other.systemStateList != null)
				return false;
		} else if (!systemStateList.equals(other.systemStateList))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[systemStateList=" + systemStateList + ", partialMoveVectorList="
				+ partialMoveVectorList + ", automatonState=" + automatonState + ", level=" + level + ", agentChoice="
				+ agentChoice + "]";
	}
}
