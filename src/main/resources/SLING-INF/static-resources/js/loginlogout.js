$(function() {
    $("#login").click(function() {
        $.get("/javamagazin/login.html", function(html) {
            var width = Math.min(window.innerWidth - 40, 400);
            $.blockUI({
                message: $(html),
                css: {
                    width: width + "px",
                    left: (window.innerWidth - width) / 2,
                    padding: "10px"
                }
            });
        });
    });

    $("#logout").click(function() {
        $.get("/system/sling/logout", function(html) {
            document.location.reload(true);
        });
    });
});