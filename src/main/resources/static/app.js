var stompClient = null;

function connect() {
    var socket = new SockJS('/websocks');

    stompClient = Stomp.over(socket);

    stompClient.connect({ 'client-id': $("#username").val() }, function (frame) {
        console.log('Connected: ' + frame);
        showServerMessageOnChat("Yay! You are now connected to the chat stream! Have fun!");

        stompClient.subscribe('/chat', function (greeting) {
            showClientChats(greeting.body);
        });

        stompClient.subscribe('/chatuploads', function(filename) {
            showClientFileUploads(filename.body);
        });

        stompClient.subscribe('/connections', function(newConnection) {
            updateUI(newConnection);
        });

        stompClient.subscribe('/liveUsers', function(update) {
            showUserCount(update);
        });

        safetyHandlerOn();
        $("#msgText").focus()
    }, function (error) {
        if(error.headers)
            alert(error.headers.message.split('?')[1]);
    });
}

function showUserCount(update) {
    let object = JSON.parse(update.body);
    let count = object.count;
    $("#onlineCount").html(count);
}

function updateUI(raw) {
    let object = JSON.parse(raw.body);
    showServerMessageOnChat(object.message);
}

function showClientChats(messageObject) {
    let message = JSON.parse(messageObject)['message'];
    $("#chats").append("<tr><td class='msg'>" + new Date().toLocaleTimeString() + " - " + message + "</td></tr>");
    scrollChat();
}

function showClientFileUploads(response) {
    response = JSON.parse(response);
    $("#chats").append(`<tr><td class='msg'>${new Date().toLocaleTimeString()} - ${response.userId}: FILE: <a target='_blank' href='/files/${response.filename}'>${response.filename}</a></td></tr>`);
    scrollChat();
}

function showServerMessageOnChat(message) {
    $("#chats").append("<br><tr><td><i class='serverMsg'>---------------- " + message + " ----------------</i></td></tr><br><br>");
    scrollChat();
}

function scrollChat() {
    $('#chats').animate({ scrollTop: $('#chats').prop("scrollHeight") }, 500);
}

function sendMessage() {
    let msgElement = $("#msgText");
    let username = $("#username").val();
    let msgText = msgElement.val();
    if(username && msgText) {
        stompClient.send("/app/message", {}, JSON.stringify({ 'id': username, 'message': msgText }));
        msgElement.val('');
        msgElement.focus();
    }
}

function disconnect() {
    stompClient.disconnect();
    showServerMessageOnChat("You have left the chat room! Connect again to start receiving the messages")
    safetyHandlerOff();
}

function safetyHandlerOn() {
    $("#connect").prop("disabled", true);
    $("#send").prop("disabled", false);
    $("#username").off("keyup");
    $("#disconnect").prop("disabled", false);
    $("#upload-file").prop("disabled", false);
}

function safetyHandlerOff() {
    $("#connect").prop("disabled", false);
    $("#send").prop("disabled", true);
    $("#username").keyup(function (e) { _keyupHandler(e); });
    $("#disconnect").prop("disabled", true);
    $("#onlineCount").html('');
    $("#upload-file").prop("disabled", true);
}

function executeOnEnter(e, callback) {
    e.preventDefault();
    if(e.keyCode == 13) {
        callback();
    }
}

function _keyupHandler(e) {
    if (e.target.value.length >= 3) {
        $("#connect").prop("disabled", false);
        executeOnEnter(e, connect);
    } else {
        $("#connect, #send").prop("disabled", true);
    }
}

function clearScreen() {
    $("#chats").html('');
}

function showName() {
    let fileName = $("#file")[0].files[0].name;
    $('#file-selected').html(fileName);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    $("#connect").click(function () { connect(); });
    $("#send").click(function () { sendMessage(); });
    $("#disconnect").click(function() { disconnect(); });
    $("#clear").click(function() { clearScreen(); });
    
    $("#username").keyup(function(e) { _keyupHandler(e); });
    $("#msgText").keyup(function(e) { executeOnEnter(e, sendMessage) });

    $("#upload-file").on("click", (e) => {
        let file = $("#file")[0].files[0];
        if(file) {
            let formData = new FormData();
            formData.append("file", file);

            fetch('/upload', {
                method: 'POST',
                body: formData
            }).then(resp => {
                console.log(resp);
                stompClient.send("/app/fileMessage", {}, JSON.stringify({ 'id': $("#username").val(), 'filename': file.name }));
            }).catch(err => {
                if(err.message.includes("NetworkError")) {
                    alert("File too large! (>50MB) 😢")
                } else {
                    alert(err.message);
                }
            });
        } else {
            alert('Choose a file first!');
        }
    });
});