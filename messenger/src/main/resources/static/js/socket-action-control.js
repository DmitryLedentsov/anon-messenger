function handleActions(e) {
    e.preventDefault();
    let cur = $(this);
    client.publish({
        destination: cur.attr('data-url'),
        body: cur.attr('data-data')
    });
}
