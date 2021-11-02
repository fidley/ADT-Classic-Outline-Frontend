package com.abapblog.classicOutline.tree;

import java.util.ArrayList;

import com.abapblog.classicOutline.views.LinkedObject;

public class ObjectTree extends TreeParent {
	private ArrayList<TreeNode> treeNodes = new ArrayList<TreeNode>();

	public ObjectTree(LinkedObject linkedobject) {
		super(linkedobject, null);
		this.setSourceNode(createRootNode());
		treeNodes.add(this);
	}

	private SourceNode createRootNode() {
		SourceNode classRoot = new SourceNode(0);
		setName("ROOT");
		setDescription("ROOT");
		setId(0);
		return classRoot;
	}

	public TreeNode getParentByID(int id) {
		for (TreeNode node : treeNodes) {
			if (node.getId() == id)
				return node;
		}
		return null;
	}

	public void addChild(SourceNode sourceNode) {
		TreeParent parentNode;
		TreeNode newNode;
		TreeParent newParentNode;
		if (sourceNode.getChild() == 0) {
			newNode = new TreeNode(super.getLinkedObject(), sourceNode);
			treeNodes.add(newNode);
			try {
				parentNode = (TreeParent) getParentByID(sourceNode.getParent());
				parentNode.addChild(newNode);
			} catch (Exception e) {
				System.out.print(e.getMessage());
			}
		} else {
			newParentNode = new TreeParent(super.getLinkedObject(), sourceNode);
			treeNodes.add(newParentNode);
			parentNode = (TreeParent) getParentByID(sourceNode.getParent());
			parentNode.addChild(newParentNode);
		}

	}

}
