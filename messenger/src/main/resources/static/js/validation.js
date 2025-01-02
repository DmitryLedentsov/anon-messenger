$(function () {
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