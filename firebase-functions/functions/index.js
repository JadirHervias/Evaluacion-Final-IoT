/* eslint-disable promise/always-return */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);
const TEMPERATURE_LIMIT_HIGH = 23;
const TEMPERATURE_LIMIT_LOW = 18;
const HUMIDITY_LIMIT_LOW = 40
const HUMIDITY_LIMIT_HIGH = 55

exports.sendTemperatureNotification = functions.database
  .ref("/temp")
  .onWrite((snapshot) => {
    // Obtenemos el post recientemente creado en la Realtime Database
    const temperature = snapshot.after.val();

    // Enviar la notificacion a los dispositivos suscritos al topico ALL
    const topic = "ALL"; // (ver MyFirebaseInstanceIDService.java)
    let payload;
    let options;

    if (temperature > TEMPERATURE_LIMIT_HIGH) {
      console.log("temp:", temperature);

      payload = {
        notification: {
          title: "¡La temperatura ha excedido los limites!",
          icon: "ic_picture",
          sound: "default",
          // clickAction: '.activities.MainActivity',
          // badge: '1'
        },
        data: {
          extra: "extra_data",
        },
      };
      // Configura la prioridad, tiempo de vida y criterio de agrupamiento
      options = {
        priority: "high",
        timeToLive: 60 * 60 * 24,
        collapseKey: "temp",
        contentAvailable: true,
      };

      return admin
        .messaging()
        .sendToTopic(topic, payload, options)
        .then((response) => {
          console.log("Successfully sent message:", response);
        });
    }

    if (temperature < TEMPERATURE_LIMIT_LOW) {
      console.log("temp:", temperature);

      payload = {
        notification: {
          title: "¡La temperatura esta por debajo de los limites!",
          icon: "ic_picture",
          sound: "default",
          // clickAction: '.activities.MainActivity',
          // badge: '1'
        },
        data: {
          extra: "extra_data",
        },
      };
      // Configura la prioridad, tiempo de vida y criterio de agrupamiento
      options = {
        priority: "high",
        timeToLive: 60 * 60 * 24,
        collapseKey: "temp",
        contentAvailable: true,
      };

      return admin
        .messaging()
        .sendToTopic(topic, payload, options)
        .then((response) => {
          console.log("Successfully sent message:", response);
        });
    }

    return () => {};
  });

exports.sendHumidityNotification = functions.database
  .ref("/hum")
  .onWrite((snapshot) => {
    // Obtenemos el post recientemente creado en la Realtime Database
    const humidity = snapshot.after.val();

    // Enviar la notificacion a los dispositivos suscritos al topico ALL
    const topic = "ALL"; // (ver MyFirebaseInstanceIDService.java)
    let payload;
    let options;

    if (humidity > HUMIDITY_LIMIT_HIGH) {
      console.log("hum:", humidity);

      payload = {
        notification: {
          title: "¡La humedad ha excedido los limites!",
          icon: "ic_picture",
          sound: "default",
          // clickAction: '.activities.MainActivity',
          // badge: '1'
        },
        data: {
          extra: "extra_data",
        },
      };
      // Configura la prioridad, tiempo de vida y criterio de agrupamiento
      options = {
        priority: "high",
        timeToLive: 60 * 60 * 24,
        collapseKey: "hum",
        contentAvailable: true,
      };

      return admin
        .messaging()
        .sendToTopic(topic, payload, options)
        .then((response) => {
          console.log("Successfully sent message:", response);
        });
    }

    if (humidity < HUMIDITY_LIMIT_LOW) {
      console.log("hum:", humidity);

      payload = {
        notification: {
          title: "¡La humedad esta por debajo de los limites!",
          icon: "ic_picture",
          sound: "default",
          // clickAction: '.activities.MainActivity',
          // badge: '1'
        },
        data: {
          extra: "extra_data",
        },
      };
      // Configura la prioridad, tiempo de vida y criterio de agrupamiento
      options = {
        priority: "high",
        timeToLive: 60 * 60 * 24,
        collapseKey: "hum",
        contentAvailable: true,
      };

      return admin
        .messaging()
        .sendToTopic(topic, payload, options)
        .then((response) => {
          console.log("Successfully sent message:", response);
        });
    }

    return () => {};
  });
