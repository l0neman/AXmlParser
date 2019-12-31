package io.l0neman.axmlparser.type;

/*
struct ResStringPool_span
{
    enum {
        END = 0xFFFFFFFF
    };

    // This is the name of the span -- that is, the name of the XML
    // tag that defined it.  The special value END (0xFFFFFFFF) indicates
    // the end of an array of spans.
    ResStringPool_ref name;

    // The range of characters in the string that this span applies to.
    uint32_t firstChar, lastChar;
};
 */

import io.l0neman.axmlparser.util.objectio.FieldOrder;
import io.l0neman.axmlparser.util.objectio.Struct;

/**
 * 字符串样式块中的字符串样式信息。
 */
public class ResStringPoolSpan implements Struct {

  public static final int END = 0xFFFFFFFF;

  /**
   * 本样式在字符串内容块中的字节位置
   */
  @FieldOrder(n = 0)
  public ResStringPoolRef name;
  /**
   * 包含样式的字符串的第一个字符索引
   */
  @FieldOrder(n = 1)
  public int firstChar;
  /**
   * 包含样式的字符串的最后一个字符索引
   */
  @FieldOrder(n = 2)
  public int lastChar;
}
