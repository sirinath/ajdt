/**********************************************************************
Copyright (c) 2002 IBM Corporation and others.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Common Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/cpl-v10.html
Contributors:
Adrian Colyer - initial version
Sian Whiting - added new images for 1.1.11 release
...
**********************************************************************/
package org.eclipse.ajdt.internal.ui.resources;

import java.net.MalformedURLException;
import java.net.URL;

import org.aspectj.ajde.ui.AbstractIcon;
import org.aspectj.ajde.ui.AbstractIconRegistry;
import org.aspectj.ajde.ui.BuildConfigNode;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.IProgramElement.Kind;
import org.eclipse.ajdt.ui.AspectJUIPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
/**
 * Utility class providing access to all the images used by this
 * plugin.
 */
public class AspectJImages extends AbstractIconRegistry {

	private static AspectJImages instance;

	private static final org.eclipse.ui.ISharedImages workbenchImages = 
		AspectJUIPlugin.getDefault().getWorkbench().getSharedImages();
		
	private static final URL ajdeIconLocation = Platform.getBundle("org.aspectj.ajde").getEntry("/"); //$NON-NLS-1$ //$NON-NLS-2$
		
	private static final String AJDE_ICON_PATH_PREFIX = "org/aspectj/ajde/resources/"; //$NON-NLS-1$

