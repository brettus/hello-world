package com.example;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import com.example.dao.IEventDAO;
import com.example.dao.MemoryEventDAO;
import com.example.models.Event;
import com.example.models.EventList;
import com.example.util.gson.CalendarGsonAdapter;
import com.example.util.gson.JAXBAnnotationsNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * This class launches the web application in an embedded Jetty container.
 * This is the entry point to your application. The Java command that is used for
 * launching should fire this main method.
 *
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        String webappDirLocation = "src/main/webapp/";

        // The port that we should run on can be set into an environment variable
        // Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }
    	
        // Load sample data
        IEventDAO dao = new MemoryEventDAO();
        final EventList eventList = loadTestEvents("/SampleData.json");
        insertAll(dao, eventList);
        
        // start server
        final Server server = startServer(webappDirLocation, Integer.valueOf(webPort));
        server.join();
    }

	public static Server startServer(String webappDirLocation, int webPort) throws Exception, InterruptedException {
		Server server = new Server(webPort);
        WebAppContext root = new WebAppContext();

        root.setContextPath("/");
        root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        root.setResourceBase(webappDirLocation);

        // Parent loader priority is a class loader setting that Jetty accepts.
        // By default Jetty will behave like most web containers in that it will
        // allow your application to replace non-server libraries that are part of the
        // container. Setting parent loader priority to true changes this behavior.
        // Read more here: http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
        root.setParentLoaderPriority(true);

        server.setHandler(root);

        server.start();
        return server;
	}
	
	
	// basically everything from BaseServiceTest
	
	private static final GsonBuilder gsonBuilder = new GsonBuilder()
			.setFieldNamingStrategy(new JAXBAnnotationsNamingStrategy())
			.registerTypeAdapter(Calendar.class, new CalendarGsonAdapter())
			.registerTypeAdapter(GregorianCalendar.class, new CalendarGsonAdapter());

	/**
	 * This can be overridden to extend or replace the GsonBuilder used by
	 * getGson()
	 * 
	 * @return A {@link GsonBuilder} with some default configurations applied.
	 */
	private static GsonBuilder getGsonBuilder() {
		return gsonBuilder;
	}

	/**
	 * Obtains the GSON instance used for type conversions. Uses a
	 * {@link GsonBuilder} obtained via {@link BaseServiceTest#getGsonBuilder()}
	 * 
	 * @return A {@link Gson} instance
	 */
	private static Gson getGson() {
		return getGsonBuilder().create();
	}

	/**
	 * Loads a JSON file with test data from src/test/resources/ and parses it.
	 * 
	 * @param testResourceFileName
	 *            The file path inside src/test/resources/
	 * @return The parsed test data
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static EventList loadTestEvents(final String testResourceFileName) throws URISyntaxException, IOException {
		final URI testDataURI = Main.class.getResource(testResourceFileName).toURI();
		final byte[] testDataBytes = Files.readAllBytes(Paths.get(testDataURI));
		final String testData = new String(testDataBytes, StandardCharsets.UTF_8);

		final Gson gson = getGson();
		final EventList el = gson.fromJson(testData, EventList.class);

		return el;
	}

	public static void insertAll(IEventDAO dao, final EventList el) {
		for (final Event ev : el.getEvents()) {
			dao.addEvent(ev);
		}
	}

}
