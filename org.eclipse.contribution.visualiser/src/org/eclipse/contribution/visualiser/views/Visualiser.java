/*******************************************************************************
 * Copyright (c) 2002 - 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Sian Whiting - initial version and later features
 *     Andy Clement - refactored for stand-alone visualiser
 *     Matt Chapman - switch to use new rendering code
 *******************************************************************************/
package org.eclipse.contribution.visualiser.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.contribution.visualiser.VisualiserPlugin;
import org.eclipse.contribution.visualiser.core.ProviderManager;
import org.eclipse.contribution.visualiser.core.Stripe;
import org.eclipse.contribution.visualiser.core.resources.VisualiserImages;
import org.eclipse.contribution.visualiser.interfaces.IContentProvider;
import org.eclipse.contribution.visualiser.interfaces.IGroup;
import org.eclipse.contribution.visualiser.interfaces.IMarkupKind;
import org.eclipse.contribution.visualiser.interfaces.IMarkupProvider;
import org.eclipse.contribution.visualiser.interfaces.IMember;
import org.eclipse.contribution.visualiser.internal.preference.VisualiserPreferences;
import org.eclipse.contribution.visualiser.internal.preference.VisualiserPreferencesDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

/**
 * This class represents the main view of the Visualiser.
 */
public class Visualiser extends ViewPart {

	private VisualiserCanvas visCanvas;

	protected static IContentProvider contentP;

	protected static IMarkupProvider markupP;

	private static ImageDescriptor groupViewImage = VisualiserImages.GROUP_VIEW;

	private static ImageDescriptor memberViewImage = VisualiserImages.MEMBER_VIEW;

	private static List data = null;

	// actions
	private Action limitAction;

//	private Action lockAction;

	private Action preferencesAction;

	private Action fitToViewAction;

	private static Action groupViewAction;

	private static Action memberViewAction;

	private Action zoomInAction;
	
	private Action zoomOutAction;

	private static boolean inGroupView = false;

	private static boolean inLimitMode = false;

//	private static boolean locked = false;

	private static boolean fitToView = false;

	private int maxBarWidth = 200;

	private String title;
	
	private String zoomString;
	
		 private static Job redrawJob;
		 
