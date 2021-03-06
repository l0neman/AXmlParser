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

  private static int parseStringLength(byte[] b) {
    return b[0] & 0x7F;
  }

  private String[] parseStringPool(ObjectInput objectIO, ResStringPoolHeader header, long stringPoolIndex)
      throws IOException {
    String[] stringPool = new String[header.stringCount];

    for (int i = 0; i < header.stringCount; i++) {
      final long index = stringPoolIndex + stringIndexArray[i].index;
      final int parseStringLength = parseStringLength(objectIO.readBytes(index, Short.BYTES));
      // 经过测试，发现 flags 为0 时，字符串每个字符间会间隔一个空白符，长度变为 2 倍。
      final int stringLength = header.flags == 0 ? parseStringLength * 2 : parseStringLength;

      // trim 去除多余空白符。
      stringPool[i] = trim(new String(objectIO.readBytes(index + Short.BYTES, stringLength), 0,
          stringLength, StandardCharsets.UTF_8));
    }

    return stringPool;
  }

  public static String trim(String str) {
    final char[] chars = str.toCharArray();
    int i = 0;
    int j = 0;
    while (j < chars.length) {
      if (chars[j] != 0) {
        chars[i++] = chars[j];
      }
      j++;
    }
    return new String(chars, 0, i);
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
