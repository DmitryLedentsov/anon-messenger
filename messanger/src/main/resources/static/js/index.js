
function AppViewModel() {
    this.chats = ko.observableArray(chats); // No initial value
}
let model = new AppViewModel();
function handleNewIncomingMessage(chat) {
    console.log(chat);
    op = chat.operation;
    data = chat.data;
    if(op==="ADD") model.chats.push(data);
    else if(op==="DELETE") model.chats.remove((e)=>e.id==data.id);
    else if(op==="UPDATE") {
        let idx = chats.map(o => o.id).indexOf(data.id);
        chats[idx]=data;
    }


}


$(function () {
    connect();
    ko.applyBindings(model);

    $('#chat-list').on('msgReceive', function (e, data) {
        handleNewIncomingMessage(data);
    })
    $('#chat-list').on('click','.socket-action',handleActions);
});
