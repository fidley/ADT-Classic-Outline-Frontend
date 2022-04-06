package com.abapblog.classicOutline.views.commands;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.abapblog.classicOutline.tree.TreeNode;
import com.abapblog.classicOutline.views.LinkedObject;
import com.abapblog.classicOutline.views.View;
import com.sap.adt.atc.AtcRunOptions;
import com.sap.adt.atc.AtcRunnerFactory;
import com.sap.adt.atc.IAtcResult;
import com.sap.adt.atc.IAtcRunner;
import com.sap.adt.tools.abapsource.common.IRestResourceFactoryFacade;
import com.sap.adt.tools.abapsource.common.RestResourceFactoryFacade;

@SuppressWarnings("restriction")
public class RunATC implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("restriction")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		TreeViewer viewer = View.getCurrentTree().getViewer();
		if (viewer.getSelection() instanceof IStructuredSelection) {
			final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

			final TreeNode object = (TreeNode) selection.getFirstElement();

			LinkedObject linkedObject = object.getLinkedObject();
			IRestResourceFactoryFacade rff = new RestResourceFactoryFacade();

			IAtcRunner atcRunner = AtcRunnerFactory.createAtcRunner(rff, linkedObject.getProject().getName());
			Set<URI> uri = new HashSet();
			if (linkedObject.getParentUri() != null) {
				uri.add(linkedObject.getParentUri());
				AtcRunOptions atcOptions = new AtcRunOptions();
//				atcOptions.setCheckVariantName("ZHAGER_DEFAULT_REMOTE");
				IAtcResult results = atcRunner.checkItemsAtBackend(uri, atcOptions);
				/*
				 * try {
				 * AtcDisplayController.getInstance().showResult(linkedObject.getProject(),
				 * results); } catch (CoreException e) { e.printStackTrace(); }
				 */ }

//			IAdtServicesFactory servicesFactory = adtServicePluginHelper.getServiceFactory();
//			IAbapUnitService abapUnitService = servicesFactory.createAbapUnitService(project.getName(), flag);

		}

		Object test = event.getTrigger();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public boolean isHandled() {
		return false;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
