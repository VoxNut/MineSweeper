import "https://www.gstatic.com/firebasejs/9.22.2/firebase-app-compat.js";
import "https://www.gstatic.com/firebasejs/9.22.2/firebase-auth-compat.js";

const firebase = window.firebase;

const firebaseConfig = {
  apiKey: "AIzaSyBvPbJhF7gkFRVoaVIye9prHY5_wo0Uyok",
  authDomain: "minesweeper-110d8.firebaseapp.com",
  projectId: "minesweeper-110d8",
  appId: "1:753083219902:web:32780cd416c1c77fb643ef",
};

if (!firebase.apps.length) {
    console.log("Initializing Firebase with config:", firebaseConfig);
    firebase.initializeApp(firebaseConfig);
    console.log("Firebase initialized");
} else {
    console.log("Firebase already initialized");
}

console.log("firebase.auth available:", typeof firebase.auth);
export { firebase };
