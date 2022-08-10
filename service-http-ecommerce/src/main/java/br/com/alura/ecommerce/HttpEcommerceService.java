package br.com.alura.ecommerce;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HttpEcommerceService {
	
	public static void main(String[] args) throws Exception {
		var server = new Server(8080);
		
		var context = new ServletContextHandler();
		context.setContextPath("/");
		context.addServlet(new ServletHolder(new NewOrderServlet()), "/new");
		
		server.setHandler(context); //avisa pro servidor para quando tiver uma requisicao, chamar este context
		
		server.start();
		server.join(); //espera o servidor acabar para assim acabar com a aplicacao
	}

}
