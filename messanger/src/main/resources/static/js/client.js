
const client = new StompJs.Client({
    brokerURL: 'ws://localhost:9087/ws'
});
client.onConnect = (frame) => {
    console.log('Connected: ' + frame);
    setEventHandlers();
    //sendMsg({senderId:userId, message:"aaaa"});
};

client.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

client.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setEventHandlers() {
    let $elem = $('.data-elem');
    $elem.each(function (e) {
        let $cur = $(this);
        let url = $elem.attr('data-url');
        client.subscribe(url, (m) => {
            $cur.trigger('msgReceive', JSON.parse(m.body));
        });
    });

    client.subscribe(`/topic/user/${userId}/error`, (m) => {
        alert(JSON.parse(m.body).message);
    });
}

function connect() {
    client.activate();
    console.log("Connected");
}

function disconnect() {
    client.deactivate();//.then(r =>  setConnected(false));
    console.log("Disconnected");
}

function sendMsg(msg, url){
    client.publish({
        destination: url,
        body: JSON.stringify(msg)
    });
}
