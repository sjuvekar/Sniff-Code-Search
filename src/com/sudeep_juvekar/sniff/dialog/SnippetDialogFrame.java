package com.sudeep_juvekar.sniff.dialog;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.sudeep_juvekar.sniff.internal.Snippet;
import com.sudeep_juvekar.sniff.internal.Statement;
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

public class SnippetDialogFrame extends Composite {
	
	public static class SnippetDialog extends Dialog {

		private SnippetDialogFrame sFrame;
		private Snippet sSnippet;
		private final String sTitle;
		
		
		public SnippetDialog(Shell parentShell) {
			super(parentShell);
			sTitle = "Snippet Details";	
		}
		
		public SnippetDialog(Shell shell, String title, int style, Snippet snippet) {
			super(shell);
			sTitle = title;
			setShellStyle(style);
			sSnippet = snippet;
		}
		
		public SnippetDialog(Shell shell, String title, int style) {
			super(shell);
			sTitle = title;
			setShellStyle(style);
		}
		
		protected void createButtonsForButtonBar(Composite parent) {
			createButton(parent, IDialogConstants.OK_ID, "Output Code", true);
			createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		}
		
		protected Control createDialogArea(Composite parent) {
			Composite container=(Composite) super.createDialogArea(parent);
			sFrame = new SnippetDialogFrame(parent, sSnippet);
			return container;
		}
		
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText(sTitle);
			//ImageCache.getInstance().addImageUser(newShell);
		}
		
