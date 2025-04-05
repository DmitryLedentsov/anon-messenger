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

    this.openUserInChat = (userId) => {
        let chatId = this.currentChatId;
  
        let user = this.api.getUserInChat(chatId, userId);
        let roles = this.api.getAllRolesInChat(chatId);
        roles = roles.map(el => ({ role: el, selected: el == user.role ? 'selected' : '' }))
        user.avaibleRoles = roles;
        user.current = user.id == this.token.userId;
        user.chatId = chatId;
        if (!user.statuses.includes('ONLINE')) user.statuses.push('OFFLINE');
        this.openRenderModal('#user-in-chat-modal',user);
    

    }
    this.editChatModal = (chatId) => {

        console.log(chatId);
        let userInChat = this.api.getUserInChat(chatId,  this.token.userId);
        userInChat.id = chatId;
        let chat = this.api.getChat(chatId);
        let roles = this.api.getAllRolesInChat(chatId);
        roles = roles.map(el => ({ role: el, selected: el == userInChat.role ? 'selected' : '' }))
        userInChat.avaibleRoles = roles;
        userInChat.name = chat.name;
        
        userInChat.isOwned = userInChat.role=='CREATOR';
        let users = this.api.getUsersInChat(chatId);
        let userNames = users.map((user)=>user.login);
        let usersStr = userNames.join(",");
        userInChat.users = usersStr;

      
        
        this.openRenderModal('#edit-chat-modal',userInChat);
   

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

    function appendListItem(listName, listItemHTML) {
        $(listItemHTML)
            .hide()
            .css('opacity', 0.0)
            .appendTo(listName)
            .slideDown(100)
            .animate({ opacity: 1.0 })
    }

    function removeItem(name) {
        $(name).fadeOut(300, function () { $(this).remove(); });
    }
    function replaceElem(selector, html) {
        var div = $(selector).hide();
        $(selector).replaceWith(html);
        $(selector).fadeIn("slow");
    }


    this.onError = (error) => {
        console.log(error);
        if (error.message) error = error.message;
        $("#errorModalMessage").html(error);
        $("#errorModal").modal('show');;
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


    this.init = () => {
        let $settings = $('#settings');
        this.api = new MessengerApi({
            serverUrl: $settings.attr('server-url'), brokerUrl: $settings.attr('socket-url'), onConnect: (m) => {
                this.chats = this.api.getChats();
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

        $("#authModal").modal('show');

        this.initEvents();


        $('textarea').css('overflow', 'hidden');


        const toggler = document.querySelector(".open-sidebar");
        toggler.addEventListener("click", function () {
            document.querySelector("#sidebar").classList.toggle("collapsed");
        });




    }
    this.auth = () => {
        this.token = this.api.authUser({ "login": $('#login').val(), "password": $('#password').val() });
        if (!this.token) return;
        this.api.init(this.token);
        this.api.socketClientConnect();

        $("#authModal").modal('hide');

    }

    this.register = () => {
        let data = { "login": $('#login').val(), "password": $('#password').val() };
        let result = this.api.registerUser(data);
        if (result) this.auth();

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


    this.openRenderModal = (modal, data) => {
        $(modal).remove();
        const template = $(`${modal}-template`).html();

        $('body').append(Mustache.render(template, data));
        $(modal).modal('show');
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
    this.renderMessages = (chatId) => {
        this.currentChatId = chatId;

        let userId = this.token.userId;
       
        console.log('rendering chat ' + chatId);
        let chat = this.findChatById(chatId);
        chat.messages = this.api.getMessagesFromChat(chatId);
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
        msg.message = msg.message.replace(new RegExp('\r?\n','g'), '<br />');
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
$(() => {
    includeHTML();
    app = new App();
    app.init();

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