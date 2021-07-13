package hyperatl.formula;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class HyperATLFormula {
	
	public enum Transformation {
		None,
		Stutter,
		Shift
	}
	
	private final List<String> pathVariables;
	
	private final List<List<Integer>> agentSets;
	
	private final List<Transformation> transformations;
	
	private String LTLformula;

	public HyperATLFormula() {
		this.pathVariables = new LinkedList<>();
		this.agentSets = new LinkedList<>();
		this.transformations = new LinkedList<>();
	}

	public String getLTLformula() {
		return LTLformula;
	}

	public void setLTLformula(String lTLformula) {
		LTLformula = lTLformula;
	}

	public List<String> getPathVariables() {
		return pathVariables;
	}

	public List<List<Integer>> getAgentSets() {
		return agentSets;
	}

	public List<Transformation> getTransformations() {
		return transformations;
	}

	public void addQuantifier(String pathVar, Set<Integer> agents, Transformation trans) {
		
		pathVariables.add(pathVar);
		agentSets.add(asSortedList(agents));
		transformations.add(trans);
	}

	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new LinkedList<>(c);
	  java.util.Collections.sort(list);
	  return list;
	}
}
