<?php
$servername = "localhost"; // Thay localhost bằng tên máy chủ MySQL của bạn
$username = "root"; // Thay root bằng tên người dùng MySQL của bạn
$password = ""; // Thay mật khẩu bằng mật khẩu MySQL của bạn
$dbname = "mydata"; // Tên cơ sở dữ liệu

// Đặt header để trả về JSON
header('Content-Type: application/json');

// Tạo kết nối
$conn = new mysqli($servername, $username, $password, $dbname);

// Kiểm tra kết nối
if ($conn->connect_error) {
    $errorResponse = array('error' => 'Database connection failed: ' . $conn->connect_error);
    echo json_encode($errorResponse);
    exit();
}

// Đọc dữ liệu JSON từ yêu cầu POST
$json = file_get_contents('php://input');
$data = json_decode($json);

if ($data && isset($data->param1) && isset($data->param2)) {
    // Lấy giá trị của các tham số và làm sạch dữ liệu
    $param1 = $conn->real_escape_string($data->param1);
    $param2 = $conn->real_escape_string($data->param2);

    // Sử dụng prepared statement để chèn dữ liệu vào bảng dataSensor
    $stmt = $conn->prepare("INSERT INTO dataSensor (var1, var2) VALUES (?, ?)");
    $stmt->bind_param("ss", $param1, $param2);

    if ($stmt->execute()) {
        // Truy vấn SQL để lấy nội dung từ hàng đầu tiên của bảng dataSend
        $fetchSql = "SELECT * FROM dataSend LIMIT 1";
        $result = $conn->query($fetchSql);

        if ($result->num_rows > 0) {
            // Lấy dữ liệu từ hàng đầu tiên
            $row = $result->fetch_assoc();
            $response = array(
                'result' => $row['command'],
                'status' => 'success'
            );
            echo json_encode($response);
        } else {
            // Nếu không có dữ liệu trong bảng dataSend
            $errorResponse = array('error' => 'No data available in dataSend');
            echo json_encode($errorResponse);
        }
    } else {
        // Nếu có lỗi trong quá trình chèn dữ liệu
        $errorResponse = array('error' => 'Error inserting data: ' . $stmt->error);
        echo json_encode($errorResponse);
    }
    
    $stmt->close();
} else {
    // Nếu thiếu tham số hoặc yêu cầu không hợp lệ
    $errorResponse = array('error' => 'Invalid request - missing param1 or param2');
    echo json_encode($errorResponse);
}

// Đóng kết nối Cơ sở dữ liệu
$conn->close();
?>
