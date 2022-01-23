var stompClient = null;

function connect() {
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({ 'client-id': $("#username").val() }, function (frame) {
        console.log('Connected: ' + frame);
        showServerMessageOnChat("Yay! You are now connected to the chat stream! Have fun!");
        stompClient.subscribe('/chat', function (greeting) {
            showClientChats(greeting.body);
        });
        stompClient.subscribe('/exits', function(disconnectedClients) {
            updateUI(disconnectedClients);
        });
        stompClient.subscribe('/connections', function(newConnection) {
            updateUI(newConnection);
        });
        stompClient.subscribe('/liveUsers', function(update) {
            let object = JSON.parse(update.body);
            let count = object.count;
            let users = object.users;
            $("#onlineCount").html(count);
            console.log(users);
        })
        safetyHandlerOn();
    }, function (error) {
        if(error.headers)
            alert(error.headers.message.split('?')[1]);
    });
}

function updateUI(raw) {
    let object = JSON.parse(raw.body);
    showServerMessageOnChat(object.message);
}

function showClientChats(message) {
    $("#chats").append("<tr><td class='msg'>" + message + "</td></tr>");
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
}

function safetyHandlerOff() {
    $("#connect").prop("disabled", false);
    $("#send").prop("disabled", true);
    $("#username").keyup(function (e) { _keyupHandler(e); });
    $("#disconnect").prop("disabled", true);
    $("#onlineCount").html('');
}

function _keyupHandler(e) {
    if (e.target.value.length >= 3) {
        $("#connect").prop("disabled", false);
    } else {
        $("#connect, #send").prop("disabled", true);
    }
}

function sendOnEnter(e) {
    e.preventDefault();
    if (e.keyCode == 13) {
        sendMessage();
    }
}

function clearScreen() {
    $("#chats").html('');
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
    $("#msgText").keyup(function(e) { sendOnEnter(e) });
});