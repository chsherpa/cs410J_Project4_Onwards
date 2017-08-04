package edu.pdx.cs410J.chsherpa;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The main class that parses the command line and communicates with the
 * Airline server using REST.
 */
public class Project4 {

    public static final String MISSING_ARGS = "Missing command line arguments";

    public static void main(String[] args)
    {
      List<String> flagOptionsList = new ArrayList<String>();
      List<String> flightInfo = new ArrayList<String>();
      List<String> hostInfo = new ArrayList<String>();
      AirlineRestClient client = null;

      airlineArgsParser( flagOptionsList, flightInfo, hostInfo, args );
      flightInfoCheck( flightInfo );
      client = connectToHost( hostInfo );

      for( String temp: flagOptionsList )
      {
        if( temp.toLowerCase().equals("search"))
        {
          searchFlight( client, flightInfo );
        }
        else if ( temp.toLowerCase().equals("print"))
        {

        }
        else if( temp.toLowerCase().equals("readme"))
        {
          README();
          System.exit(1);
        }
      }

      Flight f1 = new Flight( flightInfo );
      try
      {
        client.addFlight( f1.getFlightName(), f1 );
      }
      catch (IOException e)
      {
        error( "\nClient could not add flight: " + e.getMessage() );
      }
      System.exit(0);
    }


    /**
    * Flight Info Checks
    * @param flightInfo
    */
    public static void flightInfoCheck(List<String> flightInfo ){
      int argsSize = 6;
      try
      {
        if( flightInfo.size() > argsSize )
        {
          System.out.printf("\nFlight info should only have %d arguments.\n", argsSize );
          error("System passed in the following arguments: " + flightInfo.toString() );
        }
        if( flightInfo.size() < argsSize )
        {
          System.out.printf("\nFlight info needs %d arguments\n", argsSize );
          error("System passed in the following arguments: " + flightInfo.toString() );
        }
      }
      catch( IllegalArgumentException ex)
      {
        error( "System passed in the following arguments: " + ex.getMessage() +" " +flightInfo.toString() );
      }

      //Set Proper Name
      flightInfo.set(0, Proper(flightInfo.get(0)) );
      //Check if FLIGHT NUMBER is positive numeric
      // Source: Stackoverflow
      if (flightInfo.get(1).matches("\\d+(\\.d\\d+)?") == false) {
        error("Flight number is not a numeric value");
      }

      // Check for Source being three letters long
      flightInfo.set(2, SrcDestLengthCheckAndNotNumeric(flightInfo.get(2)));
      // Check for Dest being three letters long
      flightInfo.set(4, SrcDestLengthCheckAndNotNumeric(flightInfo.get(4)));
      //Date Check for Departure
      flightInfo.set(3, dateCheck(flightInfo.get(3)) );
      //Date Check for Arrival
      flightInfo.set(5, dateCheck(flightInfo.get(5)) );
    }

    /**
    * Check if datetime stamp is in MM/dd/yyyy hh:ss format
    * Source: Stackoverflow
    * @param inputDate String to be checked; hopefully containing the desired format
    * @return String value
    */
    public static String dateCheck( String inputDate ){
      Date date = new Date();
      String s = null;
      try{
        DateFormat dateInput = new SimpleDateFormat("mm/dd/yyyy hh:mm a");
        date = dateInput.parse(inputDate);
        if( inputDate.equals(date) ){
          date = null;
        }
        s = date.toString();
      }
      catch (ParseException ex) {
        error( "\nSystem passed in the following arguments: " + ex.getMessage() );
      }

      if( date == null ){
        error("Date argument not valid");
      }
      return s;
  }

    /**
    * Proper String Case Everything
    * @param proper
    * @return String First Letter is UpperCased, rest are left as is
    */
    private static String Proper( String proper )
    {
      StringBuilder titled = new StringBuilder();
      boolean nextTitledCase = true;

      for( char c: proper.toCharArray() )
      {
        if( Character.isSpaceChar(c) )
        {
          nextTitledCase = true;
        }
        else if( nextTitledCase )
        {
          c = Character.toTitleCase(c);
          nextTitledCase = false;
        }
        titled.append(c);
      }
      return titled.toString();
    }

