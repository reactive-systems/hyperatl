package hyperatl.cgs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/*
Transforms a CGS by including a stutter player
 */
public class CGSStutterTransformer {

	public static CGS createStutterVersion(CGS original) {
		
		CGS cgs = new CGS();

		cgs.setNumberOfAgents(original.getNumberOfAgents()+1);

		int schedID = original.getNumberOfAgents();
		
		List<String> aps = new ArrayList<>(original.getAtomicPropositions());
		assert(!aps.contains("stut"));
		aps.add("stut");

		cgs.setAtomicPropositions(aps);
		
		// Get maximal entry
		Map<Integer, Integer> agentLevelMap = new HashMap<>(original.getAgentLevelMap());
		int m = 0;
		for(Entry<Integer, Integer> en : agentLevelMap.entrySet()) {
			if(en.getValue() > m) {
				m = en.getValue();
			}
		}
		
		agentLevelMap.put(schedID, m+1);
		
		cgs.setAgentLevelMap(agentLevelMap);

		int maxStateID = 0;
		
		for(Entry<Integer, CGSState> en : original.getIndexToState().entrySet()) {
			if(en.getValue().getStateId() > maxStateID) {
				maxStateID = en.getValue().getStateId();
			}
		}
		
		// Use for giving unqiue ID to new dummy states
		int currentCount = maxStateID;

		Map<Integer, CGSState> indexToState = new HashMap<>();

		// Iterate over every possible key, value state pair
		for(Entry<Integer, CGSState> en : original.getIndexToState().entrySet()) {
				
			CGSState s = en.getValue();
			
			// Create the non-stutter version
			boolean[] apVector = new boolean[cgs.getAtomicPropositions().size()];
			for(int i = 0; i < original.getAtomicPropositions().size(); i++) {
				apVector[i] = s.getAtomicProps()[i];
			}
			
			// Set last entry to false as those are non stutter states
			apVector[cgs.getAtomicPropositions().size()-1] = false;

			List<Integer> newStratCount = new ArrayList<>(s.getNumberOfStratgies());
			// Add two choices for the new player
			newStratCount.add(2);
			
			CGSState newStateNoStutter = new CGSState(s.getStateId(), null, apVector, newStratCount);
			indexToState.put(s.getStateId(), newStateNoStutter);
			
			// Create the stutter version
			apVector = new boolean[cgs.getAtomicPropositions().size()];
			for(int i = 0; i < original.getAtomicPropositions().size(); i++) {
				apVector[i] = s.getAtomicProps()[i];
			}
			
			// Set last entry to true
			apVector[cgs.getAtomicPropositions().size()-1] = true;
			
			currentCount++;
			CGSState newStateStutter = new CGSState(currentCount, null, apVector, newStratCount);
			indexToState.put(currentCount, newStateStutter);

			// tre for self loop on stuttering state
			DecisionTree selfLoop = new DecisionTreeLeaf(currentCount);

			List<DecisionTree> l = new ArrayList<>(2);
			l.add(selfLoop);
			l.add(s.getDecisionTree());
			
			DecisionTree extendedTree = new DecisionTreeInner(schedID, 2, l);
			
			newStateNoStutter.setDecisionTree(extendedTree);
			newStateStutter.setDecisionTree(extendedTree);
		
		}
		cgs.setIndexToState(indexToState);
		cgs.setInitialState(original.getInitialState());
		
		return cgs;
	}

}
