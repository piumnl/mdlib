# mdlib

即 Markdown Library，存放 Markdown 的文档库，整理自己的笔记。

## 使用相关

### 运行要求

JDK8 + Maven

### 使用

1. 打包 `mvn clean package`
1. 在生成的 jar 目录下创建一个 `application.properties` 文件，加上自己的配置
1. 运行 `java -jar mdlib-1.0-SNAPSHOT.jar`
1. 将目录拖放到 Tomcat 或类似的应用服务器上，也可以直接丢 码云 或 Github 的Pages 服务上。

## application.properties 文件配置

key | type | 默认值 | 说明
:--- | :---: | :---: | :---:
 `lib.name` | String | mdlib | 生成的网站的左上角文字
 `lib.icon` | String | static/ml.ico | 网站图标
 `lib.uri` | String |  | 网站地址前缀，如 <piumnl.gitee.io>
 `lib.resource-path` | String |  | 直接复制不处理的文件夹，格式 /xxx,/xxx
 `lib.resource-default` | Boolean | true | 是否复制默认的资源文件夹，即 jar 中的
 `lib.mixed` | String |  | 一个列表排列下来的索引页面，格式 name:path,name2:path2
 `lib.module` | String |  | 一个两层的树格式的索引页面，格式 name:path,name2:path2
 `lib.single` | String |  | 单独展示的一个页面，格式 name:mdPath, 多用于类似 __关于我__ 这种场景
 `lib.out` | String |  | 输出目录