package com.abapblog.classicOutline.views;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;

public class SaveCommandListener implements IExecutionListener {

	@Override
	public void notHandled(String commandId, NotHandledException exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postExecuteFailure(String commandId, ExecutionException exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postExecuteSuccess(String commandId, Object returnValue) {
		switch (commandId) {
		case "org.eclipse.ui.file.saveAll": {
			refreshActiveOutline();
		}
		case "org.eclipse.ui.file.save": {
			refreshActiveOutline();
		}
		}
	}

	private void refreshActiveOutline() {
		try {
			View.view.reloadOutlineContent(true, true, true);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	@Override
	public void preExecute(String commandId, ExecutionEvent event) {
		// TODO Auto-generated method stub

	}

}
