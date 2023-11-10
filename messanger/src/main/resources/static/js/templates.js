const msgTemplate = (msg)=>
    `<li class = "message">
<span>user: <a href="/users/profile/${msg.senderId}"> ${msg.sender} </a></span>   &#160 &#160 &#160<span>message: ${msg.message}</span>
<button class="socket-action" data-url="/app/chat/${chatId}/delete/${msg.id}" data-data="${msg.message}">delete</button> 
</li>`;

const chatTemplate = (chat)=>`<li class = "chat">
<a href="/chat/${chat.id}">${chat.name}</a> 
<button class="socket-action" data-url="/app/user/${userId}/chat/delete/${chat.id}">${chat.role==="CREATOR"? "delete":"leave"}</button> 
</li>`;