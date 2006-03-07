/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ajdt.internal.ui.editor;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.text.AbstractJavaScanner;
import org.eclipse.jdt.internal.ui.text.CombinedWordRule;
import org.eclipse.jdt.internal.ui.text.ISourceVersionDependent;
import org.eclipse.jdt.internal.ui.text.JavaWhitespaceDetector;
import org.eclipse.jdt.internal.ui.text.JavaWordDetector;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaColorConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * An AspectJ code scanner - copied from JavaCodeScanner so we can
 * add in the AspectJ keywords.  Changes marked // AspectJ Change
 */
public final class AspectJCodeScanner extends AbstractJavaScanner {

	/**
	 * Rule to detect java operators.
	 *
	 * @since 3.0
	 */
	protected class OperatorRule implements IRule {

		/** Java operators */
		private final char[] JAVA_OPERATORS= { ';', '(', ')', '{', '}', '.', '=', '/', '\\', '+', '-', '*', '[', ']', '<', '>', ':', '?', '!', ',', '|', '&', '^', '%', '~'};
		/** Token to return for this rule */
		private final IToken fToken;

		/**
		 * Creates a new operator rule.
		 *
		 * @param token Token to use for this rule
		 */
		public OperatorRule(IToken token) {
			fToken= token;
		}

		/**
		 * Is this character an operator character?
		 *
		 * @param character Character to determine whether it is an operator character
		 * @return <code>true</code> iff the character is an operator, <code>false</code> otherwise.
		 */
		public boolean isOperator(char character) {
			for (int index= 0; index < JAVA_OPERATORS.length; index++) {
				if (JAVA_OPERATORS[index] == character)
					return true;
			}
			return false;
		}

		/*
		 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
		 */
		public IToken evaluate(ICharacterScanner scanner) {

			int character= scanner.read();
			if (isOperator((char) character)) {
				do {
					character= scanner.read();
				} while (isOperator((char) character));
				scanner.unread();
				return fToken;
			} else {
				scanner.unread();
				return Token.UNDEFINED;
			}
		}
	}

	private static class VersionedWordMatcher extends CombinedWordRule.WordMatcher implements ISourceVersionDependent {

		private final IToken fDefaultToken;
		private final String fVersion;
		private boolean fIsVersionMatch;

