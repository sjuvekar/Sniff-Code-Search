package com.sudeep_juvekar.sniff.internal;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.sudeep_juvekar.sniff.core.IResult;
import com.sudeep_juvekar.sniff.core.ISnippet;
import com.sudeep_juvekar.sniff.fileHandler.RetrieveSnippets;
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

public class Result implements IResult {

	private ArrayList<ISnippet> snipList = null;
	private transient ListenerList propertyChangeListeners = null;
	
	public Result() {
		this(true);
	}
	
	
	public Result(boolean b) {
		super();
		
		snipList = new ArrayList<ISnippet>();
		if(b)
			resetSnippets();
	}


	@Override
	public ISnippet addSnippet(Vector<String> query) {
		Snippet[] ans = RetrieveSnippets.retrieveSnippets(query);
		snipList = new ArrayList<ISnippet>();
		for(int i = 0; i<ans.length; i++) {
			Snippet snippet = ans[i];
			snipList.add(snippet);
			//Fire the property chang
			firePropertyChange(LISTADD, null, snippet);
		}
		if(ans.length > 0)
			return ans[0];
		return null;
	}

	@Override
	public Object[] getSnippets() {
		return snipList.toArray();
	}

	@Override
	public void removeSnippet(ISnippet snippet) {
		snipList.remove(snippet);
	    firePropertyChange(LISTREMOVE, snippet, null);
	}

	@Override
	public void resetSnippets() {
		if(snipList == null)
			snipList = new ArrayList<ISnippet>();
		snipList.clear();
		
	}
	@Override
	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		getPropertyChangeListeners().remove(listener);
	}

	@Override
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		getPropertyChangeListeners().add(listener);
	}
	
	/**
	   * Fires a property change event object to all registered listeners. These
	   * events are sent on the UI thread.
	   * 
	   * @param changeId -
	   *          Type of change: LISTADD or LISTREMOVE
	   * @param oldValue -
	   *          null for LISTADD, Removed Location for LISTREMOVE
	   * @param newValue -
	   *          null for LISTREMOVE, Added Location for LISTADD
	   */
	  void firePropertyChange(String changeId, Object oldValue,
	      Object newValue) {
	    final PropertyChangeEvent event = new PropertyChangeEvent(this,
	        changeId, oldValue, newValue);

	    //    Display.getDefault().syncExec(new Runnable() {
	    //      public void run() {
	    Object[] listeners = getPropertyChangeListeners().getListeners();
	    for (int i = 0; i < listeners.length; i++) {
	      ((IPropertyChangeListener) listeners[i]).propertyChange(event);
	    }
	    //      }
	    //    });
	  }
	  
	private ListenerList getPropertyChangeListeners() {
	    if (propertyChangeListeners == null)
	      propertyChangeListeners = new ListenerList(ListenerList.IDENTITY);
	    return propertyChangeListeners;
	  }

}
