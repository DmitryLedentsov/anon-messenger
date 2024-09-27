$(function() {
    $('body').on('click', '.nav-link', function (e) {
        e.preventDefault();
        console.log("link");
        //console.log($form.serializeJSON());
        let $cur = $(this);
        let url = $cur.attr('data-url');
        window.location = url;
    });
});