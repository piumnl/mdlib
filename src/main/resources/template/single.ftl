<#-- @ftlvariable name="content" type="java.lang.String" -->
<#-- @ftlvariable name="icon" type="java.lang.String" -->
<#-- @ftlvariable name="uri" type="java.lang.String" -->
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
    <link rel="shortcut icon" href="${icon}" title="Favicon" />
    <link href="${uri}/static/css/icon.css" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="${uri}static/css/materialize.min.css" media="screen,projection" />
    <link rel="stylesheet" href="${uri}/static/github.css" type="text/css"/>
    <link rel="stylesheet" href="${uri}/static/mdlib.css" type="text/css"/>
    <style>
        nav {
            background-color: #fff;
        }
        .mdlib-title a {
            color: #424242;
        }
    </style>
</head>
<body>
    <nav>
        <div class="nav-wrapper">
            <a href="#" class="brand-logo">${siteName}</a>
            <ul id="nav-mobile" class="right hide-on-med-and-down">
                    <#list libraries as path>
                        <li><a href="${path.url}">${path.name}</a></li>
                    </#list>
            </ul>
        </div>
    </nav>

    <div class="mdlib-md-body">
        <div class="mth-layout mth-shadow mth-card">
            ${content}
        </div>
    </div>
    <script type="text/javascript" src="${uri}static/js/jquery-3.2.1.min.js"></script>
    <script src="${uri}static/js/materialize.min.js"></script>
</body>
</html>