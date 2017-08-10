package edu.pdx.cs410J.chsherpa;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs410J.web.HttpRequestHelper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * A helper class for accessing the rest client.  Note that this class provides
 * an example of how to make gets and posts to a URL.  You'll need to change it
 * to do something other than just send key/value pairs.
 */
public class AirlineRestClient extends HttpRequestHelper
{
    private static final String WEB_APP = "airline";
    private static final String SERVLET = "flights";

    private final String url;

    /**
     * Creates a client to the airline REST service running on the given host and port
     * @param hostName The name of the host
     * @param port The port
     */
    public AirlineRestClient( String hostName, int port )
    {
        this.url = String.format( "http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET );
    }

    /**
     * Returns the value for the given key
     */
    public String getFlightsBetween(String airlineName, String source, String destination) throws IOException {
      Response response = get(this.url, "name", airlineName, "src", source, "dest", destination);
      throwExceptionIfNotOkayHttpStatus(response);
      return response.getContent();
    }

    public String displayAll(String airlineName, String source, String destination) throws IOException
    {
      //Response response = get(this.url, "name", airlineName, "src", source, "dest", destination);
      Response response = get(this.url, "name", airlineName, "src", null, "dest", null );
      throwExceptionIfNotOkayHttpStatus(response);
      return response.getContent();
    }

    public void addFlight(String airlineName, Flight flight) throws IOException {
      String[] flightInfo = null;
      //restParse( flight , flightInfo);

      String flightNumber = String.valueOf(flight.getNumber());
      String source = flight.getSource();
      String departure = flight.getDepartureString();
      String dest = flight.getDestination();
      String arrival = flight.getArrivalString();

      Response response =
        postToMyURL(
          "name", airlineName,
          "number", flightNumber,
          "src", source,
          "departure", departure,
          "dest", dest,
          "arrival", arrival
        );
      throwExceptionIfNotOkayHttpStatus(response);
    }

    /*
  private void restParse(Flight flight, String[] flightInfo)
  {
    flightInfo = flight.toString().split(" ");
    for( String n: flightInfo )
      System.out.printf("\n%s", n );
  }
  */

  @VisibleForTesting
    Response postToMyURL(String... keysAndValues) throws IOException {
      return post(this.url, keysAndValues);
    }

    public void removeAirline() throws IOException {
      Response response = delete(this.url);
      throwExceptionIfNotOkayHttpStatus(response);
    }

    private Response throwExceptionIfNotOkayHttpStatus(Response response) {
      int code = response.getCode();
      String message = response.getContent();
      if (code != HTTP_OK) {
        throw new AppointmentBookRestException(code, message);
      }
      return response;
    }

    private class AppointmentBookRestException extends RuntimeException {
      public AppointmentBookRestException(int httpStatusCode, String message) {
        super("Got an HTTP Status Code of " + httpStatusCode + ": " + message);
      }
    }
}
