package org.pageseeder.diffx.core;

import org.junit.Test;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.event.impl.CharEvent;
import org.pageseeder.diffx.handler.DiffHandler;
import org.pageseeder.diffx.test.Events;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class PerformanceTest {

  private static final DiffHandler VOID_HANDLER = new DiffHandler() {
    @Override
    public void handle(Operator operator, DiffXEvent event) {}
  };

  @Test
  public void compareRandomString_1000_10() throws IOException {
    // Generate content
    String from = getRandomString(1000, false);
    String to = vary(from, .10);
    List<CharEvent> second = Events.toCharEvents(from);
    List<CharEvent> first = Events.toCharEvents(to);

    profileX(new DefaultXMLProcessor(), first, second, 10);
    profileX(new TextOnlyProcessor(), first, second, 10);
    profileX(new ProgressiveXMLProcessor(), first, second, 10);
  }

  @Test
  public void compareRandomString_1000_50() throws IOException {
    // Generate content
    String from = getRandomString(1000, false);
    String to = vary(from, .50);
    List<CharEvent> second = Events.toCharEvents(from);
    List<CharEvent> first = Events.toCharEvents(to);

    profileX(new DefaultXMLProcessor(), first, second, 10);
    profileX(new TextOnlyProcessor(), first, second, 10);
    profileX(new ProgressiveXMLProcessor(), first, second, 10);
  }

  @Test
  public void compareSingleElement_1000_20() throws IOException, DiffXException {
    // Generate content
    String from = getRandomString(1000, true);
    String to = vary(from, .05);
    List<DiffXEvent> second = Events.recordXMLEvents("<root>"+from+"</root>", TextGranularity.SPACE_WORD);
    List<DiffXEvent> first = Events.recordXMLEvents("<root>"+to+"</root>", TextGranularity.SPACE_WORD);
    profileX(new DefaultXMLProcessor(), first, second, 10);
    profileX(new ProgressiveXMLProcessor(), first, second, 10);
    profileX(new DefaultXMLProcessor(), first, second, 10);
    profileX(new ProgressiveXMLProcessor(), first, second, 10);
    System.out.println(second);
    System.out.println(first);
  }

  @Test
  public void compareShallowXML() throws IOException, DiffXException {
    // Generate content
    StringBuilder xml1 = new StringBuilder();
    StringBuilder xml2 = new StringBuilder();
    generateXML(xml1, xml2, 10);

    // Parse events
    List<DiffXEvent> secondText = Events.recordXMLEvents(xml1.toString(), TextGranularity.TEXT);
    List<DiffXEvent> firstText = Events.recordXMLEvents(xml2.toString(), TextGranularity.TEXT);
    List<DiffXEvent> secondWord = Events.recordXMLEvents(xml1.toString(), TextGranularity.SPACE_WORD);
    List<DiffXEvent> firstWord = Events.recordXMLEvents(xml2.toString(), TextGranularity.SPACE_WORD);

    System.out.println(firstText.size()+"/"+secondText.size());
    System.out.println(firstWord.size()+"/"+secondWord.size());

    profileX(new DefaultXMLProcessor(), firstWord, secondWord, 10);
    profileX(new ProgressiveXMLProcessor(), firstText, secondText, 10);

    System.out.println(xml1);
    System.out.println(xml2);
  }

  @Test
  public void compareCoalesce() throws IOException, DiffXException {
    // Generate content
    StringBuilder xml1 = new StringBuilder();
    StringBuilder xml2 = new StringBuilder();
    generateXML(xml1, xml2, 100);

    // Parse events
    List<DiffXEvent> secondText = Events.recordXMLEvents(xml1.toString(), TextGranularity.TEXT);
    List<DiffXEvent> firstText = Events.recordXMLEvents(xml2.toString(), TextGranularity.TEXT);

    ProgressiveXMLProcessor coalescingProcessor = new ProgressiveXMLProcessor();
    coalescingProcessor.setCoalesce(true);
    ProgressiveXMLProcessor noCoalesceProcessor = new ProgressiveXMLProcessor();
    noCoalesceProcessor.setCoalesce(false);
    profileX(coalescingProcessor, firstText, secondText, 10);
    profileX(noCoalesceProcessor, firstText, secondText, 10);

  }

  private static void profileX(DiffProcessor processor, List<? extends DiffXEvent> first, List<? extends DiffXEvent> second, int times) throws IOException {
    System.out.print(processor.toString());
    System.out.println("\t"+first.size()+"/"+second.size()+" events");
    long total = 0;
    for (int i=0; i < times; i++) {
      long t = profile(processor, first, second);
      System.out.print(" #"+i+":"+t);
      total += t;
    }
    System.out.println();
    System.out.println(" Avg:"+(total*1.0 / times)+"ms");
  }

  private static long profile(DiffProcessor processor, List<? extends DiffXEvent> first, List<? extends DiffXEvent> second) throws IOException {
    long t0 = System.nanoTime();
    processor.process(first, second, VOID_HANDLER);
    long t1 = System.nanoTime();
    return (t1 - t0) / 1_000_000;
  }

  private static void generateXML(StringBuilder xml1, StringBuilder xml2, int elements) {
    // Generate content
    Random r = new Random();
    xml1.append("<root>\n");
    xml2.append("<root>\n");
    for (int i = 0; i < elements; i++) {
      int f = r.nextInt(10);
      String from = getRandomString(100+ f*100, true);
      String to = (r.nextInt(10) < 3)? vary(from, .05) : from;
      xml1.append("  <p>"+from+"</p>\n");
      xml2.append("  <p>"+to+"</p>\n");
    }
    xml1.append("</root>");
    xml2.append("</root>");
  }

  private static String getRandomString(int length, boolean spaces) {
    Random r = new Random();
    StringBuilder out = new StringBuilder();
    while (out.length() < length) {
      out.append((char)('a' +r.nextInt(26)));
      if (spaces && r.nextInt(5) == 1) out.append(' ');
    }
    return out.toString();
  }

  /**
   * Make variations on the specified String
   *
   * @param source The source string
   * @param changes The percentage of changes (from 0.0 to 1.0)
   *
   * @return A variation according to
   */
  private static String vary(String source, double changes) {
    Random r = new Random();
    StringBuilder out = new StringBuilder();
    for (char c : source.toCharArray()) {
      if (changes > r.nextDouble()) {
        int type = r.nextInt(3);
        if (type == 0) {
          // Mutate
          out.append((char)('a' +r.nextInt(26)));
        } else if (type == 1) {
          // insert
          out.append(c);
          out.append((char)('a' +r.nextInt(26)));
        }
      } else {
        out.append(c);
      }
    }
    return out.toString();
  }

}