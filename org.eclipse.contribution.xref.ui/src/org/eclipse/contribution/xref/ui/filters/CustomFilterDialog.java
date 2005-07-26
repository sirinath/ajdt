/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Ben Dalziel     - initial version
 *******************************************************************************/
package org.eclipse.contribution.xref.ui.filters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.contribution.xref.internal.ui.text.XRefMessages;
import org.eclipse.jdt.internal.ui.util.SWTUtil;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

public class CustomFilterDialog {

	private static List /* String */ populatingList;
	private static List /* String */ checkedList;
	private static List /* String */ defaultCheckedList;
	private static String dialogTitle;
	private static String dialogMessage;

	private static Shell parentShell;

	
	/**
	 * Activates the dialog, stalls until OK or CANCEL is pressed, and returns
	 * a list of strings corresponding to those items that the user has checked
	 * 
	 * @param shell - Shell of the view which provides the action
	 * @param items - List of strings used to populate the checkBox
	 * @param checkedItems - List of strings corresponding to those items that need to be selected
	 * @param defaultItems - List of strings corresponding to those items that need to be selected by default
	 * @param dlogTitle
	 * @param dlogMessage
	 * @return - List of strings corresponding to those items that the user has checked
	 */
	public static List showDialog(Shell shell, List items, List checkedItems,
			List defaultItems, String dlogTitle, String dlogMessage) {
		
		// Check that provided lists are valid
		if (!isListOfStrings(items) || !isListOfStrings(checkedItems) ||!isListOfStrings(defaultItems)
				|| checkedItems.size() > items.size() || defaultItems.size() > items.size()) {
			return new ArrayList();
		}
		populatingList = items;
		checkedList = checkedItems;
		defaultCheckedList = defaultItems;
		dialogTitle = dlogTitle;
		dialogMessage = dlogMessage;
		parentShell = shell;
		
		FilterDialog dialog = new FilterDialog(parentShell,
				populatingList, checkedList, defaultCheckedList, dialogTitle,
				dialogMessage);
		if (dialog.open() == Window.OK) {
			checkedItems = dialog.getCheckedList();
		}
		return checkedItems;
	}
	
	private static boolean isListOfStrings(List listToCheck) {
		for (Iterator iter = listToCheck.iterator(); iter.hasNext();) {
			Object itemToCheck = (Object) iter.next();
			if (!(itemToCheck instanceof String)) {
				return false;
			}
		}
		return true;
	}

	static class FilterDialog extends SelectionDialog {

		private CheckboxTableViewer fCheckBoxList;

		private static List /* String */ populatingList;
		private static List /* String */ checkedList;
		private static List /* String */ defaultCheckedList;
		private static String dialogTitle;
		private static String dialogMessage;

		public FilterDialog(Shell shell, List items,
				List checkedItems, List defaultItems, String dlogTitle,
				String dlogMessage) {
			super(shell);
			
			populatingList = items;
			checkedList = checkedItems;
			defaultCheckedList = defaultItems;
			dialogTitle = dlogTitle;
			dialogMessage = dlogMessage;
			parentShell = shell;

			setShellStyle(getShellStyle() | SWT.RESIZE);
		}

		protected void configureShell(Shell shell) {
			setTitle(dialogTitle);
			super.configureShell(shell);
			// This can be re-enabled for 3.1
//			PlatformUI.getWorkbench().getHelpSystem().setHelp(shell,
//					IJavaHelpContextIds.CUSTOM_FILTERS_DIALOG);
		}

		/**
		 * Overrides method in Dialog
		 * 
		 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(Composite)
		 */
		protected Control createDialogArea(Composite parent) {

			initializeDialogUnits(parent);
			// create a composite with standard margins and spacing
			Composite composite = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			composite.setFont(parent.getFont());
			Composite group = composite;

			if (populatingList.size() > 0)
				createCheckBoxList(group);

			applyDialogFont(parent);
			return parent;
		}

