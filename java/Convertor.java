import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//solution = "graph TD\r\n"
//		+ "    A[Christmas] -->|Get money| B(Go shopping)\r\n"
//		+ "    B --> C{Let me think}\r\n"
//		+ "    C -->|One| D[Laptop]\r\n"
//		+ "    C -->|Two| E[iPhone]\r\n"
//		+ "    C -->|Three| F[fa:fa-car Car]";
@WebServlet("/Convertor")
public class Convertor extends HttpServlet
{
	List<String> syntax = new ArrayList<>(Arrays.asList("BEGIN","END","NUMBER","INPUT","OUTPUT","WHILE","ENDWHILE","IF","THEN","ENDIF"));
	String solution = "";
	char boxId;
	Stack<Character> ifStack = new Stack<>();
	Stack<Character> whileStack = new Stack<>();
	Stack<Character> endIfStack = new Stack<>();
	Stack<Character> terminalNodes = new Stack<>();
	List<String> endIf = new ArrayList<String>();
	private static final long serialVersionUID = 1L;
  
	public Convertor() 
    {
        super();
    }
    
	public boolean isToken(String str)
    {
    	if(syntax.contains(str))
    		return true;
    	else
    		return false;
    }
	public void generateBlock(String line)
	{
		solution = solution +Character.toString(boxId++) + "["+line+"]" + "-->";
	}
	public void generateInput(String line)
	{
		solution = solution +Character.toString(boxId++) + "[/"+line+"/]" + "-->";
	}
	public void generateOutput(String line)
	{
		solution = solution +Character.toString(boxId++) + "[/"+line+"/]" + "-->";
	}
	public void generateIf(String line)
	{
		ifStack.push(boxId);
		solution = solution +Character.toString(boxId++) + "{"+line+"}" + "--True-->";
	}
	public void generateWhile(String line)
	{
		whileStack.push(boxId);
		solution = solution +Character.toString(boxId++) + "{"+line+"}" + "--True-->";
	}
	public void generateEndwhile(String line)
	{
		int temp=whileStack.pop();
		solution = solution + Character.toString(temp);
		solution = solution +"\n"+Character.toString(temp)+ "--False-->";
	}

	public void generateElse(String line)
	{
		endIfStack.push(boxId);
		int temp=ifStack.pop();
		solution = solution.substring(0,solution.length()-3);
		terminalNodes.add((char)(boxId-1));
		solution = solution +"\n"+Character.toString(temp)+ "--False-->";
	}
	public void generateEndIf(String line)
	{
	
		{
			if(!ifStack.empty())
			{
				generateElse(line);
			}
			else
			{
				char temp = terminalNodes.pop();
				endIf.add("\n"+Character.toString(temp)+ "-->" + Character.toString(boxId) + "\n");
			}
		}
	}
	public boolean insideIf()
	{
		if(!ifStack.isEmpty())
			return true;
		else 
			return false;
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{	
		
		try
		{
			//Initializing the global variables
		
		boxId='A';
		solution="";
		ifStack.clear();
		whileStack.clear();
		terminalNodes.clear();
		endIfStack.clear();
		endIf.clear();
		
		//Get the string from the user as a String
		String code = request.getParameter("code");
		String tempCode = code;
		
		//Preprocessing the input
		code = code.replaceAll("\"", "\'");
		code = code.replaceAll(">", " Greater than ");
		code = code.replaceAll("<", "Lesser than ");
		
		//Array which splits the input linewise
		String[] lines = code.split("\r\n");
		
		
		Pattern inputPattern = Pattern.compile("^INPUT\s(.+)");
		Pattern outputPattern = Pattern.compile("^OUTPUT\s(.+)");
		Pattern ifPattern = Pattern.compile("^IF\\s(.+)\\sTHEN$");
		Pattern elsePattern = Pattern.compile("^ELSE$");
		Pattern	whilePattern = Pattern.compile("^WHILE\\s(.+)\\sTHEN$");
		Pattern endwhilePattern = Pattern.compile("^ENDWHILE$");
		Pattern endIfPattern = Pattern.compile("^ENDIF$");
		
		solution = solution + "graph TD\r\n "+Character.toString(boxId++)+"((BEGIN))"+"-->";
		for(String line : lines)
		{
			line = line.strip();
			if (line==null)
				continue;
			Matcher inputMatcher = inputPattern.matcher(line);
			Matcher outputMatcher = outputPattern.matcher(line);
			Matcher ifMatcher = ifPattern.matcher(line);
			Matcher elseMatcher = elsePattern.matcher(line);
			Matcher whileMatcher = whilePattern.matcher(line);
			Matcher endwhileMatcher = endwhilePattern.matcher(line);
			Matcher endIfMatcher = endIfPattern.matcher(line);
			if(inputMatcher.matches())
			{
				generateInput(line);
			}
			else if(outputMatcher.matches())
			{
				generateOutput(line);
			}
			else if(ifMatcher.matches())
			{
				generateIf(line);
			}
			else if(elseMatcher.matches())
			{
				generateElse(line);
			}
			else if(whileMatcher.matches())
			{
				line = line.replaceAll("WHILE", "IF");
				generateWhile(line);
			}
			else if(endwhileMatcher.matches())
			{
				generateEndwhile(line);
			}
			else if(endIfMatcher.matches())
			{
				generateEndIf(line);
			}
			else
			{
				generateBlock(line);
			}
			
		}

		if(!whileStack.empty())
		{
			char a = whileStack.pop();
			solution = solution + Character.toString(a);	
			for(char b:terminalNodes)
			{
				solution = solution + "\n" +  Character.toString(b) + "-->"+Character.toString(a);
			}
		}
		else
		{
			solution = solution + Character.toString(boxId) + "((END))";
			for(char a:terminalNodes)
			{
				solution = solution + "\n" +  Character.toString(a) + "-->"+Character.toString(boxId) + "((END))";
			}
			for(String a:endIf)
			{
				solution = solution + a;
			}
		}

		System.out.println(solution);
		request.setAttribute("solution",solution);
		request.setAttribute("tempCode",tempCode);
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}
		catch(Exception e)
		{
			System.out.print(e);
			request.getRequestDispatcher("index.jsp").forward(request, response);
		}
	}
}