
/*
* Author : Bhavesh
*/
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wolfram.alpha.WAAssumption;
import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;
import com.wolfram.alpha.WAWarning;
import com.wolfram.alpha.net.ProxySettings;


/**
 * Servlet implementation class HelloWorld
 */
public class HelloWorld extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private StringWriter sw = new StringWriter();
    private PrintWriter pw = new PrintWriter(sw);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HelloWorld() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try{
		
        // wolfram alpha api's app id

	String appid = "<YOUR_APP_ID_HERE>";
        StringBuffer sb = new StringBuffer();
        boolean dbUpdated = false;
        //ProxySettings proxySettings = ProxySettings.getInstance();
    	//proxySettings.setProxyInfo(ProxySettings.PROXY_MANUAL, "proxy.cognizant.com", 6050);

        String txtweb_id = request.getParameter("txtweb-id");
        String txtweb_mobile = request.getParameter("txtweb-mobile");
        String txtweb_message = request.getParameter("txtweb-message");
        String txtweb_protocol = request.getParameter("txtweb-protocol");
        SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = new Date();
        String requestSubmitDate = sd.format(date);
        String delimiter = " ";
        String[] userInputArr = null;
        if(txtweb_message!=null && txtweb_message.length() > 0)
        {
        txtweb_message = txtweb_message.trim();
        userInputArr = txtweb_message.split(delimiter);
        }
        
        
        validateUserInputForKBC(txtweb_id, txtweb_mobile, txtweb_message, txtweb_protocol, requestSubmitDate, userInputArr, sb);
        
        
        if(sb.length() > 0)
        {
        	out.println(sb);
        }
        
        else
        {
        
        System.out.println("The input parameter is " + txtweb_message);
        
        // The WAEngine is a factory for creating WAQuery objects,
        // and it also used to perform those queries. You can set properties of
        // the WAEngine (such as the desired API output format types) that will
        // be inherited by all WAQuery objects created from it. Most applications
        // will only need to crete one WAEngine object, which is used throughout
        // the life of the application.
        WAEngine engine = new WAEngine();
        
        // These properties will be set in all the WAQuery objects created from this WAEngine.
        engine.setAppID(appid);
        engine.addFormat("plaintext");

        // Create the query.
        WAQuery query = engine.createQuery();
        
        // Set properties of the query.
        query.setInput(txtweb_message);
		
        if(txtweb_message==null || txtweb_message.isEmpty())
        {
        
        	_generateErrorMessage(sb);
        	dbUpdated = true;
        	//_saveSearchHistory(txtweb_id,txtweb_message, txtweb_mobile, txtweb_protocol, "NULL INPUT");
        }
        else if(txtweb_message!=null && txtweb_message.contains("test101"))
        {
        	_generateCongratsMessage(sb);
        	dbUpdated = true;
        	//_saveSearchHistory(txtweb_id,txtweb_message, txtweb_mobile, txtweb_protocol, "test101");
        }
        else if (!txtweb_protocol.equalsIgnoreCase("1000") )
        {
        	_generateDisableEmulatorMessage(sb);
        	dbUpdated = true;
        	//_saveSearchHistory(txtweb_id,txtweb_message, txtweb_mobile, txtweb_protocol, "Emulator");
        }
        else if(txtweb_message!=null && txtweb_message.contains("victoria") && txtweb_message.contains("secret"))
        {
        	_generateVictoriaMessage(sb);
        	dbUpdated = true;
        	//_saveSearchHistory(txtweb_id,txtweb_message, txtweb_mobile, txtweb_protocol, "victoria");
        }
        else
        {
        try {
            // For educational purposes, print out the URL we are about to send:
            System.out.println("Query URL:");
            System.out.println(engine.toURL(query));
            System.out.println("");
            
            // This sends the URL to the Wolfram|Alpha server, gets the XML result
            // and parses it into an object hierarchy held by the WAQueryResult object.
            WAQueryResult queryResult = engine.performQuery(query);
            
            if (queryResult.isError()) {
            	_generateQueryError(sb, queryResult);
            } else if (!queryResult.isSuccess()) {
            	_generateQueryFailedMsg(sb);
            } else {
            	sb.append("<html>");
            	sb.append("<head>");
            	sb.append("<meta name='txtweb-appkey' content='TXT_WEB_APP_KEY'/>");
            	sb.append("<meta http-equiv='Content-Transfer-Encoding: 7bit' />");
            	sb.append("</head>");
                // Got a result.
                System.out.println("Successful query. Pods follow:\n");
                String podDesc;
            	boolean bool = false;
            	
            	// print warnings
            	generateWarnings(sb, queryResult);
            	
                for (WAPod pod : queryResult.getPods()) {
                	bool = true;
                    if (!pod.isError()) {
                    	
                        for (WASubpod subpod : pod.getSubpods()) {
                            for (Object element : subpod.getContents()) {
                                if (element instanceof WAPlainText) {
                                	podDesc = ((WAPlainText) element).getText();
                                	if(podDesc!=null && podDesc.length() > 0 )
                                	{
                                		if (bool)
                                		{
                                		System.out.println(pod.getTitle());
                                        sb.append(pod.getTitle());
                                        System.out.println("<br/>");
                                        sb.append("<br/>");
                                        System.out.println("------------");
                                        sb.append("------------");
                                        System.out.println("<br/>");
                                        sb.append("<br/>");
                                    	bool= false;
                                		}
                                    	System.out.println(podDesc);
                                    	sb.append(podDesc);
                                    	System.out.println("<br/>");
                                        sb.append("<br/>");
                                   
                                	}
                                	
                                }
                            }
                        }
                        System.out.println("<br/>");
                        sb.append("<br/>");
                        
//                        WAPodState[] podStates = pod.getPodStates();
//                        if(podStates!=null && podStates.length > 0)
//                        {
//                        	sb.append("Option:");
//                    		sb.append("------------");
//                    		
//                    		for(WAPodState podState : podStates)
//                    		{
//                    			for (String podStateName : podState.getNames())
//                    					sb.append(podStateName);
//                    		}
//                    		sb.append("");
//                        }
                    }
                    	
                 }
                
                generateAssumptionsMsg(sb, queryResult);
                
                sb.append("</html>");
                // We ignored many other types of Wolfram|Alpha output, such as warnings, assumptions, etc.
                // These can be obtained by methods of WAQueryResult or objects deeper in the hierarchy.
            }
        } catch (WAException e) {
            e.printStackTrace(pw);
            out.println(sw.toString());
        }
	}
        if(sb.length() == 0)
        {
        	generateNullMessage(sb);
//        	if(!dbUpdated)
//        	_saveSearchHistory(txtweb_id,txtweb_message, txtweb_mobile, txtweb_protocol, "NO RESULT");
        }
        else
        {
        	String result = sb.toString();
        	
        	result = result.replaceAll("Wolfram", "");
        	
        	out.println(result);
//        	if(!dbUpdated)
//        	_saveSearchHistory(txtweb_id,txtweb_message, txtweb_mobile, txtweb_protocol, "SUCCESS");
        }
		}
        } catch (Exception e) {
            e.printStackTrace(pw);
            System.out.println(sw.toString());
        }
        
	}

	private void _generateDisableEmulatorMessage(StringBuffer sb) {
		sb.append("<html>");
    	sb.append("<head>");
    	sb.append("<meta name='txtweb-appkey' content='<TXT_WEB_APP_KEY>'/>");
    	sb.append("<meta http-equiv='Content-Transfer-Encoding: 7bit' />");
    	sb.append("</head>");
    	sb.append("Warning:");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append("This app works only when SMS is sent from phone. Please read further for instructions.");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("What does this App do?");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append("This app gives you access to the world's facts and data and answers across a range of topics, including science, nutrition, history, geography, engineering, mathematics, linguistics, sports, finance, music via Wolfram|Alpha's API - the computational search engine.");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("How to use this Service?");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append("Type @alpha your_query_here and send it to any of the txtweb numbers.");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("Few Examples:");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append("@alpha when is dussera?");
        sb.append("<br/>");
        sb.append("@alpha today + 18 days?");
        sb.append("<br/>");
        sb.append("@alpha who was the prime minister of UK in 1975?");
        sb.append("<br/>");
        sb.append("@alpha moon rise time mumbai 27th august 2012");
        sb.append("<br/>");
        sb.append("@alpha how many calories in an egg?");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("</html>");
		
	}

	private void _generateVictoriaMessage(StringBuffer sb) {

		sb.append("<html>");
    	sb.append("<head>");
    	sb.append("<meta name='txtweb-appkey' content='TXT_WEB_APP_KEY'/>");
    	sb.append("<meta http-equiv='Content-Transfer-Encoding: 7bit' />");
    	sb.append("</head>");
        sb.append("Result:");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append("Only Rajnikanth knows that! Mind It!");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("Try Other Examples:");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append("@alpha when is dussera?");
        sb.append("<br/>");
        sb.append("@alpha today + 18 days?");
        sb.append("<br/>");
        sb.append("@alpha who was the prime minister of UK in 1975?");
        sb.append("<br/>");
        sb.append("@alpha moon rise time mumbai 27th august 2012");
        sb.append("<br/>");
        sb.append("@alpha how many calories in an egg?");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("</html>");
    
		
	
		
	}

	private void _saveSearchHistory(String txtweb_id, String txtweb_message,
			String txtweb_mobile, String txtweb_protocol, String search_status) throws ClassNotFoundException, SQLException {
		  Connection connect = null;
		   PreparedStatement preparedStatement = null;
		Class.forName("com.mysql.jdbc.Driver");
	      // Setup the connection with the DB
	      connect = DriverManager
	          .getConnection("jdbc:mysql://localhost:3306/txtweb?user=root&password=root");

	      // Result set get the result of the SQL query
	      // PreparedStatements can use variables and are more efficient
	      preparedStatement = connect
	          .prepareStatement("insert into  alpha_search_history(txtweb_id, txtweb_message, txtweb_mobile, " +
	          		"			request_submit_date ,txtweb_protocol,search_status) values (?, ?, ?, now(), ? , ?)");
	      // "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
	      // Parameters start with 1
	      preparedStatement.setString(1, txtweb_id);
	      preparedStatement.setString(2, txtweb_message);
	      preparedStatement.setString(3, txtweb_mobile);
	      preparedStatement.setString(4, txtweb_protocol);
	      preparedStatement.setString(5, search_status);
	      preparedStatement.executeUpdate();
	      
	      preparedStatement.close();
	      connect.close();
	}

