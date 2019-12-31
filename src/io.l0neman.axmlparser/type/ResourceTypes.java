package io.l0neman.axmlparser.type;

/**
 * 资源表类型。
 */
public class ResourceTypes {

  public static final short RES_NULL_TYPE = 0x0000;
  public static final short RES_STRING_POOL_TYPE = 0x0001;
  public static final short RES_TABLE_TYPE = 0x0002;
  public static final short RES_XML_TYPE = 0x0003;

  public static final short RES_XML_FIRST_CHUNK_TYPE = 0x0100;
  public static final short RES_XML_START_NAMESPACE_TYPE = 0x0100;
  public static final short RES_XML_END_NAMESPACE_TYPE = 0x0101;
  public static final short RES_XML_START_ELEMENT_TYPE = 0x0102;
  public static final short RES_XML_END_ELEMENT_TYPE = 0x0103;
  public static final short RES_XML_CDATA_TYPE = 0x0104;
  public static final short RES_XML_LAST_CHUNK_TYPE = 0x017f;

  public static final short RES_XML_RESOURCE_MAP_TYPE = 0x0180;

  public static final short RES_TABLE_PACKAGE_TYPE = 0x0200;
  public static final short RES_TABLE_TYPE_TYPE = 0x0201;
  public static final short RES_TABLE_TYPE_SPEC_TYPE = 0x0202;
  public static final short RES_TABLE_LIBRARY_TYPE = 0x0203;

  public static String nameOf(int type) {
    switch (type) {
      case RES_NULL_TYPE:
        return "RES_NULL_TYPE";
      case RES_XML_TYPE:
        return "RES_XML_TYPE";
      case RES_XML_START_NAMESPACE_TYPE:
        return "RES_XML_START_NAMESPACE_TYPE";
      case RES_XML_END_NAMESPACE_TYPE:
        return "RES_XML_END_NAMESPACE_TYPE";
      case RES_XML_START_ELEMENT_TYPE:
        return "RES_XML_START_ELEMENT_TYPE";
      case RES_XML_END_ELEMENT_TYPE:
        return "RES_XML_END_ELEMENT_TYPE";
      case RES_XML_CDATA_TYPE:
        return "RES_XML_CDATA_TYPE";
      case RES_XML_LAST_CHUNK_TYPE:
        return "RES_XML_LAST_CHUNK_TYPE";
      case RES_XML_RESOURCE_MAP_TYPE:
        return "RES_XML_RESOURCE_MAP_TYPE";
      case RES_STRING_POOL_TYPE:
        return "RES_STRING_POOL_TYPE";
      case RES_TABLE_TYPE:
        return "RES_TABLE_TYPE";
      case RES_TABLE_PACKAGE_TYPE:
        return "RES_TABLE_PACKAGE_TYPE";
      case RES_TABLE_TYPE_TYPE:
        return "RES_TABLE_TYPE_TYPE";
      case RES_TABLE_TYPE_SPEC_TYPE:
        return "RES_TABLE_TYPE_SPEC_TYPE";
      default:
        return "UNKNOWN";
    }
  }
}
