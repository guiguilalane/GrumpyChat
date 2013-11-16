package client.exception;

import java.rmi.RemoteException;

import client.gui.ClientDiscussionFrame;

public class DiscussionClosedException extends DiscussionException {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -474194922088609581L;

	public DiscussionClosedException() {
		super("The discussion has been closed");
	}

	@Override
	public boolean process(ClientDiscussionFrame cdf)
			throws InterruptedException {
		try {
			String title = cdf.getDiscussion().getTitle();
			cdf.getClient().display(
					"The channel '" + title + "' has been removed", cdf);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		cdf.close();
		return true;
	}

}
