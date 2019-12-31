package io.l0neman.axmlparser.type;

import io.l0neman.axmlparser.type.ResChunkHeader;
import io.l0neman.axmlparser.type.ResStringPoolRef;
import io.l0neman.axmlparser.type.ResourceTypes;
import io.l0neman.axmlparser.util.objectio.FieldOrder;
import io.l0neman.axmlparser.util.objectio.Struct;

/*
struct ResXMLTree_node
{
    struct ResChunk_header header;

    // Line number in original source file at which this element appeared.
    uint32_t lineNumber;

    // Optional XML comment that was associated with this element; -1 if none.
    struct ResStringPool_ref comment;
};
 */
public class ResXMLTreeNode implements Struct {
  /**
   * {@link ResChunkHeader#type} =
   * {@link ResourceTypes#RES_XML_START_NAMESPACE_TYPE} or
   * {@link ResourceTypes#RES_XML_END_NAMESPACE_TYPE} or
   * {@link ResourceTypes#RES_XML_START_ELEMENT_TYPE} or
   * {@link ResourceTypes#RES_XML_END_ELEMENT_TYPE} or
   * {@link ResourceTypes#RES_XML_CDATA_TYPE}
   * <p>
   * {@link ResChunkHeader#headerSize} = sizeOf(ResXMLTreeNode.class) 表示头部大小。
   * <p>
   * if (type == RES_XML_START_NAMESPACE_TYPE)
   * <p>
   * {@link ResChunkHeader#size} = sizeof(ResXMLTreeNode.class) + sizeof(ResXMLTreeNamespaceExt.class)
   */
  @FieldOrder(n = 0)
  public ResChunkHeader header;
  /**
   * 命名空间开始标签在原来文本格式的 Xml 文件出现的行号
   */
  @FieldOrder(n = 1)
  public int lineNumber;
  /**
   * 命名空间的注释在字符池资源池的索引。
   */
  @FieldOrder(n = 2)
  public ResStringPoolRef comment;
}
