package com.sudeep_juvekar.sniff.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;

import com.sudeep_juvekar.sniff.core.IResult;
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

/*
 * The content provider class is responsible for
 * providing objects to the view. It can wrap
 * existing objects in adapters or simply return
 * objects as-is. These objects may be sensitive
 * to the current input of the view, or ignore
 * it and always show the same content 
 * (like Task List, for example). The objects returned 
 * by this class are the text box: query and the button: seacrh
 */

public class SearchContentProvider implements IStructuredContentProvider, IPropertyChangeListener {
	
	private StructuredViewer viewer;
	
	public SearchContentProvider() {
		
	} 
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		if (viewer == null)
			this.viewer = (StructuredViewer) v;

		if (oldInput != newInput) { // if not the same

			if (newInput != null) { // add listener to new - fires even if old
									// is null
				((IResult) newInput).addPropertyChangeListener(this);
			}

			if (oldInput != null) { // remove from old - fires even if new is
									// null
				((IResult) oldInput).removePropertyChangeListener(this);
			}
		}
	}
	
	public void dispose() {
		viewer = null;
	}
	
	public Object[] getElements(Object parent) {
		return ((IResult)parent).getSnippets();
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		// TODO Auto-generated method stub
		Control ctrl = viewer.getControl();
	    if (ctrl != null && !ctrl.isDisposed()) {

	      ctrl.getDisplay().asyncExec(new Runnable() {

	        public void run() {
	          if (event.getProperty() == IResult.LISTADD
	              || event.getProperty() == IResult.LISTREMOVE)
	            viewer.refresh();

	          else {
	            String[] propChange = new String[] {event.getProperty()};
	            viewer.update(event.getNewValue(), propChange);
	          }
	        }
	      });
		
	    }
	}
}

