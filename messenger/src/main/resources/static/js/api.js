console.log('messenger api loaded!!');
function MessengerApi(options) {
    if (!$) throw new Error("jquery not imported");
    //if(Stomp) StompJs=Stomp; //new vers
    if (!StompJs) throw new Error("stompjs not imported");
    const signInUrl = `/auth/signin`;
    const signUpUrl = `/auth/signup`;

    const topicUrl = '/topic';
    const publishUrl = '/app';

    let ajaxHeaders = [];
    this.authUser = function (user) {
        response = this.query('post', signInUrl, user);
        if (response) this.setAuth(response);
        return response;
    }

    this.registerUser = function (user) {
        return this.query('post', signUpUrl, user);
    }


    this.setAuth = (login) => {
        ajaxHeaders = {
            Authorization: `Bearer ${login.token}`
        }
    }

    this.query = function (method, path, data, async = false) {
        let response = null;
        console.log(`sending ${method} request on ${options.serverUrl}${path} ${data ? JSON.stringify(data) : ''}`);
        $.ajax({
            url: `${options.serverUrl}${path}`,
            method: method,
            headers: ajaxHeaders,
            dataType: 'json',
            data: JSON.stringify(data),
            async: async,
            contentType: 'application/json',
            success: function (res) {
                response = res || {};
                console.log(res);
            },
            error: function (xhr, ajaxOptions, thrownError) {
                if (xhr.status == 200) return xhr.responseText;
                console.log([xhr, ajaxOptions, thrownError])
                options.onError && thrownError.name == 'NetworkError' ? options.onError({ message: "connection error" }) : options.onError(xhr.responseJSON || xhr.responseText);
                throw new Error(xhr.responseJSON || xhr.responseText || 'connection error');
            }
        });
        return response;
    }

    this.init = (login)=>{
        this.initLogic(login);
        this.initSocketClient(login);
    }
    this.initSocketClient = (login) => {
        this.setAuth(login);
        this.client = new StompJs.Client({
            brokerURL: options.brokerUrl,
            connectHeaders: {
                Authorization: `Bearer ${login.token}`,
            },
        });


        this.client.onConnect = (frame) => {

            this.socketClientSubscribe(`/user/${login.userId}/error`, (m) => {

                alert(m.message)
            });

            
            this.initSocketLogic(login.userId);
            options.onConnect && options.onConnect(frame);
        };

        this.client.onWebSocketError = (error) => {
            options.onError && options.onError("connection error");
            console.error('Error with websocket', error);
        };

        this.client.onStompError = (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
            options.onError && options.onError(frame.headers['message']);

        };


        this.subscribtions = {};
        this.socketClientSubscribe = (topic, callback) => {
            topic = topicUrl + topic;
            console.log('subscribe ' + topic);
            this.subscribtions[topic] = this.client.subscribe(topic, (m) => {
                let data = m.body ? JSON.parse(m.body) : {};
                console.log('receive ' + topic);
                data && console.log(data);
                callback(data)
            });

        }
        this.socketClientUnSubscribe = (topic) => {
            topic = topicUrl + topic;

            if (this.subscribtions[topic]) this.subscribtions[topic].unsubscribe();
        }


        this.socketClientConnect = () => {
            this.client.activate();
        }

        this.socketClientDisconnect = () => {
            this.client.deactivate();//.then(r =>  setConnected(false));
        }

        this.publishSocketMessage = (url, msg) => {
            url = publishUrl + url;
            msg && console.log(msg);
            if (!msg) msg = { aboba: 'aboba' };
            this.client.publish({
                destination: url,
                body: JSON.stringify(msg)
            });
        }
    }

    this.initLogic = (userId) => {
        this.createChat = (chat) => {
            return this.query('post', `/chat`, chat);

        }
        this.editChat = (chatId,chat) => {
            return this.query('put', `/chat/${chatId}`,chat);

        }
        this.deleteChat = (chatId) => {
            return this.query('delete', `/chat/${chatId}`);

        }
        this.deleteMessage = (chatId, msgId) => {
            return this.query('delete', `/chat/${chatId}/message/${msgId}`);
        }
        this.banUserFromChat = (userId, chatId) => {
            return this.query('delete', `/chat/${chatId}/ban/${userId}`)
        }
        this.sendMessageToChat = (chatId, msg) => {
            msg.senderId = userId;
            return this.query('post', `/chat/${chatId}/send`, { message: msg });
        }

        this.getUserInChat = (chatId, userId) => {
            return this.query('get', `/chat/${chatId}/user/${userId}`);
        }
        this.getUsersInChat = (chatId)=>{
            return this.query('get', `/chat/${chatId}/users`);
        }

        this.getAllRolesInChat = (chatId) => {
            return this.query('get', `/chat/${chatId}/roles`);
        }
        this.getChats = () => {

            let response = this.query('get', `/chats`);
            return response;
        }
        this.getChat = (chatId) => {

            let response = this.query('get', `/chat/${chatId}`);
            return response;
        }
        
        this.getMessagesFromChat = (chatId) => {
            let messages = this.query('get', `/chat/${chatId}/messages`);

            return messages;
        }
    }
    this.initSocketLogic = (userId) => {
        this.chats = [];


        this.subscribeOnChats = (onReceive) => {
            this.socketClientSubscribe(`/user/${userId}/chats`, (m) => {
                let op = m.operation;
                let data = m.data;

                onReceive && onReceive(m);
                if (op === "DELETE") {
                    this.unsubscribeOnMessagesInChat(data.id);
                } else if (op == "ADD") {
                    // this.subscribeOnMessagesInChat(data.id, onReceiveMsg)
                }

            })
        }



        this.unsubscribeOnMessagesInChat = (chatId) => {
            this.socketClientUnSubscribe(`/chat/${chatId}/messages`);
        };
        this.subscribeOnMessagesInChat = (chatId, onReceive) => {
            this.socketClientSubscribe(`/chat/${chatId}/messages`, (m) => {
                onReceive && onReceive(m);
            });
        }
        this.subscribeOnMessagesInChats = (ids, onReceive) => {
            ids.forEach(el => {

                this.subscribeOnMessagesInChat(el, onReceive);
            });
        }
        
    }
}