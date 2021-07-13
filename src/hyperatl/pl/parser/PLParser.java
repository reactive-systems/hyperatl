package hyperatl.pl.parser;

import hyperatl.pl.Program;
import hyperatl.pl.parser.generated.PLParserCC;
import hyperatl.pl.parser.generated.ParseException;

import java.io.InputStream;

public class PLParser {

	public static Program parsePL(InputStream str) throws ParseException {
		return PLParserCC.parsePL(str);
	}
}
