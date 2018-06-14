<#-- @ftlvariable name="icon" type="java.lang.String" -->
<#-- @ftlvariable name="uri" type="java.lang.String" -->
<#-- @ftlvariable name="bookshelf" type="java.util.Set<cn.piumnl.mdlib.entity.ArchiveIndex>" -->
<#-- @ftlvariable name="libraries" type="java.util.List<cn.piumnl.mdlib.entity.Library>" -->
<#-- @ftlvariable name="siteName" type="java.lang.String" -->
<#-- @ftlvariable name="title" type="java.lang.String" -->
<#-- @ftlvariable name="tree" type="java.lang.String" -->
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
    <link type="text/css" rel="stylesheet" href="${uri}/static/css/metroStyle.css" />
    <style>
        body {
            overflow: hidden;
        }
        * {
            padding: 0;
            margin: 0;
        }
        .code-left {
            width: 30%;
            float: left;
            border-right: 1px solid #CCC;
            overflow: auto;
        }
        .code-right {
            margin-left: 30%;
            overflow: auto;
        }
        .content-iframe {
            border: none;
            width: 100%;
            height: calc(100% - 0.4rem);
        }
        .search {
            cursor:pointer;
            position: absolute;
            right: 1rem;
            top: 0.6rem;
        }
        #code-tree-div {
            margin: 0 0 0 0.5rem;
        }
    </style>
</head>
<body>
<div class="code-left">
    <div class="col s12">
        <div class="row">
            <div class="input-field col s12">
                <input type="text" id="search-input" class="autocomplete" name="search" />
                <i class="material-icons small search">search</i>
                <label for="search-input">搜索...</label>
            </div>
        </div>
    </div>
    <hr />
    <div id="code-tree-div">
        <ul id="codeTree" class="ztree"></ul>
    </div>
</div>
<div class="code-right">
    <iframe src="" name="content" class="content-iframe"></iframe>
</div>
<script type="text/javascript" src="${uri}/static/js/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="${uri}/static/js/jquery.ztree.all.min.js"></script>
<script type="text/javascript" src="${uri}/static/js/materialize.min.js"></script>
<script> let nodes = ${tree};</script>
<script type="text/javascript" src="${uri}/static/js/code.js"></script>
</body>
</html>