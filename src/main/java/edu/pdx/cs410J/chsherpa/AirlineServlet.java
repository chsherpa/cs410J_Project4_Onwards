package edu.pdx.cs410J.chsherpa;

//Backend Server Servlet
import com.google.common.annotations.VisibleForTesting;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This servlet ultimately provides a REST API for working with an
 * <code>Airline</code>.  However, in its current state, it is an example
 * of how to use HTTP and Java servlets to store simple key/value pairs.
 */
public class AirlineServlet extends HttpServlet {
  private static final String SUCCESSFULLY_ADDED_A_FLIGHT = "Successfully added a flight";
  private static final String SUCCESSFULLY_SEARCHED_FLIGHTS= "Successfully searched the flights";
  private Airline airline;

  /**
   * Handles an HTTP GET request from a client by writing the value of the key
   * specified in the "key" HTTP parameter to the HTTP response.  If the "key"
   * parameter is not specified, all of the key/value pairs are written to the
   * HTTP response.
   */
  @Override
  protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
  {
    response.setContentType( "text/plain" );

    String airlineName = getParameter( "name", request );
    if (airlineName == null) {
        missingRequiredParameter(response, "name");
        return;
    }

    String source = getParameter( "src", request );
    String destination = getParameter( "dest", request );

    if( airlineName != null && source == null && destination == null )
    {
      doDisplayAll(airlineName, response );
      response.setStatus( HttpServletResponse.SC_OK);
      return;
    }

    if ( source == null) {
        missingRequiredParameter( response, "src" );
        return;
    }

    if ( destination == null) {
        missingRequiredParameter( response, "dest" );
        return;
    }

    doSearch(airlineName, source, destination, response );
  }

  /**
   * doSearch
   * @param airlineName name of the flight
   * @param source source of the flight
   * @param destination destination of the flight
   * @param response response of the http request
   * @throws IOException standard catch
   */
  private void doSearch( String airlineName, String source, String destination, HttpServletResponse response ) throws IOException
  {
    /*
    if (!createOrValidateAirlineWithName(airlineName)) {
      nonMatchingAirlineName(airlineName, response);
      return;
    }
    */

    if ( this.airline != null )
    {
      String pretty = prettyPrintFlightsBetween(source, destination);
      response.getWriter().println(pretty);
      response.getWriter().println(SUCCESSFULLY_SEARCHED_FLIGHTS);
      response.setStatus( HttpServletResponse.SC_OK );
    }
    else
    {
      String message = new String( "No flights between " + source + " and " + destination );
      response.getWriter().println(message);
      response.getWriter().println(SUCCESSFULLY_SEARCHED_FLIGHTS);
      response.setStatus( HttpServletResponse.SC_OK );
    }
  }

  private String prettyPrintFlightsBetween(String source, String destination) {
    StringBuilder sb = new StringBuilder();
    sb.append("Flights between ").append(source).append(" and ").append(destination).append(":\n");
    this.airline.getFlights().stream()
      .filter(f -> f.getSource().equals(source) && f.getDestination().equals(destination) )
      .forEach(f->sb.append("\n\nFlight Name: "+f.getFlightName() ).append("\nSource: " + f.getSource() ).append("\nDestination: " + f.getDestination() ) );

    return sb.toString();
  }

  /**
   * Display all flights in pretty format
   * @param airlineName name of the airline
   * @param response return code of the http request
   * @throws IOException standard IO catch
   */
  private void doDisplayAll( String airlineName, HttpServletResponse response ) throws IOException
  {
    if (!createOrValidateAirlineWithName(airlineName)) {
      nonMatchingAirlineName(airlineName, response);
      return;
    }

    String pretty = prettyPrintFlights(airlineName);
    response.getWriter().println(pretty);
    response.setStatus( HttpServletResponse.SC_OK);
  }

