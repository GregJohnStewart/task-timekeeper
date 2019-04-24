package com.gjs.taskTimekeeper.desktopApp.runner;

import com.gjs.taskTimekeeper.desktopApp.runner.CommandLine.CmdLineArgumentRunner;
import com.gjs.taskTimekeeper.desktopApp.runner.CommandLine.DoExit;
import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Scanner;

public class ManagerRunner extends ModeRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(ManagerRunner.class);

	private Scanner scanner = new Scanner(System.in);

	public ManagerRunner(){
		//no need to do anything
	}

	public ManagerRunner(Scanner scanner){
		this.scanner = scanner;
	}

	public ManagerRunner(InputStream is){
		this.scanner = new Scanner(is);
	}

	@Override
	public void run() {
		LOGGER.info("Running the interactive command line manager mode.");

		String input;
		while(true){
			LOGGER.trace("Start of interactive loop.");
			input = scanner.nextLine();
			LOGGER.debug("Got the following from input stream: {}", input);

			try{
				new CmdLineArgumentRunner(false, input).run();
			}catch (DoExit e){
				break;
			} catch (CmdLineException e) {
				LOGGER.warn("Error thrown while processing arguments: ", e);
			}
		}
	}
}
