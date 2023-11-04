const client = new StompJs.Client({
    brokerURL: 'ws://localhost:9087/ws'
});

const urlSendMessage = "/app/chat/"+chatId+"/send";
const urlListenForNewMessages = '/topic/chat/'+chatId+'/messages';


client.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);

    client.subscribe(urlListenForNewMessages, (m) => {
        showMessages(JSON.parse(m.body));
    });

    sendMsg({senderId:userId, message:"aaaa"});
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
    console.log("connected");
}

function disconnect() {
    client.deactivate().then(r =>  setConnected(false));
    console.log("Disconnected");
}

function sendName() {
    client.publish({
        destination: "/app/hello",
        body: JSON.stringify({'name': $("#name").val()})
    });
}

function showMessages(messages) {
   console.log(messages);
}

$(function () {
   /* $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendName());*/
    connect();
});