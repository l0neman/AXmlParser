package io.l0neman.axmlparser.type;

import io.l0neman.axmlparser.util.objectio.FieldOrder;
import io.l0neman.axmlparser.util.objectio.Struct;


public class ResXMLTreeEndElementExt implements Struct {
  /**
   * 元素的命名空间的字符串在字符串池中的索引，未指定则为 -1
   */
  @FieldOrder(n = 0)
  public ResStringPoolRef ns;
  /**
   * 元素名称字符串在字符串池中的索引
   */
  @FieldOrder(n = 1)
  public ResStringPoolRef name;
}
