package com.sudeep_juvekar.sniff.parser;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

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


public class SniffParser {
	
	public CompilationUnit currentCu = null;
	
	/***
	 * Constructor
	 */
	public SniffParser() {
		
	}
	
	/***
	 * Parse an IFile for comments
	 * @param ifile
	 * @return
	 */
	public CompilationUnit parse(IFile ifile) {
		ICompilationUnit icu = JavaCore.createCompilationUnitFrom(ifile);
		return parse(icu);
	}
	
	/***
	 * Parse a CompilationUnit 
	 * @param cu
	 * @return
	 */
	public CompilationUnit parse(ICompilationUnit cu) {	
		ASTParser astParser = ASTParser.newParser(AST.JLS3);
		astParser.setSource(cu);
		astParser.setResolveBindings(true);
		return (CompilationUnit) astParser.createAST(null);	
	}
	
	/***
	 * Internal method for running a folder
	 * @param iresource
	 */
	private void runResource(IResource iresource) {
		try {
			if(iresource == null) {
				return;
			}
			else if(iresource.getType() == IResource.FILE) {
				IFile file = (IFile)iresource;
				String fileExtension = file.getFileExtension(); 
				
				if(fileExtension != null && fileExtension.equals("java")) {
					System.out.println(file);	
					currentCu = parse(file); 
					SniffVisitor sv = new SniffVisitor();
					currentCu.accept(sv);
					
				}
			}
			else if(iresource.getType() == IResource.FOLDER){
				IFolder folder = (IFolder)iresource;
				IResource[] members = folder.members();
				
				for(int i = 0; i < members.length; i++) 
					runResource(members[i]);
			}
			else if(iresource.getType() == IResource.PROJECT){
				IProject folder = (IProject)iresource;
				IResource[] members = folder.members();
				
				for(int i = 0; i < members.length; i++) 
					runResource(members[i]);
			}	
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * Given a folder name, parse all files in it!
	 * @param folder
	 */
	public void run(String folder) {
			
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			IResource iresource = workspaceRoot.findMember(folder);
			runResource(iresource);
	}
	
	/***
	 * Given a file, parse it!
	 * @param f
	 */
	public void run(ICompilationUnit f) {
		currentCu = parse(f);
		SniffVisitor sv = new SniffVisitor();
		currentCu.accept(sv);
		
	}
	
	/***
	 * 
	 
	public void compress() {
		ArrayList<Statement> statements = new ArrayList<Statement>();
		Snippet snip = new Snippet();
		if(statementsInFile.isEmpty()) return;
		
		for(int i = 0; i <= THRESH; i++) {
			if(i < statementsInFile.size()) {
				statements.add(statementsInFile.elementAt(i));
			}
		}
		
		snip = new Snippet(statements);
		InsertDB.insertDB(snip, 0);
		
		for(int i = 1; i <statementsInFile.size(); i++) {
			
			int j = i + THRESH;
			
			if(i > THRESH)
				statements.remove(0);
			if(j < statementsInFile.size()) 
				statements.add(statementsInFile.elementAt(j));
		
			snip = new Snippet(statements);
			if(i <= THRESH)
				InsertDB.insertDB(snip, i);
			else
				InsertDB.insertDB(snip, THRESH);
		}
//	}*/
	
	
}
