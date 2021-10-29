package com.abapblog.classicOutline.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.IEditorPart;

import com.abapblog.classicOutline.api.ApiCallerFactory;
import com.abapblog.classicOutline.utils.ClassPageChangeListener;
import com.abapblog.classicOutline.utils.ProjectUtility;
import com.abapblog.classicOutline.views.LinkedObject;
import com.sap.adt.oo.ui.classes.IMultiPageClassEditor;

@SuppressWarnings("restriction")
public class TreeContentProvider implements ITreeContentProvider {
	private TreeParent invisibleRoot;
	private LinkedObject linkedObject;
	private Object[] elements;
	private boolean refreshTree = false;
	private org.eclipse.jface.viewers.TreePath[] treePaths;
	// private View view;

	public TreeContentProvider(LinkedObject linkedObject) {
		super();
		this.setLinkedObject(linkedObject);

		// addClassPageListener();
		// setView(view);

	}

	private void addClassPageListener() {
		if (getLinkedObject() == null || getLinkedObject().isEmpty())
			return;
		IEditorPart activeEditor = ProjectUtility.getActiveEditor();
		if (activeEditor instanceof IMultiPageClassEditor) {
			IMultiPageClassEditor classEditor = (IMultiPageClassEditor) activeEditor;
			classEditor.addPageChangedListener(new ClassPageChangeListener(this));
		}
	}

	@Override
	public Object[] getElements(Object parent) {
		if (invisibleRoot == null)
			initialize();
		return getChildren(invisibleRoot);
	}

	@Override
	public Object getParent(Object child) {
		if (child instanceof TreeNode) {
			return ((TreeNode) child).getParent();
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent) parent).getChildren();
		}
		return new Object[0];
	}

	@Override
	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent) parent).hasChildren();
		return false;
	}

	public void initialize() {
		Boolean refresh = isRefreshTree();
		setRefreshTree(false);

		try {
			invisibleRoot = new TreeParent(getLinkedObject(), null);
			if (getLinkedObject() == null || getLinkedObject().isEmpty())
				return;
			invisibleRoot = ApiCallerFactory.getCaller().getObjectTree(getLinkedObject(), refresh);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object[] getElements() {
		if (invisibleRoot == null)
			initialize();
		return getChildren(invisibleRoot);
	}

	public Object[] getAllElements() {
		List<Object> allChildren = new ArrayList<Object>();
		if (invisibleRoot == null)
			initialize();

		Object[] childrens = getChildren(invisibleRoot);
		for (int i = 0; i < childrens.length; i++) {
			try {
				TreeParent child = (TreeParent) childrens[i];
				allChildren.add(child);
				getSubnodes(child, allChildren);
			} catch (Exception e) {

			}
		}
		return allChildren.toArray();
	}

	private void getSubnodes(TreeParent parent, List<Object> allChildren) {
		if (parent.hasChildren()) {
			Object[] childrens = getChildren(parent);
			for (int i = 0; i < childrens.length; i++) {
				try {
					TreeParent child = (TreeParent) childrens[i];
					allChildren.add(child);
					getSubnodes(child, allChildren);
				} catch (Exception e) {

				}
			}

		}
	}

	public void setElements(Object[] elements) {
		this.elements = elements;
	}

	public org.eclipse.jface.viewers.TreePath[] getTreePaths() {
		return treePaths;
	}

	public void setTreePaths(org.eclipse.jface.viewers.TreePath[] treePaths) {
		this.treePaths = treePaths;
	}

	public boolean isRefreshTree() {
		return refreshTree;
	}

	public void setRefreshTree(boolean refreshTree) {
		this.refreshTree = refreshTree;
	}

	public LinkedObject getLinkedObject() {
		return linkedObject;
	}

	public void setLinkedObject(LinkedObject linkedObject) {
		this.linkedObject = linkedObject;
	}

}
