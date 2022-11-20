package com.abapblog.classicOutline.tree;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.PlatformUI;

import com.abapblog.classicOutline.api.ApiCallerFactory;
import com.abapblog.classicOutline.utils.BackendComponentVersionFactory;
import com.abapblog.classicOutline.views.View;
import com.sap.adt.tools.abapsource.ui.internal.sources.editors.CompoundTextSelection;
import com.sap.adt.tools.abapsource.ui.sources.editors.AbapSourcePage;
import com.sap.adt.tools.core.IAdtObjectReference;
import com.sap.adt.tools.core.internal.navigation.AbapNavigationServicesFactory;
import com.sap.adt.tools.core.model.adtcore.IAdtCoreFactory;
import com.sap.adt.tools.core.navigation.AbapNavigationException;
import com.sap.adt.tools.core.navigation.IAbapNavigationServices;
import com.sap.adt.tools.core.navigation.IAbapNavigationServices.FilterValue;
import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;
import com.sap.adt.tools.core.ui.navigation.AdtNavigationServiceFactory;

@SuppressWarnings("restriction")
public class TreeDoubleClickListener implements IDoubleClickListener, ISelectionChangedListener {

	private static final int MINIMAL_BASIS_COMPONENT_VERSION_FOR_DIRECT_CALL = 750;
	private static final int MINIMAL_BACKEND_VERSION_FOR_SEMANTIC_URI = 1;

	@Override
	public void doubleClick(DoubleClickEvent event) {
		IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
		TreeNode selectedNode = (TreeNode) thisSelection.getFirstElement();
		String uri = "";
		IProject project = selectedNode.getLinkedObject().getProject();
		uri = getDefinitionURI(selectedNode, uri, project);
		if (uri.equals(""))
			uri = ApiCallerFactory.getCaller().getUriForTreeNode(selectedNode);

		if (uri != null && !uri.isEmpty() && !uri.equals("")) {
			uri = correctUriForNamespaces(uri, project);
			try {
				navigateToURI(uri, project);
			} catch (Exception e) {
				System.out.println(e);
			}

		}
	}

	private void navigateToURI(String uri, IProject project) {
		if (uri.contains("//")) {
			navigateUsingExternalLink(uri, project);
		} else {
			navigateDirectly(uri, project);
		}
	}

	private void navigateUsingExternalLink(String uri, IProject project) {
		uri = "adt://" + project.getName() + uri;
		AdtNavigationServiceFactory.createNavigationService().navigateWithExternalLink(uri, project);
	}

	private void navigateDirectly(String uri, IProject project) {
		com.sap.adt.tools.core.model.adtcore.IAdtObjectReference ref = IAdtCoreFactory.eINSTANCE
				.createAdtObjectReference();
		ref.setUri(uri);
		AdtNavigationServiceFactory.createNavigationService().navigate(project, ref, true);
	}

	private boolean canCallDirectNavigationService(IProject project) {
		return true;
//		return (Integer.decode(AbapReleaseFactory.getAbapRelease(project)
//				.getVersion()) >= MINIMAL_BASIS_COMPONENT_VERSION_FOR_DIRECT_CALL);
	}

	private boolean backendPreparedForSemanticURI(IProject project) {
		return (BackendComponentVersionFactory.getBackendComponentVersion(project)
				.getVersion() >= MINIMAL_BACKEND_VERSION_FOR_SEMANTIC_URI);
	}

	private boolean canCallSemanticURI(IProject project) {
		if (backendPreparedForSemanticURI(project) && canCallDirectNavigationService(project)) {
			return true;
		}
		return false;
	}

	private String correctUriForNamespaces(String uri, IProject project) {
//		if (canCallSemanticURI(project)) {
//			return escapeClassName(uri);
//		}
//		if (uriContainsScreenTitleGuiStatus(uri)) {
//			try {
//				uri = URLDecoder.decode(uri, StandardCharsets.UTF_8.toString());
//				uri = URLEncoder.encode(uri, StandardCharsets.UTF_8.toString());
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//
//		}
		uri = escapeClassName(uri);
		return uri;
	}

	private boolean uriContainsScreenTitleGuiStatus(String uri) {
		return uri.contains("/progpc/") || uri.contains("/progpz/") || uri.contains("/fugrpc/")
				|| uri.contains("/fugrpz/") || uri.contains("/fugrps/") || uri.contains("/progps/");
	}

