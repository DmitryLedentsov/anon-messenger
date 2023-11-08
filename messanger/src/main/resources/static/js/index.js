

function handleNewIncomingMessage(chat) {
    console.log(chat);
    op = chat.operation;
    data = chat.data;
    if(op==="ADD") chats.push(data);
    else if(op==="DELETE") chats = chats.filter((e)=>e.id!=data.id);

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
    $('#chat-list').on('click','.socket-action',handleActions);
});

//отправка сообщений

//делаем динамический рендеринг на основе списка сообщений
var $chatList = $("#chats .list");
function renderChat(chat){
    $chatList.append(
        `<li class = "chat">
<a href="/chat/${chat.id}">${chat.name}</a> 
<button class="socket-action" data-url="/app/user/${userId}/chat/delete/${chat.id}">delete</button> 
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
