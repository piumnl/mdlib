<#-- @ftlvariable name="icon" type="java.lang.String" -->
<#-- @ftlvariable name="uri" type="java.lang.String" -->

<#-- @ftlvariable name="title" type="java.lang.String" -->
<#-- @ftlvariable name="language" type="java.lang.String" -->
<#-- @ftlvariable name="content" type="java.lang.String" -->

<#-- 以下为未使用的变量 -->

<#-- @ftlvariable name="bookshelf" type="java.util.Map<String, Map<String, String>>" -->
<#-- @ftlvariable name="libraries" type="java.util.List<cn.piumnl.mdlib.entity.Library>" -->
<#-- @ftlvariable name="siteName" type="java.lang.String" -->

<#-- @ftlvariable name="extName" type="java.lang.String" -->
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>${title}</title>
    <link rel="shortcut icon" href="${uri}/${icon}" title="Favicon" />
    <link href="${uri}/static/css/icon.css" rel="stylesheet">
    <link rel="stylesheet" href="${uri}/static/css/highlight/default.css">
    <style>
        body,p,code,h1, h2, h3, h4, h5, h6,.hljs {
            font-family: 'Source Code Pro', Consolas, Menlo, Monaco, monospace;
        }
        .hljs {
            font-size: 0.8em
        }
    </style>
</head>
<body>
<pre><code class="${language} hljs">${content?html}</code></pre>
<script type="text/javascript" src="${uri}/static/js/jquery-3.2.1.min.js"></script>
<script src="${uri}/static/js/highlight.pack.js"></script>
<script>hljs.initHighlightingOnLoad();</script>
</body>
</html>