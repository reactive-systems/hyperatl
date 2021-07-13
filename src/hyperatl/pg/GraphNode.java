package hyperatl.pg;

import java.util.LinkedList;
import java.util.List;

public class GraphNode {

	// The unique ID of this node
	private final int id;

	// All successors of this node
	private final List<GraphNode> successors;

	// The Color of this node
	private final int colour;

	// Which player controls this node, i.e., 0 or 1
	private final int player;
	
	// The "semantic" content of this node. Used to allow for cyclic graphs during construction
	private PGStatePartialChoice content;

	public GraphNode(int id, int colour, int player, PGStatePartialChoice content) {
		super();
		this.id = id;
		this.successors = new LinkedList<>();
		this.colour = colour;
		this.player = player;
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public List<GraphNode> getSuccessors() {
		return successors;
	}

	public int getColour() {
		return colour;
	}

	public int getPlayer() {
		return player;
	}
	
	
	public void addSucc(GraphNode succ) {
		this.successors.add(succ);
	}

	public PGStatePartialChoice getContent() {
		return content;
	}

	public void setContent(PGStatePartialChoice content) {
		this.content = content;
	}
}
