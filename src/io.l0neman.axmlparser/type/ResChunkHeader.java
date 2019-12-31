package io.l0neman.axmlparser.type;
/*
struct ResChunk_header
{
    // Type identifier for this chunk.  The meaning of this value depends
    // on the containing chunk.
    uint16_t type;

    // Size of the chunk header (in bytes).  Adding this value to
    // the address of the chunk allows you to find its associated data
    // (if any).
    uint16_t headerSize;

    // Total size of this chunk (in bytes).  This is the chunkSize plus
    // the size of any data associated with the chunk.  Adding this value
    // to the chunk allows you to completely skip its contents (including
    // any child chunks).  If this value is the same as chunkSize, there is
    // no data associated with the chunk.
    uint32_t size;
};
 */

import io.l0neman.axmlparser.util.objectio.FieldOrder;
import io.l0neman.axmlparser.util.objectio.Struct;

/**
 * 资源表 Chunk 基础结构。
 */
public class ResChunkHeader implements Struct {

  /**
   * Chunk 类型
   */
  @FieldOrder(n = 0)
  public short type;
  /**
   * Chunk 头部大小
   */
  @FieldOrder(n = 1)
  public short headerSize;
  /**
   * Chunk 大小
   */
  @FieldOrder(n = 2)
  public int size;
}
