package io.github.jexperiments.latex;
import io.github.jexperiments.latex.Tabular.Cell;

/**
 *
 * @author Jeroen Vuurens
 */
public class StoreValue extends LeftAlign {
   public StoreValue( Tabular tabular, int column) {
      super( tabular, column );
   }

   @Override
   public void format(Cell c, Object v) {
      c.value = v.toString();
   } 
}
