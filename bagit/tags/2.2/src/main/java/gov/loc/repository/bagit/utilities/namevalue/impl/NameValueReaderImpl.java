package gov.loc.repository.bagit.utilities.namevalue.impl;

import gov.loc.repository.bagit.utilities.namevalue.NameValueReader;

import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NameValueReaderImpl implements NameValueReader {

	private static final Log log = LogFactory.getLog(NameValueReaderImpl.class);
	
	private Deque<String> lines = new ArrayDeque<String>();
	private String type;
	
	public NameValueReaderImpl(String encoding, InputStream in, String type) {
		this.type = type;
		try
		{
			// Replaced FileReader with InputStreamReader since all bagit manifest and metadata files must be UTF-8
			// encoded.  If UTF-8 is not explicitly set then data will be read in default native encoding.
			InputStreamReader fr = new InputStreamReader(in, encoding);
			BufferedReader reader = new BufferedReader(fr);
			String line = reader.readLine();
			while(line != null) {
				lines.addLast(line);
				line = reader.readLine();
			}
			reader.close();
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
		
	public boolean hasNext() {
		if (lines.isEmpty()) {
			return false;
		}
		return true;
	}	
		
	public NameValue next() {
		if (! this.hasNext()) {
			throw new NoSuchElementException();
		}
		//Split the first line
		String line = this.lines.removeFirst();
		String[] splitString = line.split(" *: *", 2);
		String name = splitString[0];
		String value = null;
		if (splitString.length == 2) {
			value = splitString[1].trim();
			while(! this.lines.isEmpty() && ! this.lines.getFirst().contains(":")) {
				value += " " + this.lines.removeFirst().replaceAll("^( |\\t)*", "");
			}			
		}
		NameValue ret = new NameValue(name, value);
		log.debug(MessageFormat.format("Read from {0}: {1}", this.type, ret.toString()));
		return ret;
		
	}
	
	public void remove() {
		throw new UnsupportedOperationException();		
	}

}
