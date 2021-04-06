package org.pageseeder.diffx.core;

import org.junit.Assert;
import org.pageseeder.diffx.DiffXException;
import org.pageseeder.diffx.action.Action;
import org.pageseeder.diffx.action.Actions;
import org.pageseeder.diffx.action.Operator;
import org.pageseeder.diffx.config.TextGranularity;
import org.pageseeder.diffx.event.DiffXEvent;
import org.pageseeder.diffx.format.SmartXMLFormatter;
import org.pageseeder.diffx.format.XMLDiffXFormatter;
import org.pageseeder.diffx.handler.ActionHandler;
import org.pageseeder.diffx.test.Events;
import org.pageseeder.diffx.test.TestHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public abstract class BaseProcessorTest {

  /**
   * @return The processor instance to use for texting.
   */
  public abstract DiffProcessor getDiffProcessor();

  public final void assertDiffIsCorrect(List<? extends DiffXEvent> seq1, List<? extends DiffXEvent> seq2, List<Action> actions) {
    // Ensure that the diff is applicable
    Assert.assertTrue("The resulting diff is not applicable", Actions.isApplicable(seq1, seq2, actions));

    // Apply to second sequence to ensure we get the first
    List<DiffXEvent> got1 = Actions.apply(seq2, actions);
    Assert.assertEquals("Applying diff to #2 did not produce #1 ", seq1, got1);

    // Apply to first sequence to ensure we get the second
    List<DiffXEvent> got2 = Actions.apply(seq1, Actions.reverse(actions));
    Assert.assertEquals("Applying diff to #1 did not produce #2 ", seq2, got2);
  }

  public List<Action> diffToActions(List<? extends DiffXEvent> seq1, List<? extends DiffXEvent> seq2) throws IOException {
    DiffProcessor processor = getDiffProcessor();
    ActionHandler handler = new ActionHandler();
    processor.process(seq1, seq2, handler);
    return handler.getActions();
  }

  public static String toXML(List<Action> actions) throws IOException {
    StringWriter xml = new StringWriter();
    XMLDiffXFormatter formatter = new SmartXMLFormatter(xml);
    Actions.format(actions, formatter);
    return xml.toString();
  }

  public static String toTestOutput(List<Action> actions) throws IOException {
    TestHandler handler = new TestHandler();
    Actions.format(actions, handler);
    return handler.getOutput();
  }


  public static void assertDiffIsWellFormedXML(List<Action> actions) throws IOException {
    assertIsWellFormedXML(toXML(actions));
  }

  public static void assertMatchTestOutput(List<Action> actions, String[] exp) throws IOException {
    // check the possible values
    String diffout = toTestOutput(actions);
    for (String s : exp) {
      if (s.equals(diffout)) return;
    }
    Assert.assertEquals(exp[0], diffout);
  }


  public static void assertIsWellFormedXML(String xml) throws IOException {
    try {
      InputSource source = new InputSource(new StringReader(xml));
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.newSAXParser().parse(source, new DefaultHandler());
    } catch (SAXException | ParserConfigurationException ex) {
      throw new AssertionError("XML is not well-formed");
    }
  }

  /**
   * Print the error details.
   */
  protected void printXMLErrorDetails(String xml1, String xml2, String[] exp, String got, List<Action> actions) {
    System.err.println("+------------------------------------------------");
    System.err.println("| Input A: \"" + xml1 + "\"");
    System.err.println("| Input B: \"" + xml2 + "\"");
    System.err.println("| Output:  \"" + got + "\"");
    System.err.print("| Expect:  ");
    for (String s : exp) System.err.print("\"" + s + "\" ");
    System.err.println();
    System.err.print("| Actions: ");
    for (Action action : actions) {
      System.err.print(action.type() == Operator.DEL ? '-' : action.type() == Operator.INS ? '+' : '=');
      System.err.print(action.events());
    }
    System.err.println();
  }

}
