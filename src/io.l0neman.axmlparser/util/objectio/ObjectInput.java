package io.l0neman.axmlparser.util.objectio;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * 对象输入，提供了一种从文件中解析各种文件格式的通用方法，与 ObjectOutput 配合使用。
 * <p>
 * 接口借鉴 C 语言中的 read 函数用法。
 */
public class ObjectInput implements Closeable {

  private static final int BYTE_SIZE = 8 / Byte.SIZE;
  private static final int CHAR_SIZE = 16 / Byte.SIZE;
  private static final int SHORT_SIZE = 16 / Byte.SIZE;
  private static final int INT_SIZE = 32 / Byte.SIZE;
  private static final int LONG_SIZE = 64 / Byte.SIZE;
  private static final int FLOAT_SIZE = 32 / Byte.SIZE;
  private static final int DOUBLE_SIZE = 64 / Byte.SIZE;

  private ByteOrder byteOrder;
  private final FileChannel inputChannel;
  private final long size;

  /*
    构建类内部成员列表。
   */
  public ObjectInput(String file) throws IOException {
    inputChannel = new FileInputStream(file).getChannel();
    size = inputChannel.size();
  }

  public ObjectInput(String file, boolean bigEndian) throws IOException {
    this(file);
    this.byteOrder = bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
  }

  @SuppressWarnings("unchecked")
  private <T> T fromByteBuffer(Class<T> target, ByteBuffer byteBuffer, int offset) throws Exception {
    // 处理基本类型。
    if (target == byte.class || target == Byte.class) {
      return (T) Byte.valueOf(byteBuffer.get(offset));
    }

    if (target == char.class || target == Character.class) {
      return (T) Character.valueOf((char) byteBuffer.get(offset));
    }

    if (target == short.class || target == Short.class) {
      return (T) Short.valueOf(byteBuffer.getShort(offset));
    }

    if (target == int.class || target == Integer.class) {
      return (T) Integer.valueOf(byteBuffer.getInt(offset));
    }

    if (target == long.class || target == Long.class) {
      return (T) Long.valueOf(byteBuffer.getLong(offset));
    }

    // 处理 Struct 和 Union 类型。
    final boolean isUnion = ClassUtils.isUnion(target);
    if (ClassUtils.isStruct(target) || isUnion) {
      T object = ClassUtils.newObject(target);

      int index = offset;
      Field[] fieldInfoList = ClassUtils.fullDeclaredFields(target);
      for (Field field : fieldInfoList) {
        final Class<?> fieldType = field.getType();

        if (Modifier.isStatic(field.getModifiers())) {
          continue;
        }

        if (!field.isAccessible()) {
          field.setAccessible(true);
        }

        // 处理数组成员类型。
        if (fieldType.isArray()) {
          final Class<?> componentType = field.getType().getComponentType();

          final int length = ClassUtils.getArrayFieldLength(field, target);
          Object arrayField = Array.newInstance(componentType, length);
          final int componentTypeSize = ClassUtils.sizeOf(componentType);

          for (int i = 0; i < length; i++) {
            Object item = fromByteBuffer(componentType, byteBuffer, index);
            Array.set(arrayField, i, item);
            index += componentTypeSize;
          }

          field.set(object, arrayField);
          continue;
        }

        ClassUtils.checkSupportType(fieldType);

        // 非数组递归解析。
        final int fieldTypeSize = ClassUtils.sizeOf(fieldType);

        Object fieldObj = fromByteBuffer(fieldType, byteBuffer, index);
        field.set(object, fieldObj);

        // Union 由于成员共用内存，所以需要从头读取。
        if (!isUnion) {
          index += fieldTypeSize;
        }
      }
      return object;
    }

    throw new IllegalArgumentException("Not a struct or union type: " + target);

  }

  /**
   * 从文件中解析目标类型。
   *
   * @param target 解析目标类型。
   * @param offset 文件偏移量。
   * @param <T>    目标类型的泛型。
   * @return 目标类型解析结果对象。
   * @throws IOException 可能出现的 io 异常。
   */
  public <T extends Struct> T read(Class<T> target, long offset) throws IOException {
    ClassUtils.checkSupportType(target);

    final int size;
    try {
      size = ClassUtils.sizeOf(target);
    } catch (Exception e) {
      throw new IllegalArgumentException("object size is null.", e);
    }

    final ByteBuffer byteBuffer = ByteBuffer.allocate(size);
    byteBuffer.order(byteOrder);
    inputChannel.read(byteBuffer, offset);
    byteBuffer.flip();

    try {
      return fromByteBuffer(target, byteBuffer, 0);
    } catch (Exception e) {
      throw new IOException("read error", e);
    }
  }

