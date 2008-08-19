
package lisken.systoolbox;


import java.io.*;
import java.util.*;


public class ExceptionGroup extends Exception
{
  private static final String EMPTY_MSG = "empty ExceptionGroup";

  Vector exceptionV = new Vector();

  public void add(Exception ex)
  {
    exceptionV.addElement(ex);
  }

  public String getMessage()
  {
    if (exceptionV.size() > 0) {
      StringBuffer messages = new StringBuffer();
      Enumeration exceptions = exceptionV.elements();
      int e = 0;
      while (exceptions.hasMoreElements())
      {
	if (e++ > 0) messages.append(", ");
        messages.append(e + ". ");
        messages.append(((Exception) exceptions.nextElement()).getMessage());
      }
      return messages.toString();
    } else {
      return EMPTY_MSG;
    }
  }

  public String toString()
  {
    if (exceptionV.size() > 0) {
      StringBuffer encodings = new StringBuffer();
      Enumeration exceptions = exceptionV.elements();
      int e = 0;
      while (exceptions.hasMoreElements())
      {
	if (e++ > 0) encodings.append("\n\n");
        encodings.append(e + ". ");
        encodings.append(((Exception) exceptions.nextElement()).toString());
      }
      return encodings.toString();
    } else {
      return EMPTY_MSG;
    }
  }

  public void printStackTrace()
  {
    printStackTrace(System.err);
  }

  public void printStackTrace(PrintStream s)
  {
    if (exceptionV.size() > 0) {
      Enumeration exceptions = exceptionV.elements();
      int e = 0;
      while (exceptions.hasMoreElements())
      {
	if (e++ > 0) s.print("\n");
        ((Exception) exceptions.nextElement()).printStackTrace(s);
      }
    } else {
      s.println(EMPTY_MSG);
    }
  }

  public void printStackTrace(PrintWriter s)
  {
    if (exceptionV.size() > 0) {
      Enumeration exceptions = exceptionV.elements();
      int e = 0;
      while (exceptions.hasMoreElements())
      {
	if (e++ > 0) s.print("\n");
        ((Exception) exceptions.nextElement()).printStackTrace(s);
      }
    } else {
      s.println(EMPTY_MSG);
    }
  }

  public Throwable fillInStackTrace()
  {
    if (exceptionV == null) return null;
    Enumeration exceptions = exceptionV.elements();
    for (int i = 0; i < exceptionV.size(); ++i)
    {
      exceptionV.setElementAt(
       ((Exception) exceptionV.elementAt(i)).fillInStackTrace(),
       i);
    }
    return this;
  }

  public int size()
  {
    return exceptionV.size();
  }
}

