package net.alepuzio.todolistvertx.context;

public class Status {
	
	private CurrentContext context;

	public Status(CurrentContext context) {
		super();
		this.context = context;
	}


	public String start() {
		return String.format(
				"{%s}.start() has context on thread? {%s}\n and Worker Context? {%s}\n and has Event Loop? {%s}",
				context.getClass().getName(), context.isOnThread(), context.isWorkerContext(),
				context.isEventLoopContext());
	}

	public String deploy() {
		return String.format(
				"{%s}.deploy() has context on thread? {%s}\n and Worker Context? {%s}\n and has Event Loop? {%s}",
				context.getClass().getName(), context.isOnThread(), context.isWorkerContext(),
				context.isEventLoopContext());
	}

	public String stop() {
		return String.format(
				"{%s}.stop() has context on thread? {%s}\n and Worker Context? {%s}\n and has Event Loop? {%s}",
				context.getClass().getName(), context.isOnThread(), context.isWorkerContext(),
				context.isEventLoopContext());
	}

	
}
