 

import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Dimension;

import javax.swing.JOptionPane;

class Ecs100Slider extends JSlider {
    private UISliderListener controller;

    public Ecs100Slider(int min, int max, UISliderListener ctrl) {
        this(min, max, (min+max)/2, ctrl);
    }

    public Ecs100Slider(int min, int max, int initial, UISliderListener ctrl) {
        super(min, max, initial);
        controller = ctrl;
	setMajorTickSpacing((max-min)/2);
	setPaintLabels(true);
	setPreferredSize(new Dimension(150,35));
        addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e) {
                    if (!getValueIsAdjusting()) {
                        new Thread(new Runnable(){public void run(){
                            try{
                                controller.sliderPerformed(getValue());
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
