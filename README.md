# AXmlParser

- [介绍](#介绍)
- [说明](#说明)
- [使用](#使用)
  - [AXmlParser](#axmlparser)
  - [AXmlPrinter](#axmlprinter)
- [混淆配置](#混淆配置)
- [API 参考](#api-参考)
  - [AXmlParser](#axmlparser)
  - [AXmlPrinter](#axmlprinter)
- [测试](#测试)
  - [AXmlParser Test](#axmlparser-test)
  - [AXmlPrinter Test](#axmlprinter-test)
- [原理](#原理)
- [LICENSE](#license)

## 介绍

Android 二进制 Xml 文件解析器。

用于直接解析从 Apk 文件中解压出来的二进制格式的 Xml 文件内容。

例如 Apk 文件中的 AndroidManifest.xml（清单）、activity_main.xml（布局）等。

## 说明

项目编译环境 Java 8+，IntelliJ IDEA（Java 项目）或 Android Studio（Android 项目）均可。

项目包含两个可用导出类， `AXmlParser` 和 `AXmlPrinter`。

1. AXmlParser - 用于完整解析 Xml 内容，使用方法类似于标准 Sax 解析器。

2. AXmlPrinter - 用于直接打印出 Xml 内容，输出格式为标准 Xml 文档格式。

## 使用

首先 clone 项目至本地。

### AXmlParser

Xml 解析器。

1. 创建解析器对象。

```java
AXmlParser aXmlParser = new AXmlParser();
```

2. 设置解析处理器。

```java
aXmlParser.setHandler(new AXmlParser.Handler() {
  @Override protected void startXml() {
    System.out.println("#startXml");
  }

  @Override protected void startNamespace(String ns, String nsUri) {
    System.out.println(String.format("#endNamespace ns: \"%s\", nsUri: \"%s\"", ns, nsUri));
  }

  @Override protected void startElement(String ns, String nsUri, String elementName) {
    System.out.println(String.format("#startElement ns: \"%s\", nsUri: \"%s\", elementName: \"%s\"",
        ns, nsUri, elementName));
  }

  @Override protected void onData(String data) {
    System.out.println(String.format("#onData data \"%s\"", data));
  }

  @Override protected void onAttribute(String ns, String nsUri, String attrName, String attrValue) {
    System.out.println(String.format("#onAttribute ns: \"%s\", nsUri: \"%s\", attrName \"%s\", attrValue \"%s\"",
        ns, nsUri, attrName, attrValue));
  }

  @Override protected void endElement(String ns, String nsUri, String elementName) {
    System.out.println(String.format("#endNamespace ns: \"%s\", nsUri: \"%s\"", ns, nsUri));
  }

  @Override protected void endNamespace(String ns, String nsUri) {
    System.out.println(String.format("#endNamespace ns: \"%s\", nsUri: \"%s\"", ns, nsUri));
  }

  @Override protected void endXml() {
    System.out.println("#endXml");
  }
});
```

3. 开始解析

```java
try {
  // 字符串为项目中的测试文件，从 app-debug.apk 中提取。
  aXmlParser.parse(".\\file\\AndroidManifest.xml");
  // 这里解析两个文件，为了展示两种不同类型的 xml 文件解析。
  aXmlParser.parse(".\\file\\activity_main.xml");
} catch (IOException e) {
  e.printStackTrace();
}
```

注意：解析操作为耗时操作，推荐在 Android 开发中使用线程执行。

### AXmlPrinter

创建打印器对象并解析输出 Xml 文件内容。

```java
AXmlPrinter aXmlPrinter = new AXmlPrinter();
try {
  aXmlPrinter.print(".\\file\\AndroidManifest.xml");
  aXmlPrinter.print(".\\file\\activity_main.xml");
} catch (IOException e) {
  e.printStackTrace();
}
```

备注：由于解析的 Xml 文件中可能包含资源 ID，这里无法解析出资源名称（例如：res/abc.png），直接展示为 ID 的 16 进制值（例如：0x7f070000），因为资源名称的信息包含在 Apk 的 resources.arsc 文件中，目前为基于 Xml 文件的独立解析，所以无法解析。

## 混淆配置

使用在 Android 项目中，需要将如下混淆配置加入。

```
# 需要保证 type 包中的类成员顺序，避免混淆。
-keep class io.l0neman.axmlparser.type.** { *; }
```

## API 参考

### AXmlParser

```java
/**
 * 解析处理器。
 */
public static abstract class Handler {

  /**
   * 开始解析 xml 文件。
   */
  protected void startXml() {}

  /**
   * 命名空间解析起始点。
   *
   * @param ns    命名空间名称。
   * @param nsUri 命名空间 uri。
   */
  protected void startNamespace(String ns, String nsUri) {}

  /**
   * 元素解析起始点。
   *
   * @param ns          元素命名空间名称（可能为 null）。
   * @param nsUri       元素命名空间 uri（可能为 null）。
   * @param elementName 元素名称。
   */
  protected void startElement(String ns, String nsUri, String elementName) {}

  /**
   * 元素标签中包含的数据。
   *
   * @param data 字符串数据。
   */
  protected void onData(String data) {}

  /**
   * 元素属性解析数据回调。
   *
   * @param ns        属性命名空间名称（可能为 null）。
   * @param nsUri     属性命名空间 uri（可能为 null）。
   * @param attrName  属性名。
   * @param attrValue 属性值。
   */
  protected void onAttribute(String ns, String nsUri, String attrName, String attrValue) {}

  /**
   * 元素解析终止点。
   *
   * @param ns          元素命名空间名称（可能为 null）。
   * @param nsUri       元素命名空间 uri（可能为 null）。
   * @param elementName 元素名称。
   */
  protected void endElement(String ns, String nsUri, String elementName) {}

  /**
   * 命名空间解析终止点。
   *
   * @param ns    命名空间名称。
   * @param nsUri 命名空间 uri。
   */
  protected void endNamespace(String ns, String nsUri) {}

  /**
   * 解析 xml 完毕。
   */
  protected void endXml() {}
}

/**
 * 设置解析处理器。
 *
 * @param handler 处理器对象。
 * @see Handler
 */
public void setHandler(Handler handler);

 /**
  * 解析 Android 二进制格式的 Xml 文件。
  * 
  * 首先需要通过 {@link #setHandler(Handler)} 指定解析处理器后才能调用。
  * 解析结果将回调至解析器。
  *
  * @param file 二进制 Xml 文件路径。
  * @throws IOException 文件解析异常。
  */
 public void parse(String file) throws IOException;
```

### AXmlPrinter

```java
/**
  * 将 Android 二进制格式的 Xml 文件以标准 Xml 文本格式打印出来。
  *
  * @param file 文件路径。
  * @throws IOException 文件读取异常。
  */
public void print(String file) throws IOException;
```

## 测试

### AXmlParser Test

执行以上代码测试结果：

```
#startXml
#endNamespace ns: "android", nsUri: "http://schemas.android.com/apk/res/android"
#startElement ns: "null", nsUri: "null", elementName: "manifest"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "versionCode", attrValue "1"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "versionName", attrValue "1.0"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "compileSdkVersion", attrValue "29"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "compileSdkVersionCodename", attrValue "10"
#onAttribute ns: "null", nsUri: "null", attrName "package", attrValue "io.l0neman.channelreader.example"
#onAttribute ns: "null", nsUri: "null", attrName "platformBuildVersionCode", attrValue "29"
#onAttribute ns: "null", nsUri: "null", attrName "platformBuildVersionName", attrValue "10"
#startElement ns: "null", nsUri: "null", elementName: "uses-sdk"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "minSdkVersion", attrValue "15"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "targetSdkVersion", attrValue "29"
#endNamespace ns: "null", nsUri: ""
#startElement ns: "null", nsUri: "null", elementName: "application"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "theme", attrValue "2131558405"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "label", attrValue "2131492891"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "icon", attrValue "2131427328"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "debuggable", attrValue "-1"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "testOnly", attrValue "-1"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "allowBackup", attrValue "-1"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "supportsRtl", attrValue "-1"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "roundIcon", attrValue "2131427329"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "appComponentFactory", attrValue "androidx.core.app.CoreComponentFactory"
#startElement ns: "null", nsUri: "null", elementName: "activity"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "name", attrValue "io.l0neman.channelreader.example.MainActivity"
#startElement ns: "null", nsUri: "null", elementName: "intent-filter"
#startElement ns: "null", nsUri: "null", elementName: "action"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "name", attrValue "android.intent.action.MAIN"
#endNamespace ns: "null", nsUri: ""
#startElement ns: "null", nsUri: "null", elementName: "category"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "name", attrValue "android.intent.category.LAUNCHER"
#endNamespace ns: "null", nsUri: ""
#endNamespace ns: "null", nsUri: ""
#endNamespace ns: "null", nsUri: ""
#endNamespace ns: "null", nsUri: ""
#endNamespace ns: "null", nsUri: ""
#endNamespace ns: "android", nsUri: "http://schemas.android.com/apk/res/android"
#endXml

#startXml
#endNamespace ns: "android", nsUri: "http://schemas.android.com/apk/res/android"
#endNamespace ns: "app", nsUri: "http://schemas.android.com/apk/res-auto"
#startElement ns: "null", nsUri: "null", elementName: "FrameLayout"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "layout_width", attrValue "-1"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "layout_height", attrValue "-1"
#startElement ns: "null", nsUri: "null", elementName: "androidx.appcompat.widget.AppCompatTextView"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "layout_gravity", attrValue "17"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "id", attrValue "2131165359"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "layout_width", attrValue "-2"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "layout_height", attrValue "-2"
#onAttribute ns: "android", nsUri: "http://schemas.android.com/apk/res/android", attrName "text", attrValue "Hello World!"
#onAttribute ns: "null", nsUri: "null", attrName "style", attrValue "2131558596"
#endNamespace ns: "null", nsUri: ""
#endNamespace ns: "null", nsUri: ""
#endNamespace ns: "app", nsUri: "http://schemas.android.com/apk/res-auto"
#endNamespace ns: "android", nsUri: "http://schemas.android.com/apk/res/android"
#endXml
```

### AXmlPrinter Test

测试结果：

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android" android:versionCode="1" android:versionName="1.0" android:compileSdkVersion="29" android:compileSdkVersionCodename="10" package="io.l0neman.channelreader.example" platformBuildVersionCode="29" platformBuildVersionName="10">
  <uses-sdk android:minSdkVersion="15" android:targetSdkVersion="29" />
  <application android:theme="@0x7f0d0005" android:label="@0x7f0c001b" android:icon="@0x7f0b0000" android:debuggable="true" android:testOnly="true" android:allowBackup="true" android:supportsRtl="true" android:roundIcon="@0x7f0b0001" android:appComponentFactory="androidx.core.app.CoreComponentFactory">
    <activity android:name="io.l0neman.channelreader.example.MainActivity">
      <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
      </intent-filter>
    </activity>
  </application>
</manifest>
```



```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android" xmlns:app="http://schemas.android.com/apk/res-auto" android:layout_width="-1" android:layout_height="-1">
  <androidx.appcompat.widget.AppCompatTextView android:layout_gravity="0x00000011" android:id="@0x7f0700af" android:layout_width="-2" android:layout_height="-2" android:text="Hello World!" style="@0x7f0d00c4" />
</FrameLayout>
```

## 原理

请参考：[Android 二进制 Xml 文件解析](https://github.com/l0neman/program-notes/blob/master/android-notes-reverse/android_xml_parse.md)

## LICENSE

```
Copyright 2020 l0neman

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
