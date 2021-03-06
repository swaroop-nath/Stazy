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

        return Promise.all([performer_data]).then(result => {
            const token_performer = result[0].get("token");

            const payload = {
                data: {
                    title: notification_title,
                    body: notification_body,
                    sender: sender_id,
                    intent: "SHORTLIST"
                }
            }
            admin.messaging().sendToDevice(token_performer, payload).then(result => {
                console.log("Notification Sent.");
                return 0;
            }).catch(error => {
                console.log("Error Sending Message. " + error);
            });
            return 0;
        });
    });
});

exports.respondNotification = functions.firestore.document("NotificationsHotel/{userId}/To/{toId}").onWrite((change, context) => {
    const sender_id = context.params.userId;
    const to_id = context.params.toId;
    console.log(sender_id);
    console.log(to_id);

    return admin.firestore().collection("NotificationsHotel").doc(sender_id).collection("To").doc(to_id).get().then(documentSnapshot => {
        const city = documentSnapshot.get("city");
        const notification_title = documentSnapshot.get("notification_title");
        const notification_body = documentSnapshot.get("notification_body");
        const type = documentSnapshot.get("type");
        const genre = documentSnapshot.get("genre");

        const hotel_data = admin.firestore().collection("Cities").doc(city).collection("hotels").doc(to_id).get();

        return Promise.all([hotel_data]).then(result => {
            const token_hotel = result[0].get("token");

            const payload = {
                data : {
                    title: notification_title,
                    body: notification_body,
                    sender: sender_id,
                    intent: "RESPONSE",
                    type: type,
                    genre: genre
                }
            }
            admin.messaging().sendToDevice(token_hotel, payload).then(result => {
                console.log("Notification Sent.");
                return 0;
            }).catch(error => {
                console.log("Error Sending Message. " + error);
            });
            return 0;
        });
    });
});

exports.hireNotification = functions.firestore.document("Cities/{city}/PreviousHotels/{performerUserId}/List/{hotelUserId}").onWrite((change, context) => {
    const performer_id = context.params.performerUserId;
    const hotel_id = context.params.hotelUserId;
    const city = context.params.city;

    console.log(performer_id);
    console.log(hotel_id);

    return admin.firestore().collection("Cities").doc(city).collection("PreviousHotels").doc(performer_id).collection("List").doc(hotel_id).get().then(documentSnapshot => {
        const token = documentSnapshot.get("token");
        const notification_title = "Congratulations";
        const notification_body = "You have been HIRED!!";

        const payload = {
            data : {
                title: notification_title,
                body: notification_body,
                intent: "HIRE"
            }
        }

        admin.messaging().sendToDevice(token, payload).then(result => {
            console.log("Notifciation Sent hire");
            return 0;
        }).catch(error => {
            console("Error sending hire notif:" + error);
        });
        return 0;
    });
});