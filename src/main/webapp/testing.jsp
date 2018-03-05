<html>
    <body>
        <h2>Tomcat-based WebSocket on Heroku Demo</h2>
        <p id="demo"></p>
    </body>
</html>

<script type="text/javascript">
    //set up websocket
    var url = (window.location.protocol === "https:" ? "wss:" : "ws:") + "//" + window.location.host + window.location.pathname + "weblog";
    var webSocket = new WebSocket(url);
    webSocket.onopen = function () {
        console.log("WebSocket is connected.");
    };
    webSocket.onmessage = function (event) {
        console.log(event.data);
        var demo = document.getElementById("demo");
        demo.innerHTML = demo.innerHTML + "<br/>" + event.data;
    };
   
</script>