var api = window.api || null;
var app = window.app || {};
window.currentChatId = -1;
function App() {
    this.chats = [];
    this.page = 1;
    this.messagesPerPage = 20; // Number of messages to load per page
    this.isLoading = false;
    this.hasMoreMessages = true;

    this.updateChat = (id, data) => {
        let idx = this.chats.map(o => o.id).indexOf(data.id);
        let messages =  this.chats[idx].messages;
        this.chats[idx] = data;
        this.chats[idx].messages = messages;
    }
    this.updateOrCreateChat = (id, data) => {
        let idx = this.chats.map(o => o.id).indexOf(data.id);
        if (idx == -1) {
            this.chats.push(data);
            return;
        }
        let messages = chats[idx].messages;
        this.chats[idx] = data;
        this.chats[idx].messages = messages;
    }
    this.findChatById = (id) => {
        return this.chats.find(chat => chat.id == id);
    }




    this.onAddMsg = (chat, data) => {
        chat.messages.push(data);

        $msgList = $('.message-list');
        appendListItem('.message-list', this.renderMsgTemplate(data),)
    }
    this.onUpdateMsg = (chat, data) => {

    }
    this.onDeleteMsg = (chat, data) => {

        chat.messages = chat.messages.filter((e) => e.id != data.id);
        removeItem(`.message-item[data-id=${data.id}]`);

    }

    this.onAddChat = async (data) => {
        appendListItem(".chat-list", await this.renderChat(data));
    }
    this.onDeleteChat = (data) => {
        removeItem(`.chat-item[data-id=${data.id}]`);
        if (data.id == this.currentChatId) {
            $('.message-list').empty();
        }
    }
    this.onUpdateChat = async (data) => {
        replaceElem(`.chat-item[data-id=${data.id}]`, await this.renderChat(data));
    }

    this.openChat = (id) => {
        this.api.unsubscribeOnMessagesInChat(this.currentChatId);
        
        if (id !== window.currentChatId) {
            this.currentChatId = parseInt(id);
        }
        else {
            if (id == -1) return;
            this.currentChatId = id;
        }
        window.currentChatId = this.currentChatId;
        
        $(`.chat-item`).removeClass('selected-item');
        $(`.chat-item[data-id=${id}]`).addClass('selected-item');
        let chat = this.findChatById(id);
        
        this.api.subscribeOnMessagesInChat(id, (msg) => this.receiveMsg(id, msg));
        
        // Reset pagination and load messages
        this.page = 1;
        this.hasMoreMessages = true;
        this.renderMessages(id);
    };

    this.openUserInChat = async (chatId, userId) => {

        let user = await this.api.getUserInChat(chatId, userId);
        let roles = await this.api.getAllRolesInChat(chatId);
        roles = roles.map(el => ({ role: el, selected: el == user.role ? 'selected' : '' }))
        user.avaibleRoles = roles;
        user.current = user.id == this.token.userId;
        user.chatId = chatId;
        
        if (!user.statuses.includes('ONLINE')) user.statuses.push('OFFLINE');
        openRenderModal('#user-in-chat-modal',user);
    

    }
    this.editChatModal = async (chatId) => {

        console.log(chatId);
        let userInChat = await this.api.getUserInChat(chatId,  this.token.userId);
        userInChat.chatId = chatId;
        let chat = await this.api.getChat(chatId);
        let roles = await this.api.getAllRolesInChat(chatId);
        //roles = roles.map(el => ({ role: el, selected: el == userInChat.role ? 'selected' : '' }))
        //userInChat.avaibleRoles = roles;
        userInChat.name = chat.name;
        
        userInChat.role = await this.api.getRole(chatId,chat.role);
        userInChat.isOwned = userInChat.role.name=='CREATOR';
        let users = await this.api.getUsersInChat(chatId);
        users.forEach(user=>{
            user.avaibleRoles = roles.map(el => ({ role: el, selected: el == user.role ? 'selected' : '' }))
        });
        let userNames = users.map((user)=>user.login);
        let usersStr = userNames.join(",");
        userInChat.users = users;

      
        
        openRenderModal('#edit-chat-modal',userInChat,{
            'userInChat':$('#user-in-chat-template').html()
        });
   

    }

    this.addUserModal = (chatId) => {
        console.log(chatId);
        let data={chatId: chatId};
        openRenderModal('#add-user-modal',data);
    }

    this.addUsersInChat = async(chatId, login) => {
        console.log(login);
        let roles = await this.api.getAllRolesInChat(chatId);
        this.api.addUserInChat(chatId,login).then((user)=>{
            user.avaibleRoles = roles.map(el => ({ role: el, selected: el == user.role ? 'selected' : '' }))
            user.chatId = chatId;
            console.log(user);
            appendListItem('.chat-users',render('#user-in-chat-template',user));
        });
    }
    this.saveSettings = (data)=>{
        let old = this.settings;
        this.settings = data;
        updateObject(this.api.getOptions(), data);
        console.log(data.serverUrl);
        if(old.serverUrl!=data.serverUrl)  this.init(true);
 
       
       
        saveCookie('settings',this.settings);

    }
    this.editSettingsModal = ()=>{
        openRenderModal('#settings-modal', this.settings);
    }
    this.renameChat= (id,chat)=>{

        this.api.renameChat(id, chat);
    }

    this.setUserRole= (chatId, userId, role)=>this.api.setUserRole(chatId, userId, role);

    this.openCurrentUserProfile = () => {
        this.openUserInChat(this.currentChatId,this.token.userId);
    }

    this.createChat = () => {
        let users = $('#users').val();
        users = users.split(',');
        let name = $('#chatName').val();
        this.api.createChat({ name: name, users: users });
    }

    this.receiveMsg = (chatId, m) => {
        let op = m.operation;
        let data = m.data;
        let chat = this.findChatById(chatId);
        chat.messages = chat.messages || [];

        if (chatId != this.currentChatId) return;
        if (op === "ADD") {
            this.onAddMsg(chat, data);

        }
        else if (op === "DELETE") {
            this.onDeleteMsg(chat, data);
        }
        else if (op === "UPDATE") {
            this.onUpdateMsg(chat, data);
        }
        if (chatId == this.currentChatId) {
            //this.renderMessages(chatId);
        }
    }

    this.onError = (error) => {
        if(error.error=='TokenExpiredException'){
            clearCookie('token');

            this.init(true);
        }
        console.log(error);
        if (error.message) error = error.message;
        this.showError(error);
    }

    this.showError = (error) => {
        $("#errorModalMessage").html(error);
        $("#errorModal").modal('show');;
    }
    this.showAlert = (title, msg='') => {
        $("#alert-modal-title").html(title);
        $("#alert-modal-msg").html(msg);
        $("#alert-modal").modal('show');
    }




    this.initEvents = () => {
        $('.btn-close').on('click', function (e) {
            $(this).closest('.modal').modal('hide');
            console.log('hide modal');
        })
        $('.tags-input').tagsInput();
        $(".chat-list").searcher({
            inputSelector: "#chat-search",
            itemSelector: ".chat-item",
            textSelector: "span",
            // itemSelector (tbody > tr) and textSelector (td) already have proper default values
        });
        $(".search-clear").on("click",function(e){
            $btn = $(this);
            $input = $btn.parent().find(".search-input");
            $input.val('');
            $input.trigger('change');
        });

        $('.messages-wrapper').on('scroll', () => {
            // Проверяем, достигли ли верха wrapper'а
            if ($('.messages-wrapper').scrollTop() < 100 && !this.isLoading && this.hasMoreMessages) {
                this.loadMoreMessages();//вместо кнопки
            }
        });

    }


    this.init = async (firstTime = false) => {
        this.settings = getNotEmpty(this.settings, window.settings, getCookie('settings')) || {};
        this.api = new MessengerApi({
            serverUrl: this.settings.serverUrl, brokerUrl: this.settings.serverUrl+'/ws', onConnect: async(m) => {
                this.chats = await this.api.getChats();
                this.api.subscribeOnChats((m) => {
                    let op = m.operation;
                    let data = m.data;
                    if (op === "ADD") {
                        this.chats.push(data);
                        this.onAddChat(data);
                    }
                    else if (op === "DELETE") {

                        this.chats = this.chats.filter((e) => e.id != data.id);
                        this.onDeleteChat(data)


                    }
                    else if (op === "UPDATE") {
                        this.updateChat(data.id, data);
                        this.onUpdateChat(data);
                    }
                });

                this.renderChats();
                this.openChat(window.currentChatId);
            },
            onError: this.onError
        });

        if(!firstTime) return;
        this.token =getCookie('token');
        if(!getCookie('settings')['remember']) this.token=null;
        if(this.token!=null){
            await this.api.init(this.token);
            this.api.socketClientConnect();
        }else  $("#auth-modal").modal('show');

        this.initEvents();


        $('textarea').css('overflow', 'hidden');


        const toggler = document.querySelector(".open-sidebar");
        toggler.addEventListener("click", function () {
            document.querySelector("#sidebar").classList.toggle("collapsed");
        });



    }
    this.auth = async () => {
        
        try{
        this.token = await this.api.authUser({ "login": $('#login').val(), "password": $('#password').val() });
        if (!this.token) return;
        
        await this.api.init(this.token);
        this.api.socketClientConnect();
        saveCookie('token',this.token);
        $("#auth-modal").modal('hide');
        $(".modal-backdrop").modal('hide');
     
        }catch(e){}

    }

    this.register = async () => {
        let data = { "login": $('#login').val(), "password": $('#password').val() };
        try{
        let result = await this.api.registerUser(data);
        await this.auth();
        } catch(e){};
        

    }


    this.deleteChat = (id) => {
        this.api.deleteChat(id);
    }

    this.deleteMsg = (id) => {
        this.api.deleteMessage(this.currentChatId, id);
    }

    this.banUser = (chatId, id) => {

        this.api.banUserFromChat(id, chatId).then(_=>{
            if(isModalShown('#edit-chat-modal')){
                removeItem(`.user-item[data-id=${id}]`);
            }
        });
     
    }


    this.renderChat= async (el)=>{
        let role = await this.api.getRole(el.id,el.role);
        el.isOwned = role.banUser || role.addUser || role.editChat || role.deleteMessage;
        return this.renderChatTemplate(el);
    }
    this.renderChats = () => {
        const template = $('#chat-template').html();
        console.log('rendering chats');
        let $chatList = $('.chat-list');
        $chatList.empty();
        $.each(this.chats, async  (index, el) =>{
            appendListItem('.chat-list', await this.renderChat(el));


        });
    }

    this.renderMessages = async (chatId) => {
        this.currentChatId = chatId;
        this.page = 0;
        this.hasMoreMessages = true;
        
        let userId = this.token.userId;
        let chat = this.findChatById(chatId);
        
        // Clear existing messages
        chat.messages = [];
        $('.message-list').empty();
        
        // Load first page
        await this.loadMoreMessages(chatId);
        $('.messages-wrapper').scrollTop($('.message-list')[0].scrollHeight);
    }

    this.loadMoreMessages = async (chatId=this.currentChatId) => {
        if (this.isLoading || !this.hasMoreMessages) return;
        
        this.isLoading = true;
        $('.load .btn').prop('disabled', true);
        
        try {
            let messages = await this.api.getMessagesFromChat(chatId, {
                page: this.page,
                count: this.messagesPerPage
            });
            console.log(messages);
            
            let chat = this.findChatById(this.currentChatId);
            const scrollHeight = $('.message-list')[0].scrollHeight;
            const scrollTop = $('.messages-wrapper').scrollTop();
            // Check if we have more messages to load
            if (messages.length < this.messagesPerPage) {
                this.hasMoreMessages = false;
                $('.load').hide();
            } else {
                $('.load').show();
            }
            
            // Append new messages
            messages.forEach(msg => {
                chat.messages.unshift(msg); // Add to beginning to maintain order
                prependListItem('.message-list', this.renderMsgTemplate(msg));
            });
            // Восстанавливаем позицию скролла
            if (this.page > 1) {
                $('.messages-wrapper').scrollTop($('.message-list')[0].scrollHeight - scrollHeight + scrollTop);
            }
            
            this.page++;
            
        } catch (error) {
            this.showError('Failed to load messages');
        } finally {
            this.isLoading = false;
            $('.load .btn').prop('disabled', false);
        }
    }

    this.renderChatTemplate = (chat) => {
        const template = $('#chat-template').html();
        const rendered = Mustache.render(template, chat);
        return rendered;
    }
    this.renderMsgTemplate = (msg) => {
        msg.chatId=this.currentChatId;
        let userId = this.token.userId;
        msg.isOwned = userId == msg.senderId;
        //msg.message = msg.message.replace(new RegExp('\r?\n','g'), '<br />');
        const template = $('#message-template').html();
        const rendered = Mustache.render(template, msg);
        return rendered;
    }

    this.sendMessage = () => {
        let message = $('#messageInput').val();
        if (message && this.currentChatId) {
            this.api.sendMessageToChat(this.currentChatId, message);
            $('#messageInput').val('');
        }
    }

}
$(async () => {
    includeHTML();
    app = new App();
    await app.init(true);

    const observer = new MutationObserver((mutations) => {
        mutations.forEach((mutation) => {
            mutation.addedNodes.forEach((node) => {
                if (node.nodeType === 1 && !node["htmx-internal-data"]) {
                    htmx.process(node);
                }
            })
            app.initEvents();
        })
    })
    observer.observe(document, { childList: true, subtree: true })

});