package io.l0neman.axmlparser.type;

/*
struct ResStringPool_ref
{
    // Index into the string pool table (uint32_t-offset from the indices
    // immediately after ResStringPool_header) at which to find the location
    // of the string data in the pool.
    uint32_t index;
};
 */

import io.l0neman.axmlparser.util.objectio.FieldOrder;
import io.l0neman.axmlparser.util.objectio.Struct;

/**
 * 字符串在字符串内容块中的字节偏移。
 */
public class ResStringPoolRef implements Struct {

  @FieldOrder(n = 0)
  public int index;
}
