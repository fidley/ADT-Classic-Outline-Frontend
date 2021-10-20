package com.abapblog.classicOutline.views;

import org.eclipse.core.resources.IProject;

import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;

public class LinkedObject {
	private IAdtFormEditor linkedEditor;
	private String name;
	private String type;
	private IProject project;

	public LinkedObject(IAdtFormEditor linkedEditor, IProject project) {
		this.linkedEditor = linkedEditor;
		setName();
		setType();
		setProject(project);

	}

	public String getName() {
		return name;
	}

	private void setName() {
		if (linkedEditor != null) {
			if (linkedEditor.getModel() != null) {
				this.name = linkedEditor.getModel().getName();
			}

		}
	}

	public String getType() {
		return type;

	}

	private void setType() {
		if (linkedEditor != null) {
			if (linkedEditor.getModel() != null) {
				this.type = linkedEditor.getModel().getType();
				if (this.type.equals("PROG/I") || this.type.equals("FUGR/I")) {
					this.type = "REPS";
				}
			}

		}
	}

	public IProject getProject() {
		return project;
	}

	private void setProject(IProject project) {
		this.project = project;
	}

	public boolean isEmpty() {
		if (name == null || name.isEmpty() || type == null || type.isEmpty() || project == null)
			return true;
		return false;
	}

	public boolean equals(LinkedObject comparedObject) {
		if (comparedObject == null)
			return false;
		if (getName().equals(comparedObject.getName()) && getType().equals(comparedObject.getType())
				&& getProject().equals(comparedObject.getProject()))
			return true;
		return false;
	}

}
