$(function() {
    let $form = $('.ajax-form');
    $form.submit(function (e) {
        e.preventDefault();
        console.log("click");
        $.ajax({
            url: $form.attr('action'),
            type: 'post',
            data: $form.serialize(),
            success: function (data) {
                // Whatever you want to do after the form is successfully submitted
                console.log("success!");
                console.log(data);
            },
            error: function(error) {
                // Here you can handle exceptions thrown by the server or your controller.
                console.log("error!");
                console.log(error);
            }
        });
    });
});