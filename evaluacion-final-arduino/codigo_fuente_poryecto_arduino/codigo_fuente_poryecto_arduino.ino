
/*******
 * EJERCICIO ARDUINO FIIREBASE
 */

#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

// Set these to run example.
#define FIREBASE_HOST "iottecsupjh.firebaseio.com"
#define FIREBASE_AUTH "kZc8dKxPu2o3ZfDX0hxnE0QxXdukT0HkPuZLQ0Mk"
#define WIFI_SSID "MOVISTAR_D920"
#define WIFI_PASSWORD "Egsa2QpsYgdxjmL4j62j"


// LIbrerias para el sensor de temperatura y humedad
#include "DHT.h"
#define DHTTYPE DHT11
//declaramos el pin digital del sensor
int pinSensor = D7;

//inicializamos el sensor DHT11
DHT dht(pinSensor, DHTTYPE);


//Declarando los pines de los led
int pinLed1 = D0;
int pinLed2 = D1;
int pinLed3 = D2;

//Declaramos los pines de los led de estado de temperatura
int pinTempIdeal = D5;
int pinTempPeligro = D6;

//Declaramos los pines de los led de estado de temperatura
int pinHumIdeal = D3;
int pinHumPeligro = D4;

void setup() {
  //DECLARANDO EL MODO DE LOS PINES EN EL SETUP
  pinMode(pinLed1, OUTPUT);
  pinMode(pinLed2, OUTPUT);
  pinMode(pinLed3, OUTPUT);

  pinMode(pinTempIdeal, OUTPUT);
  pinMode(pinHumIdeal, OUTPUT);

  pinMode(pinTempPeligro, OUTPUT);
  pinMode(pinHumPeligro, OUTPUT);
  
  Serial.begin(9600);

   //comenzamos el sensor DHT
  dht.begin();

   
  // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());
  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
}

void loop() {
  
  //Lectura  simple
  String led1 = Firebase.getString("led/led1");
  String led2 = Firebase.getString("led/led2");
  String led3 = Firebase.getString("led/led3");
  Serial.println(led1);
  Serial.println(led2);
  Serial.println(led3);

 if(led1.equals("1")){
    Serial.println("LED 1 ENCENDIDO");
    digitalWrite(pinLed1, HIGH);
  }else{
    Serial.println("LED 1 APAGADO");
    digitalWrite(pinLed1, LOW);
  }
  if(led2.equals("1")){
    Serial.println("LED 2 ENCENDIDO");
    digitalWrite(pinLed2, HIGH);
  }else{
    Serial.println("LED 2 APAGADO");
    digitalWrite(pinLed2, LOW);
  }
  if(led3.equals("1")){ 
    Serial.println("LED 3 ENCENDIDO");
    digitalWrite(pinLed3, HIGH);
  }else{
    Serial.println("LED 3 APAGADO");
    digitalWrite(pinLed3, LOW);
  }
    
  // Esribir en modo simple
  float temp = readTemperatura();

  if (temp < 18 || temp > 23){
    digitalWrite(pinTempIdeal, LOW);
    digitalWrite(pinTempPeligro, HIGH);
  }else {
    digitalWrite(pinTempPeligro, LOW);
    digitalWrite(pinTempIdeal, HIGH);
  }

  if (isnan(temp)){
      Serial.println("Error obteniendo los datos del sensor de temperatura");
      return;
    }
    
  Serial.print("Temperatura: ");
  Serial.println(temp);
  Firebase.setFloat("temp", temp);
  // handle error
  if (Firebase.failed()) {
      Serial.print("Error de actualización:");
      Serial.println(Firebase.error());  
      return;
  }
  float hum = readHumedad();
  
  if (hum < 40 || hum > 55){
    digitalWrite(pinHumIdeal, LOW);
    digitalWrite(pinHumPeligro, HIGH);
  }else {
    digitalWrite(pinHumPeligro, LOW);
    digitalWrite(pinHumIdeal, HIGH);
  }
     
  if (isnan(hum)){
      Serial.println("Error obteniendo los datos del sensor de humedad");
      return;
    }
    
  Serial.print("Humedad: ");
  Serial.println(hum);
  Firebase.setFloat("hum", hum);
  // handle error
  if (Firebase.failed()) {
      Serial.print("Error de actualización:");
      Serial.println(Firebase.error());  
      return;
  }
  delay(5000);
}
float readTemperatura()
{
    float t = dht.readTemperature();
    return t;
    
}

float readHumedad()
{
    float h = dht.readHumidity();
    return h;
}
