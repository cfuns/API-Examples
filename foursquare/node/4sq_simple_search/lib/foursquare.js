exports.get_checkins = function(hereNow) 
{
  var checkins = [];
  for (var i = 0; i < hereNow.groups.length; i++) {
    var group = hereNow.groups[i];
    for (var j = 0; j < group.items.length; j++) {
      var checkin = group.items[j];
      checkins.push({'user_id': checkin.user.id, 
                  'created_at': checkin.createdAt});
    }
  }
  return checkins;
}

exports.get_user = function(accessToken, user_data) 
{
  var user = {
    'id': user_data.id, 
    'accessToken': accessToken, 
    'first': user_data.firstName,
    'last': user_data.lastName,
    'photo_url': user_data.photo,
  };
  return user;
}

exports.get_checkins = function(db, venue_id, num_seconds_ago, callback) 
{
  var end   = Math.round((new Date()).getTime() / 1000);
  var start = end - num_seconds_ago;
  var venue_key = 'venue:' + venue_id + ':checkins';
  db.zrangebyscore(venue_key, start, end, function(err, result) {
    callback(err, result);
  });
}

exports.get_venue = function(db, venue_id, callback)
{
  var venue_key = "venue:" + venue_id;
  db.hgetall(venue_key, callback);
}

exports.search_venues = function(fsq, lat, lng, params, accessToken, callback) 
{
  if (!('limit' in params)) { params['limit'] = 20; }
  fsq.Venues.search(lat, lng, params, accessToken, callback);
}

