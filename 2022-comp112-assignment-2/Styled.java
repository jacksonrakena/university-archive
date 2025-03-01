 

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import javax.swing.text.StyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.BadLocationException;

  /** The Styled class provides styled output (bold, italic, or underlined) 
   *  by which a program can print out text into a styled window, separate from the 
   *  text area.<P>
   *  The styled window does not allow input.<P>
    * The methods of the Styled class are all static, so that the <I>Styled.print</I> and
    * <I>Styled.setBold(true)</I> methods can be called from any point in the program.<P>
    * The program must have created an UI window in order to use the styled facility, but
    * there is no need to explicitly create a styled window or initialise the styled facility
    * from inside the program.
    */

public class Styled {

    private static JFrame styledWin = null;
    private static JPanel noWrapPanel = null;
    private static JTextPane textPane = null;
    private static JScrollPane scrollPane = null;

    private static StyledDocument document = null;
    private static StyleContext context;
    private static Style style;

    private static int DEFAULT_WIDTH = 400;
    private static int DEFAULT_HEIGHT = 550;

    static void initialise(JMenuBar menuBar) {
        if (styledWin==null){
            styledWin = new JFrame("Styled text output");

            styledWin.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            styledWin.setLayout(new BorderLayout());

            textPane = new JTextPane();
            noWrapPanel = new JPanel( new BorderLayout() );
            noWrapPanel.add( textPane );

            scrollPane = new JScrollPane( noWrapPanel );
            //scrollPane.setViewportView(textPane);

            styledWin.add(scrollPane, BorderLayout.CENTER);

            document = textPane.getStyledDocument();
            context = new StyleContext();
            style = context.getStyle(StyleContext.DEFAULT_STYLE); 
        }
    }

    static void dispose() {
	if (styledWin != null) {
	    styledWin.dispose();
	    styledWin = null;
	}
    }

    /*-----------------------------------------------------------------*/

    // Print a value (any type) into the styled window, if the styled window is
    // currently showing.  Does nothing if the styled window is hidden.

    /** Print a String into the styled window, if the styled window is
     * currently showing.  Does nothing if the styled window is hidden.
     */
    public static void print(String s) {
	UI.checkInitialised();
	if (styledWin.isShowing()) {
            try { document.insertString(document.getLength(), s, style); }
            catch (BadLocationException e) { System.out.println(e); }
            //textPane.setVisible(true);  // Don't think this is necessary
        }
    }

    /** Print a boolean into the styled window, if the styled window is
     * currently showing.  Does nothing if the styled window is hidden.
     */
    public static void print(boolean b) {
	print(String.valueOf(b));
    }

    /** Print a character into the styled window, if the styled window is
     * currently showing.  Does nothing if the styled window is hidden.
     */
    public static void print(char c) {
	print(String.valueOf(c));
    }

    /** Print a number into the styled window, if the styled window is
     * currently showing.  Does nothing if the styled window is hidden.
     */
    public static void print(double d) {
	print(String.valueOf(d));
    }

    /** Print an integer into the styled window, if the styled window is
     * currently showing.  Does nothing if the styled window is hidden.
     */
    public static void print(int i) {
	print(String.valueOf(i));
    }

    /** Print an object into the styled window, if the styled window is
     * currently showing.  Does nothing if the styled window is hidden.
     * Note, it calls the toString() method on the object, and prints the result.
     * This may or may not be useful.
     */
    public static void print(Object o) {
	print(String.valueOf(o));
    }

    /** Print into the styled window, if the styled window is
     *  currently showing.  Does nothing if the styled window is hidden.
     *  The <TT>printf()</TT> method requires a format string (which
     *  will contain "holes" specified with %'s) and additional arguments
     *  which will be placed "in the holes", using the specified formatting.
     */
    public static void printf(String format, Object... args){ 
        print(String.format(format, args));
    }

    /** Start a new line in the styled window, if the styled window is
     * currently showing.  Does nothing if the styled window is hidden.
     */
    public static void println() {
	print("\n");
    }

    /** Print a String followed by a new line in the styled window,
     * if the styled window is currently showing.
     * Does nothing if the styled window is hidden.
     */
    public static void println(String s) {
	print(s+"\n");
    }

