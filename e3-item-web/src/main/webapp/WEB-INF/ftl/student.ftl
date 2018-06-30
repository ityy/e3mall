<html>
<head>
    <title>student</title>
</head>
<body>
学生信息：<br>
    学号：${student.id}&nbsp;&nbsp;&nbsp;&nbsp;
    姓名：${student.name}&nbsp;&nbsp;&nbsp;&nbsp;
    年龄：${student.age}&nbsp;&nbsp;&nbsp;&nbsp;
    家庭住址：${student.addr}&nbsp;&nbsp;&nbsp;&nbsp;
<br>
当前日期：${date?string('dd.MM.yyyy HH:mm:ss')}
</body>
</html>