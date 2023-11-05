const client = new StompJs.Client({
    brokerURL: 'ws://localhost:9087/ws'
});

const urlSendMessage = "/app/chat/"+chatId+"/send";
const urlListenForNewMessages = '/topic/chat/'+chatId+'/messages';


client.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);

    client.subscribe(urlListenForNewMessages, (m) => {
        handleNewIncomingMessage(JSON.parse(m.body));
    });

    //sendMsg({senderId:userId, message:"aaaa"});
};

function sendMsg(msg){
    client.publish({
        destination: urlSendMessage,
        body: JSON.stringify(msg)
    });
}

client.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

client.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    client.activate();
    console.log("Connected");
}

function disconnect() {
    client.deactivate().then(r =>  setConnected(false));
    console.log("Disconnected");
}

function handleNewIncomingMessage(message) {
   console.log(message);
   messages.push(message);
   //TODO:
   renderMessage(message);
}

$(function () {
   /* $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendName());*/
    connect();
    renderMessages();
});

//отправка сообщений

//делаем динамический рендеринг на основе списка сообщений
var $msgList = $("#messages .list");
function renderMessage(msg){
    $msgList.append(
        `<li class = "message">
<span>user: ${msg.sender}</span>   &#160 &#160 &#160<span>message: ${msg.message}</span>
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