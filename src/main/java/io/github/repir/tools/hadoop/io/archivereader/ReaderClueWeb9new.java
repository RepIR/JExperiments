package io.github.repir.tools.hadoop.io.archivereader;

import io.github.repir.tools.extract.Content;
import io.github.repir.tools.search.ByteSearch;
import io.github.repir.tools.io.Datafile;
import io.github.repir.tools.io.EOCException;
import io.github.repir.tools.search.ByteSection;
import io.github.repir.tools.lib.Log;
import java.io.ByteArrayOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/**
 * An implementation of EntityReader that reads the ClueWeb09 collection. In
 * this collection, each document is preceded by a WARC header, which contains
 * document metadata and length. Each document has a WarcIDTag of 25 characters,
 * which s used to validate Warc headers of correct documents.
 * <p/>
 * Different from the TREC disks, the .gz format used for ClueWeb09 can be
 * decompressed by Java. So the disks can be read in its original format. A
 * technical problem with reading compressed files is that there is no way to
 * determine the distance to end-of-file. The file must therefore be read until
 * and EOF exception is encountered. The Datafile class will correctly throw an
 * EOF. However, after EOF has been reached, no further attempts should be made
 * reading the file, by checking {@link Datafile#hasMore()} first.
 * <p/>
 * Notorious for the amount of spam contained in the documents, the Fusion
 * idlist provided by the University of Waterloo can be used to limit the
 * documents processed to a percentage of the idlist. For this,
 * repository.idlist should point to the directory that contains the idlist
 * files, and repository.spamthreshold controls the percentage of spammiest
 * documents left out (i.e. 10 leaves out the spammiest 10%).
 * <p/>
 * @author jeroen
 */
public class ReaderClueWeb9new extends Reader {

   public static Log log = new Log(ReaderClueWeb9new.class);
   private byte[] warcTag = "WARC/0.18".getBytes();
   private ByteSearch contentlength = ByteSearch.create("\nContent-Length: ");
   private ByteSearch warcIDTag = ByteSearch.create("WARC-TREC-ID: ");
   private ByteSearch eol = ByteSearch.create("\n");
   private ByteSection warcID = new ByteSection(warcIDTag, eol);
   private idlist ids;
   private int spamthreshold;

   @Override
   public void initialize(FileSplit fileSplit) {
      Path file = fileSplit.getPath();
      String directory = getDir(file);
      spamthreshold = conf.getInt("repository.spamthreshold", 0);
      String spamlist = conf.get("repository.spamlist", null);
      String idlist = conf.get("repository.idlist", null);
      if (spamlist != null) {
         ids = SpamFile.getIdList(new Datafile(filesystem, spamlist + "/" + directory + ".spam"), spamthreshold);
      } else if (idlist != null) {
         ids = SubSetFile.getIdList(new Datafile(filesystem, idlist + "/" + directory + ".idlist"));
      }
      readEntity(); // the first warc tag isn't a document
   }

   @Override
   public boolean nextKeyValue() {
      while (fsin.hasMore()) {
         readEntity();
         String id = warcID.getFirstString(entitywritable.content, 0, entitywritable.content.length);
         if (id.length() == 25 && (ids == null || ids.get(id))) {
            //log.info("id %s", id);
            entitywritable.get("collectionid").add(id);
            int p = contentlength.findEnd(entitywritable.content, 0, entitywritable.content.length);
            if (p >= 0) {
               p = contentlength.findEnd(entitywritable.content, p, entitywritable.content.length);
               if (p >= 0) {
                  p = eol.findEnd(entitywritable.content, p, entitywritable.content.length);
                  if (p > 0) {
                     entitywritable.addSectionPos("warcheader", 
                                entitywritable.content, 0, 0, p, p);
                     entitywritable.addSectionPos("all", 
                                entitywritable.content, p, p, entitywritable.content.length, entitywritable.content.length);
                  }
               }
            }
            key.set(fsin.getOffset());
            return true;
         }
      }
      return false;
   }

   private void readEntity() {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      entitywritable = new Content();
      key.set(fsin.getOffset());
      int match = 0;
      while (true) {
         try {
            int b = fsin.readByte();
            if (match > 0 && b != warcTag[match]) { // output falsely cached chars
               buffer.write(warcTag, 0, match);
               match = 0;
            }
            if (b == warcTag[match]) { // check if we're matching needle
               match++;
               if (match >= warcTag.length) {
                  break;
               }
            } else {
               buffer.write(b);
            }
         } catch (EOCException ex) {
            buffer.write(warcTag, 0, match);
            break;
         }
      }
      entitywritable.content = buffer.toByteArray();
   }

   public String getDir(Path p) {
      String file = p.toString();
      int pos = file.lastIndexOf('/');
      int pos2 = file.lastIndexOf('/', pos - 1);
      if (pos < 0 || pos2 < 0) {
         log.fatal("illegal path %s", file);
      }
      return file.substring(pos2 + 1, pos);
   }
}