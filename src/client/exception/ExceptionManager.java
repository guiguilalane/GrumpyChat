package client.exception;

import java.util.List;

import client.gui.ClientDiscussionFrame;

public class ExceptionManager extends Thread {
	private ClientDiscussionFrame discussionFrame;

	public ExceptionManager(ClientDiscussionFrame discussionFrame) {
		this.discussionFrame = discussionFrame;
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				List<DiscussionException> exceptions = this.discussionFrame
						.getExceptions();
				System.err.println("Size: "+exceptions.size());
				if (!exceptions.isEmpty()) {
					for (DiscussionException de : exceptions) {
						if (de.process(this.discussionFrame)) {
							return;
						}
					}
					this.discussionFrame.getExceptions().clear();
				}
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			// Thread killed
		}
	}

}
