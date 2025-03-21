$(function () {

    let $form = $('.socket-form');
    $form.submit(function (e) {
        e.preventDefault();
        let $cur = $(this);
        if ($cur.valid()) {
            client.publish({
                destination: $cur.attr('data-url'),
                body: JSON.stringify($cur.serializeJSON())
            });
        }
    });
});