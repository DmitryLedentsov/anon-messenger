$(function() {
    messages = [];
    ops = [];
    const OP_TYPE = {
        DELETE: 0,
        CREATE: 1,
    }
    function update(){
        $.ajax({
            url: "/chat/"+chatId+"/messages",
            type: 'get',
            success: function (data) {
                // Whatever you want to do after the form is successfully submitted
                ops.clear();
                messages.forEach((el)=>{
                    if(!data.includes(el)){
                        ops.push({...el, type:OP_TYPE.DELETE});
                    }
                })
                data.forEach((el)=>{
                    if(!messages.includes(el)){
                        ops.push({...el, type:OP_TYPE.CREATE});
                    }
                })

                ops.forEach((op)=> {
                    if(op.type==OP_TYPE.CREATE){
                        //TODO: update messages
                    }
                });

            },
            error: function(error) {
                // Here you can handle exceptions thrown by the server or your controller.
                console.log("error!");
                console.log(error);
            }
        });
    }

    setInterval(()=>update(), 100);
});