package com.sudeep_juvekar.sniff.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.sudeep_juvekar.sniff.internal.Snippet;
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

public class EditorHandler {

	static IEditorPart cuEditor;

	public static boolean insertSnippet(Snippet snippet) {
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (window != null) {
		    IWorkbenchPage page = window.getActivePage();
		    if (page != null) {
		        cuEditor = page.getActiveEditor();
		        IRewriteTarget rt = (IRewriteTarget) cuEditor.getAdapter(IRewriteTarget.class);
		        IDocument doc = rt.getDocument();
		        rt.beginCompoundChange();
		        
		        try {
		        	ITextSelection selection = (ITextSelection)cuEditor.getEditorSite().getSelectionProvider().getSelection();		        	
		        	doc.replace(selection.getOffset(), 0, snippet.toCode());
		        }catch (Exception e) {
		        	e.printStackTrace();
		        	return false;
		        }
		        finally {
		        	rt.endCompoundChange();
		        }
		        
		    }
		
		    return true;
		}
		
		return false;
	}
}
