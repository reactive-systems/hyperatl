package hyperatl.pl;

import hyperatl.cgs.*;
import hyperatl.pl.Statement.Player;

import java.util.*;
import java.util.Map.Entry;

public class ProgramTransformer {
	
	// Transforms programs to a CGS
	public static CGS transformToCGS(Program program) {
		
		Statement statement = program.getStatement();
		Map<String, Integer> varDomain = program.getDomain();

		VariableState initVarState = new VariableState(varDomain);
		
		ProgramState initState = new ProgramState(statement, initVarState);
		
		Queue<ProgramState> U = new LinkedList<>();
		U.add(initState);
		
		
		Map<ProgramState, Integer> statesToCGSId = new HashMap<>();
		
		int counter = 0;
		statesToCGSId.put(initState, counter);
		
		// Compute the total number of APs and a unique order on the variable names
		List<String> varNameOrder = new ArrayList<>(varDomain.size());
		
		int apCount = 0;
		for(Entry<String, Integer> en : varDomain.entrySet()) {
			apCount += en.getValue();
			varNameOrder.add(en.getKey());
		}
		
		
		////////////////////// - Create CGS
		CGS cgs = new CGS();
		cgs.setNumberOfAgents(3);
		Map<Integer, Integer> agentLevelMap = new HashMap<>();
		agentLevelMap.put(0, 0);
		agentLevelMap.put(1, 0);
		agentLevelMap.put(2, 0);
		cgs.setAgentLevelMap(agentLevelMap);
		
		cgs.setInitialState(0);
		
		List<String> atomicProps = new ArrayList<>();
		
		for(String var : varNameOrder) {
			
			int d = varDomain.get(var);
			for(int i = 0; i < d; i++) {
				atomicProps.add(var + i);
			}
		}
		
		cgs.setAtomicPropositions(atomicProps);
	
		////////////////////////////
		
		while(!U.isEmpty()) {
			ProgramState pstate = U.poll();

			// Compute all successors of pstate
			List<ProgramState> succList = pstate.getStatement().semantics(pstate.getVarState());

			List<Integer> succIdList = new ArrayList<>();
			
			for(ProgramState succpstate : succList) {
				if(statesToCGSId.containsKey(succpstate)) {
					int id = statesToCGSId.get(succpstate);
					succIdList.add(id);
				} else {
					// Compute the atomic propositions
					counter++;
					statesToCGSId.put(succpstate, counter);
					succIdList.add(counter);
					
					U.add(succpstate);
				}
				
			}
			
			boolean[] aps = computeApVector(apCount, varNameOrder, pstate.getVarState());
			int cgsstateId = statesToCGSId.get(pstate);
			CGSState cgsstate = createCgsState(cgsstateId, pstate.getStatement().getPlayer(), aps, succIdList);
			cgs.addState(cgsstate);
		}
		
		
		return cgs;
	}
	
	
	
	private static boolean[] computeApVector(int apCount,  List<String> varNameOrder, VariableState varState) {
		boolean[] aps = new boolean[apCount];
		
		int j = 0;
		for(String var : varNameOrder) {
			boolean[] b = varState.lookUp(var);
			for (boolean value : b) {
				aps[j] = value;
				j++;
			}
		}
		
		return aps;
	}
	
	
	private static DecisionTree generateDT(int agentID, List<Integer> succ) {
		List<DecisionTree> subTreeList = new ArrayList<>(succ.size());
		
		for(int i : succ) {
			subTreeList.add(new DecisionTreeLeaf(i));
		}
		
		return new DecisionTreeInner(agentID, subTreeList.size(), subTreeList);
	}

	private static CGSState createCgsState(int id, Player player, boolean[] aps, List<Integer> succ) {
		int agentId = switch (player) {
			case NONDET -> 2;
			case HIGH -> 0;
			case LOW -> 1;
			default -> 0;
		};

		List<Integer> numberOfStrategies = new ArrayList<>(3);
		numberOfStrategies.add(1);
		numberOfStrategies.add(1);
		numberOfStrategies.add(1);

		numberOfStrategies.set(agentId, succ.size());

		CGSState cgsstate = new CGSState(id, null, aps, numberOfStrategies);

		DecisionTree dt = generateDT(agentId, succ);
		cgsstate.setDecisionTree(dt);

		return cgsstate;
	}
}
