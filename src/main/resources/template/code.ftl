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
</head>
<body>
<div class="left">
    <ul id="treeDemo" class="ztree"></ul>
</div>
<script type="text/javascript" src="${uri}/static/js/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="${uri}/static/js/jquery.ztree.all.min.js"></script>
<script>
    $(document).ready(function(){
        var setting = {
            data: {
                simpleData: {
                    enable: true
                }
            }
        };

        var zNodes = ${tree};

        $.fn.zTree.init($("#treeDemo"), setting, zNodes);
    });
</script>
</body>
</html>