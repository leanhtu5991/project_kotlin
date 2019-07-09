var express = require('express');
var expressJwt = require('express-jwt');
var router = express.Router();
var db = require('../db');

function newDateTime(){
    var d=new Date();
    var date_format_str = d.getFullYear().toString()+"-"+((d.getMonth()+1).toString().length==2?(d.getMonth()+1).toString():"0"+(d.getMonth()+1).toString())+"-"+(d.getDate().toString().length==2?d.getDate().toString():"0"+d.getDate().toString())+" "+(d.getHours().toString().length==2?d.getHours().toString():"0"+d.getHours().toString())+":"+((parseInt(d.getMinutes()/5)*5).toString().length==2?(parseInt(d.getMinutes()/5)*5).toString():"0"+(parseInt(d.getMinutes()/5)*5).toString())+":00";
    return date_format_str;
}

router.get('/topics', function(req, res){
    con.query("select t.id, t.topic, t.person_id, t.date_create, p.email, p.name from topic t, person p where p.id = t.person_id order by date_create desc", function (err, result, fields) {
        if (err) throw err;
        for(var i=0; i<result.length; i++){
            var d = result[i].date_create;
            result[i].date_create = "On "+
            (d.getDate().toString().length==2?d.getDate().toString():"0"+d.getDate().toString())+"/"+
            ((d.getMonth()+1).toString().length==2?(d.getMonth()+1).toString():"0"+(d.getMonth()+1).toString())+"/"+
            d.getFullYear().toString()+ " at "+
            (d.getHours().toString().length==2?d.getHours().toString():"0"+d.getHours().toString())+":"+
            ((parseInt(d.getMinutes()/5)*5).toString().length==2?(parseInt(d.getMinutes()/5)*5).toString():"0"+(parseInt(d.getMinutes()/5)*5).toString())
            ;
        }
        res.status(200).json(result);
      });
});

router.get('/topics/:ID', function(req, res){
    var topic_id=req.params.ID;
    con.query("select m.id, m.message, m.date_create, p.name, p.email from message m, person p where m.person_id=p.id and topic_id="+topic_id+" order by date_create desc", function (err, result, fields) {
        if (err) throw err;
        for(var i=0; i<result.length; i++){
            var d = result[i].date_create;
            result[i].date_create = "On "+
            (d.getDate().toString().length==2?d.getDate().toString():"0"+d.getDate().toString())+"/"+
            ((d.getMonth()+1).toString().length==2?(d.getMonth()+1).toString():"0"+(d.getMonth()+1).toString())+"/"+
            d.getFullYear().toString()+ " at "+
            (d.getHours().toString().length==2?d.getHours().toString():"0"+d.getHours().toString())+":"+
            ((parseInt(d.getMinutes()/5)*5).toString().length==2?(parseInt(d.getMinutes()/5)*5).toString():"0"+(parseInt(d.getMinutes()/5)*5).toString())
            ;
        }
        res.status(200).json(result);
      });
})

router.post('/topics/new', function(req, res){
    var date_create = newDateTime();
    var person_name = req.body.person_name;
    var person_email = req.body.person_email;
    var topic_contents = req.body.topic_contents;

    con.query("SELECT * FROM person WHERE email= ?", [person_email], function (err, result) {
        if (err) throw err;
        else {
            if(result.length>0){
                var person_id = result[0].id;
                con.query("insert into topic (topic, person_id, date_create) values (?,?,?)", [topic_contents, person_id, date_create],function (err, result, fields) {
                    if (err) throw err;
                    res.status(200).json("result");
                });   
            } else{
                con.query("insert into person (name, email) values (?, ?)", [person_name, person_email], function (err, result, fields) {
                    if (err) throw err;
                    con.query("select id from person where email=?" , [person_email], function (err, result, fields) {
                        if (err) throw err;
                        else{
                            if(result.length>0){
                                var person_id = result[0].id;
                                con.query("insert into topic (topic, person_id, date_create) values (?,?,?)", [topic_contents, person_id, date_create],function (err, result, fields) {
                                    if (err) throw err;
                                    res.status(200).json("result");
                                });   
                            }
                        }
                    });     
                });
            }
        }
    });
})

router.post('/topics/ID/new', function(req, res){
    var date_create = newDateTime();
    var person_id = req.body.person_id;
    var topic_id = req.body.topic_id;
    var message_contents = req.body.message_contents;
    var params = [message_contents, date_create, person_id, topic_id]
    con.query("insert into message (message, date_create, person_id, topic_id) values (?, ?, ?, ?)", params, function (err, result) {
        res.status(200).json("result");
    })
})

module.exports = router;