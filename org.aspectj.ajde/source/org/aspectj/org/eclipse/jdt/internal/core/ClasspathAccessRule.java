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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.aspectj.org.eclipse.jdt.core.IAccessRule;
import org.aspectj.org.eclipse.jdt.core.compiler.IProblem;
import org.aspectj.org.eclipse.jdt.internal.compiler.env.AccessRule;

public class ClasspathAccessRule extends AccessRule implements IAccessRule {

	public ClasspathAccessRule(IPath pattern, int kind) {
		this(pattern.toString().toCharArray(), toProblemId(kind));
	}

	public ClasspathAccessRule(char[] pattern, int problemId) {
		super(pattern, problemId);
	}

	private static int toProblemId(int kind) {
		boolean ignoreIfBetter = (kind & IAccessRule.IGNORE_IF_BETTER) != 0;
		switch (kind & ~IAccessRule.IGNORE_IF_BETTER) {
			case K_NON_ACCESSIBLE:
				return ignoreIfBetter ? IProblem.ForbiddenReference | AccessRule.IgnoreIfBetter : IProblem.ForbiddenReference;
			case K_DISCOURAGED:
				return ignoreIfBetter ? IProblem.DiscouragedReference | AccessRule.IgnoreIfBetter : IProblem.DiscouragedReference;
			default:
				return ignoreIfBetter ? AccessRule.IgnoreIfBetter : 0;
		}
	}

	@Override
	public IPath getPattern() {
		return new Path(new String(this.pattern));
	}

	@Override
	public int getKind() {
		switch (getProblemId()) {
			case IProblem.ForbiddenReference:
				return K_NON_ACCESSIBLE;
			case IProblem.DiscouragedReference:
				return K_DISCOURAGED;
			default:
				return K_ACCESSIBLE;
		}
	}

}
