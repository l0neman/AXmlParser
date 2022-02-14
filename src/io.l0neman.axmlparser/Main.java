package io.l0neman.axmlparser;

import java.io.IOException;

public class Main {

  public static void main(String[] args) {
    // testParser();
    testPrinter();
  }

  private static void testParser() {
    AXmlParser aXmlParser = new AXmlParser();

    aXmlParser.setHandler(new AXmlParser.Handler() {
      @Override protected void startXml() {
        System.out.println("#startXml");
      }

      @Override protected void startNamespace(String ns, String nsUri) {
        System.out.printf("#endNamespace ns: \"%s\", nsUri: \"%s\"%n", ns, nsUri);
      }

      @Override protected void startElement(String ns, String nsUri, String elementName) {
        System.out.printf("#startElement ns: \"%s\", nsUri: \"%s\", elementName: \"%s\"%n",
            ns, nsUri, elementName);
      }

      @Override protected void onData(String data) {
        System.out.printf("#onData data \"%s\"%n", data);
      }

      @Override protected void onAttribute(String ns, String nsUri, String attrName, String attrValue) {
        System.out.printf("#onAttribute ns: \"%s\", nsUri: \"%s\", attrName \"%s\", attrValue \"%s\"%n",
            ns, nsUri, attrName, attrValue);
      }

      @Override protected void endElement(String ns, String nsUri, String elementName) {
        System.out.printf("#endNamespace ns: \"%s\", nsUri: \"%s\"%n", ns, nsUri);
      }

      @Override protected void endNamespace(String ns, String nsUri) {
        System.out.printf("#endNamespace ns: \"%s\", nsUri: \"%s\"%n", ns, nsUri);
      }

      @Override protected void endXml() {
        System.out.println("#endXml");
      }
    });

    try {
      aXmlParser.parse(".\\file\\AndroidManifest.xml");
      System.out.println();
      aXmlParser.parse(".\\file\\activity_main.xml");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void testPrinter() {
    AXmlPrinter aXmlPrinter = new AXmlPrinter();

    try {
      aXmlPrinter.print(".\\file\\AndroidManifest.xml");
      aXmlPrinter.print(".\\file\\activity_main.xml");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
