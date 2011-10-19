var express = require('express'),
    util = require('util'),
    everyauth = require('everyauth'),
    Promise   = everyauth.Promise;
  
var config = {
  "secrets" : {
    "clientId" : 'AA2ZLYDVERDRTBCLEI5QHCLSOWUNLNB02XTLFVB0OJHOFFPL',
    "clientSecret" : 'ZKHQWVZH45SYTRKZAN4RXNT2MHHTGYP5K0OOTVRBP1DN4HX5',
    "redirectUrl" : 'http://localhost:8000/auth/foursquare/callback'
  }
};

var foursquare = require('node-foursquare')(config);

var usersById = {};
var nextUserId = 0;
var usersByFoursquareId = {};

function addUser (source, sourceUser) {
  var user;
  if (arguments.length === 1) { // password-based
    user = sourceUser = source;
    user.id = ++nextUserId;
    return usersById[nextUserId] = user;
  } else { // non-password-based
    user = usersById[++nextUserId] = {id: nextUserId};
    user[source] = sourceUser;
  }
  return user;
}



everyauth.foursquare
  .appId(config.secrets.clientId)
  .appSecret(config.secrets.clientSecret)
  .handleAuthCallbackError( function (req, res) {
    // If a user denies your app, Facebook will redirect the user to
    // /auth/facebook/callback?error_reason=user_denied&error=access_denied&error_description=The+user+denied+your+request.
    // This configurable route handler defines how you want to respond to
    // that.
    // If you do not configure this, everyauth renders a default fallback
    // view notifying the user that their authentication failed and why.
  })
  .findOrCreateUser(function (sess, accessTok, accessTokExtra, fqMetaData) {
    var promise = this.Promise();
    var user_key = 'user:'+ fqMetaData.id;
    console.log(user_key);
    var user = foursquare.get_user(accessTok, fqMetaData);
    util.inspect(user, true, null);
    usersById[user_key] = user;
    promise.fulfill(user);
    return promise;
  })
  .entryPath('/auth/foursquare')
  .redirectPath('/');

everyauth.everymodule
  .findUserById( function (id, callback) {
  callback(null, usersById[id]);
});

var app = express.createServer(
  express.logger()
, express.bodyParser()
, express.cookieParser()
, express.favicon()
, everyauth.middleware()
);

app.configure( function () {
  app.set('view engine', 'ejs');
  app.use(express.session({ secret: "foursquare_simple" }));
});

app.configure('development', function() {
  app.use(express.static(__dirname + '/public'));
  app.use(express.errorHandler({ dumpExceptions: true, showStack: true }));
});

app.configure('production', function() {
  app.use(express.static(__dirname + '/public', { maxAge: 31557600000 }));
  app.use(express.errorHandler());
});

app.get('/', function(req, res) {
  res.render('index', { layout: false });
});

everyauth.helpExpress(app);
app.listen(parseInt(process.env.PORT) || 3000 );
console.log("Listening on " + app.address().port);