//	private void _sendMail(String input) throws MessagingException {
//		String smtpHost = "smtp.mail.yahoo.com";
//	    String popHost = "pop.mail.yahoo.com";
//	    String from = "rajiv4u_19@yahoo.com"; // with @yahoo.com
//	    String to = "rajiv.ashok.bhatt@gmail.com";

//	    // Get system properties
//	    Properties props = System.getProperties();
//	    props.put("http.proxySet", "true"); 
//	    props.put("http.proxyHost", "proxy.cognizant.com"); 
//	    props.put("http.proxyPort", "6050");
//	    // Setup mail server
//	    props.put("mail.smtp.host", smtpHost);
//
//	    // Get session
//	    Session session = Session.getDefaultInstance(props, null);
//	    session.setDebug(true);
//
//	    // Pop Authenticate yourself
//	    Store store = session.getStore("pop3");
//	    store.connect(popHost, username, password);
//
//	    // Define message
//	    MimeMessage message = new MimeMessage(session);
//	    message.setFrom(new InternetAddress(from));
//	    message.addRecipient(Message.RecipientType.TO, 
//	      new InternetAddress(to));
//	    message.setSubject("Hello JavaMail");
//	    message.setText("Welcome to Yahoo's JavaMail. " + input);
//
//	    // Send message
//	    Transport.send(message);
//		
//	}

	private void generateNullMessage(StringBuffer sb) {
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta name='txtweb-appkey' content='TXT_WEB_APP_KEY'/>");
		sb.append("<meta http-equiv='Content-Transfer-Encoding: 7bit' />");
		sb.append("</head>");
		sb.append("Error:");
		sb.append("<br/>");
		sb.append("------------");
		sb.append("<br/>");
		sb.append("Sorry! We failed to understand the provided input");
		sb.append("<br/>");
		sb.append("Please make sure you've entered a valid input query");
		sb.append("<br/>");
		sb.append("Few Examples:");
		sb.append("<br/>");
		sb.append("------------");
		sb.append("<br/>");
		sb.append("@alpha when is dussera?");
		sb.append("<br/>");
		sb.append("@alpha today + 18 days?");
		sb.append("<br/>");
		sb.append("@alpha who was the prime minister of UK in 1975?");
		sb.append("<br/>");
		sb.append("@alpha moon rise time mumbai 27th august 2012");
		sb.append("<br/>");
		sb.append("@alpha how many calories in an egg?");
		sb.append("<br/>");
		sb.append("</html>");
	}

	private void generateAssumptionsMsg(StringBuffer sb,
			WAQueryResult queryResult) {
		WAAssumption[] assumptions = queryResult.getAssumptions();
		
		if(assumptions!=null && assumptions.length > 0)
		{
		
			String[] descriptions = assumptions[0].getDescriptions();
			sb.append("Assumptions: ");
		    sb.append("<br/>");
			sb.append("------------");
		    sb.append("<br/>");
			sb.append("Results were assuming input as : " + descriptions[0]);
		    sb.append("<br/>");
			sb.append("<br/>");
			if(descriptions.length > 1)
			{
			sb.append("Not what you expected? Try: ");
			sb.append("<br/>");
			sb.append("------------");
			sb.append("<br/>");
			for(int i=1;i < descriptions.length; i++)
			{
				 sb.append(" - " +  descriptions[i]);
			     sb.append("<br/>");
			}
			}
			
			
			
		}
	}

	private void generateWarnings(StringBuffer sb, WAQueryResult queryResult) {
		WAWarning[] warnings  = queryResult.getWarnings();
		if(warnings!=null && warnings.length > 0)
		{
			sb.append("Warning:");
		    sb.append("<br/>");
			sb.append("------------");
		    sb.append("<br/>");
		}
		for (WAWarning warning : warnings)
		{
			sb.append(warning.getText());
		    sb.append("<br/>");
		}
		
		sb.append("<br/>");
	}

	private void _generateQueryFailedMsg(StringBuffer sb) {
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta name='txtweb-appkey' content='e0859f9d-0b8f-47be-8dfe-04a2985f0013'/>");
		sb.append("<meta http-equiv='Content-Transfer-Encoding: 7bit' />");
		sb.append("</head>");
		System.out.println("Query was not understood; no results available.");
		sb.append("<br/>");
		sb.append("Error:");
		sb.append("<br/>");
		sb.append("------------");
		sb.append("<br/>");
		sb.append("No results available for the provided input or the app failed to understand the provided input!");
		sb.append("<br/>");
		sb.append("<br/>");
		sb.append("Please make sure you've entered a valid input query.");
		sb.append("<br/>");
		sb.append("<br/>");
		sb.append("Few Examples:");
		sb.append("<br/>");
		sb.append("------------");
		sb.append("<br/>");
		sb.append("@alpha when is dussera?");
		sb.append("<br/>");
		sb.append("<br/>");
		sb.append("@alpha today + 18 days?");
		sb.append("<br/>");
		sb.append("<br/>");
		sb.append("@alpha who was the prime minister of UK in 1975?");
		sb.append("<br/>");
		sb.append("<br/>");
		sb.append("@alpha moon rise time mumbai 27th august 2012");
		sb.append("<br/>");
		sb.append("<br/>");
		sb.append("@alpha how many calories in an egg?");
		sb.append("<br/>");
		sb.append("<br/>");
		sb.append("More Examples @ http://developer.txtweb.com/user/apps/alpha");
		sb.append("</html>");
	}

	private void _generateQueryError(StringBuffer sb, WAQueryResult queryResult) {
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta name='txtweb-appkey' content='TXT_WEB_APP_KEY'/>");
		sb.append("<meta http-equiv='Content-Transfer-Encoding: 7bit' />");
		sb.append("</head>");
		System.out.println("Query error");
		System.out.println("  error code: " + queryResult.getErrorCode());
		System.out.println("  error message: " + queryResult.getErrorMessage());
		sb.append("  error message: " + queryResult.getErrorMessage());
		sb.append("</html>");
	}

	/**
	 * 
	 * @param sb
	 */
	private void _generateCongratsMessage(StringBuffer sb) {
		sb.append("<html>");
    	sb.append("<head>");
    	sb.append("<meta name='txtweb-appkey' content='e0859f9d-0b8f-47be-8dfe-04a2985f0013'/>");
    	sb.append("<meta http-equiv='Content-Transfer-Encoding: 7bit' />");
    	sb.append("</head>");
        System.out.println("Congrats! Your App Hit just got incremented by 1!! ;) ");
        sb.append("Congrats!!! ;) ");
        sb.append("</html>");
		
	}

	/**
	 * 
	 * @param sb
	 */
	private void _generateErrorMessage(StringBuffer sb) {
		sb.append("<html>");
    	sb.append("<head>");
    	sb.append("<meta name='txtweb-appkey' content='TXT_WEB_APP_KEY'/>");
    	sb.append("<meta http-equiv='Content-Transfer-Encoding: 7bit' />");
    	sb.append("</head>");
        sb.append("What does this App do?");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append("This app gives you access to the world's facts and data and answers across a range of topics, including science, nutrition, history, geography, engineering, mathematics, linguistics, sports, finance, music via Wolfram|Alpha's API - the computational search engine.");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("How to use this Service?");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append("Type @alpha [your_query_here] and send it to any of the txtweb numbers.");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("Few Examples:");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append("@alpha when is dussera?");
        sb.append("<br/>");
        sb.append("@alpha today + 18 days?");
        sb.append("<br/>");
        sb.append("@alpha who was the prime minister of UK in 1975?");
        sb.append("<br/>");
        sb.append("@alpha moon rise time mumbai 27th august 2012");
        sb.append("<br/>");
        sb.append("@alpha how many calories in an egg?");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("For More Examples Visit:");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append("http://developer.txtweb.com/user/apps/alpha");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("Disclaimer:");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append("Results and information from this application are not a certified or definitive source of information that can be relied on for legal, financial, medical, life-safety, or any other critical purposes.");
        sb.append("<br/>");
        sb.append("</html>");
    
		
	}

	private void validateUserInputForKBC(String txtweb_id, String txtweb_mobile, String txtweb_message, String txtweb_protocol, String requestSubmitDate, String[] userInputArr, StringBuffer sb) {
		System.out.println("validateUserInputForKBC() -- Enter");
		String employeeID = null;
		String employeeAnswer = "";
		if(userInputArr!=null && userInputArr.length > 0)
		{
			for(int i = 0; i < userInputArr.length ; i++)
			{
				if (i == 0) {
					if (isEmployeeID(userInputArr[i])) {
						employeeID = userInputArr[i];
						if(userInputArr.length ==1)
						{
							System.out.println("Missing Input for CogniKBC");
							_generateMissingAnswerErrorMessage(sb, employeeID);
							break;
						}
						else
						{
						System.out.println("Valid 6 digit employee ID");
						continue;
						}
					} else {
						System.out.println("Invalid employee ID or not a CogniKBC entry.");
						sb = null;
						break;
					}
				}
				else
				{
					for(int j=i; j < userInputArr.length ; j++)
					{
						employeeAnswer = employeeAnswer  + userInputArr[j] + " ";
					}
					employeeAnswer = employeeAnswer.trim();
					_acknowledgeKBCResponse(sb, employeeID, employeeAnswer);
					_saveKBCDetailsInDB(txtweb_id, txtweb_mobile, txtweb_message, txtweb_protocol, requestSubmitDate, employeeID, employeeAnswer);
					break;
				}
				
			}
		}
		
		System.out.println("validateUserInputForKBC() -- Exit");
	}


	private void _generateMissingAnswerErrorMessage(StringBuffer sb,
			String employeeID) {

		sb.append("<html>");
    	sb.append("<head>");
    	sb.append("<meta name='txtweb-appkey' content='e0859f9d-0b8f-47be-8dfe-04a2985f0013'/>");
    	sb.append("<meta http-equiv='Content-Transfer-Encoding: 7bit' />");
    	sb.append("</head>");
        sb.append("CogniKBC Input Error");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append("We noticed that you did not submit the answer in the expected format.");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("How to submit your answer?");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append("Type @alpha[space][associate_id][space][answer] and send it to 92202-92202.");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("Example:");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append("If the question asked was 'Who is the CEO of Cognizant?'");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("@alpha 123456 Francisco D'Souza");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("Results would be announced soon via E-Mail. Till then keep playing CogniKBC.");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("Visit http://url for more details.");
        sb.append("<br/>");
        sb.append("</html>");
    
		
	
		
	}

	private void _saveKBCDetailsInDB(String txtweb_id, String txtweb_mobile,
			String txtweb_message, String txtweb_protocol,
			String requestSubmitDate, String employeeID, String employeeAnswer)
			{

		Connection connect = null;
		PreparedStatement preparedStatement = null;
		try{
		Class.forName("com.mysql.jdbc.Driver");
		// Setup the connection with the DB
		connect = DriverManager
				.getConnection("jdbc:mysql://localhost:3306/txtweb?user=root&password=root");
		System.out
		.println("insert into cogni_kbc_history(txtweb_id, employee_id, employeeAnswer, "
				+ "           txtweb_mobile, txtweb_message, txtweb_protocol,request_submit_date) " +
						"  values ( '" + txtweb_id+ "','" +employeeID+ "','" +employeeAnswer+ "','"+txtweb_mobile+ "','"+txtweb_message+ "','"+txtweb_protocol+ "','" + requestSubmitDate+"'");

		// Result set get the result of the SQL query
		// PreparedStatements can use variables and are more efficient
		preparedStatement = connect
				.prepareStatement("insert into cogni_kbc_history(txtweb_id, employee_id, employee_answer, "
								+ "           					txtweb_mobile, txtweb_message, txtweb_protocol,request_submit_date) " +
								"     							values ( ?, ?, ?, ?, ? , ?, now())");
		// "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
		// Parameters start with 1
		preparedStatement.setString(1, txtweb_id);
		preparedStatement.setString(2, employeeID);
		preparedStatement.setString(3, employeeAnswer);
		preparedStatement.setString(4, txtweb_message);
		preparedStatement.setString(5, txtweb_mobile);
		preparedStatement.setString(6, txtweb_protocol);
		preparedStatement.executeUpdate();

		preparedStatement.close();
		connect.close();
		}catch (Exception e)
		{
			e.printStackTrace(pw);
			System.out.println(sw.toString());
		}
		
	}

		

	private void _acknowledgeKBCResponse(StringBuffer sb, String employeeID, String employeeAnswer) {
		sb.append("<html>");
    	sb.append("<head>");
    	sb.append("<meta name='txtweb-appkey' content='YOUR_TXT_WEB_
APP_KEY'/>");
    	sb.append("<meta http-equiv='Content-Transfer-Encoding: 7bit' />");
    	sb.append("</head>");
        sb.append("Dear Associate, thank you for submitting your answer for CogniKBC contest.");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("We have received the following response from you.");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("Employee ID");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append(employeeID);
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("Your Answer:");
        sb.append("<br/>");
        sb.append("------------");
        sb.append("<br/>");
        sb.append(employeeAnswer);
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("Please resubmit if we have received it incorrectly. This might be due to technical issues on our end	. Sorry for the inconvenience.");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("Results would be announced soon via E-Mail. Till then keep playing CogniKBC.");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("Visit http://url for more details.");
        sb.append("<br/>");
        sb.append("</html>");
		
	}

	private  boolean isEmployeeID (String s){
		   Pattern pattern = Pattern.compile("\\d{6}");
		   Matcher matcher = pattern.matcher(s);
		   if (matcher.find()){
		     return true; 
		   } 
		   return false; 
		 }

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}