  public byte readByte(long offset) throws IOException {
    final ByteBuffer byteBuffer = ByteBuffer.allocate(BYTE_SIZE);
    byteBuffer.order(byteOrder);
    inputChannel.read(byteBuffer, offset);
    byteBuffer.flip();

    return byteBuffer.get();
  }

  public byte[] readBytes(long offset, int size) throws IOException {
    final ByteBuffer byteBuffer = ByteBuffer.allocate(size);
    byteBuffer.order(byteOrder);
    inputChannel.read(byteBuffer, offset);
    byteBuffer.flip();

    byte[] bytes = new byte[size];
    byteBuffer.get(bytes, 0, size);
    return bytes;
  }

  public char readChar(long offset) throws IOException {
    final ByteBuffer byteBuffer = ByteBuffer.allocate(CHAR_SIZE);
    byteBuffer.order(byteOrder);
    inputChannel.read(byteBuffer, offset);
    byteBuffer.flip();

    return byteBuffer.getChar();
  }

  public short readShort(long offset) throws IOException {
    final ByteBuffer byteBuffer = ByteBuffer.allocate(SHORT_SIZE);
    byteBuffer.order(byteOrder);
    inputChannel.read(byteBuffer, offset);
    byteBuffer.flip();

    return byteBuffer.getShort();
  }

  public int readInt(long offset) throws IOException {
    final ByteBuffer byteBuffer = ByteBuffer.allocate(INT_SIZE);
    byteBuffer.order(byteOrder);
    inputChannel.read(byteBuffer, offset);
    byteBuffer.flip();

    return byteBuffer.getInt();
  }

  public long readLong(long offset) throws IOException {
    final ByteBuffer byteBuffer = ByteBuffer.allocate(LONG_SIZE);
    byteBuffer.order(byteOrder);
    inputChannel.read(byteBuffer, offset);
    byteBuffer.flip();

    return byteBuffer.getLong();
  }

  @Override
  public void close() throws IOException {
    ClassUtils.sClassSizeCache.clear();
    ClassUtils.sFullDeclaredFieldsMap.clear();

    if (inputChannel != null && inputChannel.isOpen()) {
      inputChannel.close();
    }
  }

  public boolean isEof(long offset) {
    return offset >= size;
  }

  public long size() {
    return size;
  }

  public static int sizeOf(Class<?> clazz) {
    return ClassUtils.sizeOf(clazz);
  }

  private static class ClassUtils {
    private static final int MAX_CACHE_CLASS = 20;

    private static SimpleLru<Class<?>, Field[]> sFullDeclaredFieldsMap = new SimpleLru<>(MAX_CACHE_CLASS);
    private static SimpleLru<Class<?>, Integer> sClassSizeCache = new SimpleLru<>(20);

    private static Comparator<Field> fieldComparator = new Comparator<Field>() {
      @Override
      public int compare(Field o1, Field o2) {
        final FieldOrder a1 = o1.getAnnotation(FieldOrder.class);
        final FieldOrder a2 = o2.getAnnotation(FieldOrder.class);
        // 静态成员可能没有注解。
        if (a1 == null) {
          return -1;
        }

        if (a2 == null) {
          return 1;
        }

        return a1.n() - a2.n();
      }
    };

    static Field[] fullDeclaredFields(Class<?> clazz) {
      Field[] fullDeclaredFields = sFullDeclaredFieldsMap.get(clazz);
      if (fullDeclaredFields == null) {
        fullDeclaredFields = buildFullDeclaredFields(clazz);
        sFullDeclaredFieldsMap.put(clazz, fullDeclaredFields);
      }

      return fullDeclaredFields;
    }

    private static Field[] buildFullDeclaredFields(Class<?> clazz) {
      List<Field> fullFields = new ArrayList<>();
      Field[] fields = clazz.getDeclaredFields();

      Class<?> parent = clazz.getSuperclass();
      while (parent != Object.class) {
        assert parent != null;
        final Field[] parentFields = parent.getDeclaredFields();

        Arrays.sort(parentFields, fieldComparator);
        fullFields.addAll(Arrays.asList(parentFields));
        parent = parent.getSuperclass();
      }

      Arrays.sort(fields, fieldComparator);
      fullFields.addAll(Arrays.asList(fields));
      return fullFields.toArray(new Field[0]);
    }

