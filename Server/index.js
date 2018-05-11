var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

app.get('/', function(req,res){
	res.sendFile(__dirname+'/index.html');
})
io.on('connection', function(socket){
	console.log('one user connected' + socket.id);
	socket.on('message', function(data){
		var sockets = io.sockets.sockets;
					socket.broadcast.emit('message',{message:data});
		console.log(data);
	})
	socket.on('disconnected', function(){
		console.log('disconnected' + socket.id)
	})
})

http.listen(3000, function(){
	console.log('server listening on port');
})

// 서버 열기 시작
//
// var app = require('express')();
// var http = require('http').Server(app);
// var io = require('socket.io')(http);
//
// app.get('/', function(req, res){
//   res.sendFile(__dirname + '/index.html');
// });
//
// http.listen(3000, function(){
//   console.log('listening on *:3000');
// });
//
//
// io.on('connection', function(socket){
//
// 	socket.on('chat message', function(msg){
//     io.emit('chat message', msg);
//   });
//
// 	socket.broadcast.emit('hi');
// });
//
// io.emit('some event', { for: 'everyone' });
// 서버 열기 끝


// var express = require('express');
// var app = express();
// var http = require('http').Server(app);
// var io = require('socket.io')(http);
// var fs = require('fs');
//
// app.listen(3000);
//
// app.get('/', function(req, res) {
// 	 res.sendFile('./index.html');
// });

// io.on('connection', function (socket) {
//   socket.on('my other event', function (data) {
//     console.log('connection');
//   });
// });
