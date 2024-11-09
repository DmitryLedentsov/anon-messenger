console.log('messenger api loaded!!');
function MessengerApi(options){
    if(!$) throw new Error("jquery not imported");
    const signInUrl = `/auth/signin`;
    const signUpUrl = `/auth/signup`;

    const topicUrl='/topic';
    const publishUrl='/app'
    this.authUser = function (user){
        response = this.query('post',signInUrl, user);
        return response;
    }

    this.registerUser = function(user){
        return this.query('post',signUpUrl,user);
    }
    


    this.query = function(method, path, data, async=false){
        let response = null;
        console.log(`sending request on ${options.serverUrl}${path}`);
        $.ajax({
            url: `${options.serverUrl}${path}`,         /* Куда отправить запрос */
            method: method,             /* Метод запроса (post или get) */
            dataType: 'json',          /* Тип данных в ответе (xml, json, script, html). */
            data: JSON.stringify(data),     /* Данные передаваемые в массиве */
            async: async,
            contentType : 'application/json',
            success: function(data){   /* функция которая будет выполнена после успешного запроса.  */
                 console.log(data); /* В переменной data содержится ответ от index.php. */
                 response = data;
            },
            error: function (xhr, ajaxOptions, thrownError){   /* функция которая будет выполнена после успешного запроса.  */
                console.log([xnr, ajaxOptions, thrownError])
                throw new Error(xhr.responseText);
            }
        });
        return response;
    }
    
    this.initSocketClient = (login)=>{
        console.log('client init with token '+ login.token);
        this.client = new StompJs.Client({
            brokerURL:  options.brokerUrl,
            connectHeaders: {
                Authorization: `Bearer ${login.token}`,
            },
        });


        this.client.onConnect = (frame) => {
            console.log('Connected: ' + frame);
            
            //sendMsg({senderId:userId, message:"aaaa"});

            this.socketClientSubscribe(`/user/${login.userId}/error`, (m) => {
               
                alert(m.message)
            });

            this.initLogic(login.userId);
            options.onConnect && options.onConnect(frame);
        };
    
        this.client.onWebSocketError = (error) => {
            options.onError && options.onError(error);
            console.error('Error with websocket', error);
        };
    
        this.client.onStompError = (frame) => {
            options.onError && options.onError( frame.headers['message']);
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
        };
    
    
        this.socketClientSubscribe = (topic,callback)=>{
            topic = topicUrl+topic;
            console.log('subscribe '+topic);
            this.client.subscribe(topic, (m) => {
                let data = m.body ? JSON.parse(m.body) : {};
                console.log('receive '+ topic);
                data && console.log(data);
                callback(data)
            });
        }
        this.socketClientUnSubscribe = (topic) => {
            topic = topicUrl+topic;
            console.log('unsubscribe '+ topic);
            this.client.unsubscribe(topic);
        }
   
    
        this.socketClientConnect =  () =>{
            this.client.activate();
            console.log("Connecting");
        }
    
        this.socketClientDisconnect = () =>{
            this.client.deactivate();//.then(r =>  setConnected(false));
            console.log("Disconnecting");
        }
    
        this.publishSocketMessage  = ( url, msg)=>{
            url = publishUrl + url;
            console.log('publish '+url);
            msg&& console.log(msg);
            this.client.publish({
                destination: url,
                body: JSON.stringify(msg)
            });
        }
    }


    this.initLogic = (userId)=>{
        console.log('init chats for user: '+userId);
        this.chats = [];
     

        this.subscribeOnChats = ()=>{
            this.socketClientSubscribe(`/user/${userId}/chats`, (m)=>{
                let op = m.operation;
                let data = m.data;
                if(op==="ADD") this.chats.push(data);
                else if(op==="DELETE") {
                    this.chats = this.chats.filter((e)=>e.id!=data.id);
                    this.unsubscribeOnMessagesInChat(data.id);
                }
                else if(op==="UPDATE") {
                    let idx = this.chats.map(o => o.id).indexOf(data.id);
                    let messages = chats[idx].messages;
                    chats[idx]=data;
                    chats[idx].messages = messages;
                }
            })
        }


        this.unsubscribeOnMessagesInChat = (chatId)=>{
            this.socketClientUnSubscribe(`/chat/${chatId}/messages`);
        };
        this.subscribeOnMessagesInChat = (chatId)=>{
            this.socketClientSubscribe(`/chat/${chatId}/messages`, (m)=>{
                //console.log(m);
                let op = m.operation;
                let data = m.data;
                let chat = this.findChatById(chatId);
                chat.messages = chat.messages || [];
                if(op==="ADD") chat.messages.push(data)
                else if(op==="DELETE") chat.messages = chat.messages.filter((e)=>e.id!=data.id);
            });
        }

        this.createChat = (chat)=>{
            this.publishSocketMessage(`/user/${userId}/chat/create`,chat);

        }
        this.deleteChat = (chatId)=>{
            this.publishSocketMessage(`/user/${userId}/chat/delete/${chatId}`);

        }
        this.sendMessageToChat = (chatId,msg)=>{
            msg.senderId=userId;
            this.publishSocketMessage(`/chat/${chatId}/send`,msg);
        }



        this.findChatById = (id)=>{
            return this.chats.find(chat=>chat.id == id);
        }
        
        
    }
}