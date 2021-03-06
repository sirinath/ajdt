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
 *
 *******************************************************************************/
package org.aspectj.org.eclipse.jdt.internal.compiler.parser;

import org.aspectj.org.eclipse.jdt.internal.compiler.ast.RequiresStatement;

public class RecoveredRequiresStatement extends RecoveredModuleStatement {

	public RecoveredRequiresStatement(RequiresStatement requiresStatement, RecoveredElement parent, int bracketBalance) {
		super(requiresStatement, parent, bracketBalance);
	}
	@Override
	public String toString(int tab) {
		return tabString(tab) + "Recovered requires: " + super.toString(); //$NON-NLS-1$
	}
	public RequiresStatement updatedRequiresStatement(){
		return (RequiresStatement)this.moduleStatement;
	}
}
