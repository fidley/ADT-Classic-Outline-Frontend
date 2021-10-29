package com.abapblog.classicOutline.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.abapblog.classicOutline.utils.TreeExpansionListenerHandler;
import com.abapblog.classicOutline.views.LinkedObject;

public class OutlineFilteredTree extends FilteredTree {
	private final List<LinkedObject> linkedObjects = new ArrayList<>();

	private final TreeExpansionListenerHandler treeExpansionListener = new TreeExpansionListenerHandler();

	public OutlineFilteredTree(Composite parent, int treeStyle, PatternFilter filter, boolean useNewLook,
			boolean useFastHashLookup, LinkedObject linkdedObject) {
		super(parent, treeStyle, filter, useNewLook, useFastHashLookup);
		addLinkedObject(linkdedObject);
		getViewer().addTreeListener(treeExpansionListener);
	}

	public void addLinkedObject(LinkedObject linkedObject) {
		getLinkedObjects().add(linkedObject);
	}

	public List<LinkedObject> getLinkedObjects() {
		return linkedObjects;
	}

	public Boolean containsObject(LinkedObject linkedObject) {
		if (linkedObjects.contains(linkedObject))
			return true;

		int count = 0;
		while (linkedObjects.size() > count) {
			LinkedObject currentlyLinkedObject = linkedObjects.get(count);
			if (currentlyLinkedObject == null) {
				count++;
				continue;
			}
			if (!linkedObject.getParentName().equals("")) {
				if (currentlyLinkedObject.getParentName().equals(linkedObject.getParentName())) {
					linkedObjects.add(linkedObject);
					return true;
				}
			}
			if (currentlyLinkedObject.getName().equals(linkedObject.getName())) {
				linkedObjects.add(linkedObject);
				return true;
			}
			count++;
		}
		return false;
	}

}
