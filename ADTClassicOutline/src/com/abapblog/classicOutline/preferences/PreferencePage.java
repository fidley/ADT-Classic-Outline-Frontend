package com.abapblog.classicOutline.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.abapblog.classicOutline.Activator;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private final IPreferenceStore store;

	public PreferencePage() {
		super(GRID);
		store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		setDescription("Classic Outline Settings");
	}

	@Override
	protected void createFieldEditors() {
//		RadioGroupFieldEditor apiCallPreference = new RadioGroupFieldEditor(PreferenceInitilizer.APICallType,
//				"Please choose a type of call to backend", 1,
//				new String[][] { { "RFC Call (JCO)", PreferenceInitilizer.APIJCoCall },
//						{ "REST API", PreferenceInitilizer.APIRestCall } },
//				getFieldEditorParent(), true);
//		addField(apiCallPreference);
		addField(new BooleanFieldEditor(PreferenceConstants.P_FETCH_METHOD_REDEFINITIONS,
				"&Fetch redefinitions for methods", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_LOAD_ALL_LEVELS_OF_REDEFINITIONS,
				"&Load all levels of redefinitions for methods", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_LOAD_ALL_LEVELS_OF_SUBCLASSES,
				"&Load all levels of subclasses", getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

}
