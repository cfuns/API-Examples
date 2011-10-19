var http = require('http');
var url = require('url');
http.createServer(function (req, res) {
	//console.log(require('util').inspect(req));
	//console.log(url.parse(req.url, true));

	var url_parts = url.parse(req.url, true);
	var code = url_parts.query.code;

	res.writeHead(200, {'Content-Type': 'text/plain'});
	res.end('YOUR 4S AUTHORIZATION CODE IS: ' + code);
    }).listen(3000, "127.0.0.1");
console.log('Server running at http://127.0.0.1:3000/');