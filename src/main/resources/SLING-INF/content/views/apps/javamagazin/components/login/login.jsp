<%@include file="../../global.jspf" %>
<form method="post" enctype="multipart/form-data" action="${resource.path}.html/j_security_check">
    <input type="hidden" name="resource" value="${resource.parent.path}.html" />
    <div class="row">
        <div class="12u 12u(mobilep)">
            <label>Name: <input type="text" name="j_username"/></label><br/>
            <label>Password: <input type="password" name="j_password"/></label>
        </div>
    </div>
    <div class="row">
        <div class="12u">
            <ul class="actions">
                <li><input type="submit" value="Login" /></li>
            </ul>
        </div>
    </div>
</form>
