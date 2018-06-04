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
    <link type="text/css" rel="stylesheet" href="${uri}/static/css/metroStyle.css" />
    <style>
        body {
            overflow: hidden;
        }
        * {
            padding: 0;
            margin: 0;
        }
        .left {
            width: 30%;
            float: left;
            border-right: 1px solid #CCC;
            overflow: auto;
        }
        .right {
            margin-left: 30%;
            overflow: auto;
        }
        .content-iframe {
            border: none;
            width: 100%;
            height: calc(100% - 4px);
        }
    </style>
</head>
<body>
<div class="left">
    <ul id="codeTree" class="ztree"></ul>
</div>
<div class="right">
    <iframe src="" name="content" class="content-iframe"></iframe>
</div>
<script type="text/javascript" src="${uri}/static/js/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="${uri}/static/js/jquery.ztree.all.min.js"></script>
<script>
    $(document).ready(function(){
        let isStandardMode = document.compatMode && document.compatMode === 'CSS1Compat';
        let body = isStandardMode ? document.documentElement : document.body;
        let height = body.clientHeight;
        $('.left').css('height', height);
        $('.right').css('height', height);

        let setting = {
            data: {
                simpleData: {
                    enable: true
                }
            }
        };

        let zNodes = ${tree};
        zNodes.target = 'content';
        nodeAddTarget(zNodes.children);
        $.fn.zTree.init($("#codeTree"), setting, zNodes);
    });

    function nodeAddTarget(nodes) {
        if (!nodes) {
            return;
        }

        for (let i = 0; i < nodes.length; i++) {
            if (nodes[i].children) {
                nodeAddTarget(nodes[i].children);
                nodes[i].url = undefined;
                nodes[i].target = undefined;
            } else {
                let extIndex = nodes[i].url.indexOf('.');
                if (extIndex !== -1) {
                    nodes[i].name = nodes[i].name.substr(0, nodes[i].name.indexOf('.'));
                    nodes[i].url = nodes[i].url.substr(0, extIndex) + '.html';
                    nodes[i].target = nodes[i].target || 'content';
                } else {
                    nodes[i].url = undefined;
                    nodes[i].target = undefined;
                }
            }
        }
    }
</script>
</body>
</html>