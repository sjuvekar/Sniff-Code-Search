package com.sudeep_juvekar.sniff.views;


import java.io.File;
import java.util.Vector;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.*;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;

import com.sudeep_juvekar.sniff.dialog.SnippetDialogFrame.SnippetDialog;
import com.sudeep_juvekar.sniff.editor.EditorHandler;
import com.sudeep_juvekar.sniff.internal.Result;
import com.sudeep_juvekar.sniff.internal.Snippet;
import com.sudeep_juvekar.sniff.parser.SniffParser;
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


public class SearchPart extends ViewPart implements IDoubleClickListener {
	private static StructuredViewer viewer;
	
	private static Text searchText;
	private Label searchHelp;
	private Button search;
	private Button upload;
	
	private SelectionAdapter searchSelectionAdapter;
	private SelectionAdapter searchMouseAdapter;
	private SelectionAdapter uploadMouseAdapter;
	private Action snippetPropertyAction;

	
	/**
	 * The constructor.
	 */
	public SearchPart() {
	}

	/**
	 * A single column table to display the snippets
	 * @param composite
	 * @return
	 */
	private TableViewer singleColumnViewer(Composite parent) {
		Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
                | SWT.FULL_SELECTION | SWT.BORDER);

        TableLayout layout = new TableLayout();
        table.setLayout(layout);
        
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        
        layout.addColumnData(new ColumnWeightData(100, 100, true));
        final TableColumn tc0 = new TableColumn(table, SWT.NONE);
        tc0.setText("Code Snippets");
        tc0.setAlignment(SWT.LEFT);
        tc0.setResizable(true);
        
        return new TableViewer(table);
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
        
		GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 10;
        createSearchBox(composite);
        composite.setLayout(gridLayout);
        
        Composite tableComp = new Composite(composite, SWT.NONE);
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 10;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        tableComp.setLayoutData(data);
        
        FillLayout fillLayout = new FillLayout();
        tableComp.setLayout(fillLayout);
        
        viewer = singleColumnViewer(tableComp);
		viewer.setContentProvider(new SearchContentProvider());
		viewer.setLabelProvider(new SearchLabelProvider());
		viewer.setInput(new Result());
		
		getViewSite().setSelectionProvider(viewer);
		
		createViewAction();
		hookDoubleClickAction();
		
		viewer.addDoubleClickListener(this);
	}

	
	public Snippet getSelectionSnippet() {
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		if(sel == null) return null;
		Snippet snippet = (Snippet) sel.getFirstElement();
		return snippet;
	}
	
	public void createViewAction() {
		
		snippetPropertyAction = new Action() {
			
			public void run() {
				
				SnippetDialog snippetDialog = new SnippetDialog(viewer.getControl().getShell(), 
						"Snippet Details", 
						SWT.CLOSE|SWT.TITLE|SWT.RESIZE|SWT.APPLICATION_MODAL,
						getSelectionSnippet());
				
				if(snippetDialog.open() == Window.OK) {
					Snippet snippet = snippetDialog.getSnippet();
					if(!EditorHandler.insertSnippet(snippet))
						System.out.println("Failed to output the Snippet");
				}
			}
		};
	}
	
	/**
	   * Opens Properties dialog (also on context menu as an action)
	   */
	  private void hookDoubleClickAction() {
		  viewer.addDoubleClickListener(this);
		  viewer.setSelection(viewer.getSelection());
	  }

	  /** 
	   * Open Properties dialog and touch selection to send update to
	   * Properties view.
	   * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	   */
	  public void doubleClick(DoubleClickEvent event) {
		  snippetPropertyAction.run();
	  }
	  
	/***
	 * Create a Search Box and search button
	 */
	public void createSearchBox(Composite composite) {
		searchHelp = new Label(composite, SWT.NONE);
        searchHelp.setText("Query");
        
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        searchHelp.setLayoutData(data);
		searchText = new Text(composite, SWT.BORDER);
       
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.horizontalSpan = 5;
        data.grabExcessHorizontalSpace = true;
        searchText.setLayoutData(data);
        
     // Listen for an enter-based invocation or list selection
        searchSelectionAdapter = new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent e) {
        
            	try {
            		String query = searchText.getText();
            		SearchProgressDialog spd = new SearchProgressDialog(viewer, query);
                	spd.run(true, true, null);
            	}
            	catch (Exception e1) {
					e1.printStackTrace();
				}
            	viewer.refresh();
            }
        
            public void widgetSelected(SelectionEvent e) {
         
            	try {
            		final String query = searchText.getText();
            		SearchProgressDialog spd = new SearchProgressDialog(viewer, query);
                	spd.run(true, false, null);
            	}
            	catch (Exception e1) {
					e1.printStackTrace();
				}
            	viewer.refresh();
            }
        };
        
        searchText.addSelectionListener(searchSelectionAdapter);
        
        search = new Button(composite, SWT.BORDER);
        search.setText("Search");
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        search.setLayoutData(data);
        
        searchMouseAdapter = new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
            
        		try {
            		String query = searchText.getText();
            		SearchProgressDialog spd = new SearchProgressDialog(viewer, query);
                	spd.run(true, false, null);
            	}
            	catch (Exception e1) {
					e1.printStackTrace();
				}
            	viewer.refresh();
        	}
        	
        	public void widgetDefaultSelected(SelectionEvent e) {
            
        		try {
            		String query = searchText.getText();
            		SearchProgressDialog spd = new SearchProgressDialog(viewer, query);
                	spd.run(true, false, null);
            	}
            	catch (Exception e1) {
					e1.printStackTrace();
				}
            	viewer.refresh();
            }
        };
        search.addSelectionListener(searchMouseAdapter);
        
        upload = new Button(composite, SWT.BORDER);
        upload.setText("Upload");
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        upload.setLayoutData(data);
        
        uploadMouseAdapter = new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		DirectoryDialog dd = new DirectoryDialog(viewer.getControl().getShell(), SWT.OPEN);
        		String fileName = dd.open();
        		if(fileName != null) {
        			int sepIndex = fileName.lastIndexOf(File.separator);
        			final String fileNameFinal = fileName.substring(sepIndex+1, fileName.length());
        			try {
        				SniffProgressDialog spd = new SniffProgressDialog(viewer.getControl().getShell(), fileNameFinal);
            			spd.run(true, false, null);
        			}catch (Exception e1) {
						e1.printStackTrace();
					}
        			
        		}
        	}
        	
        	public void widgetDefaultSelected(SelectionEvent e) {
        	}
        };
        upload.addSelectionListener(uploadMouseAdapter);
	}
	
	
	public static void updateSnippets() {
		( (Result) viewer.getInput()).resetSnippets();
		String query = searchText.getText();
		Vector<String> q = Stemmer.stemming(query);
		((Result)viewer.getInput()).addSnippet(q);
		viewer.refresh();
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	public static void startSearchThread() {
		Thread t = new Thread() {
			public void run() {
				try {
					updateSnippets();
				}
				catch (Throwable th) {
				}
				return;
			}
		};
		t.start();
	}
	
	public static void startUploadThread(final SniffParser sp, final String fileNameFinal, final Viewer viewer) {
		
		Thread t = new Thread() {
			public void run() {
				try {
					sp.run(fileNameFinal);
				}
				catch (Throwable th) {
				}
				return;
			}
		};
		t.start();
	}
}