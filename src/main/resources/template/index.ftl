<#-- @ftlvariable name="uri" type="java.lang.String" -->
<#-- @ftlvariable name="bookshelf" type="java.util.Map<String, Map<String, String>>" -->
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
    <link href="${uri}/static/css/icon.css" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="${uri}/static/css/materialize.min.css" media="screen,projection" />
    <style>
        #content {
            width:80%;
            margin: auto;
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

    <div id="content">
        <ul class="collapsible" data-collapsible="accordion">
            <#list bookshelf?keys as dir>
                <li>
                    <div class="collapsible-header">
                        ${dir}
                        <#--<i class="material-icons">whatshot</i>-->
                    </div>
                    <div class="collapsible-body">
                        <div class="collection">
                            <#assign articles = bookshelf[dir]>
                            <#list articles?keys as url>
                            <a href="${url}" class="collection-item">${articles[url]}</a>
                            </#list>
                        </div>
                    </div>
                </li>
            </#list>
        </ul>
    </div>
    <script type="text/javascript" src="${uri}/static/js/jquery-3.2.1.min.js"></script>
    <script src="${uri}/static/js/materialize.min.js"></script>
</body>
</html>