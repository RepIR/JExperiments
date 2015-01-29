package io.github.repir.tools.extract.modules;

import io.github.repir.tools.search.ByteRegex;
import io.github.repir.tools.search.ByteSearchPosition;
import io.github.repir.tools.search.ByteSearchSection;
import io.github.repir.tools.extract.Content;
import io.github.repir.tools.extract.Extractor;
import io.github.repir.tools.lib.BoolTools;
import io.github.repir.tools.lib.Log;
import java.util.ArrayList;

/**
 * Removes HTML tags from the content, leaving the content in between open and
 * close tags.
 * <p/>
 * @author jeroen
 */
public class RemoveMail extends ExtractorProcessor {

   public static Log log = new Log(RemoveMail.class);
   public ByteRegex start = new ByteRegex("@[\\c]+(\\.\\c)+");
   public boolean letter[] = BoolTools.namedot();

   private RemoveMail(Extractor extractor, String process) {
      super(extractor, process);
   }

   @Override
   public void process(Content entity, ByteSearchSection section, String attribute) {
      int startpos = section.innerstart;
      int endpos = section.innerend;
      ArrayList<ByteSearchPosition> positions = start.findAllPos(entity.content, startpos, endpos);
      for (ByteSearchPosition pos : positions) {
         while (pos.start > section.innerstart && letter[entity.content[pos.start - 1] & 0xFF]) {
            pos.start--;
         }
         if (pos.start > section.innerstart && entity.content[pos.start - 1] == ':') { // remove stuff like mailto:
            while (--pos.start > section.innerstart && letter[entity.content[pos.start - 1] & 0xFF]) {
              pos.start--;
           }
         }
         for (int a = pos.start; a < pos.end; a++) {
            entity.content[a] = 32;
         }
      }
   }
}
