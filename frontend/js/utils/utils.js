function getFormData($element) {
    var unindexed_array = $element.find(':input').serializeArray();
    var indexed_array = {};

    $.map(unindexed_array, function(n, i) {
        indexed_array[n['name']] = n['value'];
    });

    return indexed_array;
}

function getNotEmpty(...args) {
    for (const arg of args) {
        if (isNotEmpty(arg)) {
            return arg;
        }
    }
    return null; // или undefined, если предпочтительно
}

function isNotEmpty(value) {
    // Проверка на null/undefined
    if (value == null) return false;
    
    // Проверка на пустую строку
    if (typeof value === 'string' && value.trim() === '') return false;
    
    // Проверка на пустой массив
    if (Array.isArray(value) && value.length === 0) return false;
    
    // Проверка на пустой объект
    if (typeof value === 'object' && Object.keys(value).length === 0) return false;
    
    // Во всех остальных случаях считаем значение непустым
    return true;
}
function updateObject(obj, data) {
    for (const key in data) {
        if (data.hasOwnProperty(key)) {
            obj[key] = data[key];
        }
    }
    return obj;
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

function saveLocalStorage(key, data) {
    const dataString = JSON.stringify(data);
    localStorage.setItem(key, dataString);
}
function getLocalStorage(key) {
    const dataString = localStorage.getItem(key);
    return dataString ? JSON.parse(dataString) : {};
}

function saveCookie(key, data, daysToLive = 7) {
    const dataString = JSON.stringify(data);
    const encodedData = encodeURIComponent(dataString); // Кодируем для безопасного хранения

    // Формируем срок действия
    const date = new Date();
    date.setTime(date.getTime() + (daysToLive * 24 * 60 * 60 * 1000));
    const expires = `expires=${date.toUTCString()}`;

    // Устанавливаем cookie
    document.cookie = `${key}=${encodedData}; ${expires}; path=/`;
}

function getCookie(key, defaultValue = null) {
    // Ищем ключ в document.cookie
    const cookies = document.cookie.split('; ');
    const cookie = cookies.find(c => c.startsWith(`${key}=`));

    if (!cookie) return defaultValue;

    // Извлекаем и декодируем значение
    const encodedData = cookie.split('=')[1];
    const dataString = decodeURIComponent(encodedData);

    return JSON.parse(dataString);
}