var exec = require('cordova/exec');

module.exports = {
  start: function(title, text, icon, importance, notificationId, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "ForegroundPlugin", "start", [title || "", text || "", icon || "", importance || "1", notificationId || ""]);
  },
  stop: function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "ForegroundPlugin", "stop", []);
  },
  restart: function(title, text, icon, importance, notificationId, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "ForegroundPlugin", "restart", [title || "", text || "", icon || "", importance || "1", notificationId || ""]);
    }
  
};