'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite((change,context) => {
    const user_id = context.params.user_id;
    const notification_id = context.params.notification_id;
    
    console.log(notification_id);

    if(!change.after.val()){
        return console.log('notification is deleted');
    }
    const uId = admin.database().ref(`/notifications/${user_id}/${notification_id}/from`).once('value');
    return uId.then((snapshot)=>{
        from_user_id = snapshot.val();
        const deviceToken = admin.database().ref(`/Users/${user_id}/tokenId`).once('value');
        const from_user = admin.database().ref(`/Users/${from_user_id}/name`).once('value');
        return Promise.all([deviceToken,from_user]).then(result => {
            from_user_name = result[1].val();
            token_id = result[0].val();
            
                const payload = {
                    notification: {
                        title: "Friend Request",
                        body: from_user_name + ' has sent you friend request',
                        icon: "default"
                    },
                    data: {
                        notification: notification_id,
                        user: from_user_id
                    }
                };
                return admin.messaging().sendToDevice(token_id, payload).then(response=>{
                    return console.log("Success");
                });
            });
    });
    
    });

    

    
    

            