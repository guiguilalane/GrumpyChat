package client.exception;

import client.gui.ClientDiscussionFrame;

public abstract class DiscussionException extends Exception {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -6162625584457837049L;

	public DiscussionException(String message) {
		super("Discussion Exception: " + message);
	}

	public abstract boolean process(ClientDiscussionFrame cdf)
			throws InterruptedException;
}
