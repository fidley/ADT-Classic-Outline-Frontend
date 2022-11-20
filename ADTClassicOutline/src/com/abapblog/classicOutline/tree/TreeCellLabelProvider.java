package com.abapblog.classicOutline.tree;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Event;

public class TreeCellLabelProvider extends StyledCellLabelProvider {

	public TreeCellLabelProvider() {
	}

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();

		if (element instanceof TreeNode) {
			TreeNode node = (TreeNode) element;
			StyledString styledString = new StyledString(node.getName());
			Boolean haveRedefinitions = false;
			if (node instanceof TreeParent) {
				String counter = "";
				counter = " " + addNumberOfChildrenToDecoration(counter, (TreeParent) node);
				styledString.append(counter, StyledString.COUNTER_STYLER);
				if (isChildAClassMethod(node))
					haveRedefinitions = true;
			}
			String decoration = " " + node.getDescription();
			styledString.append(decoration, StyledString.DECORATIONS_STYLER);
			if (haveRedefinitions) {
				String redefintions = " (have redefinitions)";
				styledString.append(redefintions, StyledString.QUALIFIER_STYLER);
			}

			cell.setText(styledString.toString());
			cell.setStyleRanges(styledString.getStyleRanges());
			cell.setImage(node.getImage());
		}

		super.update(cell);
	}

	private String addNumberOfChildrenToDecoration(String decoration, TreeParent parent) {
		if (isChildAClassMethod(parent))
			return "(" + getElementChilderNumber(parent) + ")" + decoration;
		return "(" + getElementChilderNumber(parent) + ")" + decoration;
	}

	private int getElementChilderNumber(TreeParent parent) {
		int numberOfElements = 0;
		for (TreeNode child : parent.getChildren()) {
			try {
				if (!(child instanceof TreeParent) || isChildALocalClassOrInterface(child)
						|| isChildAClassMethod(child)) {
					++numberOfElements;
				}
			} catch (Exception e) {

			}
		}

		for (TreeNode child : parent.getChildren()) {
			try {
				if (!isChildAClassMethod(child) && !isChildALocalClassOrInterface(child))
					numberOfElements = numberOfElements + getElementChilderNumber((TreeParent) child);
			} catch (Exception e) {

			}
		}
		return numberOfElements;

	}

	private boolean isChildAClassMethod(TreeNode child) {
		return child.getType().equals("OOM");
	}

	private boolean isChildALocalClassOrInterface(TreeNode child) {
		switch (child.getType()) {
		case "OOL":
		case "OPL":
		case "OPN":
		case "OON": {
			return true;
		}
		}
		return false;
	}

	@Override
	protected void measure(Event event, Object element) {
		super.measure(event, element);
	}
}
