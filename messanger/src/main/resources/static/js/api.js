console.log('messenger api loaded!!');
function MessengerApi(options){
    if(!$) throw new Error("jquery not imported");
    const signInUrl = `/auth/signin`;
    const signUpUrl = `/auth/signup`;

    const topicUrl='/topic';
    const publishUrl='/app';

    let ajaxHeaders = [];
    this.authUser = function (user){
        response = this.query('post',signInUrl, user);
        if(response) this.setAuth(response);
        return response;
    }

    this.registerUser = function(user){
        return this.query('post',signUpUrl,user);
    }
    

    this.setAuth = (login)=>{
        ajaxHeaders = {
            Authorization: `Bearer ${login.token}`
        }
    }

    this.query = function(method, path, data, async=false){
        let response = null;
        console.log(`sending ${method} request on ${options.serverUrl}${path} ${data? JSON.stringify(data): ''}`);
        $.ajax({
            url: `${options.serverUrl}${path}`,         /* Куда отправить запрос */
            method: method,             /* Метод запроса (post или get) */
            headers: ajaxHeaders,
            dataType: 'json',          /* Тип данных в ответе (xml, json, script, html). */
            data: JSON.stringify(data),     /* Данные передаваемые в массиве */
            async: async,
            contentType : 'application/json',
            success: function(res){   /* функция которая будет выполнена после успешного запроса.  */
                 
                 response = res||{};
                 console.log(res);
            },
            error: function (xhr, ajaxOptions, thrownError){   /* функция которая будет выполнена после успешного запроса.  */
                if (xhr.status == 200) return xhr.responseText;
                console.log([xhr, ajaxOptions, thrownError])
            
                options.onError && options.onError(xhr.responseJSON || xhr.responseText);
                throw new Error(xhr.responseJSON || xhr.responseText || 'connection error');
            }
        });
        return response;
    }
    
    this.initSocketClient = (login)=>{
        this.setAuth(login);
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
            if(!msg) msg = {aboba:'aboba'};
            this.client.publish({
                destination: url,
                body: JSON.stringify(msg)
            });
        }
    }


    this.initLogic = (userId)=>{
        console.log('init chats for user: '+userId);
        this.chats = [];
     

        this.subscribeOnChats = (onReceive)=>{
            this.socketClientSubscribe(`/user/${userId}/chats`, (m)=>{
                let op = m.operation;
                let data = m.data;
               
                onReceive && onReceive(m);
                if(op==="DELETE") {
                    this.unsubscribeOnMessagesInChat(data.id);
                }else if(op=="ADD"){
                   // this.subscribeOnMessagesInChat(data.id, onReceiveMsg)
                }
                
            })
        }



        this.unsubscribeOnMessagesInChat = (chatId)=>{
            this.socketClientUnSubscribe(`/chat/${chatId}/messages`);
        };
        this.subscribeOnMessagesInChat = (chatId,onReceive)=>{
            this.socketClientSubscribe(`/chat/${chatId}/messages`, (m)=>{
                //console.log(m);
               onReceive && onReceive(m);
            });
        }

       // this.getChats = ()=>{
       //     this.publishSocketMessage(`/chats`);
//
       // }
        this.createChat = (chat)=>{
            return this.query('post',`/chat`,chat);

        }
        this.deleteChat = (chatId)=>{
            return this.query('delete',`/chat/${chatId}`);

        }
        this.deleteMessage = (chatId, msgId)=>{
            return this.query('delete',`/chat/${chatId}/message/${msgId}`);
        }
        this.banUserFromChat = (userId, chatId)=>{
            return this.query('delete',`/chat/${chatId}/ban/${userId}`)
        }
        this.sendMessageToChat = (chatId,msg)=>{
            msg.senderId=userId;
            return this.query('post',`/chat/${chatId}/send`,{message:msg});
        }

        this.getChats = ()=>{
            
            let response = this.query('get',`/chats`);
            return response;
        }
        this.subscribeOnMessagesInChats = (ids,onReceive)=>{
            ids.forEach(el => {

                this.subscribeOnMessagesInChat(el,onReceive);
            });
        }
        this.getMessagesFromChat = (chatId)=>{
            let messages = this.query('get',`/chat/${chatId}/messages`);

            return messages;
        }



     
        
        
    }
}