function includeHTML() {
    var z, i, elmnt, file, xhttp;
    var elementsToProcess = []; // Массив элементов для обработки

    // Собираем все элементы с атрибутом "include"
    z = document.getElementsByTagName("*");
    for (i = 0; i < z.length; i++) {
        elmnt = z[i];
        file = elmnt.getAttribute("include");
        if (file) {
            elementsToProcess.push({ element: elmnt, file: file });
        }
    }

    // Обрабатываем каждый элемент синхронно
    for (i = 0; i < elementsToProcess.length; i++) {
        elmnt = elementsToProcess[i].element;
        file = elementsToProcess[i].file;

        // Создаем синхронный запрос
        xhttp = new XMLHttpRequest();
        xhttp.open("GET", file, false); // false делает запрос синхронным
        try {
            xhttp.send();
            if (xhttp.status === 200) {
                elmnt.innerHTML = xhttp.responseText;
            } else if (xhttp.status === 404) {
                elmnt.innerHTML = "Page not found.";
            } else {
                elmnt.innerHTML = "Error loading file: " + xhttp.status;
            }
        } catch (e) {
            console.error("Ошибка загрузки " + file + ": " + e.message);
            elmnt.innerHTML = "Error loading file.";
        }
        // Удаляем атрибут после обработки
        elmnt.removeAttribute("include");
    }
}

