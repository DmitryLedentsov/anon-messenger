$(function () {
    $('body').on('click', '.nav-link', function (e) {
        e.preventDefault();
        let $cur = $(this);
        let url = $cur.attr('data-url');
        window.location = url;
    });
});