package hyperatl.pl;

import java.util.HashMap;
import java.util.Map;

public class VariableState {
	
	private final Map<String, boolean[]> varMap;

	// Create an initial Variable map of the specified arity
	public VariableState(Map<String, Integer> domain) {
		
		varMap = new HashMap<>(domain.size());
		
		for(String s : domain.keySet()) {
			
			boolean[] d = new boolean[domain.get(s)];
			varMap.put(s, d);
		}
	}

	// Create a copy
	public VariableState(VariableState state) {
		varMap = new HashMap<>(state.getVarMap().size());
		
		for(String var : state.getVarMap().keySet()) {
			boolean[] val = state.getVarMap().get(var);
			
			boolean[] copy = new boolean[val.length];
			
			System.arraycopy(val, 0, copy, 0, val.length);
			
			varMap.put(var, copy);
		}
	}

	public Map<String, boolean[]> getVarMap(){
		return varMap;
	}
	
	public boolean[] lookUp(String varId) {
		return varMap.get(varId);
	}
	
	public void update(String var, boolean[] value) {
		varMap.put(var, value);
	}
	
	@Override
	public String toString() {
		return "ProgramState [varMap=" + varMap + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// Not needed as in our case this value is always the same
		//result = prime * result + ((varMap == null) ? 0 : varMap.keySet().hashCode());
		
		int temp = 0;
		
		for(String s : varMap.keySet()) {
			temp += java.util.Arrays.hashCode(varMap.get(s));
		}
		
		result = prime * result + temp;
		
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
		VariableState other = (VariableState) obj;
		
		
		Map<String, boolean[]> varMapOther = other.varMap;
		
		if(!varMap.keySet().equals(varMapOther.keySet())){
			return false;
		}
		
		for(String s : varMap.keySet()) {
			
			if(!java.util.Arrays.equals(varMap.get(s), varMapOther.get(s)))
				return false;
		}
		
		return true;
		
	}
}
