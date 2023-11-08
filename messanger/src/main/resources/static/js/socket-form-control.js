$(function() {

    let $form = $('.socket-form');
    $form.submit(function (e) {
        e.preventDefault();
        console.log("click");
        //console.log($form.serializeJSON());
        client.publish({
            destination: $form.attr('data-url'),
            body: JSON.stringify($form.serializeJSON())
        });
    });
});