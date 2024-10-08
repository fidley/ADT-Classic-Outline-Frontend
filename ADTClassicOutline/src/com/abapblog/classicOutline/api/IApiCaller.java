package com.abapblog.classicOutline.api;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;

import com.abapblog.classicOutline.tree.ObjectTree;
import com.abapblog.classicOutline.tree.TreeNode;
import com.abapblog.classicOutline.utils.AbapRelease;
import com.abapblog.classicOutline.utils.BackendComponentVersion;
import com.abapblog.classicOutline.views.LinkedObject;

public interface IApiCaller {
	static ArrayList<ObjectTree> objectsList = new ArrayList<ObjectTree>();

	public ObjectTree getObjectTree(LinkedObject linkedObject, boolean forceRefresh);

	public ObjectTree getNewObjectTree(LinkedObject linkedObject);

	public ObjectTree getObjectTree(LinkedObject linkedObject);

	public String getUriForTreeNode(TreeNode treeNode);

	public String getMasterProgramForInclude(LinkedObject linkedObject);

	public AbapRelease getAbapRelease(IProject project);

	public BackendComponentVersion getBackendComponentVersion(IProject project);

}
