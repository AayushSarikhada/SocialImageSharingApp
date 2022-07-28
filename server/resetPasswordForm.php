<?php
require "DataBase.php";
$db = new DataBase();
// if(isset($_GET["key"])){
    $email = $_GET['email'];
// }
$sql = "SELECT * FROM userdata WHERE email = '$email'";
$query = mysqli_query($db->dbConnect(),$sql);
// if(mysqli_num_rows($query) === 1){
    if(isset($_POST['submit'])){
        $password = $_POST['password'];
        $cpassword = $_POST['cpassword'];
        if ($password == "" && $cpassword == "") {
            echo "some passwords are empty";
        }else{
            if($password == $cpassword){
                $update = "UPDATE userdata SET password= '$password' WHERE email = '$email'";
                if(mysqli_query($db->dbConnect(),$update)){
                    echo "Password has been changed successfull!! Please login..";
                }else{
                    echo "Some error occured, reclick the link..";
                }
            }else{
                echo "password are not match";
            }
        }
    }
    else{
        echo "Click here submit and change your password";
    }
// }else
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <form action="resetPasswordForm.php" method="POST">
        Enter new Password: <input type="text" name="password"><br>
        Enter Confirm Password: <input type="text" name="cpassword"><br>
        <input type="submit" name="submit">
    </form>
</body>
</html>