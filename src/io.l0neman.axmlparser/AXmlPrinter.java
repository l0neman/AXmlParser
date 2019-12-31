package io.l0neman.axmlparser;

import io.l0neman.axmlparser.type.*;
import io.l0neman.axmlparser.util.Formatter;
import io.l0neman.axmlparser.util.objectio.ObjectInput;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
public class AXmlPrinter {

  // 输出调试信息（打印对象值）。
  private static final boolean DEBUG_INFO = false;

  // 输出解析相关信息（可读方式打印）。
  private static final boolean PARSE_INFO = false;

  // 开启 xml 文档解析。
  private static final boolean XML_PRINT = true;

  private int mIndex;
  private String[] stringPool;
  private AXmlEditor aXmlEditor = new AXmlEditor();
  private Map<String, String> namespaceMap = new HashMap<>();

  private void parseXMLTreeHeader(ObjectInput objectInput) throws IOException {
    ResXMLTreeHeader xmlTreeHeader = objectInput.read(ResXMLTreeHeader.class, mIndex);

    if (DEBUG_INFO) {
      System.out.println("ResXMLTreeHeader:");
      System.out.println(xmlTreeHeader);
    }

    mIndex += xmlTreeHeader.header.headerSize;
  }

  private void parseStringPool(ObjectInput objectInput) throws IOException {
    final long stringPoolIndex = mIndex;
    ResStringPoolHeader stringPoolHeader = objectInput.read(ResStringPoolHeader.class, stringPoolIndex);
    if (DEBUG_INFO) {
      System.out.println("string pool header:");
      System.out.println(stringPoolHeader);
    }

    StringPoolChunkParser stringPoolChunkParser = new StringPoolChunkParser();
    stringPoolChunkParser.parseStringPoolChunk(objectInput, stringPoolHeader, stringPoolIndex);

    if (DEBUG_INFO) {
      System.out.println();
      System.out.println("string index array:");
      System.out.println(Arrays.toString(stringPoolChunkParser.getStringIndexArray()));

      System.out.println();
      System.out.println("style index array:");
      System.out.println(Arrays.toString(stringPoolChunkParser.getStyleIndexArray()));
    }

    stringPool = stringPoolChunkParser.getStringPool();

    if (PARSE_INFO) {
      System.out.println();
      System.out.println("string pool:");
      System.out.println(Arrays.toString(stringPoolChunkParser.getStringPool()));

      System.out.println();
      System.out.println("style pool:");
      final List<ResStringPoolSpan>[] stylePool = stringPoolChunkParser.getStylePool();

      System.out.println(Arrays.toString(stylePool));

      System.out.println();
      System.out.println("style detail:");
      for (List<ResStringPoolSpan> spans : stylePool) {
        System.out.println("---------");
        for (ResStringPoolSpan span : spans) {
          System.out.println(stringPool[span.name.index]);
        }
      }
    }

    // 向下移动字符串池的大小。
    mIndex += stringPoolHeader.header.size;
  }

  private void parseResourceIds(ObjectInput objectInput) throws IOException {
    ResChunkHeader header = objectInput.read(ResChunkHeader.class, mIndex);
    // 解析 xml 文件中出现的资源 ID。
    final int size = header.size;
    final int count = (size - header.headerSize) / Integer.BYTES;

    int index = mIndex + header.headerSize;

    if (PARSE_INFO) {
      for (int i = 0; i < count; i++) {
        System.out.println("resId: " + Formatter.toHex(Formatter.fromInt(
            objectInput.readInt(index), true
        )));
        index += i * Integer.BYTES;
      }
    }

    mIndex += header.size;
  }

