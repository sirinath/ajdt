/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.aspectj.org.eclipse.jdt.internal.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.aspectj.org.eclipse.jdt.core.Flags;
import org.aspectj.org.eclipse.jdt.core.IAnnotation;
import org.aspectj.org.eclipse.jdt.core.IField;
import org.aspectj.org.eclipse.jdt.core.JavaModelException;
import org.aspectj.org.eclipse.jdt.core.Signature;
import org.aspectj.org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.aspectj.org.eclipse.jdt.internal.compiler.env.IBinaryField;
import org.aspectj.org.eclipse.jdt.internal.compiler.lookup.Binding;

/**
 * @see IField
 */

/* package */ class BinaryField extends BinaryMember implements IField {

/*
 * Constructs a handle to the field with the given name in the specified type.
 */
protected BinaryField(JavaElement parent, String name) {
	super(parent, name);
}
@Override
public boolean equals(Object o) {
	if (!(o instanceof BinaryField)) return false;
	return super.equals(o);
}
@Override
public IAnnotation[] getAnnotations() throws JavaModelException {
	IBinaryField info = (IBinaryField) getElementInfo();
	IBinaryAnnotation[] binaryAnnotations = info.getAnnotations();
	return getAnnotations(binaryAnnotations, info.getTagBits());
}
/*
 * @see IField
 */
@Override
public Object getConstant() throws JavaModelException {
	IBinaryField info = (IBinaryField) getElementInfo();
	return convertConstant(info.getConstant());
}
/*
 * @see IMember
 */
@Override
public int getFlags() throws JavaModelException {
	IBinaryField info = (IBinaryField) getElementInfo();
	return info.getModifiers();
}
/*
 * @see IJavaElement
 */
@Override
public int getElementType() {
	return FIELD;
}
/*
 * @see JavaElement#getHandleMemento()
 */
@Override
protected char getHandleMementoDelimiter() {
	return JavaElement.JEM_FIELD;
}
@Override
public String getKey(boolean forceOpen) throws JavaModelException {
	return getKey(this, forceOpen);
}
/*
 * @see IField
 */
@Override
public String getTypeSignature() throws JavaModelException {
	IBinaryField info = (IBinaryField) getElementInfo();
	char[] genericSignature = info.getGenericSignature();
	if (genericSignature != null) {
		return new String(ClassFile.translatedName(genericSignature));
	}
	return new String(ClassFile.translatedName(info.getTypeName()));
}
/* (non-Javadoc)
 * @see org.aspectj.org.eclipse.jdt.core.IField#isEnumConstant()
 */@Override
public boolean isEnumConstant() throws JavaModelException {
	return Flags.isEnum(getFlags());
}

@Override
public boolean isResolved() {
	return false;
}
@Override
public JavaElement resolved(Binding binding) {
	SourceRefElement resolvedHandle = new ResolvedBinaryField(this.parent, this.name, new String(binding.computeUniqueKey()));
	resolvedHandle.occurrenceCount = this.occurrenceCount;
	return resolvedHandle;
}
/*
 * @private Debugging purposes
 */
@Override
protected void toStringInfo(int tab, StringBuffer buffer, Object info, boolean showResolvedInfo) {
	buffer.append(tabString(tab));
	if (info == null) {
		toStringName(buffer);
		buffer.append(" (not open)"); //$NON-NLS-1$
	} else if (info == NO_INFO) {
		toStringName(buffer);
	} else {
		try {
			buffer.append(Signature.toString(getTypeSignature()));
			buffer.append(" "); //$NON-NLS-1$
			toStringName(buffer);
		} catch (JavaModelException e) {
			buffer.append("<JavaModelException in toString of " + getElementName()); //$NON-NLS-1$
		}
	}
}
@Override
public String getAttachedJavadoc(IProgressMonitor monitor) throws JavaModelException {
	JavadocContents javadocContents = ((BinaryType) this.getDeclaringType()).getJavadocContents(monitor);
	if (javadocContents == null) return null;
	return javadocContents.getFieldDoc(this);
}
}
