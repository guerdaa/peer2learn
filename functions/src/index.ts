import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp();

const db = admin.firestore();
// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

export const deleteEmptyGroups = functions.firestore
    .document("GROUPS/{groupId}/MEMBERS/{memberId}")
    .onDelete(async (snap, context) => {
        // Retrieve current MEMBERS collection
        let membersCollection = await db.collection("GROUPS/" + context.params.groupId + "/MEMBERS").get();
        
        if(membersCollection.size <= 0) {
            // If the members collection is emtpy, delete the whole group
            return db.doc("GROUPS/" + context.params.groupId).delete();
        }
        return null;
    });