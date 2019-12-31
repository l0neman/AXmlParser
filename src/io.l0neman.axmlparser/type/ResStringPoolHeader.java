package io.l0neman.axmlparser.type;

import io.l0neman.axmlparser.util.objectio.FieldOrder;
import io.l0neman.axmlparser.util.objectio.Struct;

/*
struct ResStringPool_header
{
    struct ResChunk_header header;

    // Number of strings in this pool (number of uint32_t indices that follow
    // in the data).
    uint32_t stringCount;

    // Number of style span arrays in the pool (number of uint32_t indices
    // follow the string indices).
    uint32_t styleCount;

    // Flags.
    enum {
        // If set, the string index is sorted by the string values (based
        // on strcmp16()).
        SORTED_FLAG = 1<<0,

        // String pool is encoded in UTF-8
        UTF8_FLAG = 1<<8
    };
    uint32_t flags;

    // Index from header of the string data.
    uint32_t stringsStart;

    // Index from header of the style data.
    uint32_t stylesStart;
};
 */

/**
 * 字符串池头部。
 */
public class ResStringPoolHeader implements Struct {
  /**
   * {@link ResChunkHeader#type} = {@link ResourceTypes#RES_STRING_POOL_TYPE}
   * <p>
   * {@link ResChunkHeader#headerSize} = sizeOf(ResStringPoolHeader.class) 表示头部大小。
   * <p>
   * {@link ResChunkHeader#size} = 整个字符串 Chunk 的大小，包括 headerSize 的大小。
   */
  @FieldOrder(n = 0)
  public ResChunkHeader header;
  /**
   * 字符串的数量
   */
  @FieldOrder(n = 1)
  public int stringCount;
  /**
   * 字符串样式的数量
   */
  @FieldOrder(n = 2)
  public int styleCount;
  /**
   * 0, SORTED_FLAG, UTF8_FLAG or bitwise or value
   */
  @FieldOrder(n = 3)
  public int flags;
  /**
   * 字符串内容块相对于其头部的距离
   */
  @FieldOrder(n = 4)
  public int stringStart;
  /**
   * 字符串样式块相对于其头部的距离
   */
  @FieldOrder(n = 5)
  public int styleStart;
}
