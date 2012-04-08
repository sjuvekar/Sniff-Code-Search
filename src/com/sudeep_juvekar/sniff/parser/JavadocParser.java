package com.sudeep_juvekar.sniff.parser;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import com.sudeep_juvekar.sniff.stemmer.Stemmer;
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

public class JavadocParser {

	/***
	 * Given a Javadoc comment, removes all the XML Tags from the comment.
	 */
	
	private static String TOKENS = "`~!@#$%^&*()-_+=|\\/?.>,<;:'\"\n\t \r";
	
	public static String parse(String comment) {
		char LEFTPARAN = '<';
		char RIGHTPARAN = '>';
		boolean insideTag = false;
		String ans = "";
		for(int i = 0; i < comment.length();i++) {
			char CURRENTCHAR = comment.charAt(i);
			if(CURRENTCHAR == LEFTPARAN) insideTag = true;
			else if(CURRENTCHAR == RIGHTPARAN) insideTag = false;
			else if(!insideTag) ans += CURRENTCHAR;
		}
		return ans;	
	}
	
	/**
	 * Tokeninzes the comment by spaces and other characters
	 * @param comment
	 */
	public static Hashtable<String, Integer> getTokens(String comment) {
		
		Hashtable<String, Integer> table = new Hashtable<String, Integer>();
		StringTokenizer sr = new StringTokenizer(comment,TOKENS);
		String temp = "";
		
		if(sr.hasMoreTokens())
			temp += sr.nextToken();
		
		while(sr.hasMoreTokens()) 
			temp += " " + sr.nextToken();
		
		Vector<String> stemmedComment = Stemmer.stemming(temp);
		for(int i = 0; i < stemmedComment.size(); i++)
			table.put(stemmedComment.elementAt(i), 0);
		
		return table;
	}
}