	// The following icons are private and should be accessed through the 
	// getStructureIcon operation:
	private final AbstractIcon JDT_PACKAGE = new AJDTIcon(
		JavaUI.getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_PACKAGE ) );
	private final AbstractIcon JDT_FILE = new AJDTIcon(
		JavaUI.getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_CUNIT ) );
	private final AbstractIcon JDT_CLASS = new AJDTIcon(
		JavaUI.getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_CLASS ) );
	
	//following 4 icons cannot be accessed using JavaUI.getSharedImages()
	private final AbstractIcon JDT_INNER_CLASS_PRIVATE = new AJDTIcon(
			JavaPluginImages.DESC_OBJS_INNER_CLASS_PRIVATE );
	private final AbstractIcon JDT_INNER_CLASS_PROTECTED = new AJDTIcon(
			JavaPluginImages.DESC_OBJS_INNER_CLASS_PROTECTED );
	private final AbstractIcon JDT_INNER_CLASS_PUBLIC = new AJDTIcon(
			JavaPluginImages.DESC_OBJS_INNER_CLASS_PUBLIC );
	private final AbstractIcon JDT_INNER_CLASS_DEFAULT = new AJDTIcon(
			JavaPluginImages.DESC_OBJS_INNER_CLASS_DEFAULT);

	private final AbstractIcon JDT_INTERFACE = new AJDTIcon(
		JavaUI.getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_INTERFACE ) );
	private final AbstractIcon JDT_PRIVATE_METHOD = new AJDTIcon(
		JavaUI.getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_PRIVATE ) );
	private final AbstractIcon JDT_PROTECTED_METHOD = new AJDTIcon(
		JavaUI.getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_PROTECTED ) );
	private final AbstractIcon JDT_PUBLIC_METHOD = new AJDTIcon(
		JavaUI.getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_PUBLIC ) );
	private final AbstractIcon JDT_DEFAULT_METHOD = new AJDTIcon(
		JavaUI.getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_DEFAULT ) );


	private final AbstractIcon WKBENCH_FILE = new AJDTIcon(
		workbenchImages.getImageDescriptor( org.eclipse.ui.ISharedImages.IMG_OBJ_FILE ) );

	private final AbstractIcon WKBENCH_FOLDER = new AJDTIcon(
		workbenchImages.getImageDescriptor( org.eclipse.ui.ISharedImages.IMG_OBJ_FOLDER ) );
	private final AbstractIcon AJDT_ASPECT = new AJDTIcon(
		"icons/structure/aspect.gif" ); //$NON-NLS-1$
	
	// Luzius - added aspect icons with visibility 
	private final AbstractIcon ASPECT_PRIVATE   = new AJDTIcon(
		"icons/structure/aspect_pri.gif");	 //$NON-NLS-1$
	private final AbstractIcon ASPECT_PROTECTED = new AJDTIcon(
		"icons/structure/aspect_pro.gif");	 //$NON-NLS-1$
	private final AbstractIcon ASPECT_PACKAGE   = new AJDTIcon(
		"icons/structure/aspect_pac.gif"); //$NON-NLS-1$
	private final AbstractIcon ASPECT_PUBLIC   = AJDT_ASPECT;

	// These icons are publically available
	public static final AJDTIcon WKBENCH_INFO = new AJDTIcon(
		workbenchImages.getImageDescriptor( org.eclipse.ui.ISharedImages.IMG_OBJS_INFO_TSK) );
	public static final AJDTIcon JDT_IMPORTED = new AJDTIcon(
		JavaUI.getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_IMPDECL ) );
	public static AJDTIcon JDT_IMPORT_CONTAINER = new AJDTIcon(
	JavaUI.getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_IMPCONT ) );
		
	// AMC - added gutter annotation icon
	public static final AJDTIcon E_ANNOTATION = new AJDTIcon(
				"icons/actions/e_annotation.gif"); //$NON-NLS-1$
	public static final AJDTIcon ANNOTATION   = new AJDTIcon(
				"icons/actions/annotation.gif"); //$NON-NLS-1$
	
	// MPC - added wizard banner icons
	public static final AJDTIcon W_NEW_ASPECT = new AJDTIcon(
				"icons/wizban/newaspect_wiz.gif"); //$NON-NLS-1$
	public static final AJDTIcon W_NEW_AJ_PRJ = new AJDTIcon(
				"icons/wizban/newajprj_wiz.gif"); //$NON-NLS-1$
	public static final AJDTIcon W_EXPORT_AJDOC = new AJDTIcon(
				"icons/wizban/export_ajdoc_wiz.gif"); //$NON-NLS-1$

	// Sian - added new icons
	public static final AJDTIcon ADVICE           = new AJDTIcon(
					"icons/structure/advice.gif");	 //$NON-NLS-1$
	public static final AJDTIcon BEFORE_ADVICE    = new AJDTIcon(
					"icons/structure/before_advice.gif");	 //$NON-NLS-1$
	public static final AJDTIcon AFTER_ADVICE     = new AJDTIcon(
					"icons/structure/after_advice.gif");	 //$NON-NLS-1$
	public static final AJDTIcon AROUND_ADVICE    = new AJDTIcon(
					"icons/structure/around_advice.gif");	 //$NON-NLS-1$
	public static final AJDTIcon DYNAMIC_BEFORE_ADVICE = new AJDTIcon(
					"icons/structure/dynamic_before_advice.gif");	 //$NON-NLS-1$
	public static final AJDTIcon DYNAMIC_AFTER_ADVICE     = new AJDTIcon(
					"icons/structure/dynamic_after_advice.gif");	 //$NON-NLS-1$
	public static final AJDTIcon DYNAMIC_AROUND_ADVICE    = new AJDTIcon(
					"icons/structure/dynamic_around_advice.gif");	 //$NON-NLS-1$
	public static final AJDTIcon POINTCUT_DEF 	  = new AJDTIcon(
					"icons/structure/pointcut_def.gif"); //$NON-NLS-1$
	public static final AJDTIcon POINTCUT_PUB 	  = new AJDTIcon(
					"icons/structure/pointcut_pub.gif"); //$NON-NLS-1$
	public static final AJDTIcon POINTCUT_PRI 	  = new AJDTIcon(
					"icons/structure/pointcut_pri.gif"); //$NON-NLS-1$
	public static final AJDTIcon POINTCUT_PRO 	  = new AJDTIcon(
					"icons/structure/pointcut_pro.gif"); //$NON-NLS-1$
	public static final AJDTIcon DECLARE_ERROR 	  = new AJDTIcon(
					"icons/structure/dec_error.gif"); //$NON-NLS-1$
	public static final AJDTIcon DECLARE_WARNING   = new AJDTIcon(
					"icons/structure/dec_warning.gif"); //$NON-NLS-1$
	public static final AJDTIcon DECLARE_PARENTS   = new AJDTIcon(
					"icons/structure/dec_parents.gif"); //$NON-NLS-1$
	public static final AJDTIcon DECLARE_PRECEDENCE = new AJDTIcon(
					"icons/structure/dec_precedence.gif"); //$NON-NLS-1$
	public static final AJDTIcon DECLARE_SOFT 	  = new AJDTIcon(
					"icons/structure/dec_soft.gif"); //$NON-NLS-1$
	public static final AJDTIcon DECLARE_ANNOTATION = new AJDTIcon(
					"icons/structure/dec_annotation.gif"); //$NON-NLS-1$
	public static final AJDTIcon ITD  			  = new AJDTIcon(
					"icons/structure/itd.gif"); //$NON-NLS-1$
	public static final AJDTIcon ITD_METHOD_PUB   = new AJDTIcon(
					"icons/structure/itdmethod_pub.gif"); //$NON-NLS-1$
	public static final AJDTIcon ITD_METHOD_PRI   = new AJDTIcon(
					"icons/structure/itdmethod_pri.gif"); //$NON-NLS-1$
	public static final AJDTIcon ITD_METHOD_DEF   = new AJDTIcon(
					"icons/structure/itdmethod_def.gif"); //$NON-NLS-1$
	public static final AJDTIcon ITD_METHOD_PRO   = new AJDTIcon(
					"icons/structure/itdmethod_pro.gif"); //$NON-NLS-1$
	public static final AJDTIcon ITD_CONSTRUCTOR_PUB   = new AJDTIcon(
					"icons/structure/itdconstructor_pub.gif"); //$NON-NLS-1$
	public static final AJDTIcon ITD_CONSTRUCTOR_PRI   = new AJDTIcon(
					"icons/structure/itdconstructor_pri.gif"); //$NON-NLS-1$
	public static final AJDTIcon ITD_CONSTRUCTOR_DEF   = new AJDTIcon(
					"icons/structure/itdconstructor_def.gif"); //$NON-NLS-1$
	public static final AJDTIcon ITD_CONSTRUCTOR_PRO   = new AJDTIcon(
					"icons/structure/itdconstructor_pro.gif"); //$NON-NLS-1$
	public static final AJDTIcon ITD_FIELD_PUB	  = new AJDTIcon(
					"icons/structure/itdfield_pub.gif"); //$NON-NLS-1$
	public static final AJDTIcon ITD_FIELD_DEF 	  = new AJDTIcon(
					"icons/structure/itdfield_def.gif"); //$NON-NLS-1$
	public static final AJDTIcon ITD_FIELD_PRO 	  = new AJDTIcon(
					"icons/structure/itdfield_pro.gif"); //$NON-NLS-1$
	public static final AJDTIcon ITD_FIELD_PRI 	  = new AJDTIcon(
					"icons/structure/itdfield_pri.gif"); //$NON-NLS-1$
	public static final AJDTIcon E_ADVICE           = new AJDTIcon(
					"icons/structure/e_advice.gif"); //$NON-NLS-1$
	public static final AJDTIcon E_DECLARE_PARENTS  = new AJDTIcon(
					"icons/structure/e_dec_parents.gif"); //$NON-NLS-1$

	public static final AJDTIcon E_ITD_FIELD_DEF     = new AJDTIcon(
					"icons/structure/e_itdmethod_def.gif"); //$NON-NLS-1$
	public static final AJDTIcon E_POINTCUT_DEF        = new AJDTIcon(
					"icons/structure/e_joinPoint.gif"); //$NON-NLS-1$
	public static final AJDTIcon G_ADVICE           = new AJDTIcon(
					"icons/structure/g_advice.gif"); //$NON-NLS-1$
	public static final AJDTIcon G_DECLARE_PARENTS  = new AJDTIcon(
					"icons/structure/g_dec_parentss.gif"); //$NON-NLS-1$
	public static final AJDTIcon G_ITD_FIELD_DEF     = new AJDTIcon(
					"icons/structure/g_itdfield_def.gif"); //$NON-NLS-1$
	public static final AJDTIcon G_POINTCUT_DEF        = new AJDTIcon(
					"icons/structure/g_joinPoint.gif"); //$NON-NLS-1$
	public static final AJDTIcon ADVISES        = new AJDTIcon(
					"icons/structure/advises.gif"); //$NON-NLS-1$

	public static final AJDTIcon ADVICE_OVERLAY = new AJDTIcon(
		"icons/ovr/adviceoverlay.gif"); //$NON-NLS-1$

	public static final AJDTIcon AJ_CODE = new AJDTIcon(
		"icons/structure/code.gif"); //$NON-NLS-1$
	
	public static final AJDTIcon ASPECTJ_FILE = new AJDTIcon(
		"icons/structure/ajcu_obj.gif");	 //$NON-NLS-1$
	
	public static final AJDTIcon EXCLUDED_ASPECTJ_FILE = new AJDTIcon(
		"icons/structure/ajcu_obj_excluded.gif");	 //$NON-NLS-1$
	
	public static final AJDTIcon HIDE_ADVICE = new AJDTIcon("icons/actions/hide_advice.gif"); //$NON-NLS-1$
	public static final AJDTIcon HIDE_ITDS = new AJDTIcon("icons/actions/hide_itds.gif"); //$NON-NLS-1$
	public static final AJDTIcon HIDE_POINTCUTS = new AJDTIcon("icons/actions/hide_pointcuts.gif"); //$NON-NLS-1$
	public static final AJDTIcon HIDE_DECLARATIONS = new AJDTIcon("icons/actions/hide_declarations.gif"); //$NON-NLS-1$
	public static final AJDTIcon HIDE_ERRORS = new AJDTIcon("icons/actions/hide_errors.gif"); //$NON-NLS-1$
	public static final AJDTIcon HIDE_WARNINGS = new AJDTIcon("icons/actions/hide_warnings.gif"); //$NON-NLS-1$
			
	// TEMPORARY:
	private final AbstractIcon JDT_PRIVATE_FIELD = new AJDTIcon(
				"icons/jdt/field_private_obj.gif"); //$NON-NLS-1$
	private final AbstractIcon JDT_PROTECTED_FIELD = new AJDTIcon(
				"icons/jdt/field_protected_obj.gif"); //$NON-NLS-1$
	private final AbstractIcon JDT_PUBLIC_FIELD = new AJDTIcon(
				"icons/jdt/field_public_obj.gif"); //$NON-NLS-1$
	private final AbstractIcon JDT_DEFAULT_FIELD = new AJDTIcon(
				"icons/jdt/field_default_obj.gif"); //$NON-NLS-1$

	private ImageDescriptorRegistry registry;

	// Luzius - added build configuration icons
	public static final AJDTIcon BC_TICK = new AJDTIcon(
				"icons/buildconfig/tick.gif"); //$NON-NLS-1$
	public static final AJDTIcon BC_FILE = new AJDTIcon(
				"icons/buildconfig/buildconfig_file.gif"); //$NON-NLS-1$
	public static final AJDTIcon BC_SELECTED_FILE = new AJDTIcon(
				"icons/buildconfig/selected_buildconfig_file.gif"); //$NON-NLS-1$
	public static final AJDTIcon BC_HALF_FILLED_PACKAGE = new AJDTIcon(
				"icons/buildconfig/halffilledpackage.gif"); //$NON-NLS-1$

	public static final AJDTIcon RESET_COLOURS = new AJDTIcon(
				"icons/actions/reset_colours.gif"); //$NON-NLS-1$
	
	// icons for crosscutting changes view
	public static final AJDTIcon CHANGES_ADDED = new AJDTIcon(
				"icons/diff/added.gif"); //$NON-NLS-1$
	public static final AJDTIcon CHANGES_REMOVED = new AJDTIcon(
				"icons/diff/removed.gif"); //$NON-NLS-1$
	
	
	public static AspectJImages instance( ) {
		if ( instance == null ) {
			instance = new AspectJImages( );
		}
		return instance;	
	}

	private AspectJImages(){}
	
	// Methods inherited from AbstractIconRegistry

	/**
	 * @see AbstractIconRegistry#createIcon(String)
	 */
	protected AbstractIcon createIcon(String path) {
		if ( path.startsWith( AJDE_ICON_PATH_PREFIX ) ) {
			path = path.substring( AJDE_ICON_PATH_PREFIX.length( ) );
		}
		AJDTIcon retVal = null;
		try {
			URL url = new URL( ajdeIconLocation, "icons/" + path ); //$NON-NLS-1$
			retVal = new AJDTIcon( url );
		} catch (MalformedURLException malEx) {
		}
		return retVal;
	}

	/**
	 * We override this method to provide the eclipse-specific icons
	 * for certain "Kinds" of resource.
	 * @see AbstractIconRegistry#getStructureIcon(Kind)
	 */
	public AbstractIcon getIcon(Kind kind ) {
		if ( kind == IProgramElement.Kind.CONSTRUCTOR ||
			 kind == IProgramElement.Kind.METHOD ||
			 kind == IProgramElement.Kind.FIELD ) {
			 	String err = "Should use 2-arg version of getStructureIcon for contructors, methods and fields."; //$NON-NLS-1$
			 	System.err.println( err );   
				throw new RuntimeException( err );			 		
			 }
		if (kind == IProgramElement.Kind.PACKAGE) {
			return JDT_PACKAGE;
		} else if (kind == IProgramElement.Kind.FILE) {
			return WKBENCH_FILE;
		} else if (kind == IProgramElement.Kind.FILE_JAVA) {
			return JDT_FILE;
		} else if (kind == IProgramElement.Kind.CLASS) {
			return JDT_CLASS;
		} else if (kind == IProgramElement.Kind.INTERFACE) {
			return JDT_INTERFACE;
		} else if (kind == IProgramElement.Kind.ASPECT) {
			return AJDT_ASPECT;
		} else if (kind == IProgramElement.Kind.ADVICE) {
			return AFTER_ADVICE;
		} else if (kind == IProgramElement.Kind.POINTCUT) {
			return POINTCUT_DEF;
		} else if (kind == IProgramElement.Kind.DECLARE_ERROR) {
			return DECLARE_ERROR;
		} else if (kind == IProgramElement.Kind.DECLARE_PRECEDENCE) {
			return DECLARE_PRECEDENCE;
		} else if (kind == IProgramElement.Kind.DECLARE_PARENTS) { 
			return DECLARE_PARENTS;
		} else if (kind == IProgramElement.Kind.DECLARE_SOFT) {
			return DECLARE_SOFT;
		} else if (kind == IProgramElement.Kind.DECLARE_WARNING) {
			return DECLARE_WARNING;
		} else if ((kind == IProgramElement.Kind.DECLARE_ANNOTATION_AT_CONSTRUCTOR)
				|| (kind == IProgramElement.Kind.DECLARE_ANNOTATION_AT_FIELD)
				|| (kind == IProgramElement.Kind.DECLARE_ANNOTATION_AT_METHOD)
				|| (kind == IProgramElement.Kind.DECLARE_ANNOTATION_AT_TYPE)) {
			return DECLARE_ANNOTATION;
		} else if (kind == IProgramElement.Kind.INTER_TYPE_FIELD) {
			return ITD_FIELD_DEF;
		} else if(kind == IProgramElement.Kind.INTER_TYPE_METHOD) {
			return ITD_METHOD_DEF;
		} else if(kind == IProgramElement.Kind.INTER_TYPE_CONSTRUCTOR) {
			return ITD_CONSTRUCTOR_DEF;
		}

		AbstractIcon icon = super.getIcon( kind );
		if ( icon != null ) { return icon; }
		else { return AJDTIcon.MISSING_ICON; }
	}
	
	/**
	 * (copied from AJDTStructureViewNodeFactory.changeIconIfAdviceNode and adjustet) (Luzius)
	 */
	public AbstractIcon getAdviceIcon(IProgramElement.ExtraInformation extraInfo, boolean hasDynamicTests) {
		if (extraInfo.getExtraAdviceInformation()!=null) {				
				if(extraInfo.getExtraAdviceInformation().equals("before")) { //$NON-NLS-1$
					if(hasDynamicTests) {
						return AspectJImages.DYNAMIC_BEFORE_ADVICE;
					} else {
						return AspectJImages.BEFORE_ADVICE;
					}
				} else if (extraInfo.getExtraAdviceInformation().equals("around")) { //$NON-NLS-1$
					if(hasDynamicTests) {
						return AspectJImages.DYNAMIC_AROUND_ADVICE;
					} else {
						return AspectJImages.AROUND_ADVICE;
					}
				} else {
					if(hasDynamicTests) {
						return AspectJImages.DYNAMIC_AFTER_ADVICE;
					} else {
						return AspectJImages.AFTER_ADVICE;	
					}
				}
			}
		//default
		return AspectJImages.AFTER_ADVICE;
	}


	/**
	 * This method added by AMC for use by BuildConfigEditor
	 */
	public AbstractIcon getIcon( BuildConfigNode.Kind kind ) {
		if ( kind == BuildConfigNode.Kind.FILE_ASPECTJ ) {
			return AJDT_ASPECT;
		} else if ( kind == BuildConfigNode.Kind.FILE_JAVA ) {
			return JDT_FILE;
	    } else if ( kind == BuildConfigNode.Kind.DIRECTORY ) {
			return WKBENCH_FOLDER;
		} else if ( kind == BuildConfigNode.Kind.FILE_LST ) {
			return FILE_LST;
		} else if ( kind == BuildConfigNode.Kind.ERROR ) {
			return WKBENCH_FILE;
		} else {
			return AJDTIcon.MISSING_ICON;
		}				
	}

	/**
	 * Added this method for constructor, method, field to differentiate
	 * icon based on Accessbility
	 */
	public AbstractIcon getStructureIcon( Kind kind, IProgramElement.Accessibility access) {
		// eclipse uses the same icons regardless of kind...
		// no, it doesn't!
		if ( kind == IProgramElement.Kind.CONSTRUCTOR ||
			 kind == IProgramElement.Kind.METHOD) {
			if ( access == IProgramElement.Accessibility.PUBLIC ) {
				return JDT_PUBLIC_METHOD;
			} else if ( access == IProgramElement.Accessibility.PROTECTED ) {
				return JDT_PROTECTED_METHOD;
			} else if ( access == IProgramElement.Accessibility.PRIVATE ) {
				return JDT_PRIVATE_METHOD;
			} else if ( access == IProgramElement.Accessibility.PACKAGE ) {
				return JDT_DEFAULT_METHOD;
			} else if ( access == IProgramElement.Accessibility.PRIVILEGED ) {
				return JDT_DEFAULT_METHOD; //?? what to do here ??
			} else return AJDTIcon.MISSING_ICON;
		} else  if (kind == IProgramElement.Kind.FIELD) {
			if ( access == IProgramElement.Accessibility.PUBLIC ) {
				return JDT_PUBLIC_FIELD;
			} else if ( access == IProgramElement.Accessibility.PROTECTED ) {
				return JDT_PROTECTED_FIELD;
			} else if ( access == IProgramElement.Accessibility.PRIVATE ) {
				return JDT_PRIVATE_FIELD;
			} else if ( access == IProgramElement.Accessibility.PACKAGE ) {
				return JDT_DEFAULT_FIELD;
			} else return AJDTIcon.MISSING_ICON;
		} else if (kind == IProgramElement.Kind.POINTCUT) {
			if ( access == IProgramElement.Accessibility.PUBLIC ) {
				return POINTCUT_PUB;
			} else if ( access == IProgramElement.Accessibility.PROTECTED ) {
				return POINTCUT_PRO;
			} else if ( access == IProgramElement.Accessibility.PRIVATE ) {
				return POINTCUT_PRI;
			} else if ( access == IProgramElement.Accessibility.PACKAGE ) {
				return POINTCUT_DEF;
			} else return AJDTIcon.MISSING_ICON;
		} else if (kind == IProgramElement.Kind.INTER_TYPE_FIELD) {
			if ( access == IProgramElement.Accessibility.PUBLIC ) {
				return ITD_FIELD_PUB;
			} else if ( access == IProgramElement.Accessibility.PROTECTED ) {
				return ITD_FIELD_PRO;
			} else if ( access == IProgramElement.Accessibility.PRIVATE ) {
				return ITD_FIELD_PRI;
			} else if ( access == IProgramElement.Accessibility.PACKAGE ) {
				return ITD_FIELD_DEF;
			} else return AJDTIcon.MISSING_ICON;
		} else if (kind == IProgramElement.Kind.INTER_TYPE_METHOD) {
			if ( access == IProgramElement.Accessibility.PUBLIC ) {
				return ITD_METHOD_PUB;
			} else if ( access == IProgramElement.Accessibility.PROTECTED ) {
				return ITD_METHOD_PRO;
			} else if ( access == IProgramElement.Accessibility.PRIVATE ) {
				return ITD_METHOD_PRI;
			} else if ( access == IProgramElement.Accessibility.PACKAGE ) {
				return ITD_METHOD_DEF;
			} else return AJDTIcon.MISSING_ICON;
			
		} else if (kind == IProgramElement.Kind.INTER_TYPE_CONSTRUCTOR) {
			if ( access == IProgramElement.Accessibility.PUBLIC ) {
				return ITD_CONSTRUCTOR_PUB;
			} else if ( access == IProgramElement.Accessibility.PROTECTED ) {
				return ITD_CONSTRUCTOR_PRO;
			} else if ( access == IProgramElement.Accessibility.PRIVATE ) {
				return ITD_CONSTRUCTOR_PRI;
			} else if ( access == IProgramElement.Accessibility.PACKAGE ) {
				return ITD_CONSTRUCTOR_DEF;
			} else return AJDTIcon.MISSING_ICON;
			
		} else if (kind == IProgramElement.Kind.ASPECT) {
			if ( access == IProgramElement.Accessibility.PUBLIC ) {
				return ASPECT_PUBLIC;
			} else if ( access == IProgramElement.Accessibility.PROTECTED ) {
				return ASPECT_PROTECTED;
			} else if ( access == IProgramElement.Accessibility.PRIVATE ) {
				return ASPECT_PRIVATE;
			} else if ( access == IProgramElement.Accessibility.PACKAGE ) {
				return ASPECT_PACKAGE;
			} else return AJDTIcon.MISSING_ICON;
			
		} else if (kind == IProgramElement.Kind.CLASS) {
			if ( access == IProgramElement.Accessibility.PUBLIC ) {
				return JDT_INNER_CLASS_PUBLIC;
			} else if ( access == IProgramElement.Accessibility.PROTECTED ) {
				return JDT_INNER_CLASS_PROTECTED;
			} else if ( access == IProgramElement.Accessibility.PRIVATE ) {
				return JDT_INNER_CLASS_PRIVATE;
			} else if ( access == IProgramElement.Accessibility.PACKAGE ) {
				return JDT_INNER_CLASS_DEFAULT;
			} else return AJDTIcon.MISSING_ICON;
		} else if (kind == IProgramElement.Kind.CODE) {
			   return AJ_CODE;
		} else {
			return getIcon( kind );
		}
	}	

	public AbstractIcon getIcon(IRelationship.Kind relationship) {
		if (relationship == IRelationship.Kind.ADVICE 
				|| relationship == IRelationship.Kind.DECLARE
					|| relationship == IRelationship.Kind.DECLARE_INTER_TYPE){
			return ADVISES;
		} else {
			return super.getIcon(relationship);
		}
	}
	
	public ImageDescriptorRegistry getRegistry() {
		if (registry == null) {
			registry= JavaPlugin.getImageDescriptorRegistry();
		}
		return registry;
	}

}
