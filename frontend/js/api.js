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
    this.authUser = async function (user) {
        response = await this.query('post', signInUrl, user);
        if (response) this.setAuth(response);
        return response;
    }

    this.registerUser = async function (user) {
        return await this.query('post', signUpUrl, user);
    }


    this.setAuth = (login) => {
        ajaxHeaders = {
            Authorization: `Bearer ${login.token}`
        }
    }

    this.toUrlParams = (obj) => {
        var str = [];
        for (var p in obj)
          if (obj.hasOwnProperty(p)) {
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
          }
        return str.length>0?'?'+str.join("&"):'';
    }
    this.query = async function (method, path, data,headers=ajaxHeaders) {
        let response = null;
        console.log(`sending ${method} request on ${options.serverUrl}${path} ${data ? JSON.stringify(data) : ''}`);
        let request = {
            url: `${options.serverUrl}${path}`,
            method: method,
            headers: ajaxHeaders,
            dataType: 'json',
            data: JSON.stringify(data),
            contentType: 'application/json',
            
        };
        try{
            response = await $.ajax(request);
            return response;
        } catch (e){
            if(e.status==200) return;
            let error = e.responseJSON;
            if(error==null)  error = { message: "connection error" , type:'ConnectionError'}
            options.onError(error);
            throw error;
        }
    }

    this.setOptions= (o)=>{
        options=o;
    }
    this.getOptions = ()=>options;
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
        this.createChat = async (chat) => {
            return this.query('post', `/chat`, chat);

        }
        this.editChat = async (chatId,chat) => {
            return this.query('put', `/chat/${chatId}`,chat);

        }
        this.deleteChat = async (chatId) => {
            return this.query('delete', `/chat/${chatId}`);

        }
        this.deleteMessage = async (chatId, msgId) => {
            return this.query('delete', `/chat/${chatId}/message/${msgId}`);
        }
        this.deleteMessages = async (chatId) => {
            return this.query('delete', `/chat/${chatId}/messages`);
        }
        this.banUserFromChat = async (userId, chatId) => {
            return this.query('delete', `/chat/${chatId}/user/${userId}`)
        }
        this.sendMessageToChat = async (chatId, msg) => {
            msg.senderId = userId;
            return this.query('post', `/chat/${chatId}/send`, { message: msg });
        }

        this.getUserInChat = async (chatId, userId) => {
            return this.query('get', `/chat/${chatId}/user/${userId}`);
        }
        this.getUsersInChat = async (chatId)=>{
            return this.query('get', `/chat/${chatId}/users`);
        }

        this.getAllRolesInChat = async (chatId) => {
            return this.query('get', `/chat/${chatId}/roles`);
        }
        this.getChats = async () => {

            let response = this.query('get', `/chats`);
            return response;
        }
        this.getChat = async (chatId) => {

            let response = this.query('get', `/chat/${chatId}`);
            return response;
        }
        
        this.getMessagesFromChat = async (chatId, params=null) => {
            let messages = this.query('get', `/chat/${chatId}/messages`+this.toUrlParams(params));

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