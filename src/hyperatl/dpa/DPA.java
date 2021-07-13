package hyperatl.dpa;

import jhoafparser.ast.AtomLabel;
import jhoafparser.ast.BooleanExpression;
import jhoafparser.storage.StoredAutomaton;
import jhoafparser.storage.StoredEdgeWithLabel;
import jhoafparser.storage.StoredState;

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DPA {

	private final Map<Integer, DPAState> indexToState;
	
	private int initialStateId;
	
	private List<String> atomicPropositions;

	private int maxColour;

	public List<String> getAtomicPropositions() {
		return atomicPropositions;
	}

	public void switchMaxMin() {
		
		// Get even upper bound
		int upperBound = maxColour % 2 == 0 ? maxColour : maxColour + 1;
		
		for(DPAState state : indexToState.values()) {
			state.setColour(upperBound - state.getColour()); 
		}
		
		this.maxColour = upperBound;
	}
	
	public void switchEvenOdd() {
		for(DPAState state : indexToState.values()) {
			state.setColour(state.getColour() + 1); 
		}
		this.maxColour++;
	}

	public void updateMaxColour(int colour) {
		this.maxColour = Integer.max(maxColour, colour);
	}

	public DPA() {
		indexToState = new TreeMap<>();

		initialStateId = -1;
		maxColour = -1;
	}

	public void setAtomicPropositions(List<String> atomicPropositions) {
		this.atomicPropositions = atomicPropositions;
	}

	public static DPA getDpaFromStoredAutomaton(StoredAutomaton aut) {
		
		DPA dpa = new DPA();

		if(aut.hasEdgesImplicit()) {
			System.out.println("The conversion of storeAutomata to DPA is only supported for automata with explicit labels");
			System.exit(0);
		}
		
		if(aut.hasUniversalBranching()){
			System.out.println("Only support for non-universal automata");
			System.exit(0);
		}

		// Create Self-Loop on Sink
		BinaryTransitionTree selfloop = new BinaryTransitionTreeLeaf(aut.getNumberOfStates());
		DPAState loosingSink = new DPAState(aut.getNumberOfStates(), null, 0, selfloop);
		
		dpa.addState(loosingSink);

		for(StoredState s : aut.getStoredStates()) {
			int stateId = s.getStateId();
			String info = s.getInfo();
			
			// Set colour to the highest possible colour (in case the state does not have a colour)
			// Per specification this is the number of sets - 1
			int colour = aut.getStoredHeader().getNumberOfAcceptanceSets() - 1;
			
			if(!s.getAccSignature().isEmpty()) {
				colour = s.getAccSignature().get(0);
			}
			
			dpa.updateMaxColour(colour); 

			// Get explicit edges from current state
			Iterable<StoredEdgeWithLabel> explictEdges = aut.getEdgesWithLabel(stateId);
			
			// Compute Tree Representation of edges
			BinaryTransitionTree transitionTree = computeBinaryTransitionTree(explictEdges, aut.getStoredHeader().getAPs().size(), loosingSink.getStateId());

			DPAState state = new DPAState(stateId, info, colour, transitionTree);
			
			dpa.addState(state);
		}
		
		dpa.setInitialStateId(aut.getStoredHeader().getStartStates().get(0).get(0));
		dpa.setAtomicPropositions(aut.getStoredHeader().getAPs());
		
		return dpa;
	}

	public int getInitialStateId() {
		return initialStateId;
	}

	public void setInitialStateId(int initialStateId) {
		this.initialStateId = initialStateId;
	}

	private static BinaryTransitionTree computeBinaryTransitionTree(Iterable<StoredEdgeWithLabel> explicitEdges, int numberOfAtomicPropositions, int loosingSinkId) {
		
		BitSet partialAssignment = new BitSet(numberOfAtomicPropositions);
		// Call recursive Procedure
		return recComputeBinaryTransitionTree(explicitEdges, numberOfAtomicPropositions, loosingSinkId, 0, partialAssignment);
	}

	private static BinaryTransitionTree recComputeBinaryTransitionTree(Iterable<StoredEdgeWithLabel> explicitEdges, int numberOfAtomicPropositions, int loosingSinkId, int currentAP, BitSet partialAssignment) {
		
		if(currentAP == numberOfAtomicPropositions) {
			
			for(StoredEdgeWithLabel edge : explicitEdges) {
				
				if(satisfiesBooleanCombination(edge.getLabelExpr(), partialAssignment)) {
					// Found a satisfying edge
					
					int sucStateId = edge.getConjSuccessors().get(0);
					
					return new BinaryTransitionTreeLeaf(sucStateId);
				}
			}
			
			// No edge found for the current Assignment, move to loosing sink
			return new BinaryTransitionTreeLeaf(loosingSinkId);
			
		} else {
			// Compute Left
			partialAssignment.set(currentAP, false);
			BinaryTransitionTree leftSubTree = recComputeBinaryTransitionTree(explicitEdges, numberOfAtomicPropositions, loosingSinkId, currentAP+1, partialAssignment);

			// Compute Right
			partialAssignment.set(currentAP, true);
			BinaryTransitionTree righttSubTree = recComputeBinaryTransitionTree(explicitEdges, numberOfAtomicPropositions, loosingSinkId, currentAP+1, partialAssignment);
			
			return new BinaryTransitionTreeInner(currentAP, leftSubTree, righttSubTree);
		}
		
	}

	private static boolean satisfiesBooleanCombination(BooleanExpression<AtomLabel> labelExpr, BitSet assignment) {

		if(labelExpr.isAtom()) {
			AtomLabel atom = labelExpr.getAtom();		
			int index = atom.getAPIndex();		
			return assignment.get(index);
		} 
		
		if(labelExpr.isTRUE())
			return true;
		
		if(labelExpr.isFALSE())
			return false;

		if(labelExpr.isAND()) {
			
			BooleanExpression<AtomLabel> left = labelExpr.getLeft();
			BooleanExpression<AtomLabel> right = labelExpr.getRight();
			
			return satisfiesBooleanCombination(left, assignment) && satisfiesBooleanCombination(right, assignment);
		}
		
		if(labelExpr.isOR()) {
			
			BooleanExpression<AtomLabel> left = labelExpr.getLeft();
			BooleanExpression<AtomLabel> right = labelExpr.getRight();
			
			return satisfiesBooleanCombination(left, assignment) || satisfiesBooleanCombination(right, assignment);
		}
		
		if(labelExpr.isNOT()) {
			BooleanExpression<AtomLabel> left = labelExpr.getLeft();
			
			return !satisfiesBooleanCombination(left, assignment);
		}
		
		// Should never be reachable
		return false;
	}

	public void addState(DPAState state) throws UnsupportedOperationException {
		Integer stateId = state.getStateId();
		if (indexToState.containsKey(stateId)) {
			throw new UnsupportedOperationException("Duplicate state (id = "+stateId+")");
		}
		indexToState.put(stateId, state);
	}

	public DPAState getState(int stateId) {
		return indexToState.get(stateId);
	}
}
