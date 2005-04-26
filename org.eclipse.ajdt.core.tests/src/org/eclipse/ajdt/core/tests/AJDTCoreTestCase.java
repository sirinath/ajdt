/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matt Chapman  - initial version
 *******************************************************************************/
package org.eclipse.ajdt.core.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.ajdt.internal.core.CoreUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Mainly copied from AbstractJavaModelTests in org.eclipse.jdt.core.tests.model
 */
public class AJDTCoreTestCase extends TestCase {

	/**
	 * Returns the OS path to the directory that contains this plugin.
	 */
	protected String getPluginDirectoryPath() {
		try {
			URL platformURL = Platform.getBundle("org.eclipse.ajdt.core.tests").getEntry("/");
			return new File(Platform.asLocalURL(platformURL).getFile()).getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getSourceWorkspacePath() {
		return getPluginDirectoryPath() +  java.io.File.separator + "workspace";
	}

	/**
	 * Returns the IWorkspace this test suite is running on.
	 */
	public IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	public IWorkspaceRoot getWorkspaceRoot() {
		return getWorkspace().getRoot();
	}
	
	protected IProject createPredefinedProject(final String projectName) throws CoreException, IOException {
		IJavaProject jp = setUpJavaProject(projectName);
		waitForAutoBuild();
//		final IProject project = jp.getProject();
//		IWorkspaceRunnable build = new IWorkspaceRunnable() {
//			public void run(IProgressMonitor monitor) throws CoreException {
//				project.build(IncrementalProjectBuilder.FULL_BUILD,null);
//			}
//		};
//		getWorkspace().run(build, null);
		return jp.getProject();
	}
	
	
	protected IJavaProject setUpJavaProject(final String projectName) throws CoreException, IOException {
		return setUpJavaProject(projectName, "1.4");
	}
	
	protected IJavaProject setUpJavaProject(final String projectName, String compliance) throws CoreException, IOException {
		// copy files in project from source workspace to target workspace
		String sourceWorkspacePath = getSourceWorkspacePath();
		String targetWorkspacePath = getWorkspaceRoot().getLocation().toFile().getCanonicalPath();
		copyDirectory(new File(sourceWorkspacePath, projectName), new File(targetWorkspacePath, projectName));
		
		// ensure variables are set
		setUpJCLClasspathVariables(compliance);
	
		// create project
		final IProject project = getWorkspaceRoot().getProject(projectName);
		IWorkspaceRunnable populate = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				project.create(null);
				project.open(null);
			}
		};
		getWorkspace().run(populate, null);
		IJavaProject javaProject = JavaCore.create(project);
//		if ("1.5".equals(compliance)) {
//			// set options
//			Map options = new HashMap();
//			options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_5);
//			options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_5);	
//			options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_5);	
//			javaProject.setOptions(options);
//			
//			// replace JCL_LIB with JCL15_LIB, and JCL_SRC with JCL15_SRC
//			IClasspathEntry[] classpath = javaProject.getRawClasspath();
//			IPath jclLib = new Path("JCL_LIB");
//			for (int i = 0, length = classpath.length; i < length; i++) {
//				IClasspathEntry entry = classpath[i];
//				if (entry.getPath().equals(jclLib)) {
//					classpath[i] = JavaCore.newVariableEntry(
//							new Path("JCL15_LIB"), 
//							new Path("JCL15_SRC"), 
//							entry.getSourceAttachmentRootPath(), 
//							entry.getAccessRules(), 
//							new IClasspathAttribute[0], 
//							entry.isExported());
//					break;
//				}
//			}
//			javaProject.setRawClasspath(classpath, null);
//		}
		return javaProject;
	}
	
	public void setUpJCLClasspathVariables(String compliance) throws JavaModelException, IOException {
/*
		if ("1.5".equals(compliance)) {
//			if (JavaCore.getClasspathVariable("JCL15_LIB") == null) {
//				setupExternalJCL("jclMin1.5");
//				JavaCore.setClasspathVariables(
//					new String[] {"JCL15_LIB", "JCL15_SRC", "JCL_SRCROOT"},
//					new IPath[] {getExternalJCLPath(compliance), getExternalJCLSourcePath(compliance), getExternalJCLRootSourcePath()},
//					null);
//			} 
		} else {
			if (JavaCore.getClasspathVariable("JCL_LIB") == null) {
				setupExternalJCL("jclMin");
				JavaCore.setClasspathVariables(
					new String[] {"JCL_LIB", "JCL_SRC", "JCL_SRCROOT"},
					new IPath[] {getExternalJCLPath(), getExternalJCLSourcePath(), getExternalJCLRootSourcePath()},
					null);
			} 
		}
		*/	
	}
	