	public Visualiser() {
		VisualiserPlugin.getDefault().setVisualiser(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		try {	
			getSite().getPage().showView("org.eclipse.contribution.visualiser.views.Menu"); //$NON-NLS-1$
		} catch (PartInitException pie) {
			VisualiserPlugin.logException(pie);
		}
		visCanvas = new VisualiserCanvas(parent, this);
		makeActions();
		contributeToActionBars();
		memberViewAction.setChecked(true);
		String title = ProviderManager.getCurrent().getTitle();
		refreshTitle(title);
	}

	/**
	 * Adds actions to the action bar.
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * Adds actions to local pull down menu.
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(preferencesAction);
		manager.add(new Separator());
		manager.add(limitAction);
		manager.add(new Separator());
		manager.add(groupViewAction);
		manager.add(memberViewAction);
	}

	/**
	 * Called by contributeToActionBars to add actions to local tool bar.
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(zoomInAction);
		manager.add(zoomOutAction);
		manager.add(fitToViewAction);
//		manager.add(lockAction);
		manager.add(limitAction);
		manager.add(new Separator());
		manager.add(groupViewAction);
		manager.add(memberViewAction);
	}

	/**
	 * Private method called by createPartControl to create and initialise
	 * actions.
	 */
	private void makeActions() {
		makeActionPreferences();
		makeActionZoomin();
		makeActionZoomout();
		makeActionLimitvis();
		makeActionGroupView();
		makeActionMemberView();
		makeActionFitToView();
//		makeActionLock();
	}

	/**
	 * Creates the action which displays the visualiser preferences
	 */
	private void makeActionPreferences() {
		preferencesAction = new Action() {
			public void run() {
				VisualiserPreferencesDialog vpd = new VisualiserPreferencesDialog(
						Visualiser.this.getViewSite().getShell());
				vpd.create();
				vpd.open();
			}
		};
		preferencesAction.setText(VisualiserPlugin
				.getResourceString("Preferences_24")); //$NON-NLS-1$
		preferencesAction.setToolTipText(VisualiserPlugin
				.getResourceString("Preferences_Tip_25")); //$NON-NLS-1$
		preferencesAction.setImageDescriptor(VisualiserImages.PREFERENCES);
	}

	/**
	 * Makes the action which limits (and unlimits) the visualisation to
	 * affected bars only
	 */
	private void makeActionLimitvis() {
		limitAction = new Action() {
			public int getStyle() {
				return IAction.AS_CHECK_BOX;
			}

			public void run() {
				if (!inLimitMode)
					inLimitMode = true;
				else
					inLimitMode = false;

				setChecked(inLimitMode);
				updateDisplay(false); // aspectDrawing.limit(canvas,
									  // in_limit_mode);
			}
		};
		limitAction.setText(VisualiserPlugin.getResourceString("Limit_view_9")); //$NON-NLS-1$
		limitAction
				.setToolTipText(VisualiserPlugin
						.getResourceString("Limits_visualisation_to_affected_bars_only_10")); //$NON-NLS-1$
		limitAction.setImageDescriptor(VisualiserImages.LIMIT_MODE);
	}

//	/**
//	 * Make the lock action
//	 */
//	private void makeActionLock() {
//		lockAction = new Action() {
//			public void run() {
//				locked = !locked;
//				//groupViewAction.setEnabled(!locked);
//				//memberViewAction.setEnabled(!locked);
//				if (!locked) {
//					updateDisplay(true);
//				}
//			}
//
//			public int getStyle() {
//				return IAction.AS_CHECK_BOX;
//			}
//		};
//		lockAction.setText(VisualiserPlugin.getResourceString("Lock")); //$NON-NLS-1$
//		lockAction.setToolTipText(VisualiserPlugin
//				.getResourceString("Lock_Tooltip")); //$NON-NLS-1$
//		lockAction.setImageDescriptor(VisualiserImages.LOCK);
//
//	}

	/**
	 * Creates the actions that specifies whether or not the drawing should use
	 * absolute proportions
	 */
	private void makeActionFitToView() {
		fitToViewAction = new Action() {
			private String zString;

			public void run() {
				fitToView = !fitToView;
				if (fitToView) {
					zString = zoomString;
					setZoomString(VisualiserPlugin
							.getResourceString("Zoom.fittoview")); //$NON-NLS-1$
				} else {
					setZoomString(zString);
				}
				updateDisplay(false);
				zoomInAction.setEnabled(!fitToView);
				zoomOutAction.setEnabled(!fitToView);
			}

			public int getStyle() {
				return IAction.AS_CHECK_BOX;
			}
		};
		fitToViewAction.setText(VisualiserPlugin
				.getResourceString("Absolute_Proportions")); //$NON-NLS-1$
		fitToViewAction.setToolTipText(VisualiserPlugin
				.getResourceString("Absolute_Proportions")); //$NON-NLS-1$
		fitToViewAction
				.setImageDescriptor(VisualiserImages.FIT_TO_VIEW);
	}

	/**
	 * Make the action which switches the view to member mode
	 */
	private void makeActionMemberView() {
		memberViewAction = new Action() {
			public int getStyle() {
				return IAction.AS_CHECK_BOX;
			}

			public void run() {
				activateMemberView();
				updateDisplay(false);
			}
		};
		memberViewAction.setText(VisualiserPlugin
				.getResourceString("Class_View_15")); //$NON-NLS-1$
		memberViewAction.setToolTipText(VisualiserPlugin
				.getResourceString("Changes_to_member_view_16")); //$NON-NLS-1$
		memberViewAction.setImageDescriptor(memberViewImage);
	}

	/**
	 * Make the action which switches the view to group mode
	 */
	private void makeActionGroupView() {
		groupViewAction = new Action() {
			public int getStyle() {
				return IAction.AS_CHECK_BOX;
			}

			public void run() {
				if (!(inGroupView)) {

					setChecked(true);
					memberViewAction.setChecked(false);
					inGroupView = true;
				} else {
					setChecked(true);
				}
				activateGroupView();
				updateDisplay(false);
			}
		};
		groupViewAction.setText(VisualiserPlugin
				.getResourceString("Package_View_12")); //$NON-NLS-1$
		groupViewAction.setToolTipText(VisualiserPlugin
				.getResourceString("Changes_to_group_view_13")); //$NON-NLS-1$
		groupViewAction.setImageDescriptor(groupViewImage);
	}

	/**
	 * Make the zoom-out action
	 *
	 */
	private void makeActionZoomout() {
		zoomOutAction = new Action() {
			public void run() {
				visCanvas.zoomOut();
			}
		};
		zoomOutAction.setText(VisualiserPlugin.getResourceString("Zoom_Out_6")); //$NON-NLS-1$
		zoomOutAction.setToolTipText(VisualiserPlugin.getResourceString("Zooms_out_7")); //$NON-NLS-1$
		zoomOutAction.setImageDescriptor(VisualiserImages.ZOOM_OUT);
	}
		
	
	/**
	 * Make the zoom-in action
	 */
	private void makeActionZoomin() {
		zoomInAction = new Action() {
			public void run() {
				visCanvas.zoomIn();
			}
		};
		zoomInAction.setText(VisualiserPlugin.getResourceString("Zoom_In_3")); //$NON-NLS-1$
		zoomInAction.setToolTipText(VisualiserPlugin.getResourceString("Zooms_in_on_visualisation_4")); //$NON-NLS-1$
		zoomInAction.setImageDescriptor(VisualiserImages.ZOOM_IN);
	}

	public void zoominSetEnabled(boolean enabled) {
		zoomInAction.setEnabled(enabled);
	}
	public void zoomoutSetEnabled(boolean enabled) {
		zoomOutAction.setEnabled(enabled);
	}
	
	/**
	 * Activate group mode
	 */
	private static void activateGroupView() {
		inGroupView = true;
		memberViewAction.setChecked(false);
		groupViewAction.setChecked(true);
	}

	/**
	 * Activate member mode
	 */
	private static void activateMemberView() {
		inGroupView = false;
		memberViewAction.setChecked(true);
		groupViewAction.setChecked(false);
	}

	/**
	 * Refresh the title. Sets the view's title to 'Visualiser - ' plus the
	 * argument
	 * 
	 * @param title
	 */
	public void refreshTitle(String title) {
		this.title = title;
		refreshTitle();
	}

	private void refreshTitle() {
		String s = VisualiserPlugin.getResourceString("Visualiser") //$NON-NLS-1$
				+ " - " + title; //$NON-NLS-1$
		if ((zoomString != null) && (zoomString.length() > 0)) {
			s += " (" + zoomString + ")"; //$NON-NLS-1$//$NON-NLS-2$
		}
		this.setContentDescription(s);
	}
	
	public void setZoomString(String s) {
		zoomString = s;
		refreshTitle();
	}
	
	/**
	 * Set the maximum bar width for the view in pixels
	 * 
	 * @param size
	 */
	public void setMaxBarSize(int size) {
		maxBarWidth = size;
		if (data != null) {
			visCanvas.redraw(data);
		}
	}

	public int getMaxBarSize() {
		return maxBarWidth;
	}

	public int getMinBarSize() {
		return VisualiserPreferences.getMinBarSize();
	}

	public boolean isFitToView() {
		return fitToView;
	}

	public boolean isGroupView() {
		return inGroupView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		visCanvas.setFocus();
	}

	public void dispose() {
		super.dispose();
		VisualiserPlugin.visualiser=null;
		visCanvas.dispose();
	}

	/**
	 * Called by Menu when the colour selections or aspects selection in the
	 * menu has changed.
	 */
	public void draw() {
		 		 getVisualiserRedrawJob().schedule();
	}

		 private synchronized Job getVisualiserRedrawJob() {
		 		 if (redrawJob == null) {
		 		 		 redrawJob = new UIJob(VisualiserPlugin.getResourceString("Jobs.VisualiserRedraw")) { //$NON-NLS-1$

		 		 		 		 public IStatus runInUIThread(IProgressMonitor monitor) {
		 		 		 		 		if ((visCanvas!=null) && !visCanvas.isDisposed()) {
		 		 		 		 			visCanvas.redraw(data);
		 		 		 		 		}
		 		 		 		 		return Status.OK_STATUS;
		 		 		 		 }};
		 		 }
		 		 return redrawJob;
		 }
		 
	/**
	 * Update the display
	 */
	public void updateDisplay(boolean updateMenu) {
//		if (!locked) {
			if (inGroupView) {
				if (inLimitMode) {
					data = limitData(contentP.getAllGroups());
				} else {
					data = contentP.getAllGroups();
				}
			} else {
				if (inLimitMode) {
					data = limitData(contentP.getAllMembers());
				} else {
					data = contentP.getAllMembers();
				}
			}
//		}

		if (VisualiserPlugin.menu != null && updateMenu) {
			VisualiserPlugin.menu.reset();
			VisualiserPlugin.menu.ensureUptodate();
		}
		 		 draw();
	}

	/**
	 * Shorten the input data to only those bars that have active kinds.
	 */
	private static List limitData(List data) {
		if (data == null)
			return null;
		log(3, "In limit processing: Input size: " + data.size());
		List activeBars = new ArrayList();
		for (Iterator iter = data.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof IGroup) {
				IGroup aGroup = (IGroup) element;
				List stripes = markupP.getGroupMarkups(aGroup);
				if (containsActiveStripe(stripes))
					activeBars.add(element);
			} else {
				IMember aMember = (IMember) element;
				List stripes = markupP.getMemberMarkups(aMember);
				if (containsActiveStripe(stripes))
					activeBars.add(element);
			}
		}
		log(3, "Finished limit processing: Output size: " + activeBars.size());
		return activeBars;
	}

