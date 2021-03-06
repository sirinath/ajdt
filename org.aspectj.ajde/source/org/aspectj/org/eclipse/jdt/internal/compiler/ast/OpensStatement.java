/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
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
package org.aspectj.org.eclipse.jdt.internal.compiler.ast;

import org.aspectj.org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;

public class OpensStatement extends PackageVisibilityStatement {

	public OpensStatement(ImportReference pkgRef) {
		this(pkgRef, null);
	}
	public OpensStatement(ImportReference pkgRef, ModuleReference[] targets) {
		super(pkgRef, targets);
	}
	@Override
	protected int computeSeverity(int problemId) {
		return ProblemSeverities.Warning;
	}
	@Override
	public StringBuffer print(int indent, StringBuffer output) {
		printIndent(indent, output);
		output.append("opens "); //$NON-NLS-1$
		super.print(0, output);
		output.append(";"); //$NON-NLS-1$
		return output;
	}

}
