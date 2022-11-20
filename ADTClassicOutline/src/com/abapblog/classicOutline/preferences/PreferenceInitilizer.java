package com.abapblog.classicOutline.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.abapblog.classicOutline.Activator;

public class PreferenceInitilizer extends AbstractPreferenceInitializer {
	public static final String APICallType = "APICallType";
	public static final String APIRestCall = "REST";
	public static final String APIJCoCall = "JCO";

	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(APICallType, APIJCoCall);
		store.setDefault(PreferenceConstants.P_FETCH_METHOD_REDEFINITIONS, false);
		store.setDefault(PreferenceConstants.P_LOAD_ALL_LEVELS_OF_SUBCLASSES, false);
		store.setDefault(PreferenceConstants.P_LOAD_ALL_LEVELS_OF_REDEFINITIONS, false);

	}

}
