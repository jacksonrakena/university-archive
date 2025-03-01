 

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;

class Ecs100KeyListener extends KeyAdapter{
    private UIKeyListener controller;

    public Ecs100KeyListener(UIKeyListener controller) {
	this.controller = controller;
    }

    public void keyPressed(final KeyEvent e) {
	//	System.out.println("key pressed: "+ e); //DEBUG
	if (controller != null) {
	    final String key = KeyEvent.getKeyText(e.getKeyCode());
	    
	    if (System.getProperty("os.name").contains("Mac")) {
	    	switch(key) {
	    	case "→":
                    new Thread(() -> {
                            try {
                                controller.keyPerformed("Right");
                            }catch (Exception ex){
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(
                                                              UI.getFrame(),
                                                              "An exception has been thrown\n"+ex,
                                                              "Thrown Exception",
                                                              JOptionPane.ERROR_MESSAGE);
                            }}).start();
                    return;
	    	case "�?":
                    new Thread(() -> {
                            try {
                                controller.keyPerformed("Left");
                            }catch (Exception ex){
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(
                                                              UI.getFrame(),
                                                              "An exception has been thrown\n"+ex,
                                                              "Thrown Exception",
                                                              JOptionPane.ERROR_MESSAGE);
                            }}).start();
                    return;
	    	case "↑":
                    new Thread(() -> {
                            try {
                                controller.keyPerformed("Up");
                            }catch (Exception ex){
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(
                                                              UI.getFrame(),
                                                              "An exception has been thrown\n"+ex,
                                                              "Thrown Exception",
                                                              JOptionPane.ERROR_MESSAGE);
                            }}).start();
                    return;
	    	case "↓":
                    new Thread(() -> {
                            try {
                                controller.keyPerformed("Down");
                            }catch (Exception ex){
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(
                                                              UI.getFrame(),
                                                              "An exception has been thrown\n"+ex,
                                                              "Thrown Exception",
                                                              JOptionPane.ERROR_MESSAGE);
                            }}).start();
                    return;
	    	}

	    }
	    
	    if (key.length()==1){
                     new Thread(() -> {
                            try {
                                controller.keyPerformed(Character.toString(e.getKeyChar()));
                            }catch (Exception ex){
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(
                                                              UI.getFrame(),
                                                              "An exception has been thrown\n"+ex,
                                                              "Thrown Exception",
                                                              JOptionPane.ERROR_MESSAGE);
                            }}).start();
	    }
	    else if (!key.equals("Shift")&&
		     !key.equals("Ctrl")&&
		     !key.equals("Alt"))
                     new Thread(() -> {
                            try {
                                controller.keyPerformed(key);
                            }catch (Exception ex){
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(
                                                              UI.getFrame(),
                                                              "An exception has been thrown\n"+ex,
                                                              "Thrown Exception",
                                                              JOptionPane.ERROR_MESSAGE);
                            }}).start();
	}
    }
}
