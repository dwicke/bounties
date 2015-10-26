package sim.app.bounties.environment;

import java.util.ArrayList;

public class HierarchicalTask extends Task {
	private ArrayList<HierarchicalTask> children;
	private boolean leafNode;
	
	public HierarchicalTask() {
		this(true);
	}
	
	public HierarchicalTask(boolean leafNode) {
		children = new ArrayList<HierarchicalTask>();
		this.leafNode = leafNode;
	}
	
	
}
