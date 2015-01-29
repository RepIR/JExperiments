package io.github.repir.tools.extract.modules;

import io.github.repir.tools.search.ByteRegex;
import io.github.repir.tools.search.ByteSearchPosition;
import io.github.repir.tools.search.ByteSearchSection;
import io.github.repir.tools.lib.Log;
import io.github.repir.tools.extract.Content;
import io.github.repir.tools.extract.Extractor;

/**
 * Find mediawiki macro's e.g. "{{cite ...}}" that spans more than one line.
 * <p/>
 * @author jbpvuurens
 */
public class MarkWikipediaLink extends SectionMarker {

    public static Log log = new Log(MarkWikipediaLink.class);
    ByteRegex linkEnd = new ByteRegex("\\]\\]\\w*");

    public MarkWikipediaLink(Extractor extractor, String inputsection, String outputsection) {
        super(extractor, inputsection, outputsection);
    }

    @Override
    public ByteRegex getStartMarker() {
        return new ByteRegex("\\[\\[");
    }

    @Override
    public ByteSearchSection process(Content content, ByteSearchSection section) {
        ByteSearchPosition endPos = linkEnd.findPos(section);
        if (endPos.found()) {
            section.innerend = endPos.start;
            section.end = endPos.end;
            ByteSearchPosition lastStart = startmarker.findLastPos(section);
            if (lastStart.found()) {
                section.innerstart = lastStart.end;
                section.start = lastStart.start;
            }
            for (int i = section.innerstart; i < endPos.end; i++) {
                if (content.content[i] < 0) {
                    return null;
                }
            }
            log.info("found %d %d %s", section.innerstart, endPos.start, section.toOuterString());
            return content.addSectionPos(outputsection, content.content, section.start, section.innerstart, endPos.start, endPos.end);
        }
        return null;
    }
}
