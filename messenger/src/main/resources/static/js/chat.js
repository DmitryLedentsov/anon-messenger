
const urlSendMessage = "/app/chat/" + chatId + "/send";
const urlListenForNewMessages = '/topic/chat/' + chatId + '/messages';

function handleNewIncomingMessage(message) {
    console.log(message);
    op = message.operation;
    data = message.data;
    if (op === "ADD") messages.push(data);
    else if (op === "DELETE") messages = messages.filter((e) => e.id != data.id);
    clearMessages();
    renderMessages();
}

$(function () {
    /* $("form").on('submit', (e) => e.preventDefault());
     $( "#connect" ).click(() => connect());
     $( "#disconnect" ).click(() => disconnect());
     $( "#send" ).click(() => sendName());*/
    connect();
    renderMessages();

    $('#msg-list').on('msgReceive', function (e, data) {
        handleNewIncomingMessage(data);
    })
    $('#msg-list').on('click', '.socket-action', handleActions);

});

var $msgList = $("#msg-list");
function renderMessage(msg) {
    $msgList.append(msgTemplate(msg));
}
function renderMessages() {
    messages.forEach(msg => {
        renderMessage(msg);
    });
}
function clearMessages() {
    $(".message").remove();
}
