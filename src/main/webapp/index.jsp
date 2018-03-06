
<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta name="description" content="">
<meta name="author" content="">

<title>Tail2Web Demonstration Application::Log Information
	Beautifully on the Web</title>

<!-- Bootstrap CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
	integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm"
	crossorigin="anonymous">
<!-- Custom CSS -->
<link rel="stylesheet" href="css/custom.css" />
</head>

<body>
	<header>
		<div class="collapse bg-dark" id="navbarHeader">
			<div class="container">
				<div class="row">
					<div class="col-sm-8 col-md-7 py-4">
						<h4 class="text-white">About</h4>
						<p class="text-muted">The implementation shows how to use and
							combine the logging library log4j in combination with websockets
							to create beautiful web tails of logs. The implementation uses annotations to create a custom appender that streams to all connected websocket clients.</p>
					</div>
					<div class="col-sm-4 offset-md-1 py-4">
						<h4 class="text-white">Contact</h4>
						<ul class="list-unstyled">
							<li><a href="https://twitter.com/fridlutz"
								class="text-white">Follow on Twitter</a></li>
							<li><a href="https://github.com/fridlutz/tail2web"
								class="text-white">Fork on Github</a></li>
							<li><a href="mailto:me@mymess.org" class="text-white">Email
									me</a></li>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<nav class="navbar navbar-dark bg-dark fixed-top">
			<div class="container">
				<a href="#" class="navbar-brand d-flex align-items-center"> <span
					data-feather="align-left"></span>&nbsp; <strong>Tail2Web</strong>
				</a>
				<div class="btn-group" role="group" aria-label="Action buttons">
					<button id="start" type="button" class="btn btn-secondary">Start</button>
					<button id="stop" type="button" class="btn btn-secondary">Stop</button>
					<button style="width: 100px;" id="status"
						class="btn btn-sm btn-danger" disabled="disabled">
						<span id="status_text">Offline</span>
					</button>
					<button class="navbar-toggler" type="button" data-toggle="collapse"
						data-target="#navbarHeader" aria-controls="navbarHeader"
						aria-expanded="false" aria-label="Toggle navigation">
						<span class="navbar-toggler-icon"></span>
					</button>
				</div>

			</div>
		</nav>

	</header>

	<!-- Begin page content -->
	<main role="main">
	<section class="jumbotron text-center bg-light">
		<div class="container">
			<h1 class="jumbotron-heading">Tail2Web Example Application</h1>
			<p class="lead text-muted">This example application is a
				demonstration on how to present log information using websockets
				with minimal implementation effort beautifully on the web.</p>
			<p class="lead text-muted">Author: <a href="https://github.com/fridlutz/tail2web">@fridlutz</a></p>
		</div>
	</section>

	<div class="container" style="margin-top: 30px;">
		<ul class="list-group" id="logpanel">

		</ul>
	</div>
	</main>

	<footer class="footer">
		<div class="container">
			<span class="text-muted">author: <a href="https://github.com/fridlutz/tail2web">@fridlutz</a> @note: this demonstration UI uses the Album
				example from &copy;Bootstrap, available <a
				href="https://getbootstrap.com/docs/4.0/examples/album/">online</a>
			</span>
		</div>
	</footer>

	<!-- Bootstrap core JavaScript -->
	<!-- ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
		integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
		crossorigin="anonymous"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
		integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
		crossorigin="anonymous"></script>
	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"
		integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
		crossorigin="anonymous"></script>

	<!-- Extension Javascript Libraries -->
	<!-- ================================================== -->
	<script src="https://unpkg.com/feather-icons/dist/feather.min.js"></script>
	<script>
		feather.replace()
	</script>
	<!-- WebSocket Javascript Methods -->
	<!-- ================================================== -->
	<script type="text/javascript">
		var url = (window.location.protocol === "https:" ? "wss:" : "ws:")
				+ "//" + window.location.host + window.location.pathname
				+ "weblog";

		var webSocket = new WebSocket(url);

		webSocket.onerror = function(event) {
			onError(event)
		};

		webSocket.onopen = function(event) {
			onOpen(event)
		};

		webSocket.onmessage = function(event) {
			onMessage(event)
		};

		function onMessage(event) {
			var before = document.getElementById('logpanel').innerHTML;
			var logJson = JSON.parse(event.data);
			var cssClass = 'list-group-item-dark';
			if (logJson.level == "DEBUG") {
				cssClass='list-group-item-light';
			}
			else if (logJson.level == "INFO") {
				cssClass='list-group-item-info';
			}
			else if (logJson.level == "WARN") {
				cssClass='list-group-item-warning';
			}
			else if (logJson.level == "ERROR") {
				cssClass='list-group-item-danger';
			}
			else if (logJson.level == "FATAL") {
				cssClass='list-group-item-danger';
			}

			var newMessage = '<li class="list-group-item '+cssClass+'">'
					+ event.data;
			+'</li>';
			document.getElementById('logpanel').innerHTML = newMessage + before;
		}

		function onOpen(event) {
			var before = document.getElementById('logpanel').innerHTML;
			var newMessage = '<li class="list-group-item list-group-item-success">Log engine available and successfully contacted.</li>';
			document.getElementById('logpanel').innerHTML = newMessage + before;

		}

		function onError(event) {
			alert(event.data);
		}

		function start() {
			webSocket.send('start');
			return false;
		}

		function stop() {
			webSocket.send('stop');
			return false;
		}

		$(document).ready(function() {
			$('#start').click(function() {
				$("#status_text").text('Live');
				$("#status").removeClass('btn-danger');
				$("#status").addClass('btn-success');
				start();
			})
		});

		$(document).ready(function() {
			$('#stop').click(function() {
				$("#status_text").text('Offline');
				$("#status").removeClass('btn-success');
				$("#status").addClass('btn-danger');
				stop();
			})
		});
	</script>
</body>
</html>
