package io.l0neman.axmlparser.type;

import io.l0neman.axmlparser.util.objectio.FieldOrder;
import io.l0neman.axmlparser.util.objectio.Struct;


public class ResXMLTreeNamespaceExt implements Struct {
  /**
   * 命名空间字符串在字符串资源池中的索引
   */
  @FieldOrder(n = 0)
  public ResStringPoolRef prefix;
  /**
   * uri 字符串在字符串资源池中的索引
   */
  @FieldOrder(n = 0)
  public ResStringPoolRef uri;
}
