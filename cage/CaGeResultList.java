
package cage;


import javax.swing.event.*;
import com.sun.java.util.collections.*;


/**
    A result "collection" of CaGe's production process, with
    some methods to traverse it.

    A CaGeResultList maintains a "cursor" that points at the
    "current" element in the list (as long as it is not empty).
    The cursor is moved by the <code>next()</code> and
    <code>previous()</code> methods.
    <code>addGraph</code> will set the cursor to the graph
    that is added (i.e. to the end of the list).

    The class implements several methods that look like those
    of the ListIterator interface in terms of name and signature.
    But we do not keep to the (semantic) contract of that
    interface because of the different way backward/forward
    movement is handled.  Alternating calls to next() and
    previous() will not return the same element repeatedly,
    as stipulated in the contract.  Instead, if both calls succeed,
    the same two elements will be returned in alternation.
 */

public class CaGeResultList
{
  private Vector results = new Vector(0);
  private CaGeResult result = null;
  private int cursor = 0, found, highestGraphNo = 0, highestGraphNoIndex = -1;

  public CaGeResultList ()
  {
  }

  public void addGraph(EmbeddableGraph graph, int graphNo)
  {
    addResult(new CaGeResult(graph, graphNo));
  }

  public void addResult(CaGeResult result)
  {
    cursor = results.size();
    if (result.graphNo > highestGraphNo) {
      highestGraphNo = result.graphNo;
      highestGraphNoIndex = cursor;
    }
    this.result = result;
    results.addElement(result);
  }

  public EmbeddableGraph getGraph()
  {
    return result == null ? null : result.graph;
  }

  public int getGraphNo()
  {
    return result == null ? 0 : result.graphNo;
  }

  public CaGeResult getResult()
  {
    try {
      result = (CaGeResult) results.elementAt(cursor);
    } catch (ArrayIndexOutOfBoundsException e) {
      return null;
    }
    return result;
  }

  public boolean findGraphNo(int no)
  {
    int n;
    n = results.size();
    for (found = 0; found < n; ++found)
    {
      if (((CaGeResult) results.elementAt(found)).graphNo == no) {
        return true;
      }
    }
    return false;
  }

  public void gotoFound()
  {
    cursor = found;
    getResult();
  }


  public int nextIndex()
  {
    return cursor + 1;
  }

  public boolean hasNext()
  {
    return nextIndex() < results.size();
  }

  public Object next()
   throws NoSuchElementException
  {
    try {
      cursor = nextIndex();
      return getResult();
    } catch (Exception e) {
      throw new NoSuchElementException();
    }
  }

  public int nextGraphNo()
  {
    try {
      return ((CaGeResult) results.elementAt(nextIndex())).graphNo;
    } catch (Exception e) {
      throw new NoSuchElementException();
    }
  }

  public int previousIndex()
  {
    return cursor - 1;
  }

  public boolean hasPrevious()
  {
    return previousIndex() >= 0;
  }

  public Object previous()
   throws NoSuchElementException
  {
    try {
      cursor = previousIndex();
      return getResult();
    } catch (Exception e) {
      throw new NoSuchElementException();
    }
  }

  public int previousGraphNo()
  {
    try {
      return ((CaGeResult) results.elementAt(previousIndex())).graphNo;
    } catch (Exception e) {
      throw new NoSuchElementException();
    }
  }

  public int highestGraphNo()
  {
    return highestGraphNo;
  }

  public void gotoHighest()
  {
    cursor = highestGraphNoIndex;
    getResult();
  }

/*
  public void remove()
   throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("CaGe result lists can't be modified");
  }

  public void add(Object o)
   throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("CaGe result lists can't be modified");
  }

  public void set(Object o)
   throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("CaGe result lists can't be modified");
  }
*/
}