    /** Print a boolean followed by a new line in the styled window,
     * if the styled window is currently showing.
     * Does nothing if the styled window is hidden.
     */
    public static void println(boolean b) {
	print(b+"\n");
    }

    /** Print a character followed by a new line in the styled window,
     * if the styled window is currently showing.
     * Does nothing if the styled window is hidden.
     */
    public static void println(char c) {
	print(c+"\n");
    }

    /** Print a number followed by a new line in the styled window,
     * if the styled window is currently showing.
     * Does nothing if the styled window is hidden.
     */
    public static void println(double d) {
	print(d+"\n"); 
    }

    /** Print an integer followed by a new line in the styled window,
     * if the styled window is currently showing.
     * Does nothing if the styled window is hidden.
     */
    public static void println(int i) {
	print(i+"\n"); 
    }

    /** Print an object followed by a new line into the styled window, if the styled window is
     * currently showing.  Does nothing if the styled window is hidden.
     * Note, it calls the toString() method on the object, and prints the result.
     * This may or may not be useful.
     */
    public static void println(Object o) {
	print(o+"\n"); 
    }


    /** Clear all the text in the Styled window. */
    public static void clear(){
	UI.checkInitialised();
	if (styledWin.isShowing()) {
            try { document.remove(0, document.getLength()); }
            catch(BadLocationException e) { System.out.println(e); }
            //textPane.setVisible(true);  // Don't think this is necessary
        }
    }
        
        
    /* ==== FORMATTING Methods ===== */

    // Format the styled window, if the styled window is currently showing.
    // Does nothing if the styled window is hidden.

    /** Reset the italic, bold, and underlined attributes so that any text printed on the Styled
     * window after this will be plain.
     */
    public static void setPlain() {
	UI.checkInitialised();
	if (styledWin.isShowing()) {
            StyleConstants.setItalic(style, false);
            StyleConstants.setBold(style, false);
            StyleConstants.setUnderline(style, false);
        }
    }
    
    /** Set the italic attribute on the Styled window. 
     *  If the attribute is true (the default is false), then any text printed on the Styled window
     *  after this will be italic.
     */
    public static void setItalic(boolean b){
	UI.checkInitialised();
	if (styledWin.isShowing()) {
            StyleConstants.setItalic(style, b);
        }
    }
    
    /** Set the bold attribute on the Styled window. 
     *  If the attribute is true (the default is false), then any text printed on the Styled window
     *  after this will be bold.
     */
    public static void setBold(boolean b){
	UI.checkInitialised();
	if (styledWin.isShowing()) {
            StyleConstants.setBold(style, b);
        }
    }
    
    /** Set the underline attribute on the Styled window. 
     *  If the attribute is true (the default is false), then any text printed on the Styled window
     *  after this will be underlined.
     */
    public static void setUnderline(boolean b){
	UI.checkInitialised();
	if (styledWin.isShowing()) {
            StyleConstants.setUnderline(style, b);
        }
    }

    /** Return the Style object underlying the Styled window.
     *  Only needed if you want to do more complicated styles 
     *  on the styled window than are provided by methods in this
     *  Styled class.
     */
    public static Style getStyle(){
	UI.checkInitialised();
        return style;
    }

    /** Set whether to wrap the text. The default is unwrapped text */
    public static void wrapText(boolean b){
        if (b){
            scrollPane.setViewportView(textPane);
            textPane.setVisible(true);
        }
        else {
            scrollPane.setViewportView(noWrapPanel);
            noWrapPanel.add(textPane);
        }
    }

    /*-----------------------------------------------------------------*/


    /** Make the styled window visible.
     */
    public static void setVisible() {
	setVisible(true);
    }

    /** Hide the styled window.
     */
    public static void hide() {
	setVisible(false);
    }

    /** Make the styled window visible or hidden.<BR>
     * If the (boolean) argument is true, the styled window will be visible and future
     * print's and println's will be shown.<BR>
     * If the argument is false, the styled window will be hidden.
     */
    public static void setVisible(boolean v) {
	UI.checkInitialised();
	styledWin.setVisible(v);
    }


    /** Return true if and only if the styled window is currently visible.
     */
    public static boolean isVisible() {
        UI.checkInitialised();
	return styledWin.isShowing();
    }

    /*-----------------------------------------------------------------*/

}