	private String escapeClassName(String uri) {
		if (uri.contains("/sap/bc/adt/oo/classes//")) {
			Pattern p = Pattern.compile("/sap/bc/adt/oo/classes/(.*)/source/");
			Matcher m = p.matcher(uri);
			if (m.find()) {
				try {
					uri = m.replaceFirst("/sap/bc/adt/oo/classes/"
							+ URLEncoder.encode(m.group(1), StandardCharsets.UTF_8.toString()) + "/source/");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return uri;
	}

	private String getDefinitionURI(TreeNode selectedNode, String uri, IProject project) {
		if ((selectedNode.getType().equals("OOLD"))) {// && !selectedNode.getLinkedObject().getType().contains("CLAS")))
														// {
			TreeContentProvider contentProvider = (TreeContentProvider) View.getCurrentTree().getViewer()
					.getContentProvider();
			ObjectTree objectTree = (ObjectTree) contentProvider.getInvisibleRoot();
			TreeParent implementationParent = (TreeParent) objectTree
					.getParentByID(selectedNode.getDefinitionStartId());
			TreeNode implementationNode = selectedNode;
			for (TreeNode node : implementationParent.getChildren()) {
				if (node.getName().equals(selectedNode.getName())) {
					implementationNode = node;
					uri = ApiCallerFactory.getCaller().getUriForTreeNode(implementationNode);
					break;
				}
			}
			IAbapNavigationServices navigationService = AbapNavigationServicesFactory.getInstance()
					.createNavigationService(project.getName());
			URI navigationServiceUri = URI.create(uri);
			AbapSourcePage sourcePage = null;
			IAdtFormEditor editor = null;
			try {

				if (!implementationNode.getSourceNode().getText8().substring(0, 39)
						.equals(implementationNode.getLinkedObject().getName())) {
					uri = correctUriForNamespaces(uri, project);
					navigateToURI(uri, project);
					editor = (IAdtFormEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.getActiveEditor();
					navigationServiceUri = getURIWithStartAndEnd(navigationServiceUri, editor, uri);

				}
				if (editor == null)
					editor = (IAdtFormEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.getActiveEditor();
				sourcePage = editor.getAdapter(AbapSourcePage.class);
				IAdtObjectReference adtObject = navigationService.getNavigationTarget(navigationServiceUri,
						sourcePage.getSource(), null, prepareFilters());
				if (adtObject != null)
					uri = adtObject.getUri().toString();

			} catch (AbapNavigationException e) {
				e.getLocalizedMessage();
			}
		}
		return uri;
	}

	private FilterValue[] prepareFilters() {
		FilterValue[] filterValue = new FilterValue[1];
		LinkedHashSet filters = new LinkedHashSet(2);
		filters.add(FilterValue.DEFINITION);
		filterValue = (FilterValue[]) filters.toArray(new FilterValue[filters.size()]);
		return filterValue;
	}

	private URI getURIWithStartAndEnd(URI navigationServiceUri, IAdtFormEditor editor, String uri) {
		String includeUri;
		try {
			CompoundTextSelection selection = (CompoundTextSelection) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage().getSelection();
			AbapSourcePage abapSourcePage = editor.getAdapter(AbapSourcePage.class);
			IDocument document = editor.getAdapter(AbapSourcePage.class).getDocument();
			int startLine = selection.getStartLine();
			int beginningOfName = selection.getOffset() - document.getLineOffset(startLine);
			startLine = startLine + 1;
			if (uri.contains("/oo/")) {
				if (uri.contains("start"))
					return navigationServiceUri = URI.create(uri);
				includeUri = uri.replaceAll("#(.*)", "#start%3D" + startLine + "%2C" + beginningOfName + "%3Bend%3D"
						+ startLine + "%2C" + beginningOfName);
			} else {
				includeUri = "/sap/bc/adt/" + getPath(editor).toString() + "/source/main#start%3D" + startLine + "%2C"
						+ beginningOfName + "%3Bend%3D" + startLine + "%2C" + beginningOfName;
			}

			navigationServiceUri = URI.create(includeUri);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return navigationServiceUri;
	}

	private IPath getPath(IAdtFormEditor editor) {
		IPath path = editor.getModelFile().getFullPath();
		path = path.removeFirstSegments(2);
		path = path.removeLastSegments(1);
		return path;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
		TreeNode selectedNode = (TreeNode) thisSelection.getFirstElement();
		if (selectedNode == null)
			return;
		String uri = "";
		if (selectedNode.getType().startsWith("C"))
			return;
		IProject project = selectedNode.getLinkedObject().getProject();
		uri = getDefinitionURI(selectedNode, uri, project);
		if (uri.equals(""))
			uri = ApiCallerFactory.getCaller().getUriForTreeNode(selectedNode);

		if (uri != null && !uri.isEmpty() && !uri.equals("")) {
			uri = correctUriForNamespaces(uri, project);
			try {
				navigateToURI(uri, project);
			} catch (Exception e) {
				System.out.println(e);
			}

		}
	}
}
