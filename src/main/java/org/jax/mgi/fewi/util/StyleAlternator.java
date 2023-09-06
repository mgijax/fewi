package org.jax.mgi.fewi.util;


/**
 * provides an easy mechanism to alternate styles
 */
public class StyleAlternator
{
  //--------------//
  // class fields
  //--------------//

  // internal list of the two strings to cycle
  private String[] styles = new String[2];

  // index into 'styles' array;  used to determine which style to return
  private int styleIndex = 0;


  //---------------------//
  // public constructors
  //---------------------//

  /** instantiates a new 'StyleAlternator' to cycle through
   * @param str1
   * @param str2
   */
  public StyleAlternator(String str1, String str2)
  {

      styles[0] = str1;
      styles[1] = str2;
  }

  //------------------------//
  // public instance methods
  //------------------------//

  /** get the most recently returned string again (without advancing)
   * @return String
   */
  public String getCurrent() {
	  if (styleIndex == 0) { return styles[1]; }
	  return styles[0];
  }

  /**
   * get the next string
   * @return String
   */
  public String getNext()
  {
      String style = styles[styleIndex];

      styleIndex++;
      if (styleIndex == styles.length) {
          styleIndex = 0;
      }

      return style;
  }

  public void reset() {
	  styleIndex = 0;
  }
  
}
