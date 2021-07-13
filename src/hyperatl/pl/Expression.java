package hyperatl.pl;

import java.util.HashSet;
import java.util.Set;


public class Expression {

	public enum Type {
		EXP_AND,
		EXP_OR,
		EXP_NOT,
		EXP_TRUE,
		EXP_FALSE,
		EXP_VAR,
		EXP_PROJ,
		EXP_CONCAT
	}

	private final Type type;

	private final Expression left;

	private final Expression right;

	private final String varId;
	
	private Integer projIndex;

	public Expression(Type type, Expression left, Expression right) {
		this.type=type;
		this.left=left;
		this.right=right;
		varId = null;
		projIndex = null;
	}

	public Expression(Type type, Expression left, Expression right, int index) {
		this.type=type;
		this.left=left;
		this.right=right;
		varId = null;
		projIndex = index;
	}

	public Expression(boolean value) {
		this.type = (value ? Type.EXP_TRUE : Type.EXP_FALSE);
		this.left = null;
		this.right = null;
		this.varId = null;
	}

	public Expression(String varId) {
		type = Type.EXP_VAR;
		this.left = null;
		this.right = null;
		this.varId = varId;
	}

	public Type getType() {return type;}

	public Expression getLeft() {return left;}

	public Expression getRight() {return right;}

	public String getVarId() {return varId;}
	
	public int getProjIndex() {return projIndex;}


	public boolean isAND() {return type==Type.EXP_AND;}

	public boolean isOR() {return type==Type.EXP_OR;}

	public boolean isNOT() {return type==Type.EXP_NOT;}

	public boolean isTRUE() {return type==Type.EXP_TRUE;}

	public boolean isFALSE() {return type==Type.EXP_FALSE;}

	public boolean isVar() {return type==Type.EXP_VAR;}
	
	public boolean isProj() {return type==Type.EXP_PROJ;}
	
	public boolean isConcat() {return type==Type.EXP_CONCAT;}


	public Expression and(Expression other) {
		return new Expression(Type.EXP_AND, this, other);
	}


	public Expression or(Expression other) {
		return new Expression(Type.EXP_OR, this, other);
	}


	public Expression concat(Expression other) {
		return new Expression(Type.EXP_CONCAT, this, other);
	}


	public Expression not() {
		return new Expression(Type.EXP_NOT, this, null);
	}


	public Expression proj(int index) {
		return new Expression(Type.EXP_PROJ, this, null, index);
	}
	
	public Set<String> usedVars(){
		
		Set<String> used = new HashSet<>();
		
		switch(type) {
		case EXP_AND:
		case EXP_OR:
		case EXP_CONCAT:
			
			used = left.usedVars();
			used.addAll(right.usedVars());
			break;
		case EXP_FALSE:
		case EXP_TRUE:
			break;
		case EXP_NOT:
		case EXP_PROJ:
			used = left.usedVars();
			break;
		case EXP_VAR:
			used.add(varId);
		default:
			break;
		}
		
		return used;
	}
	
	
	private static boolean[] bitwiseOr(boolean[] a, boolean[] b) {
		assert(a.length == b.length);
		boolean[] res = new boolean[a.length];
		for(int i = 0; i < a.length; i++) {
			res[i] = a[i] || b[i];
		}
		return res;
	}
	
	private static boolean[] bitwiseAnd(boolean[] a, boolean[] b) {
		assert(a.length == b.length);
		boolean[] res = new boolean[a.length];
		for(int i = 0; i < a.length; i++) {
			res[i] = a[i] && b[i];
		}
		return res;
	}
	
	private static boolean[] bitwiseNot(boolean[] a) {
		boolean[] res = new boolean[a.length];
		for(int i = 0; i < a.length; i++) {
			res[i] = !a[i];
		}
		return res;
	}
	
	private static boolean[] concat(boolean[] a, boolean[] b) {
		boolean[] res = new boolean[a.length + b.length];
		for(int i = 0; i < a.length; i++) {
			res[i] = a[i];
		}
		
		for(int i = 0; i < b.length; i++) {
			res[i+a.length] = b[i];
		}
		return res;
	}

	public boolean[] evaluateExpression(VariableState varState) {
		
		boolean[] leftVal;
		boolean[] rightVal;
		boolean[] res;
		
		switch(type) {
		case EXP_AND:
			
			leftVal = left.evaluateExpression(varState);
			rightVal = right.evaluateExpression(varState);
			
			return bitwiseAnd(leftVal, rightVal);
		case EXP_FALSE:
			
			res = new boolean[1];
			res[0] = false;
			
			return res;
		case EXP_TRUE:
			res = new boolean[1];
			res[0] = true;
			
			return res;
		case EXP_NOT:
			leftVal = left.evaluateExpression(varState);
			
			return bitwiseNot(leftVal);
		case EXP_OR:
			leftVal = left.evaluateExpression(varState);
			rightVal = right.evaluateExpression(varState);
			
			return bitwiseOr(rightVal, rightVal);
		
		case EXP_VAR:
			return varState.getVarMap().get(varId);
			
		case EXP_PROJ:
			leftVal = left.evaluateExpression(varState);
			
			res = new boolean[1];
			res[0] = leftVal[projIndex];
			
			return res;
			
		case EXP_CONCAT:
			leftVal = left.evaluateExpression(varState);
			rightVal = right.evaluateExpression(varState);
			
			return concat(rightVal, rightVal);		
		
		}
		
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((projIndex == null) ? 0 : projIndex.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((varId == null) ? 0 : varId.hashCode());
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
		Expression other = (Expression) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (projIndex == null) {
			if (other.projIndex != null)
				return false;
		} else if (!projIndex.equals(other.projIndex))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		if (type != other.type)
			return false;
		if (varId == null) {
			if (other.varId != null)
				return false;
		} else if (!varId.equals(other.varId))
			return false;
		return true;
	}
}
