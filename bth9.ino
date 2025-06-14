#include <WiFi.h> 
#include <HTTPClient.h> 

// Thay IP máy tính bạn đang chạy localhost ở đây 
String URL = "http://172.20.10.8/BTH9/send_data.php";

// Thay WiFi SSID và Password của bạn
const char* ssid = "Duong Tran"; 
const char* password = "11111111"; 

int temperature = 0; 
int humidity = 1;

void setup() {
  Serial.begin(115200); 
  connectWiFi();
}

void loop() {
  if (WiFi.status() != WL_CONNECTED) {
    connectWiFi();
  }

  // Tạo chuỗi dữ liệu gửi đi
  String postData = "temperature=" + String(temperature) + "&humidity=" + String(humidity); 

  HTTPClient http; 
  http.begin(URL);
  http.addHeader("Content-Type", "application/x-www-form-urlencoded");

  int httpCode = http.POST(postData); 
  String payload = http.getString(); 
  
  Serial.print("URL      : "); Serial.println(URL); 
  Serial.print("Data     : "); Serial.println(postData); 
  Serial.print("httpCode : "); Serial.println(httpCode); 
  Serial.print("payload  : "); Serial.println(payload); 
  Serial.println("--------------------------------------------------");

  delay(3000);  // Gửi mỗi 3 giây
}
 
void connectWiFi() {
  WiFi.mode(WIFI_OFF);
  delay(1000);
  WiFi.mode(WIFI_STA);
  
  WiFi.begin(ssid, password);
  Serial.println("Connecting to WiFi...");
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.print("Connected to : "); Serial.println(ssid);
  Serial.print("IP address   : "); Serial.println(WiFi.localIP());
}