	private static boolean containsActiveStripe(List stripes) {
		if (stripes == null)
			return false;
		// Go through the stripes in the list
		for (Iterator iter = stripes.iterator(); iter.hasNext();) {
			Stripe element = (Stripe) iter.next();
			List kinds = element.getKinds();
			// Go through the kinds in each stripe
			for (Iterator iterator = kinds.iterator(); iterator.hasNext();) {
				IMarkupKind kind = (IMarkupKind) iterator.next();
				// If any kind is active, return true
				if (VisualiserPlugin.menu == null) {
					// If menu is null we assume all kinds are active
					return true;
				} else if (VisualiserPlugin.menu.getActive(kind)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Set the current content provider
	 * 
	 * @param vcp -
	 *            the current IContentProvider
	 */
	public void setVisContentProvider(IContentProvider vcp) {
		contentP = vcp;
		memberViewImage = vcp.getMemberViewIcon() == null ? VisualiserImages.MEMBER_VIEW
				: vcp.getMemberViewIcon();
		groupViewImage = vcp.getGroupViewIcon() == null ? VisualiserImages.GROUP_VIEW
				: vcp.getGroupViewIcon();
		if (groupViewAction != null) {
			groupViewAction.setImageDescriptor(groupViewImage);
		}
		if (memberViewAction != null) {
			memberViewAction.setImageDescriptor(memberViewImage);
		}
	}

	public IMarkupProvider getVisMarkupProvider() {
		return markupP;
	}

	/**
	 * Set the current markup provider
	 * 
	 * @param vmp -
	 *            the current IMarkupProvider
	 */
	public void setVisMarkupProvider(IMarkupProvider vmp) {
		markupP = vmp;
		//		if(markupP.hasMultipleModes()) {
		//			if(changeStripeModeAction == null) {
		//				makeActionChangeStripeMode();
		//				if(getViewSite() != null) {
		//					IActionBars bars = getViewSite().getActionBars();
		//					bars.getToolBarManager().removeAll();
		//					fillLocalToolBar(bars.getToolBarManager(), true);
		//					bars.updateActionBars();
		//				}
		//			}
		//		} else {
		//			if(changeStripeModeAction != null){
		//				IActionBars bars = getViewSite().getActionBars();
		//				bars.getToolBarManager().removeAll();
		//				fillLocalToolBar(bars.getToolBarManager(), false);
		//				bars.updateActionBars();
		//				changeStripeModeAction = null;
		//			}
		//		}
	}

	/**
	 * Only show kinds affecting the member or group with the given name
	 * @param name
	 */
	protected void onlyShowColorsAffecting(String name) {
		List names = new ArrayList();
		List members = contentP.getAllMembers();
		boolean found = false;
		for (Iterator it = members.iterator(); it.hasNext();) {
			IMember member = (IMember) it.next();
			if(member.getToolTip().equals(name)){
				found = true;
				List markups = markupP.getMemberMarkups(member);
				if(markups == null){
					VisualiserPlugin.menu.onlyShow(null);
					return;
				}
				for (Iterator it2 = markups.iterator(); it2.hasNext();) {
					Stripe stripe = (Stripe) it2.next();
					List kinds = stripe.getKinds();
					for (Iterator it3 = kinds.iterator(); it3.hasNext();) {
						IMarkupKind kind = (IMarkupKind) it3.next();
						if(!names.contains(kind.getName())){
							names.add(kind.getName());
						}
					}
				}
			}
		
		}
		if(!found){   // name is name of a group, not a member.
			List groups = contentP.getAllGroups();
			for (Iterator it = groups.iterator(); it.hasNext();) {
				IGroup group = (IGroup) it.next();
				if(group.getToolTip().equals(name)){
					List markups = markupP.getGroupMarkups(group);
					if(markups == null){
						VisualiserPlugin.menu.onlyShow(null);
						return;
					}
					for (Iterator it2 = markups.iterator(); it2.hasNext();) {
						Stripe stripe = (Stripe) it2.next();
						List kinds = stripe.getKinds();
						for (Iterator it3 = kinds.iterator(); it3.hasNext();) {
							IMarkupKind kind = (IMarkupKind) it3.next();
							if(!names.contains(kind.getName())){
								names.add(kind.getName());
							}
						}
					}
				}
			}
		}
		VisualiserPlugin.menu.onlyShow(names);
	}

	
	/**
	 * Handle a click that has occurred on the bar chart.
	 * 
	 * @param member
	 * @param stripe
	 * @param buttonClicked
	 */
	protected void handleClick(IMember member, Stripe stripe,
			int buttonClicked) {

		boolean proceed = contentP.processMouseclick((member != null ? member
				: null), (stripe != null ? true : false), buttonClicked);

		if (stripe != null)
			proceed = markupP.processMouseclick(
					(member != null ? member : null), stripe, 
					buttonClicked)
					&& proceed;

		if (proceed) {

			if (buttonClicked != 3) { // Left hand or middle mouse button click
				//stackContext();
				if (inGroupView) {
					//IGroup grp = member.getContainingGroup();
					// IF
					//   someone has clicked on a group
					// THEN
					//   Switch to subselect mode, and to the member view showing
					// all the members of that group
					activateMemberView();
					//					activateSubSelectMode();
					//					subselection = new ArrayList();
					//					subselection.addAll(grp.getMembers());
				} else {
					//					activateSubSelectMode();
					//					subselection = new ArrayList();
					//					subselection.add(member);
				}
				updateDisplay(false);
			} else { // Right hand button clicked
				//unstackContext();
			}
		}

	}

	/**
	 * If the current log-level is greater than or equal to the level given, log
	 * the message.
	 * 
	 * @param level -
	 *            the level
	 * @param string -
	 *            the message
	 */
	private static void log(int level, String string) {
		if (level <= VisualiserPlugin.LOGLEVEL)
			System.err.println(string);
	}

}