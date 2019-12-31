package io.l0neman.axmlparser.type;

import io.l0neman.axmlparser.util.Formatter;
import io.l0neman.axmlparser.util.objectio.FieldOrder;
import io.l0neman.axmlparser.util.objectio.Struct;


/*
struct ResXMLTree_attribute
{
    // Namespace of this attribute.
    struct ResStringPool_ref ns;

    // Name of this attribute.
    struct ResStringPool_ref name;

    // The original raw string value of this attribute.
    struct ResStringPool_ref rawValue;

    // Processesd typed value of this attribute.
    struct Res_value typedValue;
};
 */
public class ResXMLTreeAttribute implements Struct {
  /**
   * 表示属性的命令空间在字符池资源池的索引，未指定则等于 -1
   */
  @FieldOrder(n = 0)
  public ResStringPoolRef ns;
  /**
   * 属性名称字符串在字符池资源池的索引
   */
  @FieldOrder(n = 1)
  public ResStringPoolRef name;
  /**
   * 属性的原始值在字符池资源池的索引，这是可选的，如果不保留，它的值等于 -1
   */
  @FieldOrder(n = 2)
  public ResStringPoolRef rawValue;
  /**
   * resValue
   */
  @FieldOrder(n = 3)
  public ResValue typeValue;
}
