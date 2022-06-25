package io.l0neman.axmlparser;

import io.l0neman.axmlparser.type.ResStringPoolHeader;
import io.l0neman.axmlparser.type.ResStringPoolRef;
import io.l0neman.axmlparser.type.ResStringPoolSpan;
import io.l0neman.axmlparser.util.objectio.ObjectInput;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class StringPoolChunkParser {

  private ResStringPoolRef[] stringIndexArray;
  private ResStringPoolRef[] styleIndexArray;
  private String[] stringPool;
  private List<ResStringPoolSpan>[] stylePool;

  private ResStringPoolRef[] parseStringIndexArray(ObjectInput objectIO, ResStringPoolHeader header, long index)
      throws IOException {
    stringIndexArray = new ResStringPoolRef[header.stringCount];

    long start = index;
    final int resStringPoolRefSize = ObjectInput.sizeOf(ResStringPoolRef.class);

    for (int i = 0; i < header.stringCount; i++) {
      stringIndexArray[i] = objectIO.read(ResStringPoolRef.class, start);
      start += resStringPoolRefSize;
    }

    return stringIndexArray;
  }

  private ResStringPoolRef[] parseStyleIndexArray(ObjectInput objectIO, ResStringPoolHeader header, long index)
      throws IOException {
    styleIndexArray = new ResStringPoolRef[header.styleCount];

    long start = index;
    final int resStringPoolRefSize = ObjectInput.sizeOf(ResStringPoolRef.class);

    for (int i = 0; i < header.styleCount; i++) {
      styleIndexArray[i] = objectIO.read(ResStringPoolRef.class, start);
      start += resStringPoolRefSize;
    }

    return styleIndexArray;
  }

  private static int parseBytesLength(byte[] b) {
    return b[1] & 0x7F;
  }

  private static int parseStringLength(byte[] b) {
    return b[0] & 0x7F;
  }

  private String[] parseStringPool(ObjectInput objectInput, ResStringPoolHeader header, long stringPoolIndex)
          throws IOException {
    String[] stringPool = new String[header.stringCount];

    for (int i = 0; i < header.stringCount; i++) {
      final long index = stringPoolIndex + stringIndexArray[i].index;

      if (header.flags == 0) {
        final int parseStringLength = parseStringLength(objectInput.readBytes(index, Short.BYTES));
        final int bytesLength = parseStringLength * 2;

        stringPool[i] = new String(objectInput.readBytes(index + Short.BYTES, bytesLength), 0,
                bytesLength, StandardCharsets.UTF_16LE);
      } else {
        final int parseBytesLength = parseBytesLength(objectInput.readBytes(index, Short.BYTES));
        stringPool[i] = new String(objectInput.readBytes(index + Short.BYTES, parseBytesLength), 0,
                parseBytesLength, StandardCharsets.UTF_8);
      }
    }

    return stringPool;
  }

  private List<ResStringPoolSpan>[] parseStylePool(ObjectInput objectIO, ResStringPoolHeader header, long stylePoolIndex)
      throws IOException {
    @SuppressWarnings("unchecked")
    List<ResStringPoolSpan>[] stylePool = new List[header.styleCount];

    for (int i = 0; i < header.styleCount; i++) {
      final long index = stylePoolIndex + styleIndexArray[i].index;
      int end = 0;
      long littleIndex = index;

      List<ResStringPoolSpan> stringPoolSpans = new ArrayList<>();
      while (end != ResStringPoolSpan.END) {
        ResStringPoolSpan stringPoolSpan = objectIO.read(ResStringPoolSpan.class, littleIndex);
        stringPoolSpans.add(stringPoolSpan);

        littleIndex += ObjectInput.sizeOf(ResStringPoolSpan.class);

        end = objectIO.readInt(littleIndex);
      }

      stylePool[i] = stringPoolSpans;
    }
    return stylePool;
  }

  public void parseStringPoolChunk(ObjectInput objectIO, ResStringPoolHeader header, long stringPoolHeaderIndex)
      throws IOException {
    // parse string index array.
    final long stringIndexArrayIndex = stringPoolHeaderIndex + ObjectInput.sizeOf(ResStringPoolHeader.class);

    stringIndexArray = header.stringCount == 0 ? new ResStringPoolRef[0] :
        parseStringIndexArray(objectIO, header, stringIndexArrayIndex);

    final long styleIndexArrayIndex = stringIndexArrayIndex + header.stringCount *
        ObjectInput.sizeOf(ResStringPoolRef.class);

    styleIndexArray = header.styleCount == 0 ? new ResStringPoolRef[0] :
        parseStyleIndexArray(objectIO, header, styleIndexArrayIndex);

    // parse string pool.
    if (header.stringCount != 0) {
      final long stringPoolIndex = stringPoolHeaderIndex + header.stringStart;
      stringPool = parseStringPool(objectIO, header, stringPoolIndex);
    } else {
      stringPool = new String[0];
    }

    // parse style pool.
    if (header.styleCount != 0) {
      final long stylePoolIndex = stringPoolHeaderIndex + header.styleStart;
      stylePool = parseStylePool(objectIO, header, stylePoolIndex);
    } else {
      //noinspection unchecked
      stylePool = new List[0];
    }
  }

  public ResStringPoolRef[] getStringIndexArray() {
    return stringIndexArray;
  }

  public ResStringPoolRef[] getStyleIndexArray() {
    return styleIndexArray;
  }

  public String[] getStringPool() {
    return stringPool;
  }

  public List<ResStringPoolSpan>[] getStylePool() {
    return stylePool;
  }
}
