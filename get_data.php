<?php
header('Content-Type: application/json');

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "mydata";

// Kết nối database
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    echo json_encode(["error" => "Kết nối thất bại: " . $conn->connect_error]);
    exit();
}

// Nếu là POST: ghi dữ liệu var1, var2 xuống
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST["var1"]) && isset($_POST["var2"])) {
    $var1 = $conn->real_escape_string($_POST["var1"]);
    $var2 = $conn->real_escape_string($_POST["var2"]);

    $sql = "INSERT INTO datasensor (var1, var2) VALUES ('$var1', '$var2')";
    if ($conn->query($sql) === TRUE) {
        echo json_encode(["status" => "success", "message" => "Đã ghi dữ liệu"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Lỗi khi ghi: " . $conn->error]);
    }
}
// Nếu là GET: trả về dòng cuối cùng (dữ liệu mới nhất)
else {
    $sql = "SELECT var1, var2 FROM datasensor";
    $result = $conn->query($sql);

    if ($result && $result->num_rows > 0) {
        // Di chuyển con trỏ đến dòng cuối cùng
        $result->data_seek($result->num_rows - 1);
        $row = $result->fetch_assoc();

        echo json_encode([
            "status" => "success",
            "var1" => $row["var1"],
            "var2" => $row["var2"]
        ]);
    } else {
        echo json_encode(["status" => "error", "message" => "Không có dữ liệu"]);
    }
}

$conn->close();
?>