		private void createCheckBoxList(Composite parent) {

			Label info = new Label(parent, SWT.LEFT);
			info.setText(dialogMessage);

			fCheckBoxList = CheckboxTableViewer
					.newCheckList(parent, SWT.BORDER);
			GridData data = new GridData(GridData.FILL_BOTH);
			data.heightHint = fCheckBoxList.getTable().getItemHeight() * 10;
			fCheckBoxList.getTable().setLayoutData(data);

			fCheckBoxList.setLabelProvider(createLabelPrivder());
			fCheckBoxList.setContentProvider(new ArrayContentProvider());

			fCheckBoxList.setInput(populatingList);
			setInitialSelections(checkedList.toArray());

			List initialSelection = getInitialElementSelections();
			if (initialSelection != null && !initialSelection.isEmpty())
				checkInitialSelections();
			
			addSelectionButtons(parent);
		}

		private void addSelectionButtons(Composite composite) {
			Composite buttonComposite = new Composite(composite, SWT.RIGHT);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			buttonComposite.setLayout(layout);
			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END
					| GridData.GRAB_HORIZONTAL);
			data.grabExcessHorizontalSpace = true;
			composite.setData(data);

			// Select All button
			String label = XRefMessages.CustomFilterDialog_SelectAllButton_label;
			Button selectButton = createButton(buttonComposite,
					IDialogConstants.SELECT_ALL_ID, label, false);
			SWTUtil.setButtonDimensionHint(selectButton);
			SelectionListener listener = new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					fCheckBoxList.setAllChecked(true);
				}
			};
			selectButton.addSelectionListener(listener);

			// De-select All button
			label = XRefMessages.CustomFilterDialog_DeselectAllButton_label;
			Button deselectButton = createButton(buttonComposite,
					IDialogConstants.DESELECT_ALL_ID, label, false);
			SWTUtil.setButtonDimensionHint(deselectButton);
			listener = new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					fCheckBoxList.setAllChecked(false);
				}
			};
			deselectButton.addSelectionListener(listener);

			// Restore Defaults Button
			label = XRefMessages.CustomFilterDialog_RestoreDefaultsButton_label;
			Button restoreDefaultsButton = createButton(buttonComposite,
					IDialogConstants.DESELECT_ALL_ID, label, false);
			SWTUtil.setButtonDimensionHint(restoreDefaultsButton);
			listener = new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					fCheckBoxList.setAllChecked(false);
					Iterator defaultItemsInCheckboxDialog = defaultCheckedList
							.iterator();
					while (defaultItemsInCheckboxDialog.hasNext()) {
						String nextItemToCheck = (String) defaultItemsInCheckboxDialog
								.next();
						if (populatingList.contains(nextItemToCheck)) {
							fCheckBoxList.setChecked(nextItemToCheck, true);
						}
					}
				}
			};
			restoreDefaultsButton.addSelectionListener(listener);
		}

		private void checkInitialSelections() {
			Iterator itemsInCheckboxDialog = checkedList.iterator();
			while (itemsInCheckboxDialog.hasNext()) {
				String nextItemToCheck = (String) itemsInCheckboxDialog.next();
				if (populatingList.contains(nextItemToCheck)) {
					fCheckBoxList.setChecked(nextItemToCheck, true);
				}
			}
		}

		protected void okPressed() {
			Object[] newlyChecked = fCheckBoxList.getCheckedElements();
			checkedList.clear();
			for (int i = 0; i < newlyChecked.length; i++) {
				checkedList.add(newlyChecked[i]);
			}
			super.okPressed();
		}

		public List getCheckedList() {
			return checkedList;
		}

		private ILabelProvider createLabelPrivder() {
			return new LabelProvider() {
				public Image getImage(Object element) {
					return null;
				}

				public String getText(Object element) {
					if (element instanceof String)
						return (String) element;
					else
						return null;
				}
			};
		}
	}
}
