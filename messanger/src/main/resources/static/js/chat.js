
const urlSendMessage = "/app/chat/"+chatId+"/send";
const urlListenForNewMessages = '/topic/chat/'+chatId+'/messages';

function AppViewModel() {
    this.messages = ko.observableArray(messages); // No initial value
}
let model = new AppViewModel();

function handleNewIncomingMessage(message) {
    console.log(message);
    op = message.operation;
    data = message.data;
    if(op==="ADD") model.messages.push(data);
    else if(op==="DELETE") model.messages.remove((e)=>e.id==data.id);
    //clearMessages();
    //renderMessages();
    //point.render("messages", messages);
}
$(function () {
   /* $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendName());*/
    connect();
    //renderMessages();
    //point.render("messages", messages);
    
    ko.applyBindings(model);

    $('#msg-list').on('msgReceive', function (e, data) {
        handleNewIncomingMessage(data);
    })
    $('#msg-list').on('click','.socket-action',handleActions);

});
