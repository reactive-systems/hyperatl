package hyperatl;

import hyperatl.cgs.CGS;
import hyperatl.cgs.CGSShiftTransformer;
import hyperatl.cgs.CGSStutterTransformer;
import hyperatl.dpa.DPA;
import hyperatl.formula.HyperATLFormula;
import hyperatl.formula.parser.FormulaParser;
import hyperatl.pg.PG;
import hyperatl.pl.Program;
import hyperatl.pl.ProgramTransformer;
import hyperatl.pl.parser.PLParser;
import hyperatl.util.Stopwatch;
import jhoafparser.consumer.HOAConsumerException;
import jhoafparser.consumer.HOAConsumerStore;
import jhoafparser.parser.HOAFParser;
import jhoafparser.parser.generated.ParseException;
import jhoafparser.storage.StoredAutomaton;
import jhoafparser.transformations.ToExplicitLabels;
import jhoafparser.transformations.ToStateAcceptance;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Main {
	
	public static void main(String[] args) {

		String formulaFile = null;
		String programFile = null;
		String rabinizerPath = null;
		String outputPath = null;

		for (int i = 0; i < args.length; i++) {
			if (args[i].charAt(0) == '-') {
				if (args[i].length() < 2) {
					System.out.println("Not a valid argument: " + args[i]);
					System.exit(0);
				}

				if (args.length - 1 == i) {
					System.out.println("Not a valid argument: " + args[i]);
					System.exit(0);
				}

				String id = args[i].substring(1);
				String arg = args[i + 1];

				switch (id) {
					case "rab" -> rabinizerPath = arg;
					case "p" -> programFile = arg;
					case "f" -> formulaFile = arg;
					case "out" -> outputPath = arg;
					default -> {
						System.out.println("Unknown Command Line argument " + id);
						System.exit(0);
					}
				}
				i++;

			} else {
				System.out.println("No valid Command Line Args");
				System.exit(0);
			}
		}

		if (programFile == null || formulaFile == null) {
			System.out.println("You must specify both a program file and a formula file using -p and -f");
			System.exit(0);
		}

		if (rabinizerPath == null) {
			System.out.println("You have not specified a path to rabinizer 4. We look for rabinizer at the default path: /rabinizer/bin");
			rabinizerPath = "ltl2dpa";
		}

		if (outputPath == null) {
			System.out.println("You have not specified a output path. We use out.txt");
			outputPath = "out.txt";
		}

		// Stopwatch used to measure time taken
		Stopwatch sw = new Stopwatch();
		sw.start();
		
		// ############################################################
		// Read Formula to be Checked
		// Create an empty formula
		HyperATLFormula formula = null;
		
		try {
			// Create an input stream to the file containing the formula
			InputStream fInputStream = new FileInputStream(formulaFile);

			// Try to parse rge formula
			try {
				formula = FormulaParser.parsePL(fInputStream);
			} catch (hyperatl.formula.parser.generated.ParseException e) {
				System.out.println("The formula file could not be parsed");
				e.printStackTrace();
				System.exit(0);
			}
			
			
		} catch (FileNotFoundException e2) {
			System.out.println("The formula file could not be opened");
			e2.printStackTrace();
			System.exit(1);
		}
		// ############################################################

		System.out.println("The HyperATL formula has been parsed successfully");

		// ############################################################
		// Read Program

		Program program = null;
		
		try {
			InputStream pInputStream = new FileInputStream(programFile);
			
			// Try to parse the program
			try {
				program = PLParser.parsePL(pInputStream);
			} catch (hyperatl.pl.parser.generated.ParseException e) {
				System.out.println("The program could not be parsed");
				e.printStackTrace();
				System.exit(0);
			}
			
			
		} catch (FileNotFoundException e1) {
			System.out.println("The program file could not be opened");
			e1.printStackTrace();
			System.exit(1);
		}

		System.out.println("The Program formula has been parsed successfully. Starting to convert to CGS");

		// ############################################################
		
		// Convert program to CGS
		CGS cgs = ProgramTransformer.transformToCGS(program);
		
		System.out.println("Successfully converted to a CGS. CGS Size: " + cgs.getIndexToState().size());
		

		// #############################################################

		// Convert LTL Part to DPA using Rabinizer
		StringBuilder dpaString = new StringBuilder();
		
		try {
			Process process = new ProcessBuilder(rabinizerPath, formula.getLTLformula()).start();
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
		
			while ((line = br.readLine()) != null) {
				dpaString.append(line).append("\n");
			}
				
		
		} catch (IOException e2) {
			System.out.println("Translation via rabinizer not successful. Make sure that you have given the correct path via -rab");
			e2.printStackTrace();
			System.exit(1);
		}

		// #################################################

		// Convert the HOA string dpaString to a DPA. We use jhoafparser to parse the automaton
		DPA dpa;
		HOAConsumerStore factory = new HOAConsumerStore();
		// Convert string to bytearray
		InputStream input = new ByteArrayInputStream( dpaString.toString().getBytes() );

		try {
			HOAFParser.parseHOA(input, factory);
			
		} catch (ParseException e) {
			System.out.println("The output of rabinizer could not be parsed");
			e.printStackTrace();
			System.exit(1);
		}

		// Get automaton
		StoredAutomaton aut = factory.getStoredAutomaton();
		// Manipulate the Automaton to have state acceptance and explicit edges
		try {
			aut = new ToExplicitLabels(false).manipulate(aut);
			
			aut = new ToStateAcceptance().manipulate(aut);
		} catch (HOAConsumerException e) {
			e.printStackTrace();
		}
		// Convert the jhoaf-automaton to the internal DPA representation
		dpa = DPA.getDpaFromStoredAutomaton(aut);

		// Convert from (min, odd) to (max, even) as supported by pgsolver
		dpa.switchEvenOdd();
		dpa.switchMaxMin();

		System.out.println("Successfully converted to DPA using Rabinizer");


		// Init the list with the CGS for each path. This can be modified by changing the modifiers of the formula
		List<CGS> cgsList = new ArrayList<>(formula.getPathVariables().size());
		for(int i = 0; i < formula.getPathVariables().size(); i++) {
			switch (formula.getTransformations().get(i)) {
				case None ->
						// No modifier
						cgsList.add(cgs);
				case Shift ->
						// Shifter by 1
						cgsList.add(CGSShiftTransformer.createShiftedVersion(cgs, 1));
				case Stutter ->
						// Stuttered
						cgsList.add(CGSStutterTransformer.createStutterVersion(cgs));
			}
		}
		
		sw.stop();
		
		System.out.println("Setup Phase completed in " + sw.getElapsedTime() + " milliseconds");

		sw.start();

		System.out.println("Start to generate Parity game");
		
		try {
			// Create a new file to write to
			FileWriter out = new FileWriter(outputPath);

			// Write to this file
			// This method does all the heavy computation involved in constructing the product
			PG.writePG(out, cgsList, dpa, formula.getAgentSets(), formula.getPathVariables());
			
			out.close();
		} catch (IOException e1) {
			System.out.println("Error while writing to the output file");
			e1.printStackTrace();
			System.exit(1);
		}
		
		sw.stop();

		System.out.println("Successfully written parity game to " + outputPath);
		

		System.out.println("Task completed in " + sw.getElapsedTime() + " milliseconds");

	}
	
}
