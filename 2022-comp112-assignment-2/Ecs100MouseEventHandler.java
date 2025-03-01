 

import java.util.concurrent.BlockingQueue;
import javax.swing.JOptionPane;

/** Ecs100MouseEventHandler
 The thread that handles the mouse events, sequentially
*/


public class Ecs100MouseEventHandler extends Thread{


    private BlockingQueue<Ecs100MouseEvent> queue;
    private boolean finished = false;

    /**
    */
    public Ecs100MouseEventHandler(BlockingQueue<Ecs100MouseEvent> q){
	queue = q;
	this.start();
    }


    /** keep polling the queue for events and performing them */
    public void run(){
	while (!finished){
	    try {
		Ecs100MouseEvent ev = queue.take();
		ev.controller.mousePerformed(ev.action, ev.x, ev.y);
	    } catch (InterruptedException ex) {
	    } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                                              UI.getFrame(),
                                              "An exception has been thrown\n"+ex,
                                              "Thrown Exception",
                                              JOptionPane.ERROR_MESSAGE);
            }
	}
    }

    public void finish(){
	finished = true;
	queue.clear();
    }

}
