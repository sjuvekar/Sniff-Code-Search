package com.sudeep_juvekar.sniff.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
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

public class SnippetIntersection {
	
	public static Snippet[] intersectAndRank(Vector<Snippet> snippets, Vector<String> query) {
		
		Hashtable<Integer, Snippet> table = new Hashtable<Integer, Snippet>();
		for(int i = 0; i < snippets.size(); i++)
			table.put(i, snippets.elementAt(i));
		
		for(int i =0; i<snippets.size(); i++) {
			if(!table.containsKey(i)) continue;
			for(int j = i+1; j < snippets.size(); j++) {
				if(!table.containsKey(j)) continue;
				Snippet s1 = table.get(i);
				Snippet s2 = table.get(j);
					
				ArrayList<Statement> intersection = s1.intersect(s2);
					
				if (Snippet.containsQuery(query, intersection)) {
					s1.setStatements(intersection);
					s1.support += s2.support;
					
					table.remove(i);
					table.remove(j);
					table.put(i, s1);
				}
			}
		}
		
		Snippet[] toReturn = new Snippet[table.size()];
		int i = 0;
		Enumeration<Integer> e = table.keys();
		while(e.hasMoreElements())  
			toReturn[i++] = table.get(e.nextElement());
			
		Arrays.sort(toReturn, new SnippetComparator());
 		return toReturn;
	}
}
