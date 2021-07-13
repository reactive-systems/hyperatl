package hyperatl.formula.parser;

import hyperatl.formula.HyperATLFormula;
import hyperatl.formula.parser.generated.FormulaParserCC;
import hyperatl.formula.parser.generated.ParseException;

import java.io.InputStream;

public class FormulaParser {

	public static HyperATLFormula parsePL(InputStream str) throws ParseException {
		// Create an empty formula
		HyperATLFormula formula = new HyperATLFormula();

		// Run the parser, which builds the formula by adding paths
		FormulaParserCC.parseFormula(str, formula);
		return formula;
	}
}
