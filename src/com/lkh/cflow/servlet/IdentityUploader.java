package com.lkh.cflow.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.lkh.cflow.TokenAdmin;
import com.lkh.cflow.db.DbAdmin;
import com.lkh.cflow.db.DbAdminPool;

public class IdentityUploader extends HttpServlet {
	private static final long serialVersionUID = 1007L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = null;
		String vtname = null;
		FileItem theFileItem = null;
		request.setCharacterEncoding("UTF-8");
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List items = upload.parseRequest(request);// 上传文件解析
			Iterator itr = items.iterator();// 枚举方法
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				if (item.isFormField()) {// 判断是文件还是文本信息
					// System.out.println("表单参数名:" + item.getFieldName() +
					// "，表单参数值:" + item.getString("UTF-8"));
					if (item.getFieldName().equals("token"))
						token = item.getString("UTF-8");
				} else {
					theFileItem = item;
				}
			}

			DbAdmin dbadmin = DbAdminPool.get();
			dbadmin.keepConnection(true);
			try {
				String dev = TokenAdmin.getDevByToken(token);
				if (dev == null) {
					request.setAttribute("upload.message", "Session failed");
				} else {

					if (theFileItem.getName() != null && !theFileItem.getName().equals("")) {// 判断是否选择了文件
						// System.out.println("上传文件的大小:" +
						// theFileItem.getSize());
						// System.out.println("上传文件的类型:" +
						// theFileItem.getContentType());
						// item.getName()返回上传文件在客户端的完整路径名称
						// System.out.println("上传文件的名称:" +
						// theFileItem.getName());
						// 此时文件暂存在服务器的内存当中

						BufferedReader reader = null;
						try {
							reader = new BufferedReader(new InputStreamReader(theFileItem.getInputStream(), "UTF-8"));
							dbadmin.loadUser(dev, reader);

						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if (reader != null) {
								try {
									reader.close();
								} catch (IOException e1) {
								}
							}
						}
						request.setAttribute("upload.message", "Upload succesfully");// 返回上传结果
					} else {
						request.setAttribute("upload.message", "Please choose a file to upload");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("upload.message", "Failed to upload: " + e.getLocalizedMessage());
			} finally {
				DbAdminPool.ret(dbadmin);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("upload.message", "Failed to upload");
		}
		// request.getRequestDispatcher("vt.jsp?token=" +
		// token).forward(request,
		// response);
		response.sendRedirect("/cflow/identity.jsp?token=" + token);
	}
}