		public VersionedWordMatcher(IToken defaultToken, String version, String currentVersion) {
			fDefaultToken= defaultToken;
			fVersion= version;
			setSourceVersion(currentVersion);
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.ISourceVersionDependent#setSourceVersion(java.lang.String)
		 */
		public void setSourceVersion(String version) {
			fIsVersionMatch= fVersion.compareTo(version) <= 0;
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.CombinedWordRule.WordMatcher#evaluate(org.eclipse.jface.text.rules.ICharacterScanner, org.eclipse.jdt.internal.ui.text.CombinedWordRule.CharacterBuffer)
		 */
		public IToken evaluate(ICharacterScanner scanner, CombinedWordRule.CharacterBuffer word) {
			IToken token= super.evaluate(scanner, word);

			if (fIsVersionMatch || token.isUndefined())
				return token;

			return fDefaultToken;
		}
	}

	/**
	 * An annotation rule matches the '@' symbol, any following whitespace and
	 * a following java identifier or the <code>interface</code> keyword.
	 *
	 * It does not match if there is a comment between the '@' symbol and
	 * the identifier. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=82452
	 *
	 * @since 3.1
	 */
	private static class AnnotationRule implements IRule, ISourceVersionDependent {
		/**
		 * A resettable scanner supports marking a position in a scanner and
		 * unreading back to the marked position.
		 */
		private static final class ResettableScanner implements ICharacterScanner {
			private final ICharacterScanner fDelegate;
			private int fReadCount;

			/**
			 * Creates a new resettable scanner that will forward calls
			 * to <code>scanner</code>, but store a marked position.
			 *
			 * @param scanner the delegate scanner
			 */
			public ResettableScanner(final ICharacterScanner scanner) {
				Assert.isNotNull(scanner);
				fDelegate= scanner;
				mark();
			}

			/*
			 * @see org.eclipse.jface.text.rules.ICharacterScanner#getColumn()
			 */
			public int getColumn() {
				return fDelegate.getColumn();
			}

			/*
			 * @see org.eclipse.jface.text.rules.ICharacterScanner#getLegalLineDelimiters()
			 */
			public char[][] getLegalLineDelimiters() {
				return fDelegate.getLegalLineDelimiters();
			}

			/*
			 * @see org.eclipse.jface.text.rules.ICharacterScanner#read()
			 */
			public int read() {
				int ch= fDelegate.read();
				if (ch != ICharacterScanner.EOF)
					fReadCount++;
				return ch;
			}

			/*
			 * @see org.eclipse.jface.text.rules.ICharacterScanner#unread()
			 */
			public void unread() {
				if (fReadCount > 0)
					fReadCount--;
				fDelegate.unread();
			}

			/**
			 * Marks an offset in the scanned content.
			 */
			public void mark() {
				fReadCount= 0;
			}

			/**
			 * Resets the scanner to the marked position.
			 */
			public void reset() {
				while (fReadCount > 0)
					unread();

				while (fReadCount < 0)
					read();
			}
		}

		private final IWhitespaceDetector fWhitespaceDetector= new JavaWhitespaceDetector();
		private final IWordDetector fWordDetector= new JavaWordDetector();
		private final IToken fInterfaceToken;
		private final IToken fAnnotationToken;
		private final String fVersion;
		private boolean fIsVersionMatch;

		/**
		 * Creates a new rule.
		 *
		 * @param interfaceToken the token to return if
		 *        <code>'@\s*interface'</code> is matched
		 * @param annotationToken the token to return if <code>'@\s*\w+'</code>
		 *        is matched, but not <code>'@\s*interface'</code>
		 * @param version the lowest <code>JavaCore.COMPILER_SOURCE</code>
		 *        version that this rule is enabled
		 * @param currentVersion the current
		 *        <code>JavaCore.COMPILER_SOURCE</code> version
		 */
		public AnnotationRule(IToken interfaceToken, Token annotationToken, String version, String currentVersion) {
			fInterfaceToken= interfaceToken;
			fAnnotationToken= annotationToken;
			fVersion= version;
			setSourceVersion(currentVersion);
		}

		/*
		 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
		 */
		public IToken evaluate(ICharacterScanner scanner) {
			if (!fIsVersionMatch)
				return Token.UNDEFINED;

			ResettableScanner resettable= new ResettableScanner(scanner);
			if (resettable.read() == '@')
				if (skipWhitespace(resettable))
					return readAnnotation(resettable);

			resettable.reset();
			return Token.UNDEFINED;
		}

		private IToken readAnnotation(ResettableScanner scanner) {
			StringBuffer buffer= new StringBuffer();

			if (!readIdentifier(scanner, buffer)) {
				scanner.reset();
				return Token.UNDEFINED;
			}

			if ("interface".equals(buffer.toString())) //$NON-NLS-1$
				return fInterfaceToken;

			while (readSegment(new ResettableScanner(scanner))) {
				// do nothing
			}
			return fAnnotationToken;
		}

		private boolean readSegment(ResettableScanner scanner) {
			scanner.mark();
			if (skipWhitespace(scanner) && skipDot(scanner) && skipWhitespace(scanner) && readIdentifier(scanner, null))
				return true;

			scanner.reset();
			return false;
		}

		private boolean skipDot(ICharacterScanner scanner) {
			int ch= scanner.read();
			if (ch == '.')
				return true;

			scanner.unread();
			return false;
		}

		private boolean readIdentifier(ICharacterScanner scanner, StringBuffer buffer) {
			int ch= scanner.read();
			boolean read= false;
			while (fWordDetector.isWordPart((char) ch)) {
				if (buffer != null)
					buffer.append((char) ch);
				ch= scanner.read();
				read= true;
			}

			if (ch != ICharacterScanner.EOF)
				scanner.unread();

			return read;
		}

		private boolean skipWhitespace(ICharacterScanner scanner) {
			while (fWhitespaceDetector.isWhitespace((char) scanner.read())) {
				// do nothing
			}

			scanner.unread();
			return true;
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.ISourceVersionDependent#setSourceVersion(java.lang.String)
		 */
		public void setSourceVersion(String version) {
			fIsVersionMatch= fVersion.compareTo(version) <= 0; //$NON-NLS-1$
		}

	}

	private static final String SOURCE_VERSION= JavaCore.COMPILER_SOURCE;

	static String[] fgKeywords= {
		"abstract", //$NON-NLS-1$
		"break", //$NON-NLS-1$
		"case", "catch", "class", "const", "continue", //$NON-NLS-5$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
		"default", "do", //$NON-NLS-2$ //$NON-NLS-1$
		"else", "extends", //$NON-NLS-2$ //$NON-NLS-1$
		"final", "finally", "for", //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
		"goto", //$NON-NLS-1$
		"if", "implements", "import", "instanceof", "interface", //$NON-NLS-5$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
		"native", "new", //$NON-NLS-2$ //$NON-NLS-1$
		"package", "private", "protected", "public", //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
		"static", "super", "switch", "synchronized", //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
		"this", "throw", "throws", "transient", "try", //$NON-NLS-5$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
		"volatile", //$NON-NLS-1$
		"while" //$NON-NLS-1$
	};

	private static final String RETURN= "return"; //$NON-NLS-1$
	private static String[] fgJava14Keywords= { "assert" }; //$NON-NLS-1$
	private static String[] fgJava15Keywords= { "enum" }; //$NON-NLS-1$

	// AspectJ change begin - AspectJ keywords
    private static String[] ajKeywords = { "aspect", "pointcut", "privileged", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// Pointcut designators: methods and constructora
		"call", "execution", "initialization", "preinitialization" , //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		// Pointcut designators: exception handlers
		"handler", //$NON-NLS-1$
		// Pointcut designators: fields
		"get", "set", //$NON-NLS-1$ //$NON-NLS-2$
		// Pointcut designators: static initialization
		"staticinitialization", //$NON-NLS-1$
		// Pointcut designators: object
		// (this already a Java keyword)
		"target", "args", //$NON-NLS-1$ //$NON-NLS-2$
		// Pointcut designators: lexical extents
		"within", "withincode", //$NON-NLS-1$ //$NON-NLS-2$
		// Pointcut designators: control flow
		"cflow", "cflowbelow", //$NON-NLS-1$ //$NON-NLS-2$
		// Pointcut Designators for annotations
		"annotation", //$NON-NLS-1$
		// Advice
		"before", "after", "around", "proceed", "throwing" , "returning" , //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		"adviceexecution" , //$NON-NLS-1$
		// Declarations
		"declare", "parents" , "warning" , "error", "soft" , "precedence" , //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		// variables
		"thisJoinPoint" , "thisJoinPointStaticPart" , "thisEnclosingJoinPointStaticPart" , //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// Associations
		"issingleton", "perthis", "pertarget", "percflow", "percflowbelow", "pertypewithin" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    // AspectJ Change End
    
	private static String[] fgTypes= { "void", "boolean", "char", "byte", "short", "strictfp", "int", "long", "float", "double" }; //$NON-NLS-1$ //$NON-NLS-5$ //$NON-NLS-7$ //$NON-NLS-6$ //$NON-NLS-8$ //$NON-NLS-9$  //$NON-NLS-10$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-2$

	private static String[] fgConstants= { "false", "null", "true" }; //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$


	private static String[] fgTokenProperties= {
		IJavaColorConstants.JAVA_KEYWORD,
		IJavaColorConstants.JAVA_STRING,
		IJavaColorConstants.JAVA_DEFAULT,
		IJavaColorConstants.JAVA_KEYWORD_RETURN,
		IJavaColorConstants.JAVA_OPERATOR,
		IJavaColorConstants.JAVA_ANNOTATION,
	};

	private List fVersionDependentRules= new ArrayList(3);

	/**
	 * Creates a Java code scanner
	 *
	 * @param manager	the color manager
	 * @param store		the preference store
	 */
	public AspectJCodeScanner(IColorManager manager, IPreferenceStore store) {
		super(manager, store);
		initialize();
	}

	/*
	 * @see AbstractJavaScanner#getTokenProperties()
	 */
	protected String[] getTokenProperties() {
		return fgTokenProperties;
	}

	/*
	 * @see AbstractJavaScanner#createRules()
	 */
	protected List createRules() {

		List rules= new ArrayList();

		// Add rule for character constants.
		Token token= getToken(IJavaColorConstants.JAVA_STRING);
		rules.add(new SingleLineRule("'", "'", token, '\\')); //$NON-NLS-2$ //$NON-NLS-1$


		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new JavaWhitespaceDetector()));

		String version= getPreferenceStore().getString(SOURCE_VERSION);

		// Add JLS3 rule for /@\s*interface/
		AnnotationRule atInterfaceRule= new AnnotationRule(getToken(IJavaColorConstants.JAVA_KEYWORD), getToken(IJavaColorConstants.JAVA_ANNOTATION), "1.5", version); //$NON-NLS-1$
		rules.add(atInterfaceRule);
		fVersionDependentRules.add(atInterfaceRule);

		// Add word rule for new keywords, 4077
		JavaWordDetector wordDetector= new JavaWordDetector();
		token= getToken(IJavaColorConstants.JAVA_DEFAULT);
		CombinedWordRule combinedWordRule= new CombinedWordRule(wordDetector, token);

		token= getToken(IJavaColorConstants.JAVA_DEFAULT);
		VersionedWordMatcher j14Matcher= new VersionedWordMatcher(token, "1.4", version); //$NON-NLS-1$

		token= getToken(IJavaColorConstants.JAVA_KEYWORD);
		for (int i=0; i<fgJava14Keywords.length; i++)
			j14Matcher.addWord(fgJava14Keywords[i], token);

		combinedWordRule.addWordMatcher(j14Matcher);
		fVersionDependentRules.add(j14Matcher);

		token= getToken(IJavaColorConstants.JAVA_DEFAULT);
		VersionedWordMatcher j15Matcher= new VersionedWordMatcher(token, "1.5", version); //$NON-NLS-1$
		token= getToken(IJavaColorConstants.JAVA_KEYWORD);
		for (int i=0; i<fgJava15Keywords.length; i++)
			j15Matcher.addWord(fgJava15Keywords[i], token);

		combinedWordRule.addWordMatcher(j15Matcher);
		fVersionDependentRules.add(j15Matcher);

		// Add rule for operators and brackets
		token= getToken(IJavaColorConstants.JAVA_OPERATOR);
		rules.add(new OperatorRule(token));

		// Add word rule for keyword 'return'.
		CombinedWordRule.WordMatcher returnWordRule= new CombinedWordRule.WordMatcher();
		token= getToken(IJavaColorConstants.JAVA_KEYWORD_RETURN);
		returnWordRule.addWord(RETURN, token);  //$NON-NLS-1$
		combinedWordRule.addWordMatcher(returnWordRule);

		// Add word rule for keywords, types, and constants.
		CombinedWordRule.WordMatcher wordRule= new CombinedWordRule.WordMatcher();
		token= getToken(IJavaColorConstants.JAVA_KEYWORD);
		for (int i=0; i<fgKeywords.length; i++)
			wordRule.addWord(fgKeywords[i], token);
		for (int i=0; i<fgTypes.length; i++)
			wordRule.addWord(fgTypes[i], token);
		for (int i=0; i<fgConstants.length; i++)
			wordRule.addWord(fgConstants[i], token);
	
		// AspectJ Change begin - add AJ keywords
		WordRule ajDotWordRule = new DotWordRule(new JavaWordDetector());
		
		WordRule ajBracketRule = new BracketWordRule(new JavaWordDetector());
		
		// This is a bit fragile because it depends on positions in the ajKeywords array
		// but they don't change very often so should be ok..
		for (int i = 0; i < ajKeywords.length; i++) {
			if (i > 3 && i < 23
					|| i == 25
					|| i > 34 && i < 41) {
				ajBracketRule.addWord(ajKeywords[i], token);
			} else {
				ajDotWordRule.addWord(ajKeywords[i], token);
			}
		}
		
		//important: add aj rules before wordRule 
		rules.add(ajBracketRule);
		rules.add(ajDotWordRule);
		// AspectJ Change end
		
		combinedWordRule.addWordMatcher(wordRule);
		rules.add(combinedWordRule);

		setDefaultReturnToken(getToken(IJavaColorConstants.JAVA_DEFAULT));
		return rules;
	}

	/*
	 * @see AbstractJavaScanner#affectsBehavior(PropertyChangeEvent)
	 */
	public boolean affectsBehavior(PropertyChangeEvent event) {
		return event.getProperty().equals(SOURCE_VERSION) || super.affectsBehavior(event);
	}

	/*
	 * @see AbstractJavaScanner#adaptToPreferenceChange(PropertyChangeEvent)
	 */
	public void adaptToPreferenceChange(PropertyChangeEvent event) {

		if (event.getProperty().equals(SOURCE_VERSION)) {
			Object value= event.getNewValue();

			if (value instanceof String) {
				String s= (String) value;

				for (Iterator it= fVersionDependentRules.iterator(); it.hasNext();) {
					ISourceVersionDependent dependent= (ISourceVersionDependent) it.next();
					dependent.setSourceVersion(s);
				}
			}

		} else if (super.affectsBehavior(event)) {
			super.adaptToPreferenceChange(event);
		}
	}

	// AspectJ Change Begin
	/**
	 * Utility method which returns true if the given string is a keyword that
	 * can appear in a pointcut definition.
	 * 
	 * @param word
	 * @return
	 */
	public static boolean isAjPointcutKeyword(String word) {
		for (int i = 0; i < ajKeywords.length; i++) {
			if (ajKeywords[i].equals(word)) {
				return true;
			}
		}
		// "this" and "if" are not in the aj list as they are java keywords
		if ("this".equals(word)) { //$NON-NLS-1$
			return true;
		}
		if ("if".equals(word)) { //$NON-NLS-1$
			return true;
		}
		return false;
	}
	// AspectJ Change End
}
