package io.l0neman.axmlparser.type;

import io.l0neman.axmlparser.util.objectio.FieldOrder;
import io.l0neman.axmlparser.util.objectio.Struct;


/*
struct ResXMLTree_header
{
    struct ResChunk_header header;
};
 */
public class ResXMLTreeHeader implements Struct {
  /**
   * {@link ResChunkHeader#type} = {@link ResourceTypes#RES_XML_TYPE}
   * <p>
   * {@link ResChunkHeader#headerSize} = sizeOf(ResXMLTreeHeader.class) 表示头部大小。
   * <p>
   * {@link ResChunkHeader#size} = 整个二进制 Xml 文件的大小，包括头部的大小。
   */
  @FieldOrder(n = 0)
  public ResChunkHeader header;
}
