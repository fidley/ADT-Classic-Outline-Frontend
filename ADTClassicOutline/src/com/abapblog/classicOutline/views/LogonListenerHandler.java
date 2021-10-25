package com.abapblog.classicOutline.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.sap.adt.destinations.logon.notification.ILoggedOnEvent;
import com.sap.adt.destinations.logon.notification.ILogonListener;

public class LogonListenerHandler implements ILogonListener {
	private static final List<String> destinationListenerInfo = new ArrayList<>();

	@Override
	public void loggedOn(ILoggedOnEvent loggedOnEvent, IProgressMonitor progress) {
		String destId = loggedOnEvent.getDestinationData().getId();
		if (destinationListenerInfo.indexOf(destId) == -1) {
			System.out.println("LoggedOnEvent: " + destId);
			destinationListenerInfo.add(destId);
			AbapPageLoadListener.addListener(new AbapPageLoadListenerHandler());
		}

	}

}
