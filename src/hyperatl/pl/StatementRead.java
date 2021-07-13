package hyperatl.pl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatementRead extends Statement {

	
	public String var;
	
	public int channel;
	
	public boolean highSecurity;
	
	public StatementRead(String var, int channel, boolean highSecuity) {
		this.var = var;
		this.channel = channel;
		this.highSecurity = highSecuity;
	}
	
	@Override
	public List<ProgramState> semantics(VariableState varState) {
		
		int bitwidth = varState.lookUp(var).length;
		assert(bitwidth < 32);
		
		Statement term = new StatementTerm();
		
		List<ProgramState> l = new ArrayList<>(1);
		
		for(boolean[] b : enumerateCombination(bitwidth)) {
			
			VariableState copy = new VariableState(varState);
			copy.update(var, b);
			
			ProgramState succ = new ProgramState(term, copy);
			l.add(succ);
			
		}
		
		return l;
	}
	
	@Override
	public Player getPlayer() {
		return highSecurity ? Player.HIGH : Player.LOW;
	}

	private static List<boolean[]> enumerateCombination(int n){
		
		List<boolean[]> l = new ArrayList<>((int)Math.pow(2, n));
		
		if(n == 0) {
			boolean[] empty = new boolean[0];
			l.add(empty);
		} else {
			List<boolean[]> recL = enumerateCombination(n-1);
			
			for(boolean[] b : recL) {
				
				boolean[] eT = new boolean[n];
				System.arraycopy(b, 0, eT, 0, n-1);
				
				boolean[] eF = new boolean[n];
				System.arraycopy(b, 0, eF, 0, n-1);
				
				eT[n-1] = true;
				eF[n-1] = false;
				l.add(eT);
				l.add(eF);
			}
		}
		
		return l;
	}

	@Override
	public Set<String> usedVars() {
		Set<String> used = new HashSet<>();
		used.add(var);
		
		return used;
	}
	
	@Override
	public Set<Integer> usedChannels() {
		Set<Integer> used = new HashSet<>();
		used.add(channel);
		return used;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + channel;
		result = prime * result + (highSecurity ? 1231 : 1237);
		result = prime * result + ((var == null) ? 0 : var.hashCode());
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
		StatementRead other = (StatementRead) obj;
		if (channel != other.channel)
			return false;
		if (highSecurity != other.highSecurity)
			return false;
		if (var == null) {
			if (other.var != null)
				return false;
		} else if (!var.equals(other.var))
			return false;
		return true;
	}
}
