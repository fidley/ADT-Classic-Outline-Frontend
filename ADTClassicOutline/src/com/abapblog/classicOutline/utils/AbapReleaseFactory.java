package com.abapblog.classicOutline.utils;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;

import com.abapblog.classicOutline.api.ApiCallerFactory;

public final class AbapReleaseFactory {
	private static ArrayList<AbapRelease> projectsReleasesList = new ArrayList<AbapRelease>();

	public static AbapRelease getAbapRelease(IProject project) {

		int count = 0;
		while (projectsReleasesList.size() > count) {
			AbapRelease abapRelease = projectsReleasesList.get(count);
			if (abapRelease.getProject().equals(project)) {
				return abapRelease;
			}
			count++;
		}
		AbapRelease abapRelease = ApiCallerFactory.getCaller().getAbapRelease(project);
		projectsReleasesList.add(abapRelease);
		return abapRelease;

	}
}
