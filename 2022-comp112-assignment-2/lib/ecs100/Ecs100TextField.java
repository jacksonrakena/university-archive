 

// Change to JTextField.
// Needs to then change the TextListener to an ActionListener

import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import javax.swing.JOptionPane;

class Ecs100TextField extends JTextField {
    private UITextFieldListener controller;
    private boolean newEntry = false;
    private boolean redrawing = false;

    public Ecs100TextField(int cols, UITextFieldListener contrl) {
	super(cols);
	//setBackground(Color.white);
	this.controller = contrl;
	getDocument().addDocumentListener(new DocumentListener() {
		public void changedUpdate(DocumentEvent e){}
		public void insertUpdate(DocumentEvent e){newEntry = true;}
		public void removeUpdate(DocumentEvent e){newEntry = true;}
	    });
	addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    newEntry = false;
		    if (controller != null) {
			//Trace.printf("JTextField %s: enter event\n", name);
                        new Thread(new Runnable(){public void run(){
                            try{
                                controller.textFieldPerformed(getText());
                            }catch (Exception ex){
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(
                                                              UI.getFrame(),
                                                              "An exception has been thrown\n"+ex,
                                                              "Thrown Exception",
                                                              JOptionPane.ERROR_MESSAGE);
                            }
                        }}).start();
                    }
                }});
	addFocusListener(new FocusAdapter() {
		public void focusLost(FocusEvent e) {
		    if (!e.isTemporary() && newEntry && controller != null) {
			newEntry = false;
			//Trace.printf("JTextField %s: focus lost event\n", name);
                        new Thread(new Runnable(){public void run(){
                            try{
                                controller.textFieldPerformed(getText());
                                }catch (Exception ex){
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(
                                                              UI.getFrame(),
                                                              "An exception has been thrown\n"+ex,
                                                              "Thrown Exception",
                                                              JOptionPane.ERROR_MESSAGE);
                                }
                        }}).start();
		    }
		}});

    }

}
