// noinspection JSUnresolvedReference

let db = connect("mongodb://localhost/admin");

db.createUser(
    {
        user: "mongouser",
        pwd: "mongopw",
        roles: [ { role: "readWrite", db: "local_db" } ]
    }
)
rs.initiate({'_id': 'rs0', 'members': [{'_id': 0, 'host': '127.0.0.1:27017'}]});
