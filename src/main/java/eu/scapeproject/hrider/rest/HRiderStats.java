package eu.scapeproject.hrider.rest;

public class HRiderStats {
	private final int numObjects;
	private final int numIndexed;
	private final long size;

	protected HRiderStats(int numObjects, int numIndexed, long size) {
		super();
		this.numObjects = numObjects;
		this.numIndexed = numIndexed;
		this.size = size;
	}

	public int getNumObjects() {
		return numObjects;
	}

	public int getNumIndexed() {
		return numIndexed;
	}

	public long getSize() {
		return size;
	}
}
