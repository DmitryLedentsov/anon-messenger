function includeHTML() {
    // Находим все элементы <include>
    var $includes = $('include[src]');
    
    // Обрабатываем каждый элемент
    $includes.each(function() {
        var $include = $(this);
        var src = $include.attr('src');
        
        // Создаем синхронный AJAX-запрос
        $.ajax({
            url: src,
            method: 'GET',
            dataType: 'html',
            async: false, // Делает запрос синхронным
            success: function(data) {
                $include.replaceWith(data); // Заменяем элемент полученным HTML
            },
            error: function(xhr, status, error) {
                console.error('Ошибка при загрузке ' + src + ': ' + error);
                $include.replaceWith('<!-- Ошибка загрузки ' + src + ' -->');
            }
        });
    });
}