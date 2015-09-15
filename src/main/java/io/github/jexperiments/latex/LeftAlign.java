package io.github.jexperiments.latex;
import io.github.jexperiments.latex.Tabular.Cell;

/**
 *
 * @author Jeroen Vuurens
 */
public abstract class LeftAlign extends ColumnFormatter {
   public LeftAlign( Tabular tabular, int column ) {
      super(tabular, column);
   }
   @Override
   public String getColumnSpec() {
      return "l";
   }

   @Override
   public int getColumnWidth(String value) {
      return 1;
   }
}
