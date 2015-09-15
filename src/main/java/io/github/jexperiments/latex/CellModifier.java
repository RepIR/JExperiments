package io.github.jexperiments.latex;
import io.github.jexperiments.latex.Tabular.Cell;
import io.github.htools.lib.Log; 

/**
 *
 * @author Jeroen Vuurens
 */
public interface CellModifier {
   public void modify( Cell c );
}
