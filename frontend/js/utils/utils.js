
function getFormData($form){
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};

    $.map(unindexed_array, function(n, i){
        indexed_array[n['name']] = n['value'];
    });

    return indexed_array;
}

function appendListItem(listName, listItemHTML) {
    $(listItemHTML)
        .hide()
        .css('opacity', 0.0)
        .appendTo(listName)
        .slideDown(100)
        .animate({ opacity: 1.0 })
}

function removeItem(name) {
    $(name).fadeOut(300, function () { $(this).remove(); });
}
function replaceElem(selector, html) {
    var div = $(selector).hide();
    $(selector).replaceWith(html);
    $(selector).fadeIn("slow");
}


openRenderModal = (modal, data) => {
    $(modal).remove();
    const template = $(`${modal}-template`).html();

    $('body').append(Mustache.render(template, data));
    $(modal).modal('show');
}