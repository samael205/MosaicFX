package tool.clients.dialogs;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MultiplicityDialog extends Dialog<ButtonType>{

	private TextField txtMin;
	private TextField txtMax;
	private CheckBox boxOrdered;
	private CheckBox boxNavigable;
	
	private int min, max;
	private boolean ordered, navigable;

//	private transient boolean blocked;
//	private transient boolean finished;
//	private transient boolean okPressed;
	String s;
	
//	public static void main(String[] args) {
////		System.err.println(show(1,2,true,false));
//		final MultiplicityDialog m = new MultiplicityDialog(null, 1,2,true,false);
//		System.err.println(m.show(100, 100));
//	}
	
	private transient static String result;

	public static String show(final int min, final int max, final boolean ordered, final boolean navigable) {
		CountDownLatch l = new CountDownLatch(1);
		// final MultiplicityDialog THIS = this;
		// String result = null;
		Platform.runLater(() -> {

			final MultiplicityDialog m = new MultiplicityDialog(null, min, max, ordered, navigable);
			result = m == null ? "FAIL" : m.show(100, 100);

			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return result;

	}
	
	public String show(int x, int y) {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
//		    EXAMPLE
//        searchField = new TextField();
//        searchField.setText("Search here...");
//        grid.add(searchField, 0, 0);
//		searchField.setOnKeyPressed(new MySearchListener_Pressed());
//		searchField.setOnKeyReleased(new MySearchListener_Released());
		
		createDialogArea(grid);

		
		getDialogPane().setContent(grid);
		
        setValues(min+"", max+"", ordered);
//		searchField.requestFocus();

		Optional<ButtonType> result = showAndWait();
		return getResult(result.get() == ButtonType.OK);

//        return result==null?"":result;
        
//		return "not yet implemented";
	}
		
private String getResult(boolean ok) {
//		if(ok) {			
			try{
				int a = ok?Integer.parseInt(txtMin.getText()):min;
				if(ok) {
				if(!"*".equals(txtMax.getText())) {
					int b = Integer.parseInt(txtMax.getText());
					if(b < 1) throw new IllegalArgumentException();
					if(b < a) throw new IllegalArgumentException();
				} else {}}
				if(ok){
					s = txtMin.getText() + ".." + txtMax.getText() + (boxOrdered.isSelected()?"$":"");
				} else {
					s = min + ".." + (max<0?"*":(max+"")) + (ordered?"$":"");
				}
				if(s.endsWith("..1$")) s = s.substring(0, s.length()-1);
				if("1..1".equals(s)) s = "!";
				if("0..1".equals(s)) s = "1";
				return s;
			} catch (Exception e) {
//				s = null;
				return null;
			}
//		}
		
	}

//	private String showLocal() {
//		new Thread(new Runnable() {
//			public void run() {
//				Display.getDefault().syncExec(new Runnable() {
//					public void run() {
//						open();
//					}
//				});
//			}
//		}).start();
//		while(!finished) {
//			blocked = true;
////			System.err.println("Wait for Button");
//			while(blocked) {
//				try {
//					Thread.sleep(50);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//			if(okPressed) {
////				System.err.println("OK");
//				new Thread(new Runnable() {
//					public void run() {
//						Display.getDefault().syncExec(new Runnable() {
//							public void run() {
//								try{
//									int a = Integer.parseInt(txtMin.getText());
//									if(!"*".equals(txtMax.getText())) {
//										int b = Integer.parseInt(txtMax.getText());
//										if(b < 1) throw new IllegalArgumentException();
//										if(b < a) throw new IllegalArgumentException();
//									}
//									s = txtMin.getText() + ".." + txtMax.getText() + (boxOrdered.getSelection()?"$":"");
//									if(s.endsWith("1$")) s = s.substring(0, s.length()-1);
//									if("1..1".equals(s)) s = "!";
//									if("0..1".equals(s)) s = "1";								
//								} catch (Exception e) {
//									s = null;
//								};
//								blocked = false;							
//							}
//						});
//					}
//				}).start();
//				blocked = true;
//				while(blocked) {
//					try {
//						Thread.sleep(50);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//				if(s != null) {
//					dispose();
//					finished = true;
//					return s;
//				}
//			} else {
////				System.err.println("Cancel");
//				finished = true;
//				dispose();
//				return null;
//			}
//		}
//		return null;
//	}

//	private void dispose() {
//		new Thread(new Runnable() {
//			public void run() {
//				Display.getDefault().syncExec(new Runnable() {
//					public void run() {
//						dispose2();
//					}
//				});
//			}
//		}).start();
//	}

//	private void dispose2() {
//		super.okPressed();		
//	}

	protected MultiplicityDialog(Stage owner, int min, int max, boolean ordered, boolean navigable) { 
		super(); 
		initModality(Modality.WINDOW_MODAL);
		initOwner(owner);
		
		this.min = min;
		this.max = max;
		this.ordered = ordered;
		this.navigable = navigable;
		
        getDialogPane().getButtonTypes().add(ButtonType.OK);
        javafx.scene.Node okButton = getDialogPane().lookupButton(ButtonType.OK);
        okButton.managedProperty().bind(okButton.visibleProperty());
        
        getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        javafx.scene.Node cancelButton = getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.managedProperty().bind(cancelButton.visibleProperty());
//        closeButton.setVisible(false);
        
	}
    
    protected void createDialogArea(GridPane grid) {
//	    Composite area = (Composite) super.createDialogArea(parent);
//	    Composite container = new Composite(area, SWT.NONE);
//	    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//	    GridLayout layout = new GridLayout(4, false);
//	    container.setLayout(layout);
    	
    	final ToggleGroup group = new ToggleGroup();

    	RadioButton defaultR1 = new RadioButton("0..1"); 
//	    defaultR1.setText("0..1"); 
//	    GridData gd = new GridData();
//	    gd.horizontalSpan=2;
//	    defaultR1.setLayoutData(gd);
	    GridPane.setColumnSpan(defaultR1, 2);
	    grid.add(defaultR1, 0, 0);
	    defaultR1.setToggleGroup(group);
	    
        defaultR1.selectedProperty().addListener((event, oldValue, newValue) -> {
        	if(newValue && !oldValue) { setValues("0","1",false); } });
        //	    defaultR1.addSelectionListener(new SelectionListener() {
//			@Override public void widgetSelected(SelectionEvent arg0) {setValues("0","1",false);}
//			@Override public void widgetDefaultSelected(SelectionEvent arg0) {setValues("0","1",false);}
//		});
	    
	    Label label1 = new Label("Zero or one"); 
//	    label1.setText("Zero or one");
//	    gd = new GridData();
//	    gd.horizontalSpan=2;
//	    label1.setLayoutData(gd);
	    GridPane.setColumnSpan(label1, 2);
	    grid.add(label1, 2, 0);
	    
	    RadioButton defaultR2 = new RadioButton("1..1"); 
//	    defaultR2.setText("1..1"); 
//	    gd = new GridData();
//	    gd.horizontalSpan=2;
//	    defaultR2.setLayoutData(gd);
	    GridPane.setColumnSpan(defaultR2, 2);
	    grid.add(defaultR2, 0, 1);
	    defaultR2.setToggleGroup(group);
	    
        defaultR2.selectedProperty().addListener((event, oldValue, newValue) -> {
        	if(newValue && !oldValue) { setValues("1","1",false); } });
        //	    defaultR2.addSelectionListener(new SelectionListener() {
//			@Override public void widgetSelected(SelectionEvent arg0) {setValues("1","1",false);}
//			@Override public void widgetDefaultSelected(SelectionEvent arg0) {setValues("1","1",false);}
//		});
	    
	    Label label2 = new Label("Exactly one"); 
//	    label2.setText("Exactly one"); 
//	    gd = new GridData();
//	    gd.horizontalSpan=2;
//	    label2.setLayoutData(gd);
	    GridPane.setColumnSpan(label2, 2);
	    grid.add(label2, 2, 1);
	    
	    RadioButton defaultR3 = new RadioButton("0..*"); 
//	    defaultR3.setText("0..*"); 
//	    gd = new GridData();
//	    gd.horizontalSpan=2;
//	    defaultR3.setLayoutData(gd);
	    GridPane.setColumnSpan(defaultR3, 2);
	    grid.add(defaultR3, 0, 2);
	    defaultR3.setToggleGroup(group);
	    
        defaultR3.selectedProperty().addListener((event, oldValue, newValue) -> {
        	if(newValue && !oldValue) { setValues("0","*",false); } });
        //	    defaultR3.addSelectionListener(new SelectionListener() {
//			@Override public void widgetSelected(SelectionEvent arg0) {setValues("0","*",false);}
//			@Override public void widgetDefaultSelected(SelectionEvent arg0) {setValues("0","*",false);}
//		});
	    
	    Label label3 = new Label("Any number including zero"); 
//	    label3.setText("Any number including zero"); 
//	    gd = new GridData();
//	    gd.horizontalSpan=2;
//	    label3.setLayoutData(gd);
	    GridPane.setColumnSpan(label3, 2);
	    grid.add(label3, 2, 2);
	    
	    RadioButton defaultR4 = new RadioButton("1..*"); 
//	    defaultR4.setText("1..*"); 
//	    gd = new GridData();
//	    gd.horizontalSpan=2;
//	    defaultR4.setLayoutData(gd);
	    GridPane.setColumnSpan(defaultR4, 2);
	    grid.add(defaultR4, 0, 3);
	    defaultR4.setToggleGroup(group);

        defaultR4.selectedProperty().addListener((event, oldValue, newValue) -> {
        	if(newValue && !oldValue) { setValues("1","*",false); } });
//	    defaultR4.addSelectionListener(new SelectionListener() {
//			@Override public void widgetSelected(SelectionEvent arg0) {setValues("1","*",false);}
//			@Override public void widgetDefaultSelected(SelectionEvent arg0) {setValues("1","*",false);}
//		});
	    
	    Label label4 = new Label("Any number, at least one"); 
	    label4.setText("Any number, at least one"); 
//	    gd = new GridData();
//	    gd.horizontalSpan=2;
//	    label4.setLayoutData(gd);
	    GridPane.setColumnSpan(label4, 2);
	    grid.add(label4, 2, 3);
	    
	    
	    RadioButton defaultR5 = new RadioButton(""); 
	    grid.add(defaultR5, 0, 4);
	    defaultR5.setToggleGroup(group);
//	    defaultR5.setText("");
	    txtMin = new TextField(min+"");
	    grid.add(txtMin, 1, 4);
//	    txtMin.setText(min+"");
	    Label dummy1 = new Label(".."); 
	    grid.add(dummy1, 2, 4);
//	    dummy1.setText("..");
        txtMax = new TextField(max<0?"*":(max+""));
        grid.add(txtMax, 3, 4);
//	    txtMax.setText(max<0?"*":(max+""));
	    
//	    defaultR5.setSelection(true);
        defaultR5.selectedProperty().addListener((event, oldValue, newValue) -> {
        	if(newValue && !oldValue) { enableUserDef(); } });
//	    defaultR5.addSelectionListener(new SelectionListener() {
//			@Override public void widgetSelected(SelectionEvent arg0) {enableUserDef();}
//			@Override public void widgetDefaultSelected(SelectionEvent arg0) {enableUserDef();}
//		});
	    

	    boxOrdered = new CheckBox("Ordered"); 
	    boxOrdered.setSelected(ordered);
	    boxNavigable = new CheckBox("Navigable"); 
	    boxNavigable.setSelected(navigable);
	    
//	    GridData textFieldGridData = new GridData();
//	    textFieldGridData.horizontalSpan = 4;
//	    boxOrdered.setLayoutData(textFieldGridData); 

	    GridPane.setColumnSpan(boxOrdered, 4);
        grid.add(boxOrdered, 0, 5);
	    
//	    textFieldGridData = new GridData();
//	    textFieldGridData.horizontalSpan = 4;
//	    boxNavigable.setLayoutData(textFieldGridData);

	    GridPane.setColumnSpan(boxNavigable, 4);
        grid.add(boxNavigable, 0, 6);
            

//      return container;
    }

    // overriding this methods allows you to set the
    // title of the custom dialog
//    @Override
//    protected void configureShell(Shell newShell) {
//      super.configureShell(newShell);
//      newShell.setText("Selection dialog");
//    }
//    
    private void setValues(String min, String max, boolean ordered) {
    	txtMin.setText(min);
    	txtMax.setText(max);
    	boxOrdered.setSelected(ordered);
    	txtMin.setEditable(false);
    	txtMax.setEditable(false);
    	boxOrdered.setIndeterminate(true);
    } 
    
    private void enableUserDef() {
    	txtMin.setEditable(true);
    	txtMax.setEditable(true);
    	boxOrdered.setIndeterminate(false);
    }
//    
//	@Override
//	protected void okPressed() {
//		okPressed = true;
//		blocked = false;
////		super.okPressed();
//	}
//
//	@Override
//	protected void cancelPressed() {
//		okPressed = false;
//		blocked = false;
////		super.cancelPressed();
//	}

}
