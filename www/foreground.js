var exec = require('cordova/exec');

module.exports = {
  start: function(title, text, icon, importance, notificationId) {
    exec(null, null, "ForegroundPlugin", "start", [title || "", text || "", icon || "", importance || "1", notificationId || ""]);
  },
  stop: function() {
    exec(null, null, "ForegroundPlugin", "stop", []);
  },
  restart: function(title, text, icon, importance, notificationId) {
    exec(null, null, "ForegroundPlugin", "restart", [title || "", text || "", icon || "", importance || "1", notificationId || ""]);
    }
  
};