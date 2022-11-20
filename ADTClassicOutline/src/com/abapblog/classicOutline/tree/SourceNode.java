package com.abapblog.classicOutline.tree;

public class SourceNode {
	public static String fieldNameType = "TYPE";
	public static String fieldNameName = "NAME";
	public static String fieldNameId = "ID";
	public static String fieldNameParent = "PARENT";
	public static String fieldNameChild = "CHILD";
	public static String fieldNameText1 = "TEXT1";
	public static String fieldNameText2 = "TEXT2";
	public static String fieldNameText8 = "TEXT8";
	public static String fieldNameText9 = "TEXT9";
	public static String fieldNameNext = "NEXT";
	public static String fieldNameKind5 = "KIND5";
	public static String fieldNameKind6 = "KIND6";
	public static String fieldNameKind7 = "KIND7";
	public static String fieldNameKind8 = "KIND8";
	public static String fieldNameKind9 = "KIND9";
	public static String fieldNameKind4 = "KIND4";
	public static String fieldNameKind3 = "KIND3";

	private String type = "";
	private String name = "";
	private int id = 0;
	private int parent = 0;
	private int child = 0;
	private String text1 = "";
	private String text2 = "";
	private String text8 = "";
	private String text9 = "";
	private int definitionStartId = 0;
	private int index;
	private int kind5 = 0;
	private Boolean kind6 = false;
	private Boolean kind7 = false;
	private Boolean kind8 = false;
	private int kind9 = 0;
	private int kind4 = 9999;
	private Boolean kind3 = false;

	public SourceNode(int id) {
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

	public int getId() {
		return id;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public int getChild() {
		return child;
	}

	public void setChild(int child) {
		this.child = child;
	}

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}

	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		this.text2 = text2;
	}

	public String getText8() {
		return text8;
	}

	public void setText8(String text8) {
		this.text8 = text8;
	}

	public String getText9() {
		return text9;
	}

	public void setText9(String text9) {
		this.text9 = text9;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public int getDefinitionStartId() {
		return definitionStartId;
	}

	public void setDefinitionStartId(int definitionStartId) {
		this.definitionStartId = definitionStartId;
	}

	public Boolean getKind8() {
		return kind8;
	}

	public int getKind9() {
		return kind9;
	}

	public Boolean getKind6() {
		return kind6;
	}

	public Boolean getKind7() {
		return kind7;
	}

	public void setKind6(Boolean kind6) {
		this.kind6 = kind6;
	}

	public void setKind7(Boolean kind7) {
		this.kind7 = kind7;
	}

	public void setKind8(Boolean kind8) {
		this.kind8 = kind8;
	}

	public void setKind9(int kind9) {
		this.kind9 = kind9;
	}

	public int getKind5() {
		return kind5;
	}

	public void setKind5(int kind5) {
		this.kind5 = kind5;
	}

	public int getKind4() {
		return kind4;
	}

	public void setKind4(int kind4) {
		this.kind4 = kind4;
	}

	public Boolean getKind3() {
		return kind3;
	}

	public void setKind3(Boolean kind3) {
		this.kind3 = kind3;
	}

}
