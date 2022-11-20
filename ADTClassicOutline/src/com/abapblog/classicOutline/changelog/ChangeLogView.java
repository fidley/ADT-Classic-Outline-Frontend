package com.abapblog.classicOutline.changelog;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.abapblog.classicOutline.Activator;

public class ChangeLogView extends ViewPart {
	public ChangeLogView() {
	}

	@Override
	public void createPartControl(Composite parent) {

		Browser browser = new Browser(parent, SWT.NONE);
		String html = "/html/changelog.html";
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(html), null);
		try {
			url = FileLocator.toFileURL(url);
			browser.setUrl(url.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