  private void parseStartNamespace(ObjectInput objectInput) throws IOException {
    ResXMLTreeNode node = objectInput.read(ResXMLTreeNode.class, mIndex);
    int namespaceExtIndex = mIndex + node.header.headerSize;

    if (PARSE_INFO) {
      System.out.println("node comment: " + (node.comment.index != -1 ? stringPool[node.comment.index] : ""));
    }

    if (DEBUG_INFO) {
      System.out.println(node);
    }

    final ResXMLTreeNamespaceExt namespaceExt =
        objectInput.read(ResXMLTreeNamespaceExt.class, namespaceExtIndex);

    if (PARSE_INFO) {
      System.out.println();
      System.out.println("namespace:");
    }

    if (DEBUG_INFO) {
      System.out.println(namespaceExt);
    }

    String namespace = stringPool[namespaceExt.prefix.index];

    if (PARSE_INFO) {
      System.out.println("namepsace name: " + namespace);
    }

    String namespaceUri = stringPool[namespaceExt.uri.index];

    if (PARSE_INFO) {
      System.out.println("namepsace uri: " + namespaceUri);
    }

    if (XML_PRINT) {
      aXmlEditor.addNamespace(namespace, namespaceUri);
      namespaceMap.put(namespaceUri, namespace);
    }

    mIndex += node.header.size;
  }

  private void parseStartElement(ObjectInput objectInput) throws IOException {
    ResXMLTreeNode node = objectInput.read(ResXMLTreeNode.class, mIndex);
    if (PARSE_INFO) {
      System.out.println("node comment: " + (node.comment.index != -1 ? stringPool[node.comment.index] : ""));
    }

    int index = mIndex + node.header.headerSize;

    if (DEBUG_INFO) {
      System.out.println(node);
    }

    ResXMLTreeAttrExt attrExt = objectInput.read(ResXMLTreeAttrExt.class, index);

    if (DEBUG_INFO) {
      System.out.println(attrExt);
    }

    if (PARSE_INFO) {
      System.out.println("element:");
    }

    String ns = attrExt.ns.index != -1 ?
        stringPool[attrExt.ns.index] : null;

    if (PARSE_INFO) {
      System.out.println("element ns: " + ns);
    }

    final String elementName = stringPool[attrExt.name.index];

    if (PARSE_INFO) {
      System.out.println("element name: " + elementName);
    }

    if (XML_PRINT) {
      aXmlEditor.openElement(elementName);
    }

    index += ObjectInput.sizeOf(ResXMLTreeAttrExt.class);

    for (int i = 0; i < attrExt.attributeCount; i++) {
      ResXMLTreeAttribute attr = objectInput.read(ResXMLTreeAttribute.class, index);

      if (DEBUG_INFO) {
        System.out.println(attr);
      }

      if (PARSE_INFO) {
        System.out.println("attr:");
      }

      final String namespace = attr.ns.index != -1 ?
          stringPool[attr.ns.index] : null;

      if (PARSE_INFO) {
        System.out.println("attr ns: " + namespace);
      }

      final String attrName = stringPool[attr.name.index];

      if (PARSE_INFO) {
        System.out.println("attr name: " + attrName);
      }

      final String attrText = attr.rawValue.index != -1 ?
          stringPool[attr.rawValue.index] : null;

      if (PARSE_INFO) {
        System.out.println("attr text: " + attrText);
      }

      final String attrValue = attr.typeValue.dataStr();

      if (PARSE_INFO) {
        System.out.println("attr value: " + attr.typeValue);
      }

      if (XML_PRINT) {
        String nsPrefix = namespaceMap.get(namespace);

        nsPrefix = nsPrefix == null ? "" : nsPrefix + ":";
        aXmlEditor.addAttribute(nsPrefix + attrName, attrText != null ?
            attrText : attrValue);
      }

      index += ObjectInput.sizeOf(ResXMLTreeAttribute.class);
    }

    mIndex += node.header.size;
  }

  private void parseCData(ObjectInput objectInput) throws IOException {
    ResXMLTreeNode node = objectInput.read(ResXMLTreeNode.class, mIndex);
    if (PARSE_INFO) {
      System.out.println("node comment: " + (node.comment.index != -1 ? stringPool[node.comment.index] : ""));
    }

    int index = mIndex + node.header.headerSize;

    ResXMLTreeCdataExt cdataExt = objectInput.read(ResXMLTreeCdataExt.class, index);

    if (PARSE_INFO) {
      System.out.println("cdata:");
    }

    final String cdata = stringPool[cdataExt.data.index];

    if (PARSE_INFO) {
      System.out.println(cdata);
    }

    if (XML_PRINT) {
      aXmlEditor.addData(cdata);
    }

    mIndex += node.header.size;
  }

