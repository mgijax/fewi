package org.jax.mgi.fewi.matrix;

public enum OpenCloseState {
	OPEN("open"), CLOSE("close"), LEAF("leaf");

	private final String state;

	OpenCloseState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}
}
