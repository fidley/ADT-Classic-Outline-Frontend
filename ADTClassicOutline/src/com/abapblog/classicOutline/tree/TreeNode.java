package com.abapblog.classicOutline.tree;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.abapblog.classicOutline.api.ApiCallerFactory;
import com.abapblog.classicOutline.views.LinkedObject;
import com.sap.adt.tools.abapsource.model.abapsource.AbapSourceObjectVisibility;
import com.sap.adt.tools.abapsource.model.abapsource.LevelEnum;
import com.sap.adt.tools.abapsource.ui.AbapSourceUi;
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
	private Boolean readOnly = false;
	private Boolean finalMethod = false;
	private Boolean abstractMethod = false;
	private int declarationType = 0;
	private int objectVisibility = 0;
	private int componentType = 99999;
	private Boolean testMethod = false;

	public TreeNode(LinkedObject linkedObject, SourceNode sourceNode) {
		try {
			mapSourceNodeToTreeNode(sourceNode);

		} catch (Exception e) {
			// TODO
		}
		this.linkedObject = linkedObject;

	}

	private void mapSourceNodeToTreeNode(SourceNode sourceNode) {
		setId(sourceNode.getId());
		setType(sourceNode.getType());
		setName(sourceNode.getText1());
		setDescription(sourceNode.getText2());
		setVisibility(sourceNode.getName());
		setSourceNode(sourceNode);
		setDefinitionStartId(sourceNode.getDefinitionStartId());
		setReadOnly(sourceNode.getKind7());
		setFinalMethod(sourceNode.getKind8());
		setDeclarationType(sourceNode.getKind9());
		setAbstractMethod(sourceNode.getKind6());
		setObjectVisibility(sourceNode.getKind5());
		setComponentType(sourceNode.getKind4());
		setTestMethod(sourceNode.getKind3());
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
		if (getComponentType() == 1 && !getType().contains("OE")) {
			return AbapSourceUi.getInstance().getSharedImages().getMethodImage(
					AbapSourceObjectVisibility.get(getObjectVisibility()), getLevel(), getAbstractMethod(),
					getFinalMethod(), false, isConstructorMethod(), getTestMethod());
		} else if (getComponentType() == 0) {
			DecorationOverlayIcon decoratedImage = new DecorationOverlayIcon(typeInfo.getImage(),
					AbapSourceUi.getInstance().getSharedImages().getImageDescriptor(
							getAttributeImageKey(AbapSourceObjectVisibility.get(getObjectVisibility()), getLevel(),
									getReadOnly(), isConstant())),
					IDecoration.TOP_RIGHT);
			return decoratedImage.createImage();
		} else if (getComponentType() == 2) {
			return AbapSourceUi.getInstance().getSharedImages()
					.getEventHandlerImage(AbapSourceObjectVisibility.get(getObjectVisibility()), getLevel());
		} else if (getComponentType() == 3) {
			return AbapSourceUi.getInstance().getSharedImages()
					.getDDicTypeImage(AbapSourceObjectVisibility.get(getObjectVisibility()));
		} else if (getType().equals("OOL") || getType().equals("OPL")) {
			return com.sap.adt.tools.core.ui.Activator.getDefault().getClassImage(getAbstractMethod(), getFinalMethod(),
					false, false, getTestMethod(), true);
		} else if (getType().equals("OOC")) {
			return com.sap.adt.tools.core.ui.Activator.getDefault().getClassImage(getAbstractMethod(), getFinalMethod(),
					false, false, getTestMethod());
		} else if (typeInfo != null) {
			return typeInfo.getImage();
		} else if (getType().contains("PG") || getType().contains("PS") || getType().contains("PC")
				|| getType().contains("PZ") || getType().equals("CT") || getType().equals("OT")
				|| getType().equals("CMO") || getType().equals("OMO") || getType().equals("CDH")
				|| getType().equals("ODH") || getType().equals("CWO") || getType().equals("OWO")
				|| getType().equals("C5F") || getType().equals("O5F") || getType().equals("C5I")
				|| getType().equals("O5I") || getType().equals("CQ0Q") || getType().equals("OQ0Q")
				|| getType().equals("B5") || getType().equals("BW") || getType().equals("CXI")
				|| getType().equals("OXI") || getType().equals("CAD") || getType().equals("OAD")

		) {
			return com.sap.adt.tools.core.ui.Activator.getDefault()
					.getImage(com.sap.adt.tools.core.ui.Activator.IMG_OBJECT_VISUAL_INTEGRATED);
		} else {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
	}

	public String getAttributeImageKey(AbapSourceObjectVisibility visibility, LevelEnum level, boolean readOnly,
			boolean constant) {
		long key = 2L;
		long visKey = visibility.ordinal();
		long levelKey = level.ordinal();
		long attrKey = ((readOnly ? 1 : 0) + (constant ? 2 : 0));
		key = key + (visKey << 8L) + (levelKey << 16L) + (attrKey << 24L);
		return String.valueOf(key);
	}

	public boolean isConstant() {
		switch (getDeclarationType()) {
		case 2:
			return true;
		}
		return false;
	}

	public LevelEnum getLevel() {
		switch (getDeclarationType()) {
		case 1:
			return LevelEnum.STATIC;
		}

		return LevelEnum.INSTANCE;
	}

	private boolean isConstructorMethod() {
		if (getName().equals("CONSTRUCTOR") || getName().equals("CLASS_CONSTRUCTOR"))
			return true;
		return false;
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
		case "OK":
		case "CK": {
			return "DEVC/K";
		}
		case "CPK":
		case "OPK": {
			return "FUGR/PK";
		}

		case "CPX":
		case "OPX": {
			return "FUGR/PX";
		}

		case "CQ1O":
		case "OQ1O":
		case "CFF":
		case "OFF": {
			return "FUGR/FF";
		}
		case "CF":
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

		case "COC":
		case "BO":
		case "CPL":
		case "OPL":
		case "COL":
		case "OOL":
		case "OOC":
		case "COU":
		case "COS":
		case "ROM": {
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

		case "CDF":
		case "ODF":
			wbType = "DDLS/DF";
			break;
		case "ODO":
			wbType = "STOB/DO";
			break;
		case "CDV":
		case "ODV":
			wbType = "VIEW/DV";
			break;

		case "ODT":
		case "CDT":
			wbType = "TABL/DT";
			break;

		case "ODE":
		case "CDE":
			wbType = "DTEL/DE";
			break;

		case "ODD":
		case "CDD":
			wbType = "DOMA/DD";
			break;

		case "CDL":
		case "ODL":
			wbType = "ENQU/DL";
			break;

		case "CDA":
		case "ODA":
			wbType = "TTYP/DA";
			break;

		case "CDS":
		case "ODS":
			wbType = "TABL/DS";
			break;

		case "CDH":
		case "ODH":
			wbType = "SHLP/DH";
			break;

		case "CKI":
		case "OKI":
			wbType = "PINF/KI";
			break;

		case "CB":
		case "OB":
			wbType = "SUSO/B";
			break;

		case "CXH":
		case "OXH":
			wbType = "ENHO/XHB";
			break;

		case "OXS":
		case "CXS":
		case "CXT":
		case "OXT":

			wbType = "ENHS/XSB";
			break;

		case "CN":
		case "ON":
			wbType = "MSAG/N";
			break;

		case "CQ15":
		case "OQ15":
			wbType = "NROB/NRO";
			break;

		case "CR":
		case "OR":
			wbType = "PARA/R";
			break;

		case "CQ01":
		case "OQ01":
			wbType = "AUTH";
			break;

		case "CDTX":
		case "ODTX":
			wbType = "XINX/DTX";
			break;

		case "CQ1E":
		case "OQ1E":
			wbType = "SAMC";
			break;

		case "C3I":
		case "O3I":
			wbType = "WEBI/3I";
			break;
		case "C31":
		case "O31":
			wbType = "SPRX/31";
			break;
		case "C3C":
		case "O3C":
			wbType = "SPRX/3C";
			break;
		case "C3D":
		case "O3D":
			wbType = "SPRX/3D";
			break;

		case "CQ0I":
		case "OQ0I":
			wbType = "BOBX";
			break;

		case "CQ0H":
		case "OQ0H":
			wbType = "BOBF";
			break;

		case "CVT":
		case "OVT":
			wbType = "XSLT/VT";
			break;

		case "CDG":
		case "ODG":
			wbType = "TYPE/DG";
			break;

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

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Boolean getFinalMethod() {
		return finalMethod;
	}

	public void setFinalMethod(Boolean finalMethod) {
		this.finalMethod = finalMethod;
	}

	public Boolean getAbstractMethod() {
		return abstractMethod;
	}

	public void setAbstractMethod(Boolean abstractMethod) {
		this.abstractMethod = abstractMethod;
	}

	public int getDeclarationType() {
		return declarationType;
	}

	public void setDeclarationType(int declarationType) {
		this.declarationType = declarationType;
	}

	public int getObjectVisibility() {
		if (objectVisibility == 2) {
			return objectVisibility + 1;
		}
		return objectVisibility;
	}

	public void setObjectVisibility(int objectVisibility) {
		this.objectVisibility = objectVisibility;
	}

	public int getComponentType() {
		return componentType;
	}

	public void setComponentType(int componentType) {
		this.componentType = componentType;
	}

	public Boolean getTestMethod() {
		return testMethod;
	}

	public void setTestMethod(Boolean testMethod) {
		this.testMethod = testMethod;
	}

}
