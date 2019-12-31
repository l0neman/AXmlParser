package io.l0neman.axmlparser.type;

import io.l0neman.axmlparser.util.objectio.FieldOrder;
import io.l0neman.axmlparser.util.objectio.Struct;


public class ResXMLTreeCdataExt implements Struct {
  /**
   * CDATA 原始值在字符串池中的索引
   */
  @FieldOrder(n = 0)
  public ResStringPoolRef data;
  /**
   * CDATA
   */
  @FieldOrder(n = 1)
  public ResValue typeData;
}
