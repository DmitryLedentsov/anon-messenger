$(function () {
    let $form = $('.ajax-form');
    $form.submit(function (e) {
        e.preventDefault();
        $.ajax({
            url: $form.attr('action'),
            type: 'post',
            data: $form.serialize(),
            success: function (data) {
                console.log("success!");
                console.log(data);
            },
            error: function (error) {
                console.log("error!");
                console.log(error);
            }
        });
    });
});