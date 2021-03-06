# mdlib

即 Markdown Library，存放 Markdown 的文档库，整理自己的笔记。

## 使用相关

### 运行要求

JDK8 + Maven

### 使用

1. 打包 `mvn clean package`
1. 在生成的 jar 目录下创建一个 `application.properties` 文件，加上自己的配置
1. 运行 `java -jar mdlib-jar-with-dependencies.jar`
1. 将目录拖放到 Tomcat 或类似的应用服务器上，也可以直接丢 码云 或 Github 的Pages 服务上。

### 参数说明

`java -jar mdlib-jar-with-dependencies.jar [command args]`

- `-run/-r`: 是否运行静态服务器，以方便预览
- `-port/-p`: 设置静态服务器的端口，默认为 20000

## application.properties 文件配置

key | type | 默认值 | 说明
:--- | :---: | :---: | :---:
lib.name | String | mdlib | 生成的网站的左上角文字
lib.icon | String | static/ml.ico | 网站图标
lib.uri | String |  | 网站地址前缀，如 <piumnl.gitee.io>
lib.resource-path | String |  | 直接复制不处理的文件夹，格式 /xxx,/xxx
lib.resource-default | Boolean | true | 是否复制默认的资源文件夹，即 jar 中的
lib.mixed.name | String |  | 列表格式的索引页面，格式 path,path2
lib.module.name | String |  | 两层树型格式的索引页面，格式 path,path2
lib.single.name | String |  | 单独展示的页面，格式 mdPath, 多用于类似 __关于我__ 这种场景
lib.out | String | mdlib | 输出目录
lib.code | String | code | 要识别的代码库目录

关于 lib.mixed 、 lib.module 和 lib.single 格式有如下示例：

```properties
# 此处的 blog 为一个目录，与运行 jar 的目录相同
lib.mixed.博客 = blog
# 类似上面的
lib.module.Java = core,framework
# 为一个单独的 md
lib.single.关于我 = about.md
```

因为 `代码库` 中的所有文件将会作为文本读取，
__所以不能在 `代码库` 该目录下存放二进制文件（比如 pdf、doc(x)、xmind等文件类型）__。

效果如下：

Mixed 树型格式：

![首页](img/mixed.png)

详情页

![详情页](img/detail.png)

代码库页

![代码库页](img/code.png)
