package com.abapblog.classicOutline.utils;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;

import com.abapblog.classicOutline.api.ApiCallerFactory;

public final class BackendComponentVersionFactory {
	private static ArrayList<BackendComponentVersion> projectsReleasesList = new ArrayList<BackendComponentVersion>();

	public static BackendComponentVersion getBackendComponentVersion(IProject project) {

		int count = 0;
		while (projectsReleasesList.size() > count) {
			BackendComponentVersion backendComponentVersion = projectsReleasesList.get(count);
			if (backendComponentVersion.getProject().equals(project)) {
				return backendComponentVersion;
			}
			count++;
		}
		BackendComponentVersion backendComponentVersion = ApiCallerFactory.getCaller()
				.getBackendComponentVersion(project);
		projectsReleasesList.add(backendComponentVersion);
		return backendComponentVersion;

	}
}
