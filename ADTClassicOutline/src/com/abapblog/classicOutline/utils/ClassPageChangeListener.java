package com.abapblog.classicOutline.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.abapblog.classicOutline.tree.TreeContentProvider;
import com.sap.adt.oo.ui.classes.IMultiPageClassEditor;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;

@SuppressWarnings("restriction")
public class ClassPageChangeListener implements IPageChangedListener {

	private ArrayList<IEditorInput> addedListeners = new ArrayList<IEditorInput>();
	private TreeContentProvider treeContentProvider;

	public ClassPageChangeListener(TreeContentProvider treeContentProvider) {
		this.treeContentProvider = treeContentProvider;
		IEditorPart activeEditor = ProjectUtility.getActiveEditor();
		if (activeEditor instanceof IMultiPageClassEditor) {
			IMultiPageClassEditor classEditor = (IMultiPageClassEditor) activeEditor;
			List<IAbapSourcePage> loadedPages = classEditor.getLoadedPages();
			for (int i = 0; i < loadedPages.size(); i++) {
				IAbapSourcePage codePage = loadedPages.get(i);
				IDocumentProvider provider = codePage.getDocumentProvider();
				IEditorInput editorInput = codePage.getEditorInput();
				provider.addElementStateListener(new ClassPageStateListener(editorInput, treeContentProvider));
				addedListeners.add(editorInput);

			}
		}
	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		IMultiPageClassEditor classEditor = (IMultiPageClassEditor) event.getSource();
		IEditorInput editorInput = classEditor.getEditorInput();
		int count = 0;
		while (addedListeners.size() > count) {
			IEditorInput listenedEditorInput = addedListeners.get(count);
			if (listenedEditorInput.equals(editorInput)) {
				return;
			}
			count++;
		}
		IAbapSourcePage codePage = (IAbapSourcePage) event.getSelectedPage();
		IDocumentProvider provider = codePage.getDocumentProvider();
		provider.addElementStateListener(new ClassPageStateListener(editorInput, treeContentProvider));
		addedListeners.add(editorInput);
	}
}
