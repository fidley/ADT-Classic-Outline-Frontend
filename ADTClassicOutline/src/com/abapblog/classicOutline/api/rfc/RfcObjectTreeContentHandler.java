package com.abapblog.classicOutline.api.rfc;

import com.abapblog.classicOutline.tree.ObjectTree;
import com.abapblog.classicOutline.tree.SourceNode;
import com.abapblog.classicOutline.views.LinkedObject;
import com.sap.conn.jco.JCoTable;

public class RfcObjectTreeContentHandler {

	public static ObjectTree deserialize(LinkedObject linkedObject, JCoTable rfcTable) {
		int definitionStartId = 0;
		ObjectTree newObjectTree = new ObjectTree(linkedObject);
		if (rfcTable == null)
			return null;
		for (int i = 0; i < rfcTable.getNumRows(); i++) {
			rfcTable.setRow(i);
			SourceNode sourceNode = new SourceNode(rfcTable.getInt(SourceNode.fieldNameId));
			sourceNode.setChild(rfcTable.getInt(SourceNode.fieldNameChild));
			sourceNode.setParent(rfcTable.getInt(SourceNode.fieldNameParent));
			sourceNode.setType(rfcTable.getString(SourceNode.fieldNameType));
			sourceNode.setName(rfcTable.getString(SourceNode.fieldNameName));
			sourceNode.setText1(rfcTable.getString(SourceNode.fieldNameText1));
			sourceNode.setText2(rfcTable.getString(SourceNode.fieldNameText2));
			sourceNode.setText8(rfcTable.getString(SourceNode.fieldNameText8));
			sourceNode.setText9(rfcTable.getString(SourceNode.fieldNameText9));
			sourceNode.setIndex(i);
			if (sourceNode.getType().equals("COLD")) {
				definitionStartId = rfcTable.getInt(SourceNode.fieldNameNext);
			} else if (sourceNode.getType().equals("OOLD")) {
				sourceNode.setDefinitionStartId(definitionStartId);
			} else {
				definitionStartId = 0;
			}
			newObjectTree.addChild(sourceNode);
		}
		return newObjectTree;
	}

}
