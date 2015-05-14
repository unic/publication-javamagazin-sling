/**
 * Leverages the resource type's form definition resources to present
 * edit dialogues for content resources marked by the EditContentFilter. Uses
 * JQuery Block UI to render the dialogues.
 */
$(function() {
    // Factory for input fields of the edit forms
    var Fields = {
        "textarea": function(name) {
            return $('<textarea name="' + name + '" rows="4"></textarea>')
        },
        "text": function (name) {
            return $('<input type="text" name="' + name + '" />')
        },
        "image": function (name) {
            return $('<input type="file" name="' + name + '" accept=".png" />')
        }
    };

    $("#editmode").click(function() {
        /* edit or cancel button in the top bar */
        var $editables = $(".editable");

        $(this).toggleClass("alt");

        if ($(this).hasClass("alt")) {
            $(this).text("Cancel");
        } else {
            $editables.each(function () {
                $(this).unblock()
            });
            $.unblockUI();
            $(this).text("Edit");
            document.editMode = false;
            return;
        }
        document.editMode = true;

        /* For each editable resource, as defined by the markup inserted by the EditContentFilter */
        $editables.each(function() {
            var self    = this,
                $this   = $(this),
                $button = $('<a class="button editbutton">Edit</a>').css("max-width", $this.width() + "px");

            /* Create a resource-specific Block UI edit button */
            this.blocked && $this.unblock() ||
                            $this.block({
                                message: $button,
                                css: {
                                    width: "350px",
                                    top: ($this.height() - 100) / 2 + "px",
                                    left: ($this.width() - 350) / 2 + "px",
                                    padding: "0",
                                    border: "none",
                                    backgroundColor: "transparent"
                                },
                                onBlock: function() {self.blocked = true },
                                onUnblock: function() {self.blocked = false}});

            /* When clicking the resource-specific edit-button */
            $button.click(function() {
                $editables.each(function() {$(this).unblock()});

                var resourcePath = $this.attr("data-resource"),
                    selectors = $this.attr("data-selectors"),
                    resourceType = $this.attr("data-type");

                /* Show a form consisting of the form fields defined in the view's form definition (See form.json in the views) */
                var showForm = function(data) {
                    $.get("/apps/" + resourceType + "/form.infinity.json", function(formDefinition) {
                        var $form = $('<form method="post" action="' + resourcePath + '" enctype="multipart/form-data">');

                        /* Create a suitable input field HTML and populate it with the value provided in data, if any */
                        Object.keys(formDefinition).map(function (key) {
                            return formDefinition[key].type ? Fields[formDefinition[key].type](key) : undefined;
                        }).forEach(function (field) {
                            !field ||
                            field.val(data[field.attr("name")] || "") &&
                            field.appendTo($('<div class="12u">').appendTo($('<div class="row">').appendTo($form)));
                        });

                        $('<div class="row">' +
                            '<div class="12u">' +
                                '<ul class="actions">' +
                                    '<li><input type="submit" value="Update"></li>' +
                                '</ul>' +
                            '</div>' +
                        '</div>').appendTo($form);

                        /* If allowed by the form definition, add a "delete" button that will cause a HTTP DELETE request */
                        formDefinition.allowDelete && $form.find(".actions").append($('<li>').append($('<input type="submit" value="Delete" class="alt">').click(function() {
                            $form.attr("method", "delete");
                        })));

                        /* When the form is submitted, send the form data with regard to the form's method and action, e.g. POST or DELETE. */
                        $form.submit(function () {
                            var formData = new FormData(this);
                            // Redirect to the HTML representation of the altered resource
                            formData.append(":redirect", resourcePath + (selectors ? "." + selectors : "") + ".html");
                            // Set the resource type fo the resource, if the resource does not yet exist
                            formData.append("sling:resourceType", resourceType);

                            $.ajax({
                                type: $form.attr("method"),
                                url: $(this).attr("action"),
                                data: formData,
                                contentType: false,
                                processData: false,
                                success: function (html) {
                                    html && $this.html(html) || $this.remove();
                                    $("#editmode").click();
                                },
                                error: function() {
                                    $("#editmode").click();
                                }
                            });
                            return false;
                        });

                        $.blockUI({
                            message: $form,
                            css: {
                                padding: "10px",
                                top: "20%",
                                left: window.innerWidth > 1500 ? "30%" : (window.innerWidth - Math.min($this.width() - 20, 500)) / 2,
                                width: window.innerWidth > 1500 ? "40%" : Math.min($this.width() - 20, 500) + "px"
                            }
                        })
                    });
                };

                /* Request the JSON representation of the editable resource, and pre-populate the resource form with it if it exists */
                $.ajax({
                    url: resourcePath + ".json",
                    success: function() {
                        $.get(resourcePath + ".json", function(data) {
                            showForm(data);
                        });
                    },
                    error: function() {
                        showForm({});
                    }
                });
            });
        });
    });

    /* Convenience: Cancel any block UI dialog on ESC */
    $(document).keyup(function(e) {
        // Unblock anything on ESC
        e.keyCode == 27 && ($.unblockUI() || document.editMode && $("#editmode").click());
    });
});