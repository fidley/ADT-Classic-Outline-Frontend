package com.abapblog.classicOutline.utils;

import org.eclipse.core.resources.IProject;

public final class BackendComponentVersion {

	private final Integer version;
	private final IProject project;

	public BackendComponentVersion(Integer version, IProject project) {
		this.version = version;
		this.project = project;
	}

	public Integer getVersion() {
		return version;
	}

	public IProject getProject() {
		return project;
	}

}
