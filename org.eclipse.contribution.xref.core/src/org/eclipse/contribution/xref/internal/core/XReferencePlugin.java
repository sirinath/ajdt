/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Helen Hawkins   - iniital version
 *******************************************************************************/
package org.eclipse.contribution.xref.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.PluginVersionIdentifier;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class XReferencePlugin extends Plugin {

	// the plugin id
	public static final String PLUGIN_ID = "org.eclipse.contribution.xref.core"; //$NON-NLS-1$
	
	//The shared instance.
	private static XReferencePlugin plugin;

	public static final int ERROR_BAD_PROVIDER = 1;
	
	/**
	 * 3.0 compatible Plugin constructor - load the resource bundle 
	 */
	public XReferencePlugin() {
		super();
		plugin = this;
	}
		
	/**
	 * Returns the shared instance.
	 */
	public static XReferencePlugin getDefault() {
		return plugin;
	}

	public static void log(CoreException ex) {
		getDefault().getLog().log(ex.getStatus());
	}
	
	public static String getVersion() {
	    String pluginVersion = (String) Platform.getBundle(PLUGIN_ID).getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
		PluginVersionIdentifier version = new PluginVersionIdentifier(pluginVersion); 
		StringBuffer result = new StringBuffer();
		result.append(version.getMajorComponent());
		result.append("."); //$NON-NLS-1$
		result.append(version.getMinorComponent());
		result.append("."); //$NON-NLS-1$
		result.append(version.getServiceComponent());		
		return result.toString();		
	}

	/**
	 * This method is called upon plug-in activation - process any 
	 * defined extensions and add the resource change listener.
	 * 
	 * 3.0 compatible
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 * 
	 * 3.0 compatible
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}
}
