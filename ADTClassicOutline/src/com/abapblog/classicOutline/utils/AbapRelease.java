package com.abapblog.classicOutline.utils;

import org.eclipse.core.resources.IProject;

public class AbapRelease {
	private final String Version;
	private final String PatchLevel;
	private final IProject project;

	public AbapRelease(String Version, String PatchLevel, IProject project) {
		this.Version = Version;
		this.PatchLevel = PatchLevel;
		this.project = project;
	}

	public String getVersion() {
		return Version;
	}

	public String getPatchLevel() {
		return PatchLevel;
	}

	public IProject getProject() {
		return project;
	}

}
