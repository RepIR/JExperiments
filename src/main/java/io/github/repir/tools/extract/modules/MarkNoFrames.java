package io.github.repir.tools.extract.modules;

import io.github.repir.tools.search.ByteRegex;
import io.github.repir.tools.search.ByteSearchPosition;
import io.github.repir.tools.extract.Content;
import io.github.repir.tools.extract.Extractor;
import io.github.repir.tools.search.ByteSearch;
import io.github.repir.tools.search.ByteSearchSection;
import io.github.repir.tools.lib.Log;
import java.util.ArrayList;

/**
 * Marks <noframes> </noframes> sections.
 * <p/>
 * @author jbpvuurens
 */
public class MarkNoFrames extends SectionMarker {

    public static Log log = new Log(MarkNoFrames.class);
    public ByteSearch endmarker = ByteSearch.create("</noframes").toSection(ByteSearch.create(">"));

    public MarkNoFrames(Extractor extractor, String inputsection, String outputsection) {
        super(extractor, inputsection, outputsection);
    }

    @Override
    public ByteRegex getStartMarker() {
        return new ByteRegex("<noframes");
    }

    @Override
    public ByteSearchSection process(Content content, ByteSearchSection section) {
        int tagclose = findQuoteSafeTagEnd(section) + 1;
        if (tagclose > -1) {
            ByteSearchPosition end = endmarker.findPos(section, tagclose);
            if (end.found()) {
                return content.addSectionPos(outputsection, content.content, section.start, tagclose, end.start, end.end);
            }
        }
        return null;
    }
}
