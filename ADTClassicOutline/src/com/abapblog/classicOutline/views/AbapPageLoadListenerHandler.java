package com.abapblog.classicOutline.views;

import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;

@SuppressWarnings("restriction")
public class AbapPageLoadListenerHandler implements IAbapPageLoadListener {

	@Override
	public void pageLoaded(IAbapSourcePage sourcePage) {
		View.view.reloadOutlineContent(true, true, false);
	}

}
