/**********************************************************************
Copyright (c) 2002 IBM Corporation and others.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Common Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/cpl-v10.html
Contributors:
Adrian Colyer, Andy Clement, Tracy Gardner - initial version
...
**********************************************************************/
package org.eclipse.ajdt.internal.ui.actions;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.ajdt.internal.utils.AJDTUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * This action is driven from the popup menu of a project that does not
 * have the AspectJ nature. It converts the project to use the AspectJ builder
 * etc.
 */
public class AddAJNatureAction implements IObjectActionDelegate {

	private Vector selected = new Vector();

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction arg0) {

		for (Iterator iter = selected.iterator(); iter.hasNext();) {

			IProject project = (IProject) iter.next();
			
			try {			
				// Add the AspectJ nature to the project and update the
				// build classpath with the aspectjrt.jar	
				AJDTUtils.addAspectJNature(project);
			} catch (CoreException e) {
			}
		}
	}

	/**
	 * From IActionDelegate - set the availability or otherwise of this
	 * action.
	 */
	public void selectionChanged(IAction action, ISelection sel) {
		selected.clear();
		boolean enable = true;
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) sel;
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object object = iter.next();
				if (object instanceof IAdaptable) {
					IProject project = (IProject) ((IAdaptable)object).getAdapter(IProject.class);	
					if(project != null) {
						selected.add(project);
					} else {
						enable = false;
						break;
					}
				} else {
					enable = false;
					break;
				}
			}
			action.setEnabled(enable);
		}
	}

	/**
	 * From IObjectActionDelegate
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
}