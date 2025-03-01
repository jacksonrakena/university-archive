 

/** Interface for programs that listen to Sliders on the UI window.
*/

public interface UISliderListener {

    /** Respond to slider events.
     * The arguments is the new value the user just entered.
     */
  public void sliderPerformed(double value);
}
