package com.abapblog.classicOutline.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
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

		addField(new BooleanFieldEditor(PreferenceConstants.P_FETCH_METHOD_REDEFINITIONS,
				"&Fetch redefinitions for methods", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_LOAD_ALL_LEVELS_OF_REDEFINITIONS,
				"&Load all levels of redefinitions for methods", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_LOAD_ALL_LEVELS_OF_SUBCLASSES,
				"&Load all levels of subclasses", getFieldEditorParent()));
		RadioGroupFieldEditor TreeNavigationTrigger = new RadioGroupFieldEditor(
				PreferenceConstants.P_TREE_NAVIGATION_TRIGGER,
				"Please choose how the navigation to editor should be triggered", 1,
				new String[][] { { "Double-Click", TreeNavigationEvent.DoubleClick.toString() },
						{ "Selection", TreeNavigationEvent.NodeSelection.toString() } },
				getFieldEditorParent(), true);
		addField(TreeNavigationTrigger);
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

}
