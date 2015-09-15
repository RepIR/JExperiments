package io.github.jexperiments.latex;
import io.github.jexperiments.latex.Tabular.Cell;
import io.github.jexperiments.latex.Tabular.Row;
import io.github.htools.lib.Log; 

/**
 *
 * @author Jeroen Vuurens
 */
public class Upperline implements RowModifier {
   
   @Override
   public void modify(Row c, StringBuilder sb) {
      sb.insert(0, "\\hline\n");
   }

   @Override
   public void modify(Cell c) { }
   
}
