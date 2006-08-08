/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matt Chapman  - initial version
 *******************************************************************************/
package org.eclipse.ajdt.core.tests.codeconversion;

import org.eclipse.ajdt.core.codeconversion.AspectsConvertingParser;
import org.eclipse.ajdt.core.codeconversion.ConversionOptions;
import org.eclipse.ajdt.core.tests.AJDTCoreTestCase;

public class AspectsConvertingParserTest extends AJDTCoreTestCase {

	public void testBug110751() {
		String source = "public aspect Aspect {\n" //$NON-NLS-1$
				+ "	private void Cloneable.loop(Object[] objects) throws IOException {\n" //$NON-NLS-1$
				+ "		for (Object obj : objects) {\n" //$NON-NLS-1$
				+ "			obj.toString();\n" //$NON-NLS-1$
				+ "		}\n" //$NON-NLS-1$
				+ "	}\n" //$NON-NLS-1$
				+ "};\n"; //$NON-NLS-1$
		ConversionOptions conversionOptions = ConversionOptions.STANDARD;
		AspectsConvertingParser conv = new AspectsConvertingParser(source
				.toCharArray());
		conv.convert(conversionOptions);
		String converted = new String(conv.content);
		assertTrue("Parser failed to handle enhanced for loop", //$NON-NLS-1$
				converted.indexOf("for (Object obj : objects)")!=-1); //$NON-NLS-1$
	}
	
	public void testBug118052() {
		String source = "public aspect Aspect pertypewithin(type_pattern){\n" //$NON-NLS-1$
				+ "	public static void main(String[] args) {\n" //$NON-NLS-1$
				+ "	}\n" //$NON-NLS-1$
				+ "};\n"; //$NON-NLS-1$
		ConversionOptions conversionOptions = ConversionOptions.STANDARD;
		AspectsConvertingParser conv = new AspectsConvertingParser(source
				.toCharArray());
		conv.convert(conversionOptions);
		String converted = new String(conv.content);
		assertTrue("Parser failed to handle pertypewithin", //$NON-NLS-1$
				converted.indexOf("pertypewithin")==-1); //$NON-NLS-1$
	}
	
	public void testBug134343() {
		String source = "public aspect Aspect {\n" //$NON-NLS-1$
				+ "	   declare parents : foo.inspector..* &&\n" //$NON-NLS-1$
				+ " (junit.framework.TestCase+ || *..Test*\n" //$NON-NLS-1$
    		    + " ||foo.inspector.test..*) implements Serializable;\n" //$NON-NLS-1$
				+ "};\n"; //$NON-NLS-1$
		ConversionOptions conversionOptions = ConversionOptions.STANDARD;
		AspectsConvertingParser conv = new AspectsConvertingParser(source
				.toCharArray());
		conv.convert(conversionOptions);
		String converted = new String(conv.content);
		assertTrue("Parser should not have considered Test as an import", //$NON-NLS-1$
				converted.indexOf("Test x") == -1); //$NON-NLS-1$
		assertTrue("Parser should not have considered TestCase as an import", //$NON-NLS-1$
				converted.indexOf("TestCase x") == -1); //$NON-NLS-1$
	}
}
