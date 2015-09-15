package io.github.jexperiments.latex;
import io.github.jexperiments.latex.Tabular.Cell;
import io.github.jexperiments.latex.Tabular.Row;
import io.github.htools.lib.Log; 

/**
 *
 * @author Jeroen Vuurens
 */
public interface ColumnModifier {
   public void modify( Cell c );
}