  /**
   * Pretty Prints Flight Info
   * @param airlineName name of the airline
   * @return Return a string to be printed
   */
  private String prettyPrintFlights( String airlineName )
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Flights for ").append(airlineName).append(":\n");
    this.airline.getFlights().stream()
      .forEach(f->sb.append("\nFlight Name: "+f.getFlightName()+"\n").append("\nNumber: " + f.getNumber() + "\n").append("\nSource: " + f.getSource() + "\n").append("\nDeparture: " + f.getDepartureString() + "\n").append("\nDestination: " + f.getDestination() + "\n").append("\nArrival: " + f.getArrivalString() + "\n") );
    return sb.toString();
  }

  /**
   * Handles an HTTP POST request by storing the key/value pair specified by the
   * "key" and "value" request parameters.  It writes the key/value pair to the
   * HTTP response.
   */
  @Override
  protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
  {
      response.setContentType( "text/plain" );

      String airlineName = getParameter( "name", request );
      if (airlineName == null) {
        missingRequiredParameter(response, "name");
        return;
      }

      String numberAsString = getParameter( "number", request );
      if ( numberAsString == null) {
          missingRequiredParameter( response, "number" );
          return;
      }

      String source = getParameter( "src", request );
      if ( source == null) {
        missingRequiredParameter( response, "src" );
        return;
      }

      String departure = getParameter("departure", request );
      if ( departure == null ){
        missingRequiredParameter( response, "departure");
        return;
      }

      String destination = getParameter( "dest", request );
      if ( destination == null) {
        missingRequiredParameter( response, "dest" );
        return;
      }

      String arrival = getParameter( "arrival", request );
      if( arrival == null ){
        missingRequiredParameter( response, "arrival");
        return;
      }

      int number;
      try {
        number = Integer.parseInt(numberAsString);

      } catch (NumberFormatException ex) {
        invalidFlightNumber(numberAsString, response);
        return;
      }

      if (!createOrValidateAirlineWithName(airlineName)) {
        nonMatchingAirlineName(airlineName, response);
        return;
      }

      Flight flight = new Flight( airlineName, number, source , departure, destination, arrival );
      this.airline.addFlight(flight);
      response.getWriter().println(SUCCESSFULLY_ADDED_A_FLIGHT);
      response.setStatus( HttpServletResponse.SC_OK);
  }

  private void nonMatchingAirlineName(String airlineName, HttpServletResponse response) throws IOException {
    String message = "Airline not named " + airlineName;
    response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, message);
  }

  private boolean createOrValidateAirlineWithName(String airlineName) {
    if ( this.airline != null )
    {
      return this.airline.getName().equals(airlineName);
    }
    else
    {
      this.airline = new Airline(airlineName);
      return true;
    }
  }

  private void invalidFlightNumber(String numberAsString, HttpServletResponse response) throws IOException {
    String message = "Invalid flight number" + numberAsString;
    response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED, message);
  }

  /**
   * Handles an HTTP DELETE request by removing all key/value pairs.  This
   * behavior is exposed for testing purposes only.  It's probably not
   * something that you'd want a real application to expose.
   */
  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      response.setContentType("text/plain");

      this.airline = null;

      response.setStatus(HttpServletResponse.SC_OK);

  }

  /**
   * Writes an error message about a missing parameter to the HTTP response.
   *
   * The text of the error message is created by {@link Messages#missingRequiredParameter(String)}
   */
  private void missingRequiredParameter( HttpServletResponse response, String parameterName )
      throws IOException
  {
      String message = Messages.missingRequiredParameter(parameterName);
      response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, message);
  }

  /**
   * Returns the value of the HTTP request parameter with the given name.
   *
   * @return <code>null</code> if the value of the parameter is
   *         <code>null</code> or is the empty string
   */
  private String getParameter(String name, HttpServletRequest request) {
    String value = request.getParameter(name);
    if (value == null || "".equals(value)) {
      return null;

    } else {
      return value;
    }
  }

  @VisibleForTesting
  Airline getAirline() {
    return airline;
  }
}
