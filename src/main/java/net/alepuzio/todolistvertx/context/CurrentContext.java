package net.alepuzio.todolistvertx.context;

import io.vertx.core.Context;

public class CurrentContext {

	private Context context = null;
	
	
	public CurrentContext(Context context) {
		super();
		this.context = context;
	}

	public String isOnThread() {
		String msg = null;
        if(Context.isOnVertxThread()) {
        	msg = "Context attached to a thread managed by vert.x";
        }else {
        	msg = "Context not attached to a thread managed by vert.x";
        }
        return msg;
	}

	public String isWorkerContext() {
		String msg = null;
        if(Context.isOnVertxThread()) {
        	msg = "Context attached to Worker Thread";
        }else {
        	msg = "Context de-attached to Worker Thread";
        }
        return msg;
	}
	
	public String isEventLoopContext() {
		String msg = null;
        if(context.isEventLoopContext()) {
        	msg = "Context attached to Event Loop";
        }else {
        	msg = "Context de-attached to Event Loop";
        }
        return msg;
	}
}
