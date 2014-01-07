<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%
private void testDbAdminPool() {
	for (int i = 0; i < 100; i++) {
		new Thread() {
			public void run() {
				ObjectPool<DbAdmin> dbAdminPool = DbAdminPool.pool();
				DbAdmin dbadmin = null;
				try {
					dbadmin = dbAdminPool.getObj();
					System.out.println(dbadmin.getCreateTimestamp() + "\t" + Thread.currentThread().getId());
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					dbAdminPool.returnObj(dbadmin);
				}
			}

		}.start();
	}
}

%>
</body>
</html>