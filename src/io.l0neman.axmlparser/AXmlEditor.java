package io.l0neman.axmlparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Android Xml 编辑工具。
 */
class AXmlEditor {
  private static final String ACTION_OPEN = "open";
  private static final String ACTION_DATA = "data";
  private static final String ACTION_CLOSE = "close";

  private final StringBuilder xmlBuilder;
  private String tab = "";
  private List<String[]> namespaceUris = new ArrayList<>();
  private String lastAction;

  AXmlEditor() {
    xmlBuilder = new StringBuilder();
    addHeader();
  }

  private void addHeader() {
    xmlBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").append('\n');
  }

  void openElement(String elementName) {
    // 上次是 open 操作，证明本次打开了子标签。
    if (ACTION_OPEN.equals(lastAction)) {
      xmlBuilder.append(">\n");
    }

    xmlBuilder.append(tab).append('<').append(elementName);

    // 最外层的标签添加命名空间。
    if (!namespaceUris.isEmpty()) {
      for (String[] nu : namespaceUris) {
        xmlBuilder.append(' ')
            .append("xmlns:").append(nu[0]).append("=\"").append(nu[1]).append("\"");
      }
      namespaceUris.clear();
    }

    // 每打开一个新元素就增加 tab。
    tab = tab + "  ";
    lastAction = ACTION_OPEN;
  }

  void addNamespace(String namespace, String uri) {
    namespaceUris.add(new String[]{namespace, uri});
  }

  void addAttribute(String name, String value) {
    xmlBuilder.append(' ')
        .append(name).append("=\"").append(value).append("\"");
  }

  void addData(String data) {
    xmlBuilder.append('>').append(data);

    lastAction = ACTION_DATA;
  }

  void closeElement(String elementName) {
    // 每关闭一个元素就减少 tab。
    tab = tab.substring(2);

    StringBuilder t = new StringBuilder();

    // 上次是 open 操作，证明不含有子标签。
    if (ACTION_OPEN.equals(lastAction)) {
      t.append(">");
    } else if (ACTION_CLOSE.equals(lastAction)) {
      t.append(tab);
    }

    t.append("</").append(elementName).append(">\n");

    // 空标签化简。
    if (t.toString().contains("><")) {
      t.delete(0, t.length()).append(" />\n");
    }

    xmlBuilder.append(t);

    lastAction = ACTION_CLOSE;
  }

  /** 输出 xml 文档 */
  String print() {
    return xmlBuilder.toString();
  }
}
