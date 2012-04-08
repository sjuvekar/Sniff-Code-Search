package com.sudeep_juvekar.sniff.parser;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

import com.sudeep_juvekar.sniff.fileHandler.InsertFile;
import com.sudeep_juvekar.sniff.internal.Snippet;
import com.sudeep_juvekar.sniff.internal.Statement;
/**
 * Copyright (c) 2007-2008,
 * Sudeep Juvekar    <sjuvekar@cs.berkeley.edu>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * <p/>
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * <p/>
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p/>
 * 3. The names of the contributors may not be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public class SniffVisitor extends ASTVisitor {

	ITypeBinding returnType;
	SimpleName methodName;
	String name;
	ITypeBinding declaringClass;
	int modifiers;
	ITypeBinding[] parameterTypes;
	String[]params;
	Comment comment;
	IMethodBinding methodInvocation;
	IMethodBinding methodDeclaration;
	String javadoc;
	static Vector<Statement> statementsInMethod;
	static int THRESH = 3;
	
	public SniffVisitor() {
		super();
		returnType = null;
		declaringClass = null;
		modifiers = 0;
		methodName = null;
		name = "";
		params = null;
		comment = null;
		methodInvocation = null;
		methodDeclaration = null;
		javadoc = "";
		statementsInMethod = new Vector<Statement>();
	}
	

	public void postVisit(ASTNode node) {
		int nodeType = node.getNodeType();
		if(nodeType == ASTNode.METHOD_INVOCATION) {
			MethodInvocation mi = (MethodInvocation) node;
			try {
				methodName = mi.getName();
				name = methodName.getFullyQualifiedName();
				methodInvocation = mi.resolveMethodBinding();
				if(methodInvocation == null)
					System.out.println("Binding can not be resolved\n***********************");
				else  { 
					returnType = methodInvocation.getReturnType();
					declaringClass = methodInvocation.getDeclaringClass();
					modifiers = methodInvocation.getModifiers();
					parameterTypes = methodInvocation.getParameterTypes();
					params = new String[parameterTypes.length];
					for(int i = 0; i < parameterTypes.length; i++) params[i] = parameterTypes[i].toString();
					
					methodDeclaration = methodInvocation.getMethodDeclaration();
					javadoc = methodDeclaration.getJavaElement().getAttachedJavadoc(null);
					Hashtable<String, Integer> h = (javadoc == null)? new Hashtable<String, Integer>():
						JavadocParser.getTokens(JavadocParser.parse(javadoc));
			        
					Statement stat = new Statement(returnType.getName(),
												methodName.getFullyQualifiedName(),
												declaringClass.getName(),
												params,
												h
												);
					statementsInMethod.add(stat);
				}
				
			}
			catch (Exception e) {
				javadoc = null;
			}
		}
		
		else if(nodeType == ASTNode.CLASS_INSTANCE_CREATION) {
			ClassInstanceCreation ci = (ClassInstanceCreation) node;
			try {
				methodInvocation = ci.resolveConstructorBinding();
			
				if(methodInvocation == null)
					System.out.println("Binding can not be resolved\n***********************");
				else {
					declaringClass = methodInvocation.getDeclaringClass();
					returnType = methodInvocation.getReturnType();
					name = declaringClass.getQualifiedName();
					name = methodInvocation.getName();
					modifiers = methodInvocation.getModifiers();
					parameterTypes = methodInvocation.getParameterTypes();
					params = new String[parameterTypes.length];
					for(int i = 0; i < parameterTypes.length; i++) params[i] = parameterTypes[i].toString();
					
					methodDeclaration = methodInvocation.getMethodDeclaration();
					IJavaElement j = methodDeclaration.getJavaElement();
					javadoc = (j==null)?null:j.getAttachedJavadoc(null);
					Hashtable<String, Integer> h = (javadoc == null)? new Hashtable<String, Integer>():
						JavadocParser.getTokens(JavadocParser.parse(javadoc));
			        
					Statement stat = new Statement(declaringClass.getName(),
													name,
													declaringClass.getName(),
													params,
													h);
					statementsInMethod.add(stat);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		else if(nodeType == ASTNode.SUPER_METHOD_INVOCATION) {
			SuperMethodInvocation mi = (SuperMethodInvocation)node;
			try{
				methodInvocation = mi.resolveMethodBinding();
			
				if(methodInvocation == null)
					System.out.println("Binding can not be resolved\n***********************");
				else {
					returnType = methodInvocation.getReturnType();
					methodName = mi.getName();
					declaringClass = methodInvocation.getDeclaringClass();
					modifiers = methodInvocation.getModifiers();
					parameterTypes = methodInvocation.getParameterTypes();
					params = new String[parameterTypes.length];
					for(int i = 0; i < parameterTypes.length; i++) params[i] = parameterTypes[i].toString();
				
					methodDeclaration = methodInvocation.getMethodDeclaration();
					javadoc = methodDeclaration.getJavaElement().getAttachedJavadoc(null);
					Hashtable<String, Integer> h = (javadoc == null)? new Hashtable<String, Integer>():
						JavadocParser.getTokens(JavadocParser.parse(javadoc));
			        
					Statement stat = new Statement(returnType.getName(),
													methodName.getFullyQualifiedName(),
													declaringClass.getName(),
													params,	
													h);
					statementsInMethod.add(stat);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/*
	public boolean visit(MethodInvocation mi) {
		
		try {
			methodName = mi.getName();
			name = methodName.getFullyQualifiedName();
			methodInvocation = mi.resolveMethodBinding();
			if(methodInvocation == null)
				System.out.println("Binding can not be resolved\n***********************");
			else  { 
				returnType = methodInvocation.getReturnType();
				declaringClass = methodInvocation.getDeclaringClass();
				modifiers = methodInvocation.getModifiers();
				parameterTypes = methodInvocation.getParameterTypes();
				params = new String[parameterTypes.length];
				for(int i = 0; i < parameterTypes.length; i++) params[i] = parameterTypes[i].toString();
				
				methodDeclaration = methodInvocation.getMethodDeclaration();
				javadoc = methodDeclaration.getJavaElement().getAttachedJavadoc(null);
				Hashtable<String, Integer> h = (javadoc == null)? new Hashtable<String, Integer>():
					JavadocParser.getTokens(JavadocParser.parse(javadoc));
		        
				Statement stat = new Statement(returnType.getName(),
											methodName.getFullyQualifiedName(),
											declaringClass.getName(),
											params,
											h
											);
				statementsInMethod.add(stat);
			}
			
		}
		catch (Exception e) {
			javadoc = null;
		}
		return true;
	}
	
	public boolean visit(ClassInstanceCreation ci) {
		
		try {
			methodInvocation = ci.resolveConstructorBinding();
		
			if(methodInvocation == null)
				System.out.println("Binding can not be resolved\n***********************");
			else {
				declaringClass = methodInvocation.getDeclaringClass();
				returnType = methodInvocation.getReturnType();
				name = declaringClass.getQualifiedName();
				name = methodInvocation.getName();
				modifiers = methodInvocation.getModifiers();
				parameterTypes = methodInvocation.getParameterTypes();
				params = new String[parameterTypes.length];
				for(int i = 0; i < parameterTypes.length; i++) params[i] = parameterTypes[i].toString();
				
				methodDeclaration = methodInvocation.getMethodDeclaration();
				IJavaElement j = methodDeclaration.getJavaElement();
				javadoc = (j==null)?null:j.getAttachedJavadoc(null);
				Hashtable<String, Integer> h = (javadoc == null)? new Hashtable<String, Integer>():
					JavadocParser.getTokens(JavadocParser.parse(javadoc));
		        
				Statement stat = new Statement(declaringClass.getName(),
												name,
												declaringClass.getName(),
												params,
												h);
				statementsInMethod.add(stat);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean visit(SuperMethodInvocation mi) {
		
		try{
			methodInvocation = mi.resolveMethodBinding();
		
			if(methodInvocation == null)
				System.out.println("Binding can not be resolved\n***********************");
			else {
				returnType = methodInvocation.getReturnType();
				methodName = mi.getName();
				declaringClass = methodInvocation.getDeclaringClass();
				modifiers = methodInvocation.getModifiers();
				parameterTypes = methodInvocation.getParameterTypes();
				params = new String[parameterTypes.length];
				for(int i = 0; i < parameterTypes.length; i++) params[i] = parameterTypes[i].toString();
			
				methodDeclaration = methodInvocation.getMethodDeclaration();
				javadoc = methodDeclaration.getJavaElement().getAttachedJavadoc(null);
				Hashtable<String, Integer> h = (javadoc == null)? new Hashtable<String, Integer>():
					JavadocParser.getTokens(JavadocParser.parse(javadoc));
		        
				Statement stat = new Statement(returnType.getName(),
												methodName.getFullyQualifiedName(),
												declaringClass.getName(),
												params,	
												h);
				statementsInMethod.add(stat);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	*/
	
	public void postVisit(MethodDeclaration md) {
		addToDatabase();
		statementsInMethod.clear();
	}
	
	/***
	 * 
	 */
	public static void addToDatabase() {
		ArrayList<Statement> statements = new ArrayList<Statement>();
		Snippet snip = new Snippet();
		if(statementsInMethod.isEmpty()) return;
		
		for(int i = 0; i <= THRESH; i++) {
			if(i < statementsInMethod.size()) {
				statements.add(statementsInMethod.elementAt(i));
			}
		}
		
		snip = new Snippet(statements);
		InsertFile.insertFile(snip, 0);
		
		for(int i = 1; i <statementsInMethod.size(); i++) {
			
			int j = i + THRESH;
			
			if(i > THRESH)
				statements.remove(0);
			if(j < statementsInMethod.size()) 
				statements.add(statementsInMethod.elementAt(j));
		
			snip = new Snippet(statements);
			if(i <= THRESH)
				InsertFile.insertFile(snip, i);
			else
				InsertFile.insertFile(snip, THRESH);
		}
	}
	
}
