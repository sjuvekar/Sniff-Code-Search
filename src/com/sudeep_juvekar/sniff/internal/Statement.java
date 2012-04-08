package com.sudeep_juvekar.sniff.internal;

import java.util.Enumeration;
import java.util.Hashtable;

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

public class Statement {
	
	String returnType;
	String returnTypeVariable;
	String declaringClassPlaceHolder;
	String methodName;
	String declaringClass;
	String[] parameterTypes;
	public Hashtable<String, Integer> comment;
	
	public Statement(String returnType, String methodName, String declaringClass, String[] parameterTypes, Hashtable<String, Integer> comment) {
		this.returnType = returnType;
		this.methodName = methodName;
		this.declaringClass = declaringClass;
		this.parameterTypes = parameterTypes;
		this.comment = comment;
		this.returnTypeVariable = "";
	}
	
	boolean equals(Statement s) {
		return ( (returnType.equals( (s.returnType)) &&
				 methodName.equals( s.methodName)) &&
				 (declaringClass.equals( s.declaringClass) )
				);
	}

	public String getReturnType() {
		return returnType;
	}
	
	public void setReturnTypeVariable(String v) {
		returnTypeVariable = v;
	}
	
	public void setDeclaringClassPlaceHolder(String d) {
		declaringClassPlaceHolder = d;
	}
	
	public void setReturnType(String s) {
		returnType = s;
	}
	
	public void setDeclaringClass(String s) {
		declaringClass = s;
	}
	
	public String getComments() {
		String ret = "<";
		Enumeration<String> e = comment.keys();
		
		while(e.hasMoreElements()) 
			ret += e.nextElement() + ",";
		ret += ">";
		return ret;
	}
	
	public String getDeclaringClass() {
		return declaringClass;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public String toString() {
		String ret = returnType + "=" +declaringClass+"."+methodName+"()";
		ret += "<";
		
		Enumeration<String> e = comment.keys();
		while(e.hasMoreElements()) 
			ret += e.nextElement() + " ";
		
		ret += "\n";
		return ret;
	}
}
