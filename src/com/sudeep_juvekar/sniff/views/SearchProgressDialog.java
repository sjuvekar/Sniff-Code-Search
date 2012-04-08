package com.sudeep_juvekar.sniff.views;

import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.Viewer;

import com.sudeep_juvekar.sniff.internal.Result;
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

public class SearchProgressDialog extends ProgressMonitorDialog {
	
	String query;
	Viewer viewer;
	
	public SearchProgressDialog(Viewer v, String q) {
		super(v.getControl().getShell());
		query = q;
		viewer = v;
	}
	
	public void run(boolean fork, boolean cancelable, IRunnableWithProgress p) throws InvocationTargetException, InterruptedException {
		
		SearchProgressMonitor sniffProgressMonitor = new SearchProgressMonitor(query, viewer);
		super.run(fork, cancelable, sniffProgressMonitor);
	}
}

class SearchProgressMonitor implements IRunnableWithProgress {
	
	private String query;
	Viewer viewer;
	
	public SearchProgressMonitor(String q, Viewer v) {
		query = q;
		viewer = v;
	}
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		monitor.beginTask("Searching for... " + query, IProgressMonitor.UNKNOWN);
		
		Thread t = new Thread() {
			public void run() {
				( (Result) viewer.getInput()).resetSnippets();
				Vector<String> q = Stemmer.stemming(query);
				((Result)viewer.getInput()).addSnippet(q);
			}
		};
		t.start();	
		t.join();
		monitor.done();
	}
}
