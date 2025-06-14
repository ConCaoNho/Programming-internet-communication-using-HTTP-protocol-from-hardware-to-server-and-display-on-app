<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "mydata";

// Nhận dữ liệu từ ESP32
$temperature = $_POST['temperature'] ?? '';
$humidity = $_POST['humidity'] ?? '';

// Kết nối CSDL
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Ghi dữ liệu vào bảng datasensor (cột var1 và var2)
$sql = "INSERT INTO datasensor (var1, var2) VALUES ('$temperature', '$humidity')";

if ($conn->query($sql) === TRUE) {
    echo "Insert OK - Temp: $temperature, Humi: $humidity";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>
