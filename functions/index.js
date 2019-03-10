'use-script'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.firestore.document("NotificationsPerformer/{userId}/To/{toId}").onWrite((change, context) => {
    const sender_id = context.params.userId;
    const to_id = context.params.toId;
    console.log(sender_id);
    console.log(to_id);
    return admin.firestore().collection("NotificationsPerformer").doc(sender_id).collection("To").doc(to_id).get().then(documentSnapshot => {

        const city = documentSnapshot.get("city");
        const notification_title = documentSnapshot.get("notification_title");
        const notification_body = documentSnapshot.get("notification_body");
        const type = documentSnapshot.get("type");
        const genre = documentSnapshot.get("genre");

        console.log(city);

        const performer_data = admin.firestore().collection("Cities").doc(city).collection("type").doc(type).collection(genre).doc(to_id).get();
        const hotel_data = admin.firestore().collection("Cities").doc(city).collection("hotels").doc(sender_id).get();

        return Promise.all([performer_data, hotel_data]).then( result =>{
            const token_performer = result[0].get("token");
            const token_hotel = result[1].get("token");

            const payload = {
                notification: {
                    title: notification_title,
                    body: notification_body
                  },
                data: {
                    sender: token_hotel,
		    intent: "HIRE"
                }
            }

            admin.messaging().sendToDevice(token_performer, payload).then(result => {
                console.log("Notification Sent.");
                
                return 0;
            }).catch(error => {
                console.log("Error Sending Message.");
            });
            return 0;
        });
    });
});