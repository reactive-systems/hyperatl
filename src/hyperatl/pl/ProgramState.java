package hyperatl.pl;

public class ProgramState {

	private Statement statement;
	private VariableState varState;
	
	public ProgramState(Statement statement, VariableState varState) {
		this.statement = statement;
		this.varState = varState;
	}

	public Statement getStatement() {
		return statement;
	}

	public VariableState getVarState() {
		return varState;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((statement == null) ? 0 : statement.hashCode());
		result = prime * result + ((varState == null) ? 0 : varState.hashCode());
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
		ProgramState other = (ProgramState) obj;
		if (statement == null) {
			if (other.statement != null)
				return false;
		} else if (!statement.equals(other.statement))
			return false;
		if (varState == null) {
			if (other.varState != null)
				return false;
		} else if (!varState.equals(other.varState))
			return false;
		return true;
	}
	
	
	

}
