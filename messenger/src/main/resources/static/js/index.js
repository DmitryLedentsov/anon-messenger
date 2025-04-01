

function handleNewIncomingMessage(chat) {
    console.log(chat);
    op = chat.operation;
    data = chat.data;
    if (op === "ADD") chats.push(data);
    else if (op === "DELETE") chats = chats.filter((e) => e.id != data.id);
    else if (op === "UPDATE") {
        let idx = chats.map(o => o.id).indexOf(data.id);
        chats[idx] = data;
    }

    //TODO:
    clearChats();
    renderChats();
}


$(function () {
    connect();
    renderChats();

    $('#chat-list').on('msgReceive', function (e, data) {
        handleNewIncomingMessage(data);
    })
    $('#chat-list').on('click', '.socket-action', handleActions);
});

var $chatList = $("#chats .list");
function renderChat(chat) {
    $chatList.append(chatTemplate(chat));
}
function renderChats() {
    chats.forEach(c => {
        renderChat(c);
    });
}
function clearChats() {
    $(".chat").remove();
}
