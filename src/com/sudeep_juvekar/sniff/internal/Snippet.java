package com.sudeep_juvekar.sniff.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;

import com.sudeep_juvekar.sniff.core.IResult;
import com.sudeep_juvekar.sniff.core.ISnippet;
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

public class Snippet extends PlatformObject implements ISnippet {
	public Hashtable<String, Integer> comments = new Hashtable<String, Integer>();
	private ArrayList<Statement> statements = new ArrayList<Statement>();
	private IResult result;
	private String code = "";
	int support = 1;
	
	
	public Snippet() {
		comments.clear();
	}
	
	public Snippet(ArrayList<Statement> s) {
		statements = s;
		comments.clear();
		
		for(int i = 0; i < s.size(); i++) {
			Statement st = s.get(i);
			code += st.toString();
			Enumeration<String> e = st.comment.keys();
			while(e.hasMoreElements()) comments.put(e.nextElement(), 0);
		}	

	}
	
	public void setStatements(ArrayList<Statement> s) {
		statements = s;
		
		for(int i = 0; i < s.size(); i++) {
			Statement st = s.get(i);
			code += st.toString();
			Enumeration<String> e = st.comment.keys();
			while(e.hasMoreElements()) comments.put(e.nextElement(), 0);
		}	

	}
	
	public ArrayList<Statement> getStatements() {
		return statements;
	}
	
	public void addStatement(Statement s) {
		statements.add(s);
	
		code += s.toString();
		Enumeration<String> e = s.comment.keys();
		while(e.hasMoreElements()) comments.put(e.nextElement(), 0);
	
	}
	
	public Snippet(String q, IResult r) {
		result = r;
		code = q;
	}
	
	public String toString() {
		String ans = "";
		if(statements.size() > 0)
			ans += statements.get(0).methodName;
		if(statements.size() > 1)
			ans += " ->...-> " + statements.get(statements.size() - 1).methodName; 
		return ans;
	}

	public String returnCodeToView() {
		String ans = "";
		for(int i = 0; i < statements.size(); i++) {
			Statement curStat = statements.get(i);
			if(!curStat.returnType.equals("void")) 
				ans += curStat.returnType + " " + curStat.returnTypeVariable + " = ";
			if(curStat.declaringClass.equals(curStat.methodName))
				ans += "new ";
			else
				ans +=	curStat.declaringClassPlaceHolder + "."; 
			ans += curStat.methodName+"();\n";
		}
		code = ans;
		return ans;
	}
	
	public void setCode(String s) {
		code = s;
	}
	
	public String toCode() {
		return code;
	}
	
	/**
	 * Intersection of two snippets
	 * @param s
	 */
	public ArrayList<Statement> intersect(Snippet s) {
		
		ArrayList<Statement> ans = new ArrayList<Statement>();
		int size1 = statements.size();
		int size2 = s.statements.size();
		if(size1 == 0 || size2 == 0) return ans;
		
		int[][] score = new int[size1+1][size2+1];
		int[][] pointers = new int[size1+1][size2+1];
		
		for(int i = 0; i <= size1; i++) {
			score[i][0] = 0;
			pointers[i][0] = 1;
		}
		
		for(int i = 0; i <= size2; i++) {
			score[0][i] = 0;
			pointers[0][i] = -1;
		}
		
		//Main loop... doing Dynamic Programming
		for(int i = 1; i <= size1; i++) {
			for(int j = 1; j <= size2; j++) {
				int max = score[i-1][j];
				int maxi = i - 1;
				int maxj = j;
				
				if(score[i][j-1] > max) {
					max = score[i][j-1];
					maxi = i;
					maxj = j -1;
				}
				
				int diag = (statements.get(i-1)).equals(s.statements.get(j-1))? score[i-1][j-1] + 1: score[i-1][j-1];
				if(diag > max) {
					max = diag;
					maxi = i - 1;
					maxj = j - 1;
				}
				
				score[i][j] = max;
				pointers[i][j] = (i - maxi) - (j - maxj);
			}
		}
		
		//Destructive! Removes statements from list
		int i = size1;
		int j = size2;
		
		while(i != 0 || j != 0) {
			if(pointers[i][j] == 0) {
				
				ans.add(0, statements.get(i-1));	
				i--;
				j--;
			}
			else if(pointers[i][j] == 1) i--;
			else if(pointers[i][j] == -1) j--;
		}
		
		return ans;
	}
	
	public static boolean containsQuery(String query, Statement s) {
		if(s.comment.containsKey(query)) return true;
		return false;
	}
	
	public static boolean containsQuery(String query, ArrayList<Statement> s) {
		for(int i = 0; i < s.size(); i++) 
			if(containsQuery(query, s.get(i))) return true;
		return false;
	}
	
	public static boolean containsQuery(Vector<String> query, ArrayList<Statement> s) {
		for(int i = 0; i < query.size();i++)
			if(!containsQuery(query.elementAt(i), s)) return false;
		return true;
	}
	
	public boolean containsQuery(Vector<String> query) {
		for(int i = 0; i <query.size();i++)
			if(!comments.containsKey(query.elementAt(i))) return false;
		return true;
	}
	
	/***
	 * Construct a variable name
	 * @param returnType
	 * @return
	 */
	public static String constructReturnTypeVariable(String returnType, int stateId) {
		String ans = "_";
		for(int i = 0; i < returnType.length(); i++) {
			char ithChar = returnType.charAt(i);
			if(ithChar <= 'Z' && ithChar >= 'A')
				ans += returnType.substring(i,i+1).toLowerCase();
		}
			
		ans += stateId;
		return ans;
	}
	/**
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
     */
    public String getLabel(Object o) {
        return o.toString();
    }

    public void addStatement(String returnType, String methodName, String declaringClass, String[] parameterTypes, Hashtable<String, Integer> comment) {
    	statements.add(new Statement(returnType, methodName, declaringClass, parameterTypes, comment));
    }
    /**
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object o) {
        o.toString();
        return new Object[0];
    }

    /**
     * 
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
     */
    public ImageDescriptor getImageDescriptor(Object object) {
        object.toString();
        return null;
    }

    /**
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
     */
    public Object getParent(Object o) {
        o.toString();
        return null;
    }
    
    /***
     * Class for comparing the Snippets
     * @author sudeep
     *
     */
}

class SnippetComparator implements Comparator<Snippet> {
	public int compare(Snippet s1, Snippet s2) {
		int support1 = s1.support;
		int support2 = s2.support;
		if(support1 > support2) return -1;
		else if(support1 == support2) return 0;
		else return 1;
	}
	
}
