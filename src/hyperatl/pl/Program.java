package hyperatl.pl;

import java.util.HashMap;
import java.util.Map;

public class Program {
	
	private Statement statement;
	
	private final Map<String, Integer> domain;
	
	private String name;
	
	
	public Program() {
		domain = new HashMap<>();
	}
	
	public Program(Statement statement, Map<String, Integer> domain) {
		this.statement = statement;
		this.domain = domain;
	}

	public Statement getStatement() {
		return statement;
	}


	public void setStatement(Statement statement) {
		this.statement = statement;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	
	public Map<String, Integer> getDomain() {
		return domain;
	}

}
