package no.difi.datahotel;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.ProtectionDomain;

public class JettyServer {

	private final int port;
	private final String contextPath;
	private final String workPath;
	// private final String secret;

	public static void main(String[] args) throws Exception {
		JettyServer sc = new JettyServer();

		if (args.length != 1)
			sc.start();
		else if ("stop".equals(args[0]))
			sc.stop();
		else if ("start".equals(args[0]))
			sc.start();
		else
			sc.usage();
	}

	public JettyServer() {
		try {
			String configFile = System.getProperty("config", "jetty.properties");
			System.getProperties().load(new FileInputStream(configFile));
		} catch (Exception ignored) {
		}

		port = Integer.parseInt(System.getProperty("jetty.port", "8080"));
		contextPath = System.getProperty("jetty.contextPath", "/");
		workPath = System.getProperty("jetty.workDir", null);
		// secret = System.getProperty("jetty.secret", "eb27fb2e61ed603363461b3b4e37e0a0");
	}

	private void start() {
		// Start a Jetty server with some sensible(?) defaults
		try {
			Server srv = new Server();
			srv.setStopAtShutdown(true);

			// Allow 5 seconds to complete.
			// Adjust this to fit with your own webapp needs.
			// Remove this if you wish to shut down immediately (i.e. kill <pid>
			// or Ctrl+C).
			srv.setGracefulShutdown(5000);

			// Increase thread pool
			QueuedThreadPool threadPool = new QueuedThreadPool();
			threadPool.setMaxThreads(100);
			srv.setThreadPool(threadPool);

			// Ensure using the non-blocking connector (NIO)
			Connector connector = new SelectChannelConnector();
			connector.setPort(port);
			connector.setMaxIdleTime(30000);
			srv.setConnectors(new Connector[] { connector });

			// Get the war-file
			ProtectionDomain protectionDomain = JettyServer.class.getProtectionDomain();
			String warFile = protectionDomain.getCodeSource().getLocation().toExternalForm();
			String currentDir = new File(protectionDomain.getCodeSource().getLocation().getPath()).getParent();

			// Add the warFile (this jar)
			WebAppContext context = new WebAppContext(warFile, contextPath);
			context.setServer(srv);
			resetTempDirectory(context, currentDir);

			// Add the handlers
			HandlerList handlers = new HandlerList();
			handlers.addHandler(context);
			srv.setHandler(handlers);

			srv.start();
			srv.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stop() {
		// System.out.println(ShutdownHandler.shutdown(port, secret));
		System.out.println("Not implemented.");
	}

	private void usage() {
		System.out.println("Usage: java -jar <file.jar> [start|stop]\n\t" + "start    Start the server (default)\n\t"
				+ "stop     Stop the server gracefully\n\t");
		System.exit(-1);
	}

	private void resetTempDirectory(WebAppContext context, String currentDir) throws IOException {
		File workDir;
		if (workPath != null) {
			workDir = new File(workPath);
		} else {
			workDir = new File(currentDir, "work");
		}
		FileUtils.deleteDirectory(workDir);
		context.setTempDirectory(workDir);
	}
}