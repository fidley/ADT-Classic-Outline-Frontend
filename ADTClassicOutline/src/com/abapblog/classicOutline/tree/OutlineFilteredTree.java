package com.abapblog.classicOutline.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.progress.WorkbenchJob;

import com.abapblog.classicOutline.views.LinkedObject;

public class OutlineFilteredTree extends FilteredTree {
	private final List<LinkedObject> linkedObjects = new ArrayList<>();
	private IProject linkedProject = null;
	private static final long SOFT_MAX_EXPAND_TIME = 200;
	private boolean narrowingDown;
	// private final TreeExpansionListenerHandler treeExpansionListener = new
	// TreeExpansionListenerHandler();

	public OutlineFilteredTree(Composite parent, int treeStyle, PatternFilter filter, boolean useNewLook,
			boolean useFastHashLookup, LinkedObject linkdedObject) {
		super(parent, treeStyle, filter, useNewLook, useFastHashLookup);
		addLinkedObject(linkdedObject);
		if (linkdedObject != null)
			linkedProject = linkdedObject.getProject();
		// getViewer().addTreeListener(treeExpansionListener);
	}

	public void addLinkedObject(LinkedObject linkedObject) {
		getLinkedObjects().add(linkedObject);
	}

	public List<LinkedObject> getLinkedObjects() {
		return linkedObjects;
	}

	public Boolean containsObject(LinkedObject linkedObject) {
		if (linkedProject != null && linkedObject != null) {
			if (!linkedProject.getName().equals(linkedObject.getProject().getName()))
				return false;
		}

		if (linkedObjects.contains(linkedObject))
			return true;
		if (linkedObject == null)
			return false;

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

	@Override
	protected WorkbenchJob doCreateRefreshJob() {
		return new WorkbenchJob("Refresh Filter") {//$NON-NLS-1$
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (treeViewer.getControl().isDisposed()) {
					return Status.CANCEL_STATUS;
				}

				String text = getFilterString();
				if (text == null) {
					return Status.OK_STATUS;
				}

				boolean initial = initialText != null && initialText.equals(text);
				if (initial) {
					getPatternFilter().setPattern(null);
				} else if (text != null) {
					getPatternFilter().setPattern(text);
				}

				Control redrawFalseControl = treeComposite != null ? treeComposite : treeViewer.getControl();
				try {
					// don't want the user to see updates that will be made to
					// the tree
					// we are setting redraw(false) on the composite to avoid
					// dancing scrollbar
					redrawFalseControl.setRedraw(false);
					if (!narrowingDown) {
						// collapse all
						treeViewer.collapseAll();
						TreeItem[] is = treeViewer.getTree().getItems();
						for (TreeItem item : is) {
							if (item.getExpanded()) {
								treeViewer.setExpandedState(item.getData(), false);
							}
						}
					}
					treeViewer.refresh(true);

					if (text.length() > 0 && !initial) {
						/*
						 * Expand elements one at a time. After each is expanded, check to see if the
						 * filter text has been modified. If it has, then cancel the refresh job so the
						 * user doesn't have to endure expansion of all the nodes.
						 */
						TreeItem[] items = getViewer().getTree().getItems();
						int treeHeight = getViewer().getTree().getBounds().height;
						int numVisibleItems = treeHeight / getViewer().getTree().getItemHeight();
						long stopTime = SOFT_MAX_EXPAND_TIME + System.currentTimeMillis();
						if (items.length > 0
								&& recursiveExpand(items, monitor, stopTime, new int[] { numVisibleItems })) {
							return Status.CANCEL_STATUS;
						}
					}
					if (text.length() == 0 && !initial) {
						treeViewer.expandToLevel(2);
					}
				} finally {
					// done updating the tree - set redraw back to true
					TreeItem[] items = getViewer().getTree().getItems();
					if (items.length > 0 && getViewer().getTree().getSelectionCount() == 0) {
						treeViewer.getTree().setTopItem(items[0]);
					}
					redrawFalseControl.setRedraw(true);
				}
				return Status.OK_STATUS;
			}

		};
	}

	/**
	 * Returns true if the job should be canceled (because of timeout or actual
	 * cancellation).
	 *
	 * @param items
	 * @param monitor
	 * @param cancelTime
	 * @param numItemsLeft
	 * @return true if canceled
	 */
	private boolean recursiveExpand(TreeItem[] items, IProgressMonitor monitor, long cancelTime, int[] numItemsLeft) {
		boolean canceled = false;
		for (int i = 0; !canceled && i < items.length; i++) {
			TreeItem item = items[i];
			boolean visible = numItemsLeft[0]-- >= 0;
			if (monitor.isCanceled() || (!visible && System.currentTimeMillis() > cancelTime)) {
				canceled = true;
			} else {
				Object itemData = item.getData();
				if (itemData != null) {
					if (!item.getExpanded()) {
						// do the expansion through the viewer so that
						// it can refresh children appropriately.
						treeViewer.setExpandedState(itemData, true);
					}
					TreeItem[] children = item.getItems();
					if (items.length > 0) {
						canceled = recursiveExpand(children, monitor, cancelTime, numItemsLeft);
					}
				}
			}
		}
		return canceled;
	}

	@Override
	protected void updateToolbar(boolean visible) {
		super.updateToolbar(visible);
		if (getFilterControl() != null && getFilterControl().getText().length() == 0)
			treeViewer.expandToLevel(2);
	}
}
