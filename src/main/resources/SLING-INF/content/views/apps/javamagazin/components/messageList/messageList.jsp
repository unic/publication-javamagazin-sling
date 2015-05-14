<%@include file="../../global.jspf" %>
<html>
<head>
    <title>${properties.title}</title>
</head>
<body>
<h1>${properties.title}</h1>
<p>${properties.description}</p>
<form method="POST" enctype="multipart/form-data" action="${resource.path}/*">
    <input type="hidden" name="sling:resourceType" value="javamagazin/message">
    <input type="hidden" name=":nameHint" value="message">
    <input type="hidden" name=":redirect" value="${resource.path}.html">
    <input type="hidden" name="_charset_" value="UTF-8" />

    <input type="text" name="subject" />
    <textarea name="text"></textarea>
    <button type="submit">Submit</button>
</form>
</body>
</html>