

function handleNewIncomingMessage(chat) {
    console.log(chat);
    chats.push(chat);
    //TODO:
    clearChats();
    renderChats();
}

$('#chat-list').on('msgReceive', function (e, data) {
    handleNewIncomingMessage(data);
})

$(function () {
    connect();
    renderChats();
});

//отправка сообщений

//делаем динамический рендеринг на основе списка сообщений
var $chatList = $("#chats .list");
function renderChat(chat){
    $chatList.append(
        `<li class = "chat">
<a href="/chat/${chat.id}">${chat.name}</a> 
</li>`);
}
function renderChats(){
    chats.forEach(c=>{
        renderChat(c);
    });
}
function clearChats(){
    $( ".chat" ).remove();
}
