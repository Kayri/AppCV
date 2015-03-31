var http = require('http');
var fs = require('fs');

var server = http.createServer(function(req , res){
	fs.readFile('./index.html', 'utf-8', function(error, content){
		res.writeHead(200, {"Content-Type": "text/html"});
		res.end(content);
	});
});

var io = require('socket.io').listen(server);

io.sockets.on('connection', function(socket){

	socket.on('time', function() {
        console.log(socket.name + ' me demande le temps');
    });	

    socket.on('message', function(message) {
        console.log(socket.name +' - message :'+ message);
    });
    socket.on('test', function(){
        console.log(socket.name +" a envoyé une requette");
    	socket.broadcast.emit("etat", "Up !!");
        clearInterval(myTimer);
        myTimer = setInterval(funcTimer, temps);
    });
    socket.on('who', function(nom){
        socket.name = nom;
    	console.log(socket.name + ' viens de ce connecter!');
    });

    var temps = 3*60*1000;
    var myTimer = setInterval(funcTimer, temps);
    function funcTimer(){
    socket.broadcast.emit("etat", "Down !!");
    console.log(" Temps écoulé, Game Over!");
    };
});



server.listen(1234);