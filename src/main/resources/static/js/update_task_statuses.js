$(document).ready(function () {
    sendRequest();
});

function sendRequest() {
    $.ajax({
        url: "/status/isTaskRunning",
        success:
            function (result) {
                updateHTML(result);
                setTimeout(function () {
                    sendRequest();
                }, 30_000);
            }
    });
}

function updateHTML(isRunning) {
    var buttons = $("a[name='runTask']");
    for (var i = 0; i < buttons.length; i++) {
        var button = buttons[i];
        if (isRunning) {
            $(button).addClass("disabled");
        } else {
            $(button).removeClass("disabled");
        }
    }
}
