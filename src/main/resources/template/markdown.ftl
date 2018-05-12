<#-- @ftlvariable name="icon" type="java.lang.String" -->
<#-- @ftlvariable name="uri" type="java.lang.String" -->
<#-- @ftlvariable name="title" type="java.lang.String" -->
<#-- @ftlvariable name="content" type="java.lang.String" -->
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>${title}</title>
    <link rel="shortcut icon" href="${uri}/${icon}" title="Favicon" />
    <link rel="stylesheet" href="${uri}/static/github.css" type="text/css"/>
    <link rel="stylesheet" href="${uri}/static/mdlib.css" type="text/css"/>
    <style>
        #content {
            width:80%;
            margin: auto;
        }

        .content {
            margin-left: 5% !important;
            width: 65%;
        }
    </style>
</head>
<body>
<div class="mth-layout mth-shadow mth-card content">
    ${content}
</div>
</body>
</html>