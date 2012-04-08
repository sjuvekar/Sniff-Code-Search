package com.sudeep_juvekar.sniff.core;

import java.util.Vector;

import org.eclipse.jface.util.IPropertyChangeListener;
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

public interface IResult {

	/**
	 * Property Change Event key
	 */
	static final String LISTADD = "LISTADD";
	/**
	 * Property Change Event key
	 */
	static final String LISTREMOVE = "LISTREMOVE";

	/**
	 * Resets list to the default location content.
	 * 
	 */
	public abstract void resetSnippets();

	/**
	 * Returns the model content in the form of an array.  Intended to support the
	 * needs of the content provider getElements() method.
	 * 
	 * @return Object[] - an <code>ISnippet</code> array.
	 */
	public abstract Object[] getSnippets();

	/**
	 * Creates an ISnippet object from the given Query
	 * @param Query
	 * @return ISnippet, the object that was created
	 */
	public abstract ISnippet addSnippet(Vector<String> query);

	/**
	 * The passed <code>ISnippet</code> reference is removed from the model.
	 * 
	 * @param snippet
	 */
	public abstract void removeSnippet(ISnippet snippet);

	/**
	 * Add a property change listener.
	 * 
	 * This simple change listener is used to identify changes to <code>IResult</code> content
	 * (adds/removes). It does not report attribute changes to ModelElements.
	 * 
	 * @param listener
	 */
	public abstract void addPropertyChangeListener(
			IPropertyChangeListener listener);

	/** 
	 * Removes a property change listener.
	 * @param listener
	 */
	public abstract void removePropertyChangeListener(
			IPropertyChangeListener listener);
}

