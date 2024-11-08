function MessengerApi(options){
    this.client =  options.client || new StompJs.Client({
        brokerURL: 'ws://localhost:9087/ws'
    });
    this.client.onConnect = (frame) => {
        console.log('Connected: ' + frame);
        setEventHandlers();
        //sendMsg({senderId:userId, message:"aaaa"});
    };

    this.client.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
    };

    this.client.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    };

    this.setEventHandlers = function() {
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

    this.connect = function () {
        client.activate();
        console.log("Connected");
    }

    this.disconnect = function() {
        client.deactivate();//.then(r =>  setConnected(false));
        console.log("Disconnected");
    }

    this.sendMsg  = function(msg, url){
        client.publish({
            destination: url,
            body: JSON.stringify(msg)
        });
    }
}