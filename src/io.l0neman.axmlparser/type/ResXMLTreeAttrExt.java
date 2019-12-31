package io.l0neman.axmlparser.type;

import io.l0neman.axmlparser.type.ResStringPoolRef;
import io.l0neman.axmlparser.util.objectio.FieldOrder;
import io.l0neman.axmlparser.util.objectio.Struct;


/*
struct ResXMLTree_attrExt
{
    // String of the full namespace of this element.
    struct ResStringPool_ref ns;

    // String name of this node if it is an ELEMENT; the raw
    // character data if this is a CDATA node.
    struct ResStringPool_ref name;

    // Byte offset from the start of this structure where the attributes start.
    uint16_t attributeStart;

    // Size of the ResXMLTree_attribute structures that follow.
    uint16_t attributeSize;

    // Number of attributes associated with an ELEMENT.  These are
    // available as an array of ResXMLTree_attribute structures
    // immediately following this node.
    uint16_t attributeCount;

    // Index (1-based) of the "id" attribute. 0 if none.
    uint16_t idIndex;

    // Index (1-based) of the "class" attribute. 0 if none.
    uint16_t classIndex;

    // Index (1-based) of the "style" attribute. 0 if none.
    uint16_t styleIndex;
};

 */
public class ResXMLTreeAttrExt implements Struct {
  /**
   * 元素所在命令空间在字符池资源池的索引，未指定则为 -1
   */
  @FieldOrder(n = 0)
  public ResStringPoolRef ns;
  /**
   * 元素名称在字符串池资源的索引
   */
  @FieldOrder(n = 1)
  public ResStringPoolRef name;
  /**
   * 等于 sizeOf(ResXMLTreeAttrExt.class)，表示元素属性 chunk 相对于 RES_XML_START_ELEMENT_TYPE 头部的偏移。
   */
  @FieldOrder(n = 2)
  public short attributeStart;
  /**
   * sizeOf(ResXMLTreeAttribute.class)，表示每一个属性占据的 chunk 大小
   */
  @FieldOrder(n = 3)
  public short attributeSize;
  /**
   * 表示属性 chunk 数量
   */
  @FieldOrder(n = 4)
  public short attributeCount;
  /**
   * 如果元素有一个名称为 id 的属性，那么将它出现在属性列表中的位置再加上 1 的值记录在 idIndex 中，否则 idIndex 的值等于 0。
   */
  @FieldOrder(n = 5)
  public short idIndex;
  /**
   * 如果元素有一个名称为 class 的属性，那么将它出现在属性列表中的位置再加上 1 的值记录在 classIndex 中，否则 classIndex 的值等于 0。
   */
  @FieldOrder(n = 6)
  public short classIndex;
  /**
   * 如果元素有一个名称为 style 的属性，那么将它出现在属性列表中的位置再加上 1 的值记录在 styleIndex 中，否则 styleIndex 的值等于 0。
   */
  @FieldOrder(n = 7)
  public short styleIndex;
}
