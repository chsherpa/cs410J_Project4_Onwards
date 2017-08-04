package edu.pdx.cs410J.chsherpa;

import edu.pdx.cs410J.AbstractFlight;

import java.text.*;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

//import static edu.pdx.cs410J.chsherpa.Project3.*;

public class Flight extends AbstractFlight implements Comparable<Flight>
{
  private String flightName; //= "Flight Default";
  private int flightNumber = 42;
  private String src;//= "Source Airport";
  private String departTime;
  private String dest;// = "Destination Airport";
  private String arriveTime;

  /**
   * Constructor with parameter List for easy input of List object
   *
   * @param flightInfo
   */
  public Flight(List<String> flightInfo)
  {
    addFlightInfo(flightInfo);
  }

  public Flight( String flightName, int number, String source, String departTime, String destination, String arriveTime ) {
    setSource(source);
    setDestination(destination);
    setFlightName(flightName);
    setNumber(number);
    setDepartureString(departTime);
    setArrivalString(arriveTime);
  }

  public Flight(String airlineName, String source, String destination, int number) {
    this.setFlightName( airlineName );
    this.setSource( source) ;
    this.setDestination(destination);
    this.setNumber(number);
  }

  /**
   * Returns the name of the Flight
   *
   * @return flightName Name property of the Flight object
   */
  public String getFlightName()
  {
    return this.flightName;
  }

  /**
   * Set the name of the Flight
   *
   * @param flightName Name property of the Flight object
   */
  public void setFlightName(String flightName)
  {
    this.flightName = flightName;
  }

  @Override
  /**
   * Override of parent function getNumber
   *
   * @return integer; no limits assigned yet
   */
  public int getNumber()
  {
    return flightNumber;
  }

  /**
   * Set the Flight Number
   *
   * @param flightNumber numeric flight number
   */
  public void setNumber(int flightNumber)
  {
    this.flightNumber = flightNumber;
  }

  @Override
  /**
   * Override of the AbstractClass Method getSource
   */
  public String getSource()
  {
    return src;
//    throw new UnsupportedOperationException("This method is not implemented yet");
  }

  /**
   * Set the source of the flight
   *
   * @param src Source property of the flight object
   */
  public void setSource(String src)
  {
    this.src = src;
  }

  @Override
  /**
   * Override of the Abstract Class method getDepartureString
   */
  public String getDepartureString()
  {
    String shDate = null;
    try
    {
      shDate = ShortDate(this.departTime);
    }
    catch (ParseException e)
    {
      throw new IllegalArgumentException("\nParse Problems!!\n");
    }
    return shDate;
  }

  /**
   * Set the depart time of the flight object
   *
   * @param departTime
   */
  public void setDepartureString(String departTime)
  {
    this.departTime = departTime;
  }

  @Override
  /**
   * Override of the AbstractClass Method getDestination
   */
  public String getDestination()
  {
    return dest;
    // throw new UnsupportedOperationException("This method is not implemented yet");
  }

  /**
   * Sets the destination property of the flight object
   *
   * @param dest destination of the flight
   */
  public void setDestination(String dest)
  {
    this.dest = dest;
  }

  @Override
  /**
   * Override of the AbstractClass Method getArrivalString
   *
   */
  public String getArrivalString()
  {
    String shDate = new String();
    try
    {
      shDate = ShortDate(this.arriveTime);
    }
    catch (ParseException e)
    {
      throw new IllegalArgumentException("\nParse Problems!!\n");
    }
    return shDate;
  }

  /**
   * Set the arrival time property of the flight object
   *
   * @param arriveTime arrival time of the object; in format
   */
  public void setArrivalString(String arriveTime)
  {
    this.arriveTime = arriveTime;
  }

  /**
   * Method to add multiple flights to this object
   *
   * @param flightInfo List Object of flights
   */
  public void addFlightInfo(List<String> flightInfo)
  {
    try
    {
      if (flightInfo.size() == 6)
      {
        this.setFlightName(flightInfo.get(0));
        this.setNumber(Integer.parseInt(flightInfo.get(1)));
        this.setSource(flightInfo.get(2));
        this.setDepartureString(flightInfo.get(3));
        this.setDestination(flightInfo.get(4));
        this.setArrivalString(flightInfo.get(5));
      }
    }
    catch (IllegalArgumentException ex)
    {
      throw new IllegalArgumentException("Not enough arguments for adding to flight info");
    }
  }

  /**
   * Display function for a Flight
   */
  public void displayFlightInfo() throws ParseException
  {
    System.out.println("Flight");
    System.out.println("|_" + "Name: " + this.getFlightName());
    System.out.println("|_" + "Number: " + this.getNumber());
    System.out.println("|_" + "Source: " + this.getSource());
    System.out.printf("|_Departure Time: %s%n", prettyDate(this.getDepartureString()));
    System.out.println("|_" + "Destination: " + this.getDestination());
    System.out.printf("|_Arrival Time: %s%n", prettyDate(this.getArrivalString()));
    return;
  }

  /**
   * Pretty Date format for checking the date
   *
   * @param inputDate
   * @return
   * @throws ParseException
   */
  public String prettyDate(String inputDate) throws ParseException
  {
    Date date;
    DateFormat dateInput = new SimpleDateFormat("mm/dd/yyyy hh:mm a");
    date = dateInput.parse(inputDate);
    if (inputDate.equals(date))
    {
      date = null;
    }
    return date.toString();
  }

  /**
   * Method to get the Short Date
   *
   * @param SourceDate Date of the current: expected format "MM/dd/yyyy hh:aa"
   * @return String value of shortDate
   * @throws ParseException Standard error catch
   */
  private String ShortDate(String SourceDate) throws ParseException
  {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
    Date dDate = sdf.parse(SourceDate);
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    return df.format(dDate);
  }

  /**
   * Date Sort
   * @param comparedFlight Flight to be compared to
   * @return int value signifies order
   */
  public int compareTo( Flight comparedFlight ){
    DateFormat sf = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    Date dDate1, dDate2;
    try
    { dDate1 = sf.parse(this.getDepartureString()); }
    catch (ParseException e)
    { throw new IllegalArgumentException("\nf1 Comparator Parse Problems!\n"); }
    try
    { dDate2 = sf.parse(comparedFlight.getDepartureString()); }
    catch (ParseException xe)
    { throw new IllegalArgumentException("\nf2 Comparator Parse Problems!\n"); }

    int value2 = 0;
    if (dDate1.before(dDate2))
      { value2 = -1; }
    else if (dDate1.before(dDate2))
      { value2 = 1; }
    else
      { value2 = 0; }
    return value2;
  }

  /**
   * Comparator Sort based on Source and Departure String
   */
  public static Comparator<Flight> FlightSourceAndFlightDepartureComparator = new Comparator<Flight>()
  {
    @Override
    public int compare(Flight f1, Flight f2)
    {
      String f1Source = f1.getSource().toUpperCase();
      String f2Source = f2.getSource().toUpperCase();

      return f1Source.compareTo(f2Source);
    }
  };
}
