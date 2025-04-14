function getFormData($element) {
    var formData = {};
    
    $element.find(':input').each(function() {
        var $input = $(this);
        var name = $input.attr('name');
        
        // Пропускаем элементы без name
        if (!name) return;
        
        // Обработка чекбоксов
        if ($input.is(':checkbox')) {
            // Если чекбокс не отмечен, сохраняем false (или можно пропустить)
            formData[name] = $input.is(':checked');
        } 
        // Обработка radio-кнопок
        else if ($input.is(':radio')) {
            // Добавляем только если radio выбран
            if ($input.is(':checked')) {
                formData[name] = $input.val();
            }
        }
        // Обработка остальных элементов
        else {
            formData[name] = $input.val();
        }
    });
    
    return formData;
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

function prependListItem(selector, html) {
    $(selector).prepend(html);
}

openRenderModal = (modal, data,fragments) => {
    $(modal).remove();
    const template = $(`${modal}-template`).html();

    $('body').append(Mustache.render(template, data,fragments));
    $(modal).modal('show');
}

render = (selector, data, fragments)=>{
    const template = $(selector).html();
    return Mustache.render(template, data,fragments);
}
isModalShown = (modal)=>{
    return $(modal).hasClass('show');
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
function clearCookie(key) {
    // Устанавливаем cookie с истекшим сроком действия
    document.cookie = `${key}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/`;
}