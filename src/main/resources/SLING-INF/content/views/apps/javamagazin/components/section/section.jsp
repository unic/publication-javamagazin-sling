<%@include file="../../global.jspf" %>
<section class="feature ${slingRequest.requestPathInfo.selectorString}">
    <div class="image icon"><img src="${resource.path}/image.png" alt="" /></div>
    <div class="content">
        <h3>${properties.title}</h3>
        <p>${properties.text}</p>
    </div>
</section>
