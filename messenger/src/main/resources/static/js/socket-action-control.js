/*$(function() {

    let $el = $('.socket-action');
    $el.click(handleActions);
});*/
function handleActions(e) {
    e.preventDefault();
    /*e.stopPropagation();
    e.stopImmediatePropagation();*/
    let cur = $(this);
    //DEBUG
    //console.log("click: " + cur.attr('data-url'));
    //console.log($form.serializeJSON());
    client.publish({
        destination: cur.attr('data-url'),
        body: cur.attr('data-data')
    });
}
