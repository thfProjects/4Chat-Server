$( document ). ready(function(){

    var roomId

    var msgInput = $("#msg_input")
    var chatContainer = $("#chat_container")
    var sendButton = $("#send_button")

    var ws = new WebSocket("ws://192.168.1.3:8080/chatwebsocket")

    ws.onmessage = function (event) {
        var msg = JSON.parse(event.data)
        switch (msg.type) {
            case "greeting": roomId = msg.roomId
                break
            case "chat": chatContainer.append(`<p>${msg.sender}: ${msg.content}</p>`)
        }
    }

    ws.onerror = function (event) {
        console.log('WebSocket error: ', event)
    }

    ws.onclose = function (event) {
        console.log('WebSocket closed: ', event)
    }

    sendButton.click(function(){
        var msg = {"roomId": roomId, "sender": "webclient", "content": msgInput.val()}
        ws.send(JSON.stringify(msg))
        msgInput.val("")
    })
})