  private void parseEndElement(ObjectInput objectInput) throws IOException {
    ResXMLTreeNode node = objectInput.read(ResXMLTreeNode.class, mIndex);
    if (PARSE_INFO) {
      System.out.println("node comment: " + (node.comment.index != -1 ? stringPool[node.comment.index] : ""));
    }

    int index = mIndex + node.header.headerSize;

    ResXMLTreeEndElementExt endElementExt = objectInput.read(ResXMLTreeEndElementExt.class, index);
    if (PARSE_INFO) {
      System.out.println("element end:");
    }

    final String ns = endElementExt.ns.index != -1 ?
        stringPool[endElementExt.ns.index] : "";

    if (PARSE_INFO) {
      System.out.println("element end ns: " + ns);
    }

    final String elementName = stringPool[endElementExt.name.index];

    if (PARSE_INFO) {
      System.out.println("element end name: " + elementName);
    }

    if (XML_PRINT) {
      aXmlEditor.closeElement(elementName);
    }

    mIndex += node.header.size;
  }

  private void parseEndNamespace(ObjectInput objectInput) throws IOException {
    ResXMLTreeNode node = objectInput.read(ResXMLTreeNode.class, mIndex);
    if (PARSE_INFO) {
      System.out.println("node comment: " + (node.comment.index != -1 ? stringPool[node.comment.index] : ""));
    }

    int index = mIndex + node.header.headerSize;

    ResXMLTreeNamespaceExt namespaceExt = objectInput.read(ResXMLTreeNamespaceExt.class, index);

    if (PARSE_INFO) {
      System.out.println();
      System.out.println("namespace: end");
    }

    if (DEBUG_INFO) {
      System.out.println(namespaceExt);
    }

    String namespace = stringPool[namespaceExt.prefix.index];

    if (PARSE_INFO) {
      System.out.println("namepsace end name: " + namespace);
    }

    String namespaceUri = stringPool[namespaceExt.uri.index];

    if (PARSE_INFO) {
      System.out.println("namepsace end uri: " + namespaceUri);
    }

    mIndex += node.header.size;
  }

  private void parse(ObjectInput objectInput) throws IOException {
    while (!objectInput.isEof(mIndex)) {
      ResChunkHeader header = objectInput.read(ResChunkHeader.class, mIndex);

      if (PARSE_INFO || DEBUG_INFO) {
        System.out.println();
        System.out.println("================================ " + ResourceTypes.nameOf(header.type) +
            " ================================");
        System.out.println(header);
      }

      switch (header.type) {
        case ResourceTypes.RES_XML_TYPE:
          parseXMLTreeHeader(objectInput);
          break;

        case ResourceTypes.RES_STRING_POOL_TYPE:
          parseStringPool(objectInput);
          break;

        case ResourceTypes.RES_XML_RESOURCE_MAP_TYPE:
          parseResourceIds(objectInput);
          break;

        case ResourceTypes.RES_XML_START_NAMESPACE_TYPE:
          parseStartNamespace(objectInput);
          break;

        case ResourceTypes.RES_XML_START_ELEMENT_TYPE:
          parseStartElement(objectInput);
          break;

        case ResourceTypes.RES_XML_CDATA_TYPE:
          parseCData(objectInput);
          break;

        case ResourceTypes.RES_XML_END_ELEMENT_TYPE:
          parseEndElement(objectInput);
          break;

        case ResourceTypes.RES_XML_END_NAMESPACE_TYPE:
          parseEndNamespace(objectInput);
          break;

        default:
          break;
      }
    }

    if (XML_PRINT) {
      System.out.println();
      System.out.println(aXmlEditor.print());
    }
  }

  private static void closeQuietly(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException ignore) {
      } catch (RuntimeException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * 将 Android 二进制格式的 Xml 文件以标准 Xml 文本格式打印出来。
   *
   * @param file 二进制 Xml 文件路径。
   * @throws IOException 文件解析异常。
   */
  public void print(String file) throws IOException {
    mIndex = 0;
    stringPool = null;
    namespaceMap.clear();
    ObjectInput objectInput = null;
    try {
      objectInput = new ObjectInput(file);
      parse(objectInput);
    } finally {
      closeQuietly(objectInput);
    }
  }
}

