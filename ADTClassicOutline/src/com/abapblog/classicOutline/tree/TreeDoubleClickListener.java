package com.abapblog.classicOutline.tree;

import java.net.URI;
import java.util.LinkedHashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;

import com.abapblog.classicOutline.api.ApiCallerFactory;
import com.sap.adt.tools.abapsource.ui.sources.editors.AbapSourcePage;
import com.sap.adt.tools.core.IAdtObjectReference;
import com.sap.adt.tools.core.internal.navigation.AbapNavigationServicesFactory;
import com.sap.adt.tools.core.navigation.AbapNavigationException;
import com.sap.adt.tools.core.navigation.IAbapNavigationServices;
import com.sap.adt.tools.core.navigation.IAbapNavigationServices.FilterValue;
import com.sap.adt.tools.core.ui.navigation.AdtNavigationServiceFactory;

public class TreeDoubleClickListener implements IDoubleClickListener {

	@Override
	public void doubleClick(DoubleClickEvent event) {
		IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
		TreeNode selectedNode = (TreeNode) thisSelection.getFirstElement();
		String uri = ApiCallerFactory.getCaller().getUriForTreeNode(selectedNode);
		if (uri != null && !uri.isEmpty()) {
			IProject project = selectedNode.getLinkedObject().getProject();
			uri = getDefinitionURI(selectedNode, uri, project);
			uri = "adt://" + project.getName() + uri;
			AdtNavigationServiceFactory.createNavigationService().navigateWithExternalLink(uri, project);

		}

	}

	private String getDefinitionURI(TreeNode selectedNode, String uri, IProject project) {
		if ((selectedNode.getType().equals("OOLD") && !selectedNode.getLinkedObject().getType().contains("CLAS"))
				|| ((selectedNode.getType().substring(0, 2).equals("OOLD")
						&& selectedNode.getLinkedObject().getType().contains("REPS")))) {

			IAbapNavigationServices navigationService = AbapNavigationServicesFactory.getInstance()
					.createNavigationService(project.getName());
			URI navigationServiceUri = URI.create(uri);
			FilterValue[] filterValue = new FilterValue[1];
			LinkedHashSet filters = new LinkedHashSet(2);
			filters.add(FilterValue.DEFINITION);
			filterValue = (FilterValue[]) filters.toArray(new FilterValue[filters.size()]);
			try {

				AbapSourcePage sourcePage;
				if (!selectedNode.getSourceNode().getText8().substring(0, 39)
						.equals(selectedNode.getLinkedObject().getName())) {
					String includeUri = "adt://" + project.getName() + uri;
					AdtNavigationServiceFactory.createNavigationService().navigateWithExternalLink(includeUri, project);
				}
				sourcePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
						.getAdapter(AbapSourcePage.class);
				IAdtObjectReference adtObject = navigationService.getNavigationTarget(navigationServiceUri,
						sourcePage.getSource(), null, filterValue);
				adtObject.getUri().getPath();
				uri = adtObject.getUri().toString();
			} catch (AbapNavigationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return uri;
	}
}