		public Snippet getSnippet() {
			return sSnippet;
		}
	}

	public static class BoundedComposite extends Composite 
	{
		private int minPrefWidth=SWT.DEFAULT;
		private int minPrefHeight=SWT.DEFAULT;
		private int maxPrefWidth=SWT.DEFAULT;
		private int maxPrefHeight=SWT.DEFAULT;
		
		public BoundedComposite(Composite parent, int style) {
			super(parent, style);
		}
		public void setMaxPrefWidth(int width) {
			this.maxPrefWidth=width;
		}
		public void setMaxPrefHeight(int height) {
			this.maxPrefHeight=height;
		}
		public void setMinPrefWidth(int width) {
			this.minPrefWidth=width;
		}
		public void setMinPrefHeight(int height) {
			this.minPrefHeight=height;
		}
		
		public Point computeSize(int wHint, int hHint, boolean changed) {
			Point dim=super.computeSize(wHint, hHint, changed);
			boolean redo=false;
			if(wHint==SWT.DEFAULT && maxPrefWidth!=SWT.DEFAULT && dim.x>maxPrefWidth) {
				wHint=maxPrefWidth;
				redo=true;
			}
			if(hHint==SWT.DEFAULT && maxPrefHeight!=SWT.DEFAULT && dim.y>maxPrefHeight) {
				hHint=maxPrefHeight;
				redo=true;
			}
			if(wHint==SWT.DEFAULT && minPrefWidth!=SWT.DEFAULT && dim.x<minPrefWidth) {
				wHint=minPrefWidth;
				redo=true;
			}
			if(hHint==SWT.DEFAULT && minPrefHeight!=SWT.DEFAULT && dim.y<minPrefHeight) {
				hHint=minPrefHeight;
				redo=true;
			}
			if(redo) dim=super.computeSize(wHint, hHint, false);
			return dim;
		}
	}
	private Button btnInline;
	private Button btnFunc;
	private Text txtFuncName;
	private final Snippet sSnippet;
	
	private SourceViewer sourceViewer;
	
	private SelectionAdapter fListener;
	
	public SnippetDialogFrame(Composite parent, Snippet snippet) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1,false));
		((GridLayout)getLayout()).marginHeight=2;
		setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,true));
		
		sSnippet = snippet;
		
		fListener = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
                
            }
        
            public void widgetSelected(SelectionEvent e) {
            	
            }
		};
		
		//makeCodeGenOptionsFrame(parent);
		makeSniffFrame(parent);
		makePreviewFrame(parent);
	}

	private Composite makeCodeGenOptionsFrame(Composite parent) {
		Group frame=new Group(parent,SWT.NONE);
		frame.setLayout(new GridLayout(2,false));
		frame.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		frame.setText("Code generation options");
		
		btnInline=new Button(frame,SWT.RADIO);
		btnInline.setText("Insert code inline");
		GridData gd=new GridData();
		gd.horizontalSpan=2;
		btnInline.setLayoutData(gd);	
		btnInline.addSelectionListener(fListener);
		
		btnFunc=new Button(frame,SWT.RADIO);
		btnFunc.setText("Generate helper method");
		btnFunc.setLayoutData(new GridData());
		btnFunc.addSelectionListener(fListener);
		txtFuncName=new Text(frame,SWT.SINGLE|SWT.BORDER);
		txtFuncName.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		//txtFuncName.addModifyListener(fListener);
		
		return frame;
	}
		
	
	
	private void addStatementToSniffFrame(final Statement s, Composite frame, int stateId, Hashtable<String, String> methodHash) {
		
		String returnType = s.getReturnType();
		String returnTypeVariable = Snippet.constructReturnTypeVariable(returnType, stateId);
		s.setReturnTypeVariable(returnTypeVariable);
		String declaringClass = s.getDeclaringClass();
		String methodName = s.getMethodName();
		
		Composite statementPanel = new Composite(frame, SWT.NONE);
		statementPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		statementPanel.setLayout(new RowLayout());
		
		if(!returnType.equals("void")) {	
			final SourceViewer returnTypeText = new SourceViewer(statementPanel, null, SWT.SINGLE);
			configureSourceViewer(returnTypeText, statementPanel, returnType + " " + returnTypeVariable, false);
			returnTypeText.addTextListener(
					new ITextListener() {

						@Override
						public void textChanged(TextEvent event) {
							String temp = returnTypeText.getDocument().get();
							StringTokenizer sr = new StringTokenizer(temp);
							if(sr.hasMoreTokens())
								s.setReturnType(sr.nextToken());
							if(sr.hasMoreTokens())
								s.setReturnTypeVariable(sr.nextToken());
							
							sourceViewer.getDocument().set(sSnippet.returnCodeToView());
						}
						
					});
			
			Label equalLabel = new Label(statementPanel, SWT.NONE);
			equalLabel.setText(" = ");
			equalLabel.setLayoutData(new RowData());
		}
		
		if(declaringClass.equals(methodName)) {
			Label state = new Label(statementPanel, SWT.NONE);
			state.setText("new ");
			state.setLayoutData(new RowData());
		}
		else {
			final SourceViewer declaringClassText = new SourceViewer(statementPanel, null, SWT.SINGLE);
			String declaringClassPlaceHolder = methodHash.containsKey(declaringClass)?methodHash.get(declaringClass):declaringClass;
			s.setDeclaringClassPlaceHolder(declaringClassPlaceHolder);
			configureSourceViewer(declaringClassText, statementPanel, declaringClassPlaceHolder, false);
			declaringClassText.addTextListener(
				new ITextListener() {
					
					@Override
					public void textChanged(TextEvent event) {
						s.setDeclaringClassPlaceHolder(declaringClassText.getDocument().get());
						sourceViewer.getDocument().set(sSnippet.returnCodeToView());
					}
					
				});
		}
		
		Label state = new Label(statementPanel, SWT.NONE);
		if(declaringClass.equals(methodName))
			state.setText(methodName+"();");
		else
			state.setText(" . " + methodName+"();");
		state.setLayoutData(new RowData());
		
		//Add everything to methodHash
		methodHash.put(returnType, returnTypeVariable);
	}
	
	private Composite makeSniffFrame(Composite parent) {
		Hashtable<String, String> methodHash = new Hashtable<String, String>();
		Group frame = new Group(parent, SWT.NONE);
		frame.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		frame.setLayout(new GridLayout(1, false));
		frame.setText("Code Snippet");
		
		ArrayList<Statement> statements = sSnippet.getStatements();
		
		for(int i=0; i<statements.size(); i++) {
			Statement s = statements.get(i);
			addStatementToSniffFrame(s, frame, i, methodHash);
		}
		
		return frame;
	}
	
	private Composite makePreviewFrame(Composite parent) {
		Group frame=new Group(parent,SWT.NONE);
		frame.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		frame.setLayout(new GridLayout(1, false));
		frame.setText("Preview");
		int maxPerfHight = 160; int maxPerfWidth = 560;
		BoundedComposite panel=new BoundedComposite(frame,SWT.NONE);
		panel.setMaxPrefHeight(maxPerfHight);
		panel.setMinPrefHeight(maxPerfHight);
		panel.setMaxPrefWidth(maxPerfWidth);
		panel.setMinPrefWidth(maxPerfWidth);
		
		panel.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		panel.setLayout(new GridLayout(1, false));
		
		sourceViewer=new SourceViewer(panel,null,SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL);
		configureSourceViewer(sourceViewer, panel, sSnippet.returnCodeToView(), true);
		
		sourceViewer.addTextListener(
				new ITextListener() {

					@Override
					public void textChanged(TextEvent event) {
						// TODO Auto-generated method stub
						sSnippet.setCode(sourceViewer.getTextWidget().getText());
					}
					
				});
		return frame;
	}
	
	private static void configureSourceViewer(SourceViewer sourceViewer, Composite panel, String data, boolean layout) {
		
		IColorManager colorMgr=JavaUI.getColorManager();
		
		IPreferenceStore store=new ScopedPreferenceStore(new InstanceScope(),"org.eclipse.jdt.ui");
		sourceViewer.configure(new JavaSourceViewerConfiguration(colorMgr,store,null,null));
		sourceViewer.getTextWidget().setFont(JFaceResources.getFont(PreferenceConstants.EDITOR_TEXT_FONT));
		if(!EditorsUI.getPreferenceStore().getBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT)) {
			Color bg=new Color(Display.getCurrent(),PreferenceConverter.getColor(EditorsUI.getPreferenceStore(), AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND));
			sourceViewer.getTextWidget().setBackground(bg);

		}
		
		if(layout)
			sourceViewer.getControl().setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		else
			sourceViewer.getControl().setLayoutData(new RowData());
		
		sourceViewer.setDocument(new Document());
		sourceViewer.getDocument().set(data);
	}
};
