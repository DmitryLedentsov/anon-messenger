
const urlSendMessage = "/app/chat/"+chatId+"/send";
const urlListenForNewMessages = '/topic/chat/'+chatId+'/messages';

function handleNewIncomingMessage(message) {
    console.log(message);
    op = message.operation;
    data = message.data;
    if(op==="ADD") messages.push(data);
    else if(op==="DELETE") messages = messages.filter((e)=>e.id!=data.id);
   //TODO:
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
    $('#msg-list').on('click','.socket-action',handleActions);

});

//отправка сообщений

//делаем динамический рендеринг на основе списка сообщений
var $msgList = $("#msg-list");
function renderMessage(msg){
    $msgList.append(
        `<li class = "message">
<span>user: ${msg.sender}</span>   &#160 &#160 &#160<span>message: ${msg.message}</span>
<button class="socket-action" data-url="/app/chat/${chatId}/delete/${msg.id}" data-data="${msg.message}">delete</button> 
</li>`);
}
function renderMessages(){
    messages.forEach(msg=>{
        renderMessage(msg);
    });
}
function clearMessages(){
    $( ".message" ).remove();
}

/*
var viewModel = function (items) {
    var self = this;
    self.items = ko.observableArray(items);
    self.selectedItemId = ko.observable();
    self.item = ko.observable();
    self.selectItem = function (item) {
        for (var i = 0; i < self.items().length; i++) {
            if (self.items()[i].Id === self.selectedItemId()) {
                self.item(self.items()[i]);
                break;
            }
        }
    };
};

ko.applyBindings(new viewModel(items));*/