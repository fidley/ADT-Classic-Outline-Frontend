package com.abapblog.classicOutline.api.rfc;

import com.abapblog.classicOutline.api.IApiCaller;
import com.abapblog.classicOutline.tree.ObjectTree;
import com.abapblog.classicOutline.tree.SourceNode;
import com.abapblog.classicOutline.tree.TreeNode;
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
			function.getImportParameterList().getField("OBJECT_NAME").setValue(linkedObject.getName());
			function.getImportParameterList().getField("OBJECT_TYPE").setValue(linkedObject.getType());
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

			function.getImportParameterList().getField("OBJECT_NAME").setValue(linkedObject.getName());
			function.getImportParameterList().getField("OBJECT_TYPE").setValue(linkedObject.getType());
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
//				String masterType = function.getExportParameterList().getString("MASTER_TYPE");
////				if (!masterType.isEmpty()) {
////					linkedObject.setType(masterType);
////				}
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

}
