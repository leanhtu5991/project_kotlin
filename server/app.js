var express = require('express');
var path = require('path');
var bodyParser = require('body-parser');
var app = express();
var router = express.Router();
var index = require('./routes/index');
var passport = require('passport');
var jwt = require('jsonwebtoken');
var db = require('./db');
var fs = require('fs');

db.tryConnect();

app.set('secretKey', process.env.JWT_SECRET );
// startTasks.createFirstUserIfNotExists();
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true }));

app.use(function (req, res, next) {
    res.setHeader("Access-Control-Allow-Origin", "*");
    res.setHeader("Access-Control-Allow-Credentials", "true");
    res.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,PATCH,OPTIONS");
    res.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
    next();
  });

app.use(passport.initialize());
app.use('/api', index)

app.use(function (err, req, res, next) {
  let errMessage = {};
  errMessage.message = err.message;
  errMessage.error = req.app.get('env') === 'development' ? err : {};
  errMessage.status = (err.status || 500);
  res.json(errMessage);
});

module.exports = app;