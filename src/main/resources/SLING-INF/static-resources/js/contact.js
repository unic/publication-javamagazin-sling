/**
 * Handles the contact form submission and includes the created resource's success view in the current page.
 */
$(function() {
    $("#contact").submit(function() {
        var $form = $(this);
        $.ajax($form.attr("action"), {
            "method": "POST",
            "dataType" : "json",
            "success": function(response) {
                $.get(response.path + ".success.html", function(html) {
                    $form.replaceWith($(html));
                });
            },
            "data": $form.serialize()
        });
        return false;
    });
});