    static int sizeOfUnion(Class<?> clazz) {

      if (clazz.isPrimitive()) {
        return sizeOf(clazz);
      }

      Field[] fieldInfoList = fullDeclaredFields(clazz);

      // 取出 union 类型。
      int[] typeSize = new int[fieldInfoList.length];
      for (int i = 0; i < typeSize.length; i++) {
        final Field field = fieldInfoList[i];
        final Class<?> fieldType = field.getType();

        if (Modifier.isStatic(field.getModifiers())) {
          continue;
        }

        // 处理数组成员类型。
        if (fieldType.isArray()) {
          typeSize[i] = sizeOf(fieldType.getComponentType()) * getArrayFieldLength(field, clazz);
          continue;
        }

        typeSize[i] = sizeOf(fieldType);
      }

      // 得到字节占用（最大成员的大小）。
      Arrays.sort(typeSize);
      return typeSize[typeSize.length - 1];
    }

    public static int sizeOf(Class<?> clazz) {
      // 不支持数组类型，因为无法获取数组长度。
      if (clazz.isArray()) {
        throw new IllegalArgumentException("not support array type: " + clazz);
      }

      checkSupportType(clazz);

      // 计算基本类型大小。
      if (clazz == byte.class || clazz == Byte.class) {
        return BYTE_SIZE;
      }

      if (clazz == char.class || clazz == Character.class) {
        return BYTE_SIZE;
      }

      if (clazz == short.class || clazz == Short.class) {
        return SHORT_SIZE;
      }

      if (clazz == int.class || clazz == Integer.class) {
        return INT_SIZE;
      }

      if (clazz == float.class || clazz == Float.class) {
        return FLOAT_SIZE;
      }

      if (clazz == double.class || clazz == Double.class) {
        return DOUBLE_SIZE;
      }

      if (clazz == long.class || clazz == Long.class) {
        return LONG_SIZE;
      }

      final Integer cacheSize = sClassSizeCache.get(clazz);
      if (cacheSize != null) {
        return cacheSize;
      }

      if (isStruct(clazz)) {
        int size = 0;
        Field[] fieldInfoList = fullDeclaredFields(clazz);
        for (Field field : fieldInfoList) {
          final Class<?> fieldType = field.getType();

          if (Modifier.isStatic(field.getModifiers())) {
            continue;
          }

          // 处理数组成员类型。
          if (fieldType.isArray()) {
            final Class<?> componentType = field.getType().getComponentType();
            size += sizeOf(componentType) * getArrayFieldLength(field, clazz);
            continue;
          }

          size += sizeOf(fieldType);
        }

        sClassSizeCache.put(clazz, size);
        return size;
      }

      if (isUnion(clazz)) {
        return sizeOfUnion(clazz);
      }

      throw new IllegalArgumentException("Not a struct or union type: " + clazz);
    }

    private static boolean isStruct(Class<?> type) {
      return Struct.class.isAssignableFrom(type);
    }

    private static boolean isUnion(Class<?> type) {
      return Union.class.isAssignableFrom(type);
    }

    private static void checkSupportType(Class<?> type) {
      if (!type.isPrimitive() && !isStruct(type) && !isUnion(type)) {
        throw new IllegalArgumentException("Not a struct or union type: " + type);
      }

      if (type == double.class || type == Double.class ||
          type == float.class || type == Float.class ||
          type == boolean.class || type == Boolean.class) {
        throw new IllegalArgumentException("Not a struct or union type: " + type);
      }
    }

    static <T> T newObject(Class<T> target) {
      try {
        return target.newInstance();
      } catch (Exception e) {
        throw new IllegalArgumentException("Must implement the default constructor.", e);
      }
    }

    static int getArrayFieldLength(Field field, Class<?> extra) {
      final Object helper = newObject(extra);
      try {
        final Object array = field.get(helper);
        final int length = Array.getLength(array);
        if (length == 0) {
          throw new IllegalArgumentException("array length is zero: [" + field.getType().getComponentType());
        }
        return length;
      } catch (Exception e) {
        throw new IllegalArgumentException("error", e);
      }
    }

    private static final class SimpleLru<K, V> {
      private final LinkedHashMap<K, V> lruMap;

      SimpleLru(final int max) {
        this.lruMap = new LinkedHashMap<K, V>(
            (int) (Math.ceil(max / 0.75F)) + 1, 0.75F, true) {
          @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > max;
          }
        };
      }

      V get(K key) {
        return lruMap.get(key);
      }

      void put(K key, V value) {
        lruMap.put(key, value);
      }

      void clear() {
        lruMap.clear();
      }
    }
  }
}
