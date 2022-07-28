$( document ). ready(function(){

    var roomId
    var username

    var connected = false

    var msgInput = $("#msg_input")
    var chatContainer = $("#chat_container")
    var sendButton = $("#send_button")
    var connectButton = $("#connect_button")
    var whisperToInput = $("#whisper_to_input")
    var usernameInput = $("#username_input")

    var ws

    sendButton.click(function(){
        if (!connected) return;
        var msg = {"roomId": roomId, "content": msgInput.val()}
        if (whisperToInput.val() != "") msg.whisperingTo = whisperToInput.val()
        ws.send(JSON.stringify(msg))
        msgInput.val("")
    })

    connectButton.click(function(){
        ws = new WebSocket(`ws://192.168.1.3:8080/chatwebsocket/${usernameInput.val()}`)

        ws.onopen = function (event) {
            alert("Connected!")
            connected = true
        }

        ws.onerror = function (event) {
            console.log('WebSocket error: ', event)
        }

        ws.onclose = function (event) {
            console.log('WebSocket closed: ', event)
            connected = false
        }

        ws.onmessage = function (event) {
            var msg = JSON.parse(event.data)
            switch (msg.type) {
                case "greeting":
                    roomId = msg.roomId
                    username = msg.username
                    break
                case "chat":
                    chatContainer.append(`<p>${ msg.whisperRecipient != null ? "(Whisper)" : "" }${msg.sender}: ${msg.content}</p>`)
            }
        }
    })
})