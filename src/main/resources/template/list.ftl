<#-- @ftlvariable name="icon" type="java.lang.String" -->
<#-- @ftlvariable name="uri" type="java.lang.String" -->
<#-- @ftlvariable name="articles" type="java.util.List<cn.piumnl.mdlib.entity.Article>" -->
<#-- @ftlvariable name="libraries" type="java.util.List<cn.piumnl.mdlib.entity.Library>" -->
<#-- @ftlvariable name="siteName" type="java.lang.String" -->
<#-- @ftlvariable name="title" type="java.lang.String" -->
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>${title}</title>
    <link rel="shortcut icon" href="${uri}/${icon}" title="Favicon" />
    <link href="${uri}/static/css/icon.css" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="${uri}/static/css/materialize.min.css" media="screen,projection" />
    <style>
        #content {
            width:80%;
            margin: auto;
        }
        .mdlib-collection {
            border-top: 1px solid #ddd;
            border-right: 1px solid #ddd;
            border-left: 1px solid #ddd;
        }
        nav {
            background-color: #fff;
        }
        .mdlib-title {
            margin-left: 1rem;
        }
        .mdlib-title a {
            color: #424242;
        }
    </style>
</head>
<body>
    <nav>
        <div class="nav-wrapper">
            <div class="mdlib-title">
                <a href="#" class="brand-logo">${siteName}</a>
                <ul id="nav-mobile" class="right hide-on-med-and-down">
                <#list libraries as path>
                    <li><a href="${path.url}">${path.name}</a></li>
                </#list>
                </ul>
            </div>
        </div>
    </nav>

    <div id="content">
        <div class="collection mdlib-collection">
        <#list articles as article>
            <a href="${article.url}" class="collection-item">${article.name}</a>
        </#list>
        </div>
    </div>

    <script type="text/javascript" src="${uri}/static/js/jquery-3.2.1.min.js"></script>
    <script src="${uri}/static/js/materialize.min.js"></script>
</body>
</html>