package com.abapblog.classicOutline.utils;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.abapblog.classicOutline.views.LinkedObject;
import com.abapblog.classicOutline.views.View;
import com.sap.adt.destinations.logon.AdtLogonServiceFactory;
import com.sap.adt.destinations.model.IAuthenticationToken;
import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.destinations.model.internal.AuthenticationToken;
import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;
import com.sap.adt.project.AdtCoreProjectServiceFactory;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.util.ProjectUtil;
import com.sap.adt.ris.search.AdtRisQuickSearchFactory;
import com.sap.adt.ris.search.RisQuickSearchNotSupportedException;
import com.sap.adt.sapgui.ui.editors.AdtSapGuiEditorUtilityFactory;
import com.sap.adt.tools.abapsource.ui.AbapSourceUi;
import com.sap.adt.tools.abapsource.ui.IAbapSourceUi;
import com.sap.adt.tools.abapsource.ui.sources.IAbapSourceScannerServices;
import com.sap.adt.tools.abapsource.ui.sources.IAbapSourceScannerServices.Token;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourceMultiPageEditor;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;
import com.sap.adt.tools.core.model.adtcore.IAdtObject;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.tools.core.project.IAbapProject;
import com.sap.adt.tools.core.ui.dialogs.AbapProjectSelectionDialog;
import com.sap.adt.tools.core.ui.editors.IAdtFormEditor;
import com.sap.adt.tools.core.ui.navigation.AdtNavigationServiceFactory;
import com.sap.adt.tools.core.wbtyperegistry.WorkbenchAction;

@SuppressWarnings("restriction")
public class ProjectUtility {

	public static IProject getActiveAdtProject() {
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IWorkbenchWindow window = page.getWorkbenchWindow();
			ISelection adtSelection = window.getSelectionService().getSelection();
			IProject project = ProjectUtil.getActiveAdtCoreProject(adtSelection, null, null,
					IAdtCoreProject.ABAP_PROJECT_NATURE);
			return project;
		} catch (Exception e) {
			return null;
		}
	}

	public static String getDestinationID(IProject project) {

		String destinationId = AdtCoreProjectServiceFactory.createCoreProjectService().getDestinationId(project);
		return destinationId;
	}

	public static LinkedObject getObjectFromEditor(IEditorPart editor) {
		if (editor instanceof IAdtFormEditor) {
			IAdtFormEditor formEditor = (IAdtFormEditor) editor;
			if (formEditor.getModel() != null) {
				int count = 0;
				IProject project = getActiveAdtProject();
				while (View.linkedObjects.size() > count) {
					LinkedObject currentlyLinkedObject = View.linkedObjects.get(count);

					if (currentlyLinkedObject.getName().equals(formEditor.getModel().getName())
							&& currentlyLinkedObject.getType().equals(formEditor.getModel().getType())
							&& currentlyLinkedObject.getProject().equals(project)) {
						return currentlyLinkedObject;
					}
					count++;
				}
				LinkedObject linkedObject = new LinkedObject(formEditor, project);
				View.linkedObjects.add(linkedObject);
				return linkedObject;
			}
		}
		return null;
	}

	public static void ensureLoggedOn(IProject project) {
		try {
			IAbapProject abapProject = project.getAdapter(IAbapProject.class);
			AdtLogonServiceUIFactory.createLogonServiceUI().ensureLoggedOn(abapProject.getDestinationData(),
					PlatformUI.getWorkbench().getProgressService());
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}

	public static IEditorPart getActiveEditor() {
		try {
			IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
			return activeEditor;
		} catch (Exception e) {
			return null;
		}
	}

	public static LinkedObject getObjectFromEditor() {
		return getObjectFromEditor(getActiveEditor());
	}

	public static IAbapSourcePage getAbapSourcePage(IEditorPart editor) {
		try {
			MultiPageEditorPart multiPartEditor = (MultiPageEditorPart) editor;
			IAbapSourcePage sourcePage = (IAbapSourcePage) multiPartEditor.getSelectedPage();
			return sourcePage;
		} catch (Exception e) {
			return null;
		}
	}

	public static List<Token> getTokens(IAbapSourcePage sourcePage, int cursorPosition) {
		IDocument document = sourcePage.getDocument();
		IAbapSourceUi sourceUi = AbapSourceUi.getInstance();
		IAbapSourceScannerServices scannerServices = sourceUi.getSourceScannerServices();
		int startOfStatement = scannerServices.goBackToDot(document, cursorPosition) + 1;
		return scannerServices.getStatementTokens(document, startOfStatement);
	}

	public static IAdtObject getAdtObject(IEditorPart editor) {
		if (editor instanceof IAbapSourceMultiPageEditor) {
			IAbapSourcePage abapSourcePage = editor.getAdapter(IAbapSourcePage.class);
			if (abapSourcePage == null) {
				return null;
			}
			return abapSourcePage.getMultiPageEditor().getModel();
		}
		return null;
	}

	public static IAdtObjectReference getClassReference(final IProject project, final String className) {
		List<IAdtObjectReference> res = null;
		try {
			res = AdtRisQuickSearchFactory.createQuickSearch(project, new NullProgressMonitor()).execute(className, 10,
					false, "CLAS");
		} catch (OperationCanceledException | RisQuickSearchNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		for (final IAdtObjectReference ref : res) {
			if (ref.getName().equals(className))
				return ref;
		}
		return null;
	}

	public static int getCaretOffset(IEditorPart editor) {
		StyledText text = (StyledText) editor.getAdapter(Control.class);
		return text.getCaretOffset();
	}

	public static IProject getProjectByName(String projectName) {
		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			return project;
		} catch (Exception e) {
			return null;
		}
	}

	public static IProject getProjectFromProjectChooserDialog() {
		return AbapProjectSelectionDialog.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null);
	}

	public static void logonWithPassword(IProject project, String password) {
		IAdtCoreProject adtProject = project.getAdapter(IAdtCoreProject.class);
		IDestinationData destinationData = adtProject.getDestinationData();
		try {
			if (!password.isEmpty()) {
				IAuthenticationToken authenticationToken = new AuthenticationToken();

				authenticationToken.setPassword(password);

				AdtLogonServiceFactory.createLogonService().ensureLoggedOn(destinationData, authenticationToken,
						new NullProgressMonitor());
			}
		} catch (Exception e) {
			ensureLoggedOn(project);
		}
	}

	public static void openObject(final IProject project, final IAdtObjectReference adtObjectRef) {

		AdtNavigationServiceFactory.createNavigationService().navigate(project, adtObjectRef, true);
	}

	public static void openAdtLink(final IProject project, String adtLink) {
		adtLink = adtLink.replace("(?<=\'/\'/)(.*?)(?=\'/)", project.getName().toString());
		AdtNavigationServiceFactory.createNavigationService().navigateWithExternalLink(adtLink, project);
		return;
	}

	public static void runObject(final IProject project, final IAdtObjectReference adtObjectRef) {

		AdtSapGuiEditorUtilityFactory.createSapGuiEditorUtility().openEditorForObject(project, adtObjectRef, true,
				WorkbenchAction.EXECUTE.toString(), null, Collections.<String, String>emptyMap());
	}

}
