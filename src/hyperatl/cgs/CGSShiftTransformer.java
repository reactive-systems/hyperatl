package hyperatl.cgs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/*
Transforms a CGS by shifting it by a specified number of steps. Used to create an offset between each copies
 */
public abstract class CGSShiftTransformer {

	public static CGS createShiftedVersion(CGS original, int shift) {
		
		CGS cgs = new CGS();

		cgs.setNumberOfAgents(original.getNumberOfAgents());
				
		cgs.setAtomicPropositions(new ArrayList<>(original.getAtomicPropositions()));
		
		cgs.setAgentLevelMap(new HashMap<>(original.getAgentLevelMap()));
		
		Map<Integer, CGSState> indexToState = new HashMap<>();
		
		int maxId = 0;
		
		for(Entry<Integer, CGSState> en : original.getIndexToState().entrySet()) {
			
			CGSState s = en.getValue();
			
			// Copy the state to the new CGS
			boolean[] apVector = new boolean[cgs.getAtomicPropositions().size()];
			for(int i = 0; i < original.getAtomicPropositions().size(); i++) {
				apVector[i] = s.getAtomicProps()[i];
			}
			
			List<Integer> newStratCount = new ArrayList<>(s.getNumberOfStratgies());
			
			CGSState newState = new CGSState(s.getStateId(), null, apVector, newStratCount);
			indexToState.put(s.getStateId(), newState);
			newState.setDecisionTree(s.getDecisionTree());
			
			if(s.getStateId() > maxId) {
				maxId = s.getStateId();
			}
		}
		
		maxId++;
		int currentStart = original.getInitialState();

		// Now create the offset nodes at the beginning
		for(int i = 0; i < shift; i++) {
			
			// No aps holds for the added states
			boolean[] apVector = new boolean[cgs.getAtomicPropositions().size()];
			for(int j = 0; j < original.getAtomicPropositions().size(); j++) {
				apVector[j] = false;
			}
			// Only a single move from this state
			List<Integer> newStratCount = new ArrayList<>();
			for(int a = 0; a < original.getNumberOfAgents(); a++) {
				newStratCount.add(1);
			}

			CGSState newState = new CGSState(maxId, null, apVector, newStratCount);
			indexToState.put(maxId, newState);

			DecisionTree dt = new DecisionTreeLeaf(currentStart);
			newState.setDecisionTree(dt);
			
			currentStart = maxId;
			// Increase for next iteration
			maxId++;
		}
		cgs.setIndexToState(indexToState);
		
		cgs.setInitialState(currentStart);

		return cgs;
	}

}
