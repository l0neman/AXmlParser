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
        System.out.println(String.format("#endNamespace ns: \"%s\", nsUri: \"%s\"", ns, nsUri));
      }

      @Override protected void startElement(String ns, String nsUri, String elementName) {
        System.out.println(String.format("#startElement ns: \"%s\", nsUri: \"%s\", elementName: \"%s\"",
            ns, nsUri, elementName));
      }

      @Override protected void onData(String data) {
        System.out.println(String.format("#onData data \"%s\"", data));
      }

      @Override protected void onAttribute(String ns, String nsUri, String attrName, String attrValue) {
        System.out.println(String.format("#onAttribute ns: \"%s\", nsUri: \"%s\", attrName \"%s\", attrValue \"%s\"",
            ns, nsUri, attrName, attrValue));
      }

      @Override protected void endElement(String ns, String nsUri, String elementName) {
        System.out.println(String.format("#endNamespace ns: \"%s\", nsUri: \"%s\"", ns, nsUri));
      }

      @Override protected void endNamespace(String ns, String nsUri) {
        System.out.println(String.format("#endNamespace ns: \"%s\", nsUri: \"%s\"", ns, nsUri));
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
