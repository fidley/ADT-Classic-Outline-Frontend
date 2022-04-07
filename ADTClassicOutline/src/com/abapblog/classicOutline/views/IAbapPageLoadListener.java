package com.abapblog.classicOutline.views;

import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;

@SuppressWarnings("restriction")
public interface IAbapPageLoadListener {
	void pageLoaded(IAbapSourcePage sourcePage);

	String getDestinationId();

}