    /**
    * Source and Destination Check for Length and not being numeric
    * @param places  SRC/DEST input
    * @return  String value of SRC/DEST input in uppercase
    */
    public static String SrcDestLengthCheckAndNotNumeric( String places ){
      if( places.length() != 3 ) {
        System.out.println( places );
        System.out.println( "Length:" + places.length() );
        error("\n"+ places +" source is not three letters");
      }
      if (places.matches("-?\\d+(\\.d\\d+)?") ) {
        error("\n"+ places +" source has numeric values ");
      }
      return places.toUpperCase();
    }

    /**
    *
    * @param client
    * @param search
    * @return
    */
    private static boolean searchFlight( AirlineRestClient client, List<String> search  )
    {
      String airlineName = null;
      String source = null;
      String destination = null;
      String flightNumberAsString = null;

      airlineName = new String( search.get(0) );
      flightNumberAsString = new String( search.get(1) );
      source = new String( search.get(2) );
      destination = new String( search.get(4) );

      try {
        if (source == null) {
            usage("Missing flight source");

        } else if (destination == null) {
            usage("Missing destination");

        } else if (flightNumberAsString == null) {
            String prettyAirline = client.getFlightsBetween(airlineName, source, destination);
            System.out.println(prettyAirline);

        } else {
            int flightNumber;
            try {
                flightNumber = Integer.parseInt(flightNumberAsString);

            } catch (NumberFormatException ex) {
                usage("Invalid flight number: " + flightNumberAsString);
                return false;
            }
            Flight flight = new Flight(search);
            client.addFlight(airlineName, flight);
        }
      } catch ( IOException ex ) {
        error("While contacting server: " + ex);
      }
      return true;
    }

    private static void airlineArgsParser(List<String> flags, List<String> flightInfo, List<String> hostInfo, String[] args)
    {
      for( int i = 0; i < args.length; i++ ) {
        switch (args[i].charAt(0)) {
          case '-'://Flag Catch
            if (args[i].length() < 2){
                error("\nFlag arguments are too short and thus invalid.");
            }

            //Host Catch
            if( args[i].substring(1,args[i].length() ).toLowerCase().equals("host") )
            {
              if( ( args[i+1].isEmpty() || args[i+1] == null ) == false )
              {
                String host = new String( args[i].substring(1,args[i].length() ).trim() +" " + args[i+1].trim());
                hostInfo.add( host );
                i++;
                break;
              }
              error("\nHost requires a argument");
            }

            //Port Catch
            if( args[i].substring(1,args[i].length() ).toLowerCase().equals("port") )
            {
              if( ( args[i+1].isEmpty() || args[i+1] == null ) == false )
              {
                hostInfo.add( new String( args[i].substring(1,args[i].length() ) +" " + args[i+1]) );
                i++;
                break;
              }

              error("\nPort requires a argument");
              System.exit(0);
            }

            //Search
            if( args[i].substring(1,args[i].length() ).toLowerCase().equals("search") )
            {
              if( (args[i+1].isEmpty() || args[i+1] == null
                   || args[i+2].isEmpty() || args[i+1] == null ) == false )
              {
                flags.add(args[i]);
                flightInfo.add( args[i+1].toUpperCase() );
                flightInfo.add( args[i+2].toUpperCase() );
                i=i+2;
                break;
              }
              else
              {
                error("\nSearch requires more arguments");
                System.exit(0);
              }
            }

            flags.add(args[i].trim().substring(1,args[i].length()));
            break;
          case '0': case '1': case '2': case '3': case '4': case '5': case '7': case '8': case '9':
            //SimpleDateCheck: Only breaks if in format ##/ or #/
            if( args[i].charAt(1) == '/' || args[i].charAt(2) == '/' )
            {
              if ( args[i].trim().matches( "\\d{1,2}/\\d{1,2}/\\d{4}") ) //DateRegexMatchi
              {
                try
                {
                  if (args[i + 1] != null && args[i + 1].trim().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) //TimeRegexMatch
                  {
                    flightInfo.add(new String(args[i] + " " + args[i + 1] +" "+args[i+2]).trim());
                    i=i+2;
                    break;
                  }
                }
                catch ( IllegalArgumentException ex )
                {
                  throw new IllegalArgumentException("\nTime Arg passed in from Command Line not valid" + "\nTime Arg passed: " + args[i + 1].toString() +"\n");
                }
                catch ( IndexOutOfBoundsException ex)
                {
                  throw new IndexOutOfBoundsException( "\nMissing arguments: #"+ ex.getMessage() + "\nCause: "+ ex.getCause() );
                }
              }
              else
              {
                throw new IllegalArgumentException("\nDate Args passed in from Command Line not valid" + "\nDate Passed In: " + args[i].toString());
              }
            }
            default:
              flightInfo.add( args[i].trim() );
              break;
        }
      }
    }

