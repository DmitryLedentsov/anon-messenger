// Wait for the DOM to be ready
$(function () {
    // Initialize form validation on the registration form.
    // It has the name attribute "registration"
    $("form[name='register'], form[name='login']").validate({
        rules: {
            login: "required",
            password: {
                required: true,
                minlength: 1
            }
        },
        messages: {
            login: "Please enter login",
            password: {
                required: "Please provide a password",
                minlength: "Your password must be at least 1 characters long"
            },
        },
        // Make sure the form is submitted to the destination defined
        // in the "action" attribute of the form when valid
        submitHandler: function (form) {
            form.submit();
        }
    });

    $("form[name='chat-create']").validate({
        rules: {
            name: "required",
            users: "required"
        },
        messages: {
            name: "Please enter name",
            users: "Please enter user list",
        }
    });
    $("form[name='message-send']").validate({
        rules: {
            message: "required",
        },
        messages: {
            message: "Please enter message",
        },
        errorPlacement: function (error, element) {
            error.insertAfter("#message-send-container");
        },
    });
});