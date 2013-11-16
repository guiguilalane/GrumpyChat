package client.exception;

import javax.swing.JOptionPane;

import client.gui.ClientDiscussionFrame;

public class DiscussionNeedOwnerException extends DiscussionException {

	/**
	 * ID
	 */
	private static final long serialVersionUID = 8740733284795932252L;

	public DiscussionNeedOwnerException() {
		super("The discussion does not have owner any more");
	}

	@Override
	public boolean process(ClientDiscussionFrame cdf)
			throws InterruptedException {
		boolean accepted = JOptionPane
				.showConfirmDialog(
						cdf,
						"The old channel ower "
								+ "left this channel, do you want to be the new owner?",
						"Become the new BOSS? \\o/", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == 0;
		if (accepted) {
			cdf.tryToBeNewOwner();
		}
		return false;
	}

}