	/**
	 * Wait for autobuild notification to occur
	 */
	public static void waitForAutoBuild() {
		boolean wasInterrupted = false;
		do {
			try {
				Platform.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
				wasInterrupted = false;
			} catch (OperationCanceledException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				wasInterrupted = true;
			}
		} while (wasInterrupted);
	}
	
	/**
	 * Copy the given source directory (and all its contents) to the given target directory.
	 */
	protected void copyDirectory(File source, File target) throws IOException {
		if (!target.exists()) {
			target.mkdirs();
		}
		File[] files = source.listFiles();
		if (files == null) return;
		for (int i = 0; i < files.length; i++) {
			File sourceChild = files[i];
			String name =  sourceChild.getName();
			if (name.equals("CVS")) continue;
			File targetChild = new File(target, name);
			if (sourceChild.isDirectory()) {
				copyDirectory(sourceChild, targetChild);
			} else {
				copy(sourceChild, targetChild);
			}
		}
	}
	
	/**
	 * Copy file from src (path to the original file) to dest (path to the destination file).
	 */
	public void copy(File src, File dest) throws IOException {
		// read source bytes
		byte[] srcBytes = this.read(src);
		
		if (convertToIndependantLineDelimiter(src)) {
			String contents = new String(srcBytes);
			contents = convertToIndependantLineDelimiter(contents);
			srcBytes = contents.getBytes();
		}
	
		// write bytes to dest
		FileOutputStream out = new FileOutputStream(dest);
		out.write(srcBytes);
		out.close();
	}
	
	public byte[] read(java.io.File file) throws java.io.IOException {
		int fileLength;
		byte[] fileBytes = new byte[fileLength = (int) file.length()];
		java.io.FileInputStream stream = new java.io.FileInputStream(file);
		int bytesRead = 0;
		int lastReadSize = 0;
		while ((lastReadSize != -1) && (bytesRead != fileLength)) {
			lastReadSize = stream.read(fileBytes, bytesRead, fileLength - bytesRead);
			bytesRead += lastReadSize;
		}
		stream.close();
		return fileBytes;
	}
	public boolean convertToIndependantLineDelimiter(File file) {
		return CoreUtils.ASPECTJ_SOURCE_FILTER.accept(file.getName());
	}
	
	public static String convertToIndependantLineDelimiter(String source) {
		if (source.indexOf('\n') == -1 && source.indexOf('\r') == -1) return source;
		StringBuffer buffer = new StringBuffer();
		for (int i = 0, length = source.length(); i < length; i++) {
			char car = source.charAt(i);
			if (car == '\r') {
				buffer.append('\n');
				if (i < length-1 && source.charAt(i+1) == '\n') {
					i++; // skip \n after \r
				}
			} else {
				buffer.append(car);
			}
		}
		return buffer.toString();
	}
	
	protected IProject getProject(String project) {
		return getWorkspaceRoot().getProject(project);
	}

	protected void deleteProject(IProject project) throws CoreException {
		if (project.exists() && !project.isOpen()) { // force opening so that project can be deleted without logging (see bug 23629)
			project.open(null);
		}
		deleteResource(project);
	}
	
	protected void deleteProject(String projectName) throws CoreException {
		deleteProject(this.getProject(projectName));
	}
	
	/**
	 * Delete this resource.
	 */
	public void deleteResource(IResource resource) throws CoreException {
		CoreException lastException = null;
		try {
			resource.delete(true, null);
		} catch (CoreException e) {
			lastException = e;
			// just print for info
			System.out.println(e.getMessage());
		} catch (IllegalArgumentException iae) {
			// just print for info
			System.out.println(iae.getMessage());
		}
		int retryCount = 60; // wait 1 minute at most
		while (resource.isAccessible() && --retryCount >= 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			try {
				resource.delete(true, null);
			} catch (CoreException e) {
				lastException = e;
				// just print for info
				System.out.println("Retry "+retryCount+": "+ e.getMessage());
			} catch (IllegalArgumentException iae) {
				// just print for info
				System.out.println("Retry "+retryCount+": "+ iae.getMessage());
			}
		}
		if (!resource.isAccessible()) return;
		System.err.println("Failed to delete " + resource.getFullPath());
		if (lastException != null) {
			throw lastException;
		}
	}
}
