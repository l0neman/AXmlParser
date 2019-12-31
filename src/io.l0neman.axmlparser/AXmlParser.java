package io.l0neman.axmlparser;

import io.l0neman.axmlparser.type.ResChunkHeader;
import io.l0neman.axmlparser.type.ResStringPoolHeader;
import io.l0neman.axmlparser.type.ResXMLTreeAttrExt;
import io.l0neman.axmlparser.type.ResXMLTreeAttribute;
import io.l0neman.axmlparser.type.ResXMLTreeCdataExt;
import io.l0neman.axmlparser.type.ResXMLTreeEndElementExt;
import io.l0neman.axmlparser.type.ResXMLTreeHeader;
import io.l0neman.axmlparser.type.ResXMLTreeNamespaceExt;
import io.l0neman.axmlparser.type.ResXMLTreeNode;
import io.l0neman.axmlparser.type.ResourceTypes;
import io.l0neman.axmlparser.util.objectio.ObjectInput;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AXmlParser {
  private int mIndex;
  private String[] mStringPool;
  private Map<String, String> mNamespaceMap = new HashMap<>();
  private Handler mHandler;

  /**
   * 解析处理器。
   */
  public static abstract class Handler {

    /**
     * 开始解析 xml 文件。
     */
    protected void startXml() {}

    /**
     * 命名空间解析起始点。
     *
     * @param ns    命名空间名称。
     * @param nsUri 命名空间 uri。
     */
    protected void startNamespace(String ns, String nsUri) {}

    /**
     * 元素解析起始点。
     *
     * @param ns          元素命名空间名称（可能为 null）。
     * @param nsUri       元素命名空间 uri（可能为 null）。
     * @param elementName 元素名称。
     */
    protected void startElement(String ns, String nsUri, String elementName) {}

    /**
     * 元素标签中包含的数据。
     *
     * @param data 字符串数据。
     */
    protected void onData(String data) {}

    /**
     * 元素属性解析数据回调。
     *
     * @param ns        属性命名空间名称（可能为 null）。
     * @param nsUri     属性命名空间 uri（可能为 null）。
     * @param attrName  属性名。
     * @param attrValue 属性值。
     */
    protected void onAttribute(String ns, String nsUri, String attrName, String attrValue) {}

    /**
     * 元素解析终止点。
     *
     * @param ns          元素命名空间名称（可能为 null）。
     * @param nsUri       元素命名空间 uri（可能为 null）。
     * @param elementName 元素名称。
     */
    protected void endElement(String ns, String nsUri, String elementName) {}

    /**
     * 命名空间解析终止点。
     *
     * @param ns    命名空间名称。
     * @param nsUri 命名空间 uri。
     */
    protected void endNamespace(String ns, String nsUri) {}

    /**
     * 解析 xml 完毕。
     */
    protected void endXml() {}
  }

  private void parseXMLTreeHeader(ObjectInput ObjectInput) throws IOException {
    ResXMLTreeHeader xmlTreeHeader = ObjectInput.read(ResXMLTreeHeader.class, mIndex);

    mHandler.startXml();

    mIndex += xmlTreeHeader.header.headerSize;
  }

  private void parseStringPool(ObjectInput objectInput) throws IOException {
    final long stringPoolIndex = mIndex;
    ResStringPoolHeader stringPoolHeader = objectInput.read(ResStringPoolHeader.class, stringPoolIndex);

    StringPoolChunkParser stringPoolChunkParser = new StringPoolChunkParser();
    stringPoolChunkParser.parseStringPoolChunk(objectInput, stringPoolHeader, stringPoolIndex);

    mStringPool = stringPoolChunkParser.getStringPool();

    // 向下移动字符串池的大小。
    mIndex += stringPoolHeader.header.size;
  }

  private void parseResourceIds(ObjectInput ObjectInput) throws IOException {
    ResChunkHeader header = ObjectInput.read(ResChunkHeader.class, mIndex);
    // 解析 xml 文件中出现的资源 ID。
    /*
    final int size = header.size;
    final int count = (size - header.headerSize) / Integer.BYTES;

    int index = mIndex + header.size;
    // */

    mIndex += header.size;
  }

  private void parseStartNamespace(ObjectInput ObjectInput) throws IOException {
    ResXMLTreeNode node = ObjectInput.read(ResXMLTreeNode.class, mIndex);
    int namespaceExtIndex = mIndex + node.header.headerSize;

    final ResXMLTreeNamespaceExt namespaceExt =
        ObjectInput.read(ResXMLTreeNamespaceExt.class, namespaceExtIndex);
    String namespace = mStringPool[namespaceExt.prefix.index];
    String namespaceUri = mStringPool[namespaceExt.uri.index];

    mHandler.startNamespace(namespace, namespaceUri);
    mNamespaceMap.put(namespaceUri, namespace);

    mIndex += node.header.size;
  }

  private void parseStartElement(ObjectInput objectInput) throws IOException {
    ResXMLTreeNode node = objectInput.read(ResXMLTreeNode.class, mIndex);
    int index = mIndex + node.header.headerSize;

    ResXMLTreeAttrExt attrExt = objectInput.read(ResXMLTreeAttrExt.class, index);
    String ns = attrExt.ns.index != -1 ?
        mStringPool[attrExt.ns.index] : null;
    final String elementName = mStringPool[attrExt.name.index];

    mHandler.startElement(mNamespaceMap.get(ns), ns, elementName);

    index += ObjectInput.sizeOf(ResXMLTreeAttrExt.class);

    for (int i = 0; i < attrExt.attributeCount; i++) {
      ResXMLTreeAttribute attr = objectInput.read(ResXMLTreeAttribute.class, index);

      final String nsUri = attr.ns.index != -1 ?
          mStringPool[attr.ns.index] : null;
      final String attrName = mStringPool[attr.name.index];
      final String attrText = attr.rawValue.index != -1 ?
          mStringPool[attr.rawValue.index] : null;
      final String attrValue = attrText == null ? String.valueOf(attr.typeValue.data) : attrText;
      String nsName = mNamespaceMap.get(nsUri);

      mHandler.onAttribute(nsName, nsUri, attrName, attrValue);

      index += ObjectInput.sizeOf(ResXMLTreeAttribute.class);
    }

    mIndex += node.header.size;
  }

  private void parseCData(ObjectInput objectInput) throws IOException {
    ResXMLTreeNode node = objectInput.read(ResXMLTreeNode.class, mIndex);
    int index = mIndex + node.header.headerSize;

    ResXMLTreeCdataExt cdataExt = objectInput.read(ResXMLTreeCdataExt.class, index);
    final String cdata = mStringPool[cdataExt.data.index];

    mHandler.onData(cdata);

    mIndex += node.header.size;
  }

  private void parseEndElement(ObjectInput objectInput) throws IOException {
    ResXMLTreeNode node = objectInput.read(ResXMLTreeNode.class, mIndex);
    int index = mIndex + node.header.headerSize;

    ResXMLTreeEndElementExt endElementExt = objectInput.read(ResXMLTreeEndElementExt.class, index);
    final String nsUri = endElementExt.ns.index != -1 ?
        mStringPool[endElementExt.ns.index] : "";
    final String elementName = mStringPool[endElementExt.name.index];

    mHandler.endElement(mNamespaceMap.get(nsUri), nsUri, elementName);

    mIndex += node.header.size;
  }

  private void parseEndNamespace(ObjectInput objectInput) throws IOException {
    ResXMLTreeNode node = objectInput.read(ResXMLTreeNode.class, mIndex);
    int index = mIndex + node.header.headerSize;

    ResXMLTreeNamespaceExt namespaceExt = objectInput.read(ResXMLTreeNamespaceExt.class, index);
    String namespace = mStringPool[namespaceExt.prefix.index];
    String namespaceUri = mStringPool[namespaceExt.uri.index];

    mHandler.endNamespace(namespace, namespaceUri);

    mIndex += node.header.size;
  }

  private void parse(ObjectInput objectInput) throws IOException {
    while (!objectInput.isEof(mIndex)) {
      ResChunkHeader header = objectInput.read(ResChunkHeader.class, mIndex);

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

    mHandler.endXml();
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
   * 设置解析处理器。
   *
   * @param handler 处理器对象。
   * @see Handler
   */
  public void setHandler(Handler handler) {
    this.mHandler = handler;
  }

  /**
   * 解析 Android 二进制格式的 Xml 文件。
   * <p>
   * 首先需要通过 {@link #setHandler(Handler)} 指定解析处理器后才能调用。
   * 解析结果将回调至解析器。
   *
   * @param file 二进制 Xml 文件路径。
   * @throws IOException 文件解析异常。
   */
  public void parse(String file) throws IOException {
    if (mHandler == null) {
      throw new NullPointerException("handler is null");
    }

    mIndex = 0;
    mStringPool = null;
    mNamespaceMap.clear();

    ObjectInput objectInput = null;
    try {
      objectInput = new ObjectInput(file);
      parse(objectInput);
    } finally {
      closeQuietly(objectInput);
    }
  }
}
