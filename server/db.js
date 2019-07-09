var mysql = require('mysql');
const local = require('./config/local')

exports.tryConnect = function() {
    con = mysql.createConnection({
    host: local.host,
    user: local.user,
    password: local.password,
    database: local.database,
    port: local.port
  });
  con.connect(function(err) {
    if (err) throw err;
    console.log("Connected!");
  });
}
  