//establish Firebase connection with sdkCredentials.json
const admin = require("firebase-admin");
const serviceAccount = require(__dirname + "/../credentials/firebaseSdkCredentials.json");

admin.initializeApp({
	  credential: admin.credential.cert(serviceAccount),
	  databaseURL: "https://unique-result-161212.firebaseio.com/"
});

module.exports = admin;