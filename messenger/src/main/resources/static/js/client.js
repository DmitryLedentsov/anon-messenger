function MessengerApi(options){

    function getToken(user){
        $.ajax()
    }
    this.client = new StompJs.Client({
        brokerURL: 'ws://localhost:9087/ws',
        connectHeaders: {
            Authorization: "Bearer eyJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6ImFib2JhIiwibG9naW4iOiJhYm9iYTEiLCJzdWIiOiJhYm9iYTEiLCJpYXQiOjE3MzEwMzQzNTUsImV4cCI6MTczMTE3ODM1NX0.d3X93EMNVknIXIv-Glm4eujBCL3B25KHnIb4r6O-Lak",
        },
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
}
