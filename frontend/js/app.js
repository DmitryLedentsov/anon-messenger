var api = window.api || null;
var app = window.app || {};
window.currentChatId = -1;
function App() {
    this.chats = [];
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

    this.onAddChat = (data) => {
        data.isOwned = data.role=='CREATOR';
        appendListItem(".chat-list", this.renderChatTemplate(data));
    }
    this.onDeleteChat = (data) => {
        removeItem(`.chat-item[data-id=${data.id}]`);
        if (data.id == this.currentChatId) {
            $('.message-list').empty();
        }
    }
    this.onUpdateChat = (data) => {
        data.isOwned = data.role=='CREATOR';
        replaceElem(`.chat-item[data-id=${data.id}]`, this.renderChatTemplate(data));
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
        console.log(this.currentChatId);
        $(`.chat-item`).removeClass('selected-item');
        $(`.chat-item[data-id=${id}]`).addClass('selected-item');
        let chat = this.findChatById(id);
        console.log(chat);
        this.api.subscribeOnMessagesInChat(id, (msg) => this.receiveMsg(id, msg));
        this.renderMessages(id);

    };

    this.openUserInChat = async (userId) => {
        let chatId = this.currentChatId;
  
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
        userInChat.id = chatId;
        let chat = await this.api.getChat(chatId);
        let roles = await this.api.getAllRolesInChat(chatId);
        roles = roles.map(el => ({ role: el, selected: el == userInChat.role ? 'selected' : '' }))
        userInChat.avaibleRoles = roles;
        userInChat.name = chat.name;
        
        userInChat.isOwned = userInChat.role=='CREATOR';
        let users = await this.api.getUsersInChat(chatId);
        let userNames = users.map((user)=>user.login);
        let usersStr = userNames.join(",");
        userInChat.users = usersStr;

      
        
        openRenderModal('#edit-chat-modal',userInChat);
   

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
    this.editChat= (id,chat)=>{
        console.log(chat);
        chat.users = chat.users.split(',');
        this.api.editChat(id, chat);
    }

    this.openCurrentUserProfile = () => {
        this.openUserInChat(this.token.userId);
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
        this.token = getCookie('token');
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

    this.banUser = (id) => {
        this.api.banUserFromChat(id, this.currentChatId);
    }



    this.renderChats = () => {
        const template = $('#chat-template').html();
        console.log('rendering chats');
        let $chatList = $('.chat-list');
        $chatList.empty();
        $.each(this.chats, function (index, el) {
            el.isOwned = el.role=='CREATOR';
            const rendered = Mustache.render(template, el);
            appendListItem('.chat-list', rendered);


        });
    }
    this.renderMessages = async (chatId) => {
        this.currentChatId = chatId;

        let userId = this.token.userId;
       
        console.log('rendering chat ' + chatId);
        let chat = this.findChatById(chatId);
        chat.messages = await this.api.getMessagesFromChat(chatId);
        $msgList = $('.message-list');
        $msgList.empty();
        chat.messages && $.each(chat.messages,  (index, el)=> {
            appendListItem('.message-list', this.renderMsgTemplate(el),)

        });
    }

    this.renderChatTemplate = (chat) => {
        const template = $('#chat-template').html();
        const rendered = Mustache.render(template, chat);
        return rendered;
    }
    this.renderMsgTemplate = (msg) => {
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