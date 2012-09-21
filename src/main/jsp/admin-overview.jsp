<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Admin overview</title>
</head>
<body>
	<a href="stats">Repository Stats</a>
	<br/>
	<form action="clear-index" method="post">
		<input type="submit" value="clear index"/>
	</form>
	<br/>
	<form action="clear-data" method="post">
		<input type="submit" value="clear data"/>
	</form>
</body>
</html>