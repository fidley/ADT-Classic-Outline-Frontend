package com.abapblog.classicOutline.tree;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.abapblog.classicOutline.api.ApiCallerFactory;
import com.abapblog.classicOutline.views.LinkedObject;
import com.sap.adt.tools.core.ui.AbapCoreUi;
import com.sap.adt.tools.core.ui.IAdtObjectTypeInfoUi;

@SuppressWarnings("restriction")
public class TreeNode implements IAdaptable {

	private int id;
	private String type;
	private String name;
	private String description;
	private TreeParent parent;
	private String adtUri;
	private String visibility = "";
	private SourceNode sourceNode;
	private int definitionStartId = 0;
	protected LinkedObject linkedObject;

	public TreeNode(LinkedObject linkedObject, SourceNode sourceNode) {
		try {
			setId(sourceNode.getId());
			setType(sourceNode.getType());
			setName(sourceNode.getText1());
			setDescription(sourceNode.getText2());
			setVisibility(sourceNode.getName());
			setSourceNode(sourceNode);
			setDefinitionStartId(sourceNode.getDefinitionStartId());

		} catch (Exception e) {
			// TODO
		}
		this.linkedObject = linkedObject;

	}

	private void setVisibility(String visibility) {
		switch (visibility) {
		case "@5B@": {
			this.visibility = "public";
			break;
		}
		case "@5C@": {
			this.visibility = "private";
			break;
		}
		case "@5D@": {
			this.visibility = "protected";
			break;
		}
		default: {
		}
		}
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TreeParent getParent() {
		return parent;
	}

	public void setParent(TreeParent parent) {
		this.parent = parent;
	}

	public String getAdtUri() {
		if (adtUri.isEmpty() && isClassElementType(getType())) {
			setAdtUri(ApiCallerFactory.getCaller().getUriForTreeNode(this));
		}
		return adtUri;
	}

	private boolean isClassElementType(String type) {
		if (type.charAt(0) == 'C') {
			return false;
		} else {
			return true;
		}
	}

	private void setAdtUri(String adtUri) {
		this.adtUri = adtUri;
	}

	public Image getImage() {
		IAdtObjectTypeInfoUi typeInfo = AbapCoreUi.getObjectTypeRegistry()
				.getObjectTypeByGlobalWorkbenchType(buildWorkbenchType(getType(), getVisibility()));
		if (typeInfo != null) {
			return typeInfo.getImage();
		} else {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
	}

	private String getVisibility() {
		return visibility;
	}

	private String buildWorkbenchType(String treeNodeType, String visibility) {
		String wbType;
		switch (treeNodeType) {

		case "CPO":
		case "OPO": {
			return "FUGR/PO";
		}

		case "CPM":
		case "OPM": {
			return "FUGR/PM";
		}

		case "CPY":
		case "OPY": {
			return "FUGR/PY";
		}

		case "CPN":
		case "OPN": {
			return "INTF/I";
		}
		case "OK": {
			return "DEVC/K";
		}
		case "CPK":
		case "OPK": {
			return "FUGR/PK";
		}

		case "CFF":
		case "OFF": {
			return "FUGR/FF";
		}
		case "OF": {
			return "FUGR/F";
		}
		case "CPG":
		case "OPG": {
			return "FUGR/PG";
		}
		case "CP":
		case "OP": {
			return "PROG/P";
		}
		case "CI":
		case "OI": {
			if (linkedObject.getType().contains("FUGR"))
				return "FUGR/I";
			return "PROG/I";
		}
		case "CT":
		case "OT": {
			return "TRAN/T";
		}
		case "CPT":
		case "OPT": {
			return "PROG/PT";
		}
		case "CPS":
		case "OPS": {
			return "PROG/PS";
		}
		case "CPD":
		case "OPD": {
			return "PROG/PD";
		}
		case "CPE":
		case "OPE": {
			return "PROG/PE";
		}
		case "CPU":
		case "OPU": {
			return "PROG/PU";
		}

		case "BII": {
			return "INTF/OI";
		}
		case "COI":
		case "CIT":
		case "OIT":
		case "CIA":
		case "OIA":
		case "CIO":
		case "OIO":
		case "CPI":
		case "OPI":
		case "OOI": {
			wbType = "INTF/" + treeNodeType.substring(1, treeNodeType.length());
			return wbType;
		}

		case "CPL":
		case "OPL":
		case "OOL":
		case "OOC":
		case "COU":
		case "COS": {
			wbType = "CLAS/OC";
			return wbType;
		}
		case "COLD":
		case "OOLD":
		case "COND":
		case "OOND":
		case "COLI":
		case "OOLI": {
			wbType = "CLAS/OM";
			break;
		}

		case "COY":
		case "OOY":
		case "CONT":
		case "OONT":
		case "COLT":
		case "OOLT": {
			wbType = "CLAS/OT";
			break;
		}
		case "COK":
		case "OOK": {
			return "CLAS/OK";
		}
		case "COLN":
		case "CONN":
		case "OONN":
		case "OOLN":
		case "CON":
		case "OON": {
			return "INTF/OI";
		}

		case "COLA":
		case "OOLA":
		case "CONA":
		case "OONA": {
			wbType = "CLAS/OA";
			break;
		}

		default: {
			wbType = "CLAS/" + treeNodeType.substring(1, treeNodeType.length());
		}
		}
		if (visibility.isEmpty())

		{
			return wbType;
		} else {
			return wbType + "/" + visibility;
		}
	}

	public SourceNode getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(SourceNode sourceNode) {
		this.sourceNode = sourceNode;
	}

	public LinkedObject getLinkedObject() {
		return linkedObject;
	}

	@Override
	public String toString() {
		return getName() + " " + getDescription() + " ";

	}

	public int getDefinitionStartId() {
		return definitionStartId;
	}

	public void setDefinitionStartId(int definitionStartId) {
		this.definitionStartId = definitionStartId;
	}

}
