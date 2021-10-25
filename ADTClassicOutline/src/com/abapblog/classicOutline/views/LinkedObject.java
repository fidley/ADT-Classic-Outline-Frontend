package com.abapblog.classicOutline.views;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;

import com.abapblog.classicOutline.utils.RegularExpressionUtils;
import com.sap.adt.tools.core.model.adtcore.IAdtObject;
import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;

@SuppressWarnings("restriction")
public class LinkedObject {
	public final static String functionGroupPattern = "functions\\/groups\\/(\\w*)\\/";
	private IAdtFormEditor linkedEditor;
	private String name;
	private String type;
	private IProject project;
	private String parentName;

	public LinkedObject(IAdtFormEditor linkedEditor, IProject project) {
		this.linkedEditor = linkedEditor;
		setName();
		setType();
		setProject(project);
		setParentName();
	}

	public String getName() {
		return name;
	}

	private void setName() {
		if (linkedEditor != null) {
			IAdtObject model = linkedEditor.getModel();
			if (model != null) {
				this.name = model.getName();
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

	public String getParentName() {
		return parentName;
	}

	private void setParentName() {
		if (linkedEditor == null)
			return;
		String pathString = linkedEditor.getModelFile().getFullPath().toString();
		parentName = matchPattern(functionGroupPattern, pathString);
	}

	private String matchPattern(String pattern, String path) {

		Pattern regexPattern = Pattern.compile(pattern, Pattern.MULTILINE + Pattern.CASE_INSENSITIVE);

		Matcher regexPatterMatcher = RegularExpressionUtils.createMatcherWithTimeout(path, regexPattern, 1000);
		while (regexPatterMatcher.find()) {
			return regexPatterMatcher.group(1);
		}
		return "";
	}

}