    private static AirlineRestClient connectToHost( List<String> hostInfo ){
        String hostName = null;
        String portString = null;
        AirlineRestClient client = null;

        for( int i =0; i < hostInfo.size(); i++ )
        {
          if( hostInfo.get(i).contains("host") )
          {
            String[] temp = hostInfo.get(i).split(" ");
            hostName = temp[1];
          }
          if( hostInfo.get(i).contains("port") )
          {
            String[] temp = hostInfo.get(i).split(" ");
            portString = temp[1];
          }
        }

        if (hostName == null) {
            usage( MISSING_ARGS );

        } else if ( portString == null) {
            usage( "Missing port" );
        }

        int port;
        try {
            port = Integer.parseInt( portString );

        } catch (NumberFormatException ex) {
            usage("Port \"" + portString + "\" must be an integer");
            return null;
        }

        client = new AirlineRestClient(hostName, port);
        return client;

        /*
        String message;
        try {
            if (key == null) {
                // Print all key/value pairs
                Map<String, String> keysAndValues = client.getAllKeysAndValues();
                StringWriter sw = new StringWriter();
                Messages.formatKeyValueMap(new PrintWriter(sw, true), keysAndValues);
                message = sw.toString();

            } else if (value == null) {
                // Print all values of key
                message = Messages.formatKeyValuePair(key, client.getValue(key));

            } else {
                // Post the key/value pair
                client.addKeyValuePair(key, value);
                message = Messages.mappedKeyValue(key, value);
            }

        } catch ( IOException ex ) {
            error("While contacting server: " + ex);
            return;
        }

        System.out.println(message);

        */
    }

    private static void error( String message )
    {
        PrintStream err = System.err;
        err.println("\n** " + message);

        System.exit(1);
    }

    /**
     * Prints usage information for this program and exits
     * @param message An error message to print
     */
    private static void usage( String message )
    {
        PrintStream err = System.err;
        err.println("** " + message);
        err.println();
        err.println("usage: java Project4 host port [key] [value]");
        err.println("  host    Host of web server");
        err.println("  port    Port of web server");
        err.println("  key     Key to query");
        err.println("  value   Value to add to server");
        err.println();
        err.println("This simple program posts key/value pairs to the server");
        err.println("If no value is specified, then all values are printed");
        err.println("If no key is specified, all key/value pairs are printed");
        err.println();

        System.exit(1);
    }

    /**
     * README : This explains usage of this program
     */
    public static void README(){
      int ProjNum = 4;
      System.out.println("Name: Chhewang Sherpa");
       System.out.println("Project " + ProjNum );
      System.out.println("The objectives of this project is to be able to input the following command"
                         + "\nline usage, as well having that added to a flight object for future use in Project two"
                         + "\nwith multiple Flight that are airlines.");
      System.out.println("Project "+ ProjNum +" was achieved through extensive Google searching through Stackoverflow"
                         + "\nand the use of the Java natives libraries in the form of List<T> and Date.\n"
                         + "\nUsage is described below:");
      System.out.printf( "\nusage: java edu.pdx.cs410J.csherpa.Project%d [options] <args>", ProjNum );
      System.out.println( "args are (in this order):");
      System.out.printf("%-20s%s","name", "The name of the airline\n");
      System.out.printf("%-20s%s", "flightNumber", "The flight number\n");
      System.out.printf("%-20s%s", "src", "Three-letter code of departure airport\n");
      System.out.printf("%-20s%s", "departTime", "Departure date and time (12-hour time)\n");
      System.out.printf("%-20s%s","dest", "Three-letter code of arrival airport\n");
      System.out.printf("%-20s%s","arriveTime", "Arrival date and time (12-hour time)\n");
      System.out.println( "|_Date and time should be in the format: mm/dd/yyyy hh:mm am/pm");
      System.out.println( "\nflag options (options may appear in any order):");
      System.out.printf("%-20s%s%s", "-host hostname", "Host computer on which the server runs");
      System.out.printf("%-20s%s", "-port port", "Port on which the server is listening");
      System.out.printf("%-20s%s","-search", "Search for flights" );
      System.out.printf("%-20s%s","-print", "Prints a description of the new flight\n");
      System.out.printf("%-20s%s","-README", "Prints a README for this project and exits\n");
    }
}