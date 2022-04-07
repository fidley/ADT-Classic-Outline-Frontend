package com.abapblog.classicOutline.views;

import java.util.ArrayList;
import java.util.List;

import com.sap.adt.tools.abapsource.ui.sources.editors.AbstractAbapSourcePageExtensionProcessor;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;

@SuppressWarnings("restriction")
public class AbapPageLoadListener extends AbstractAbapSourcePageExtensionProcessor {
	private static final List<IAbapPageLoadListener> listeners = new ArrayList<>();

	public static void addListener(IAbapPageLoadListener listener) {
		listeners.add(listener);
	}

	public static void removeListener(IAbapPageLoadListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void processOnDocumentLoaded(IAbapSourcePage sourcePage) {
		if (listeners.size() == 0) {
			return;
		}
		try {
			for (int i = 0; i < listeners.size(); i++) {
				listeners.get(i).pageLoaded(sourcePage);
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
