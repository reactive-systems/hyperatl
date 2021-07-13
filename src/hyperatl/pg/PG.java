package hyperatl.pg;

import hyperatl.cgs.CGS;
import hyperatl.dpa.DPA;
import hyperatl.util.Stopwatch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public abstract class PG {

	public static void writePG(FileWriter out, List<CGS> cgsList, DPA dpa, List<List<Integer>> agentSets, List<String> pathVariables) {
		
		// Compute the alignment of the atomic propositions in both systems
		List<Map<Integer, Integer>> apAlignment = computeApAlignment(cgsList, dpa, pathVariables);
		
		Map<PGStateSimple, GraphNode> stateIdMap = new HashMap<>();

		int currentCounter = 0;

		// Create the initial state for all CGSs
		List<Integer> l = new ArrayList<>(agentSets.size());
		for(int index = 0; index < agentSets.size(); index++) {
			l.add(cgsList.get(index).getInitialState());
		}
		PGStateSimple initState = new PGStateSimple(l, dpa.getInitialStateId());

		PGStatePartialChoice initialState = initState.convert();
		
		// Add the initial graph node holding the initial CGS state
		List<GraphNode> graph = new LinkedList<>();

		GraphNode initialGNode = new GraphNode(currentCounter, initialState.getColour(dpa), 0, initialState);
		currentCounter++;
		
		// Add the mapping
		stateIdMap.put(initState, initialGNode);

		// Init a new queue
		Queue<GraphNode> U = new LinkedList<>();
		U.add(initialGNode);
		graph.add(initialGNode);
		
		
		///////////////////////
		
		// Generate agents sets by level
		
		List<List<List<Integer>>> agentsByLevel = new ArrayList<>(cgsList.size());
		
		// We can set the max Level to 2 as our CGS are the result of a program and therefore have at most two stages
		// For general CGS this can be determined given the CGS
		int maxLevel = 1;

		for(int level = 0; level <= maxLevel; level++) {
			
			List<List<Integer>> levelList = new ArrayList<>(cgsList.size());
			
			for(int index = 0; index < cgsList.size(); index++) {
				// Filter all agents on level level
				List<Integer> temp = new ArrayList<>();
				
				for(int a : agentSets.get(index)) {
					
					if(cgsList.get(index).getAgentLevelMap().get(a) == level) {
						temp.add(a);
					}
				}
				levelList.add(temp);
			}
			agentsByLevel.add(levelList);
		}
		
		
		// Anti-Agents by Level
		List<List<List<Integer>>> antiAgentsByLevel = new ArrayList<>(cgsList.size());
		
		for(int level = 0; level <= maxLevel; level++) {
			
			List<List<Integer>> levelList = new ArrayList<>(cgsList.size());
			
			for(int index = 0; index < cgsList.size(); index++) {
				
				// Filter all agents on level level
				List<Integer> temp = new ArrayList<>();
				
				// Iterate over every agent not included in the strategy
				for(int a = 0; a < cgsList.get(index).getNumberOfAgents(); a++) {
					
					if(! agentSets.get(index).contains(a) && cgsList.get(index).getAgentLevelMap().get(a) == level) {
						temp.add(a);
					}
				}
				levelList.add(temp);
			}
			antiAgentsByLevel.add(levelList);
		}

		///////////////////////////
		
		// Stop the time for pure writing and internal computations
		Stopwatch sw_writing = new Stopwatch();
		
		Stopwatch sw_succComputing = new Stopwatch();

		while(!U.isEmpty()){
			GraphNode gNode = U.poll();
			
			PGStatePartialChoice state = gNode.getContent();

			int level = state.getLevel();

			sw_succComputing.start();
			List<PGStatePartialChoice> res;
			if(state.isAgentChoice()) {
				res = state.computeSuccessors(cgsList, agentsByLevel.get(level));
			} else {
				res = state.computeSuccessors(cgsList, antiAgentsByLevel.get(level));
			}
			sw_succComputing.stop();
			
			// Create a new node for each successor
			for(PGStatePartialChoice s : res) {
				
				// If it is a player choice the player is 0, otherwise 1
				int player = s.isAgentChoice() ? 0 : 1;
				
				
				if(s.allChoicesFixed(cgsList)) {
					// All choices are fixed so we can directly compute the successor state in the system
					
					// Compute the successor state
					PGStateSimple simpleState = s.successorState(cgsList, dpa, apAlignment);
					
					// Check if this state was already seen and if so, use the existing one
					if(stateIdMap.containsKey(simpleState)) {
						// Already explored this state. So we can fetch the ID from our map and stop the unrolling
						
						GraphNode n = stateIdMap.get(simpleState);
						gNode.addSucc(n);
						
					} else {
						// If not seen create a new one
						PGStatePartialChoice pc = simpleState.convert();
						GraphNode n = new GraphNode(currentCounter, pc.getColour(dpa), 0, pc);
						currentCounter++;
						
						gNode.addSucc(n);
						
						graph.add(n);
						U.add(n);
						
						stateIdMap.put(simpleState, n);
					}
					
				} else {
					
					// Create a new node and add to queue, graph and as a successor
					// We do not check if we have seen this node as we are in a new subpart of the game
					GraphNode n = new GraphNode(currentCounter, s.getColour(dpa), player, s);
					currentCounter++;

					gNode.addSucc(n);
					
					graph.add(n);
					U.add(n);
				}
			}
		}
		
		// The graph if now complete

		int numberOfNodes = graph.size();
		System.out.println("Size of parity Game: " + numberOfNodes);
		
		// Start to write the graph to the stream
		sw_writing.start();
		
		try {
			// Header
			out.write("parity " + numberOfNodes + ";\n");
			
			for(GraphNode n : graph) {
				out.write(n.getId() + " " + n.getColour() + " " + n.getPlayer() + " ");

				List<GraphNode> suc = n.getSuccessors();
				
				for(int i = 0; i < suc.size(); i++) {
					if(i == suc.size() -1) {
						out.write(Integer.toString(suc.get(i).getId()));
					} else {
						out.write(suc.get(i).getId() + ",");
					}
				}
				out.write(" \"" + n.getContent().toString() + "\";\n");
			}
		
		} catch (IOException e) {
			System.out.println("An error occurred while writing to the output");
			e.printStackTrace();
		}
		
		sw_writing.stop();

		System.out.println("Successor Computation Took: " + sw_succComputing.getElapsedTime());
		System.out.println("Writing took: " + sw_writing.getElapsedTime());
	}

	private static List<Map<Integer, Integer>> computeApAlignment(List<CGS> cgsList, DPA dpa, List<String> pathVariables) {
		
		assert(cgsList.size() == pathVariables.size());
		
		List<String> apInDPA = dpa.getAtomicPropositions();
		
		List<Map<Integer, Integer>> apAlignment = new ArrayList<>(pathVariables.size());
		
		for(int index = 0; index < pathVariables.size(); index++) {
			
			String currentPathVariable = pathVariables.get(index);
			
			List<String> apInCGS = cgsList.get(index).getAtomicPropositions();

			Map<Integer, Integer> map = new HashMap<>();

			for(int apIndex = 0; apIndex < apInCGS.size(); apIndex++) {
				
				String ap = apInCGS.get(apIndex);
				String combinedAP = ap + "_" + currentPathVariable;
				
				// Check if the AP of this system is relevant, otherwise we can just ignore it as it is never true
				if(apInDPA.contains(combinedAP)) {
					// Get index and include in map
					map.put(apIndex, apInDPA.indexOf(combinedAP));
					
				}
			}
			apAlignment.add(map);
		}
		return apAlignment;
	}
}
