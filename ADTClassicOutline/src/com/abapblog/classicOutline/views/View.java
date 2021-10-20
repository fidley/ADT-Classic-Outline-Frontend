package com.abapblog.classicOutline.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.part.ViewPart;

import com.abapblog.classicOutline.tree.TreeCellLabelProvider;
import com.abapblog.classicOutline.tree.TreeContentProvider;
import com.abapblog.classicOutline.tree.TreeDoubleClickListener;
import com.abapblog.classicOutline.tree.TreePatternFilter;
import com.abapblog.classicOutline.utils.ProjectUtility;
import com.sap.adt.activation.ui.IActivationSuccessListener;
import com.sap.adt.destinations.logon.notification.ILoggedOnEvent;
import com.sap.adt.destinations.logon.notification.ILogonListener;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;
import com.sap.adt.tools.core.IAdtObjectReference;

public class View extends ViewPart
		implements ILinkedWithEditorView, ILogonListener, IAbapPageLoadListener, IActivationSuccessListener {
	protected static TreeViewer viewer;
	private static TreeContentProvider contentProvider;
	private Composite parent;
	private static final List<String> destinationListenerInfo = new ArrayList<>();
	protected IPartListener2 linkWithEditorPartListener = new LinkWithEditorPartListener(this);
	public LinkedObject linkedObject = new LinkedObject(null, null);
	private ArrayList<TreeContentProvider> contentProviders = new ArrayList<TreeContentProvider>();

	@Override
	public void createPartControl(Composite parent) {

		this.parent = parent;
		TreePatternFilter filter = new TreePatternFilter();

		final FilteredTree filteredTree = new FilteredTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, filter,
				true, true);
		viewer = filteredTree.getViewer();
		linkedObject = ProjectUtility.getObjectFromEditor();
		contentProvider = getContentProvider(linkedObject);
		viewer.setContentProvider(contentProvider);
		setTreeProperties();
		createColumns();
		viewer.setLabelProvider(new TreeCellLabelProvider());
		createGridData();
		setLinkingWithEditor();
		viewer.addDoubleClickListener(new TreeDoubleClickListener());

	}

	private void setTreeProperties() {
		getViewer().getTree().setHeaderVisible(false);
		getViewer().getTree().setLinesVisible(false);
	}

	private void createColumns() {
		TreeViewerColumn viewerColumn = new TreeViewerColumn(getViewer(), SWT.NONE);
		viewerColumn.getColumn().setWidth(500);
		viewerColumn.getColumn().setText("Names");
	}

	private void createGridData() {
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, true);
		getViewer().getControl().setLayoutData(data);
		try {
			getViewer().setInput(getViewSite());
		} catch (Exception e) {

		}
	}

	@Override
	public void setFocus() {
		parent.setFocus();
		refreshObjectTree();

	}

	private void refreshObjectTree() {
		if ((linkedObject != null && linkedObject.isEmpty() && (isLinkingActive()))) {
			IProject LinkedProject = ProjectUtility.getActiveAdtProject();
			if (LinkedProject == null)
				return;
			linkedObject = ProjectUtility.getObjectFromEditor();
			if (linkedObject == null || linkedObject.isEmpty())
				return;
			new Thread(() -> {
				contentProvider = getContentProvider(linkedObject);
				getViewer().setContentProvider(contentProvider);
				treeViewerRefresh();
				getViewer().expandToLevel(2);
			}).start();
		}
	}

	public void treeViewerRefresh() {
		getViewer().refresh();
	}

	@Override
	public void editorActivated(IEditorPart activeEditor) {
		if (isLinkingActive()) {
			IProject LinkedProject = ProjectUtility.getActiveAdtProject();
			if (LinkedProject == null)
				return;
			linkedObject = ProjectUtility.getObjectFromEditor(activeEditor);
			if (linkedObject == null || linkedObject.isEmpty())
				return;
			if (!linkedObject.equals(contentProvider.getLinkedObject())) {

				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							try {
								contentProvider.setElements(getViewer().getExpandedElements());
								contentProvider.setTreePaths(getViewer().getExpandedTreePaths());
							} catch (Exception e) {
							}
							contentProvider = getContentProvider(linkedObject);
							getViewer().setContentProvider(contentProvider);
							treeViewerRefresh();
							expandTree();
						} catch (Exception e) {
						}
					}

				});
			}
		}
	}

	public void expandTree() {
		getViewer().expandToLevel(2);
		Object[] expandedElements = contentProvider.getElements();
		if (expandedElements != null) {
			getViewer().setExpandedElements(expandedElements);
		}
		TreePath[] expandedTreePaths = contentProvider.getTreePaths();
		if (expandedTreePaths != null) {
			getViewer().setExpandedTreePaths(expandedTreePaths);
		}
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(this.linkWithEditorPartListener);
		super.dispose();
	}

	public boolean isLinkingActive() {
//		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
//		Command toggleCommand = commandService.getCommand(LinkWithEditorHandler.ID);
//		State state = toggleCommand.getState(RegistryToggleState.STATE_ID);
//		return (Boolean) state.getValue();
		return true;
	}

	public void callEditorActivationWhenNeeded(final boolean linkingActive) {
		if (linkingActive)
			editorActivated(getSite().getPage().getActiveEditor());
	}

	private void setLinkingWithEditor() {
		getSite().getPage().addPartListener(this.linkWithEditorPartListener);
	}

	private TreeContentProvider getContentProvider(LinkedObject linkedObject) {
		int count = 0;
		while (getContentProviders().size() > count) {
			TreeContentProvider contentProvider = getContentProviders().get(count);
			if (linkedObject != null && linkedObject.equals(contentProvider.getLinkedObject())) {
				return contentProvider;
			}
			count++;
		}

		TreeContentProvider contentProvider = new TreeContentProvider(linkedObject, this);
		contentProvider.initialize();
		getContentProviders().add(contentProvider);
		return contentProvider;
	}

	public ArrayList<TreeContentProvider> getContentProviders() {
		return contentProviders;
	}

	public void setContentProviders(ArrayList<TreeContentProvider> contentProviders) {
		this.contentProviders = contentProviders;
	}

	public TreeContentProvider getContentProvider() {
		return contentProvider;
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	@Override
	public void loggedOn(ILoggedOnEvent loggedOnEvent, IProgressMonitor progressMonitor) {
		String destId = loggedOnEvent.getDestinationData().getId();
		int test = destinationListenerInfo.indexOf(destId);
		if (destinationListenerInfo.indexOf(destId) == -1) {
			System.out.println("LoggedOnEvent: " + destId);
			destinationListenerInfo.add(destId);
			AbapPageLoadListener.addListener(this);
		}

	}

	@Override
	public void pageLoaded(IAbapSourcePage sourcePage) {
		reloadOutlineContent();
	}

	@Override
	public void onActivationSuccess(List<IAdtObjectReference> adtObject, IProject project) {
		reloadOutlineContent();

	}

	private void reloadOutlineContent() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				linkedObject = ProjectUtility.getObjectFromEditor();
				contentProvider.setElements(getViewer().getExpandedElements());
				contentProvider.setTreePaths(getViewer().getExpandedTreePaths());
				contentProvider = getContentProvider(linkedObject);
				contentProvider.setRefreshTree(true);
				contentProvider.initialize();
				getViewer().setContentProvider(contentProvider);
				treeViewerRefresh();
				expandTree();
			}

		});
	};
}
