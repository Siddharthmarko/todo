package todo.web;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.naming.Context;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import todo.dao.*;
import todo.model.*;


@WebServlet("/")
public class TodoController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TodoDao todoDAO;

	public void init() {
		todoDAO = new TodoDaoImpl();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getServletPath();
		System.out.println("action ke baad hua kya" + request.getAttribute("currUser"));
		try {
			switch (action) {
				case "/new":
					showNewForm(request, response);
					break;
				case "/insert":
					insertTodo(request, response);
					break;
				case "/delete":
					deleteTodo(request, response);
					break;
				case "/edit":
					showEditForm(request, response);
					break;
				case "/update":
					updateTodo(request, response);
					break;
				case "/list":
					listTodo(request, response);
					break;
				default:
					RequestDispatcher dispatcher = request.getRequestDispatcher("login/login.jsp");
					dispatcher.forward(request, response);
					break;
			}
		} catch (SQLException ex) {
			throw new ServletException(ex);
		}
	}

	private void listTodo(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, IOException, ServletException {
		List<Todo> listTodo = todoDAO.selectAllTodos();
		System.out.println("listTodo method");
		request.setAttribute("listTodo", listTodo);
		System.out.println("print todo");
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("todo/todo-list.jsp");
		dispatcher.forward(request, response);
	}

	private void showNewForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("showNewForm");
		RequestDispatcher dispatcher = request.getRequestDispatcher("todo/todo-form.jsp");
		System.out.println("sending");
		dispatcher.forward(request, response);
		System.out.println("sent");
	}

	private void showEditForm(HttpServletRequest request, HttpServletResponse response)
			throws SQLException, ServletException, IOException {
		System.out.println("showEditForm");
		int id = Integer.parseInt(request.getParameter("id"));
		Todo existingTodo = todoDAO.selectTodo(id);
		System.out.println(id);
		RequestDispatcher dispatcher = request.getRequestDispatcher("todo/todo-form.jsp");
		System.out.println(existingTodo.getId());
		System.out.println(existingTodo.getTitle());
		System.out.println(existingTodo.getUsername());
		// System.out.println(existingTodo.isDone());

		request.setAttribute("todo", existingTodo);
		dispatcher.forward(request, response);

	}

	private void insertTodo(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		System.out.println("insertTodo ooraha");

		String title = request.getParameter("title");
		String user = LoginController.usekaro;
		String username = user;
		// String username = user;
		String description = request.getParameter("description");
		System.out.println(user);

		boolean isDone = Boolean.valueOf(request.getParameter("isDone"));
		Todo newTodo = new Todo(title, username, description, LocalDate.now(), isDone);
		todoDAO.insertTodo(newTodo);
		response.sendRedirect("list");
	}

	private void updateTodo(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		System.out.println("updateTodo");
		int id = Integer.parseInt(request.getParameter("id"));

		String title = request.getParameter("title");
		String username = request.getParameter("username");
		String description = request.getParameter("description");
		// DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-mm-dd");
		LocalDate targetDate = LocalDate.parse(request.getParameter("targetDate"));

		boolean isDone = Boolean.valueOf(request.getParameter("isDone"));
		Todo updateTodo = new Todo(id, title, username, description, targetDate, isDone);

		todoDAO.updateTodo(updateTodo);

		response.sendRedirect("list");
	}

	private void deleteTodo(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		System.out.println("deleteTodo");
		int id = Integer.parseInt(request.getParameter("id"));
		todoDAO.deleteTodo(id);
		response.sendRedirect("list");
	}

}
