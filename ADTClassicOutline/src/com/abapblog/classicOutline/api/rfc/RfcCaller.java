package com.abapblog.classicOutline.api.rfc;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

import com.abapblog.classicOutline.Activator;
import com.abapblog.classicOutline.api.IApiCaller;
import com.abapblog.classicOutline.preferences.PreferenceConstants;
import com.abapblog.classicOutline.tree.ObjectTree;
import com.abapblog.classicOutline.tree.SourceNode;
import com.abapblog.classicOutline.tree.TreeNode;
import com.abapblog.classicOutline.utils.AbapRelease;
import com.abapblog.classicOutline.utils.BackendComponentVersion;
import com.abapblog.classicOutline.utils.ProjectUtility;
import com.abapblog.classicOutline.views.LinkedObject;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

public class RfcCaller implements IApiCaller {

	@Override
	public ObjectTree getObjectTree(LinkedObject linkedObject, boolean forceRefresh) {

		ObjectTree rfcObjectTree = getObjectTree(linkedObject);

		if (forceRefresh == false & rfcObjectTree != null) {
			return rfcObjectTree;
		}
		return getNewObjectTree(linkedObject);

	}

	@Override
	public ObjectTree getNewObjectTree(LinkedObject linkedObject) {
		try {
			String destinationId = ProjectUtility.getDestinationID(linkedObject.getProject());
			JCoDestination destination = JCoDestinationManager.getDestination(destinationId);
			JCoFunction function = destination.getRepository().getFunction("Z_ADTCO_GET_OBJECT_TREE");
			if (function == null) {
				ObjectTree newObjectTree = new ObjectTree(linkedObject);
				SourceNode sourceNode = new SourceNode(1);
				sourceNode.setName("DummyNode");
				sourceNode.setText1("Z_ADTCO_GET_OBJECT_TREE not found. "
						+ "This plugin needs a ABAP Backend components that have to be installed in the system in order to use it."
						+ "Use abapGit to install repository from https://github.com/fidley/ADT-Classic-Outline-Backend");
				sourceNode.setType("CO");
				newObjectTree.addChild(sourceNode);
				return newObjectTree;
			}
			function.getImportParameterList().getField("OBJECT_NAME").setValue(linkedObject.getParentName());
			function.getImportParameterList().getField("OBJECT_TYPE").setValue(linkedObject.getParentType());

			addParametersForTreeCall(function);

			try {
				function.execute(destination);
				JCoTable objectTree = function.getExportParameterList().getTable("TREE");
				return RfcObjectTreeContentHandler.deserialize(linkedObject, objectTree);
			} catch (AbapException e) {
				System.out.println(e.toString());
				return null;
			}
		} catch (

		JCoException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void addParametersForTreeCall(JCoFunction function) {
		try {
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			JCoTable parametersTable = function.getImportParameterList().getTable("PARAMETERS");
			parametersTable.appendRow();
			parametersTable.setValue("NAME", PreferenceConstants.P_FETCH_METHOD_REDEFINITIONS);
			parametersTable.setValue("VALUE",
					parseToAbapBool(store.getBoolean(PreferenceConstants.P_FETCH_METHOD_REDEFINITIONS)));
			parametersTable.appendRow();
			parametersTable.setValue("NAME", PreferenceConstants.P_LOAD_ALL_LEVELS_OF_SUBCLASSES);
			parametersTable.setValue("VALUE",
					parseToAbapBool(store.getBoolean(PreferenceConstants.P_LOAD_ALL_LEVELS_OF_SUBCLASSES)));
			parametersTable.appendRow();
			parametersTable.setValue("NAME", PreferenceConstants.P_LOAD_ALL_LEVELS_OF_REDEFINITIONS);
			parametersTable.setValue("VALUE",
					parseToAbapBool(store.getBoolean(PreferenceConstants.P_LOAD_ALL_LEVELS_OF_REDEFINITIONS)));

		} catch (Exception e) {
			e.getLocalizedMessage();
		}
	}

	private String parseToAbapBool(boolean booleanValue) {
		if (booleanValue)
			return "X";
		return "";
	}

	@Override
	public ObjectTree getObjectTree(LinkedObject linkedObject) {
		int count = 0;
		while (objectsList.size() > count) {
			ObjectTree rfcObjectTree = objectsList.get(count);
			if (rfcObjectTree.getLinkedObject().getName() == linkedObject.getName()
					&& rfcObjectTree.getLinkedObject().getProject().getName() == linkedObject.getProject().getName()
					&& rfcObjectTree.getLinkedObject().getType() == linkedObject.getType()) {
				return rfcObjectTree;
			}
			count++;
		}
		return null;
	}

	@Override
	public String getUriForTreeNode(TreeNode treeNode) {
		LinkedObject linkedObject = treeNode.getLinkedObject();
		String destinationId = ProjectUtility.getDestinationID(linkedObject.getProject());
		try {
			JCoDestination destination = JCoDestinationManager.getDestination(destinationId);
			JCoFunction function = destination.getRepository().getFunction("Z_ADTCO_GET_URI_FOR_TREE_NODE");
			if (function == null)
				throw new RuntimeException("Z_ADTCO_GET_URI_FOR_TREE_NODE not found."
						+ "This plugin needs a ABAP Backend components that have to be installed in the system in order to use it."
						+ "Use abapGit to install repository from https://github.com/fidley/ADT-Classic-Outline-Backend");

			function.getImportParameterList().getField("OBJECT_NAME").setValue(linkedObject.getParentName());
			function.getImportParameterList().getField("OBJECT_TYPE").setValue(linkedObject.getParentType());
			RfcObjectNodeContentHandler.serialize(treeNode.getSourceNode(),
					function.getImportParameterList().getStructure("NODE"));

			try {
				function.execute(destination);
				return function.getExportParameterList().getString("URI");

			} catch (AbapException e) {
				System.out.println(e.toString());
				return "";
			}
		} catch (JCoException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String getMasterProgramForInclude(LinkedObject linkedObject) {
		String destinationId = ProjectUtility.getDestinationID(linkedObject.getProject());
		try {
			JCoDestination destination = JCoDestinationManager.getDestination(destinationId);
			JCoFunction function = destination.getRepository().getFunction("Z_ADTCO_GET_INC_MASTER_PROGRAM");
			if (function == null) {
				System.out.println("Z_ADTCO_GET_INC_MASTER_PROGRAM not found."
						+ "This plugin needs a lastest ABAP Backend components that have to be installed in the system in order to use it."
						+ "Use abapGit to install repository from https://github.com/fidley/ADT-Classic-Outline-Backend");
				return "";
			}

			function.getImportParameterList().getField("INCLUDE").setValue(linkedObject.getName());
			try {
				function.execute(destination);
				String masterType = function.getExportParameterList().getString("MASTER_TYPE");
				if (!masterType.isEmpty()) {
					linkedObject.setParentType(masterType);
				}
				return function.getExportParameterList().getString("MASTER");

			} catch (AbapException e) {
				System.out.println(e.toString());
				return "";
			}
		} catch (JCoException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public AbapRelease getAbapRelease(IProject project) {
		String destinationId = ProjectUtility.getDestinationID(project);
		try {
			JCoDestination destination = JCoDestinationManager.getDestination(destinationId);
			JCoFunction function = destination.getRepository().getFunction("DELIVERY_GET_COMPONENT_RELEASE");
			if (function == null) {
				System.out.println("DELIVERY_GET_COMPONENT_RELEASE not found."
						+ "This plugin needs a lastest ABAP Backend components that have to be installed in the system in order to use it."
						+ "Use abapGit to install repository from https://github.com/fidley/ADT-Classic-Outline-Backend");
				return null;
			}

			String BASIS_COMPONENT_NAME = "SAP_BASIS";
			function.getImportParameterList().getField("IV_COMPNAME").setValue(BASIS_COMPONENT_NAME);
			try {
				function.execute(destination);

				String version = function.getExportParameterList().getString("EV_COMPVERS");
				String patchLevel = function.getExportParameterList().getString("EV_PATCHLVL");
				AbapRelease abapRelease = new AbapRelease(version, patchLevel, project);
				return abapRelease;

			} catch (AbapException e) {
				System.out.println(e.toString());
				return null;
			}
		} catch (JCoException e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public BackendComponentVersion getBackendComponentVersion(IProject project) {
		String destinationId = ProjectUtility.getDestinationID(project);
		try {
			JCoDestination destination = JCoDestinationManager.getDestination(destinationId);
			JCoFunction function = destination.getRepository().getFunction("Z_ADTCO_GET_BACKEND_COMP_VER");
			if (function == null) {
				System.out.println("Z_ADTCO_GET_BACKEND_COMP_VER not found."
						+ "This plugin needs a lastest ABAP Backend components that have to be installed in the system in order to use it."
						+ "Use abapGit to install repository from https://github.com/fidley/ADT-Classic-Outline-Backend");
				return new BackendComponentVersion(0, project);
			}

			try {
				function.execute(destination);

				Integer version = function.getExportParameterList().getInt("VERSION");
				return new BackendComponentVersion(version, project);

			} catch (AbapException e) {
				System.out.println(e.toString());
				return null;
			}
		} catch (JCoException e) {
			e.printStackTrace();
		}
		return new BackendComponentVersion(0, project);

	}

}
