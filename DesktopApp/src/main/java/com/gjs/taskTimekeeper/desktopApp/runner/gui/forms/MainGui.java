package com.gjs.taskTimekeeper.desktopApp.runner.gui.forms;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.baseCode.core.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.baseCode.core.objects.Task;
import com.gjs.taskTimekeeper.baseCode.core.objects.Timespan;
import com.gjs.taskTimekeeper.baseCode.core.objects.WorkPeriod;
import com.gjs.taskTimekeeper.baseCode.core.timeParser.TimeParser;
import com.gjs.taskTimekeeper.baseCode.core.utils.Name;
import com.gjs.taskTimekeeper.baseCode.core.utils.ObjectMapperUtilities;
import com.gjs.taskTimekeeper.baseCode.core.utils.Outputter;
import com.gjs.taskTimekeeper.baseCode.managerIO.ManagerIO;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.DataSource;
import com.gjs.taskTimekeeper.baseCode.managerIO.dataSource.exception.DataSourceParsingException;
import com.gjs.taskTimekeeper.baseCode.stats.diagramming.PieCharter;
import com.gjs.taskTimekeeper.baseCode.stats.processor.OverallStatProcessor;
import com.gjs.taskTimekeeper.baseCode.stats.processor.TimeSpentOnTaskProcessor;
import com.gjs.taskTimekeeper.baseCode.stats.stats.OverallStats;
import com.gjs.taskTimekeeper.baseCode.stats.stats.PercentStats;
import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.DesktopAppConfiguration;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.editHelpers.AttributeEditor;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.editHelpers.SpanEditHelper;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.editHelpers.TaskEditHelper;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.options.GuiOptions;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.util.IndexAction;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.util.TableLayoutHelper;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.util.WrapLayout;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.util.listener.OpenDialogBoxOnClickListener;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.util.listener.OpenUrlOnClickListener;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gjs.taskTimekeeper.baseCode.core.crudAction.Action.ADD;
import static com.gjs.taskTimekeeper.baseCode.core.crudAction.Action.EDIT;
import static com.gjs.taskTimekeeper.baseCode.core.crudAction.Action.REMOVE;
import static com.gjs.taskTimekeeper.baseCode.core.crudAction.Action.VIEW;

/**
 * https://www.jetbrains.com/help/idea/designing-gui-major-steps.html
 *
 * <p>TODO:: test using: https://joel-costigliola.github.io/assertj/assertj-swing.html
 */
@Slf4j
public class MainGui {
	// <editor-fold desc="Static members">
	private static final String ABOUT_MESSAGE_FORMAT =
		"Task Timekeeper\n\nVersion: %s"
			+ "\nUsing Lib version: %s"
			+ "\n\nThis program is made for you to easily keep track of time spent on tasks."
			+ "\nFor help, please visit the Github for this project."
			+ "\nPlease consider donating if you find this program was helpful to you!";
	
	private static final double INDEX_COL_WIDTH = 35;
	private static final double DATETIME_COL_WIDTH = 140;
	private static final double DURATION_COL_WIDTH = 65;
	
	private static final List<String> PERIOD_LIST_TABLE_HEADERS =
		Arrays.asList("#", "Start", "End", "Duration", "Complete", "Actions");
	private static final Map<Integer, Double> PERIOD_LIST_COL_WIDTHS =
		new HashMap<Integer, Double>() {
			{
				put(0, INDEX_COL_WIDTH);
				put(1, DATETIME_COL_WIDTH);
				put(2, DATETIME_COL_WIDTH);
				put(3, DURATION_COL_WIDTH);
				put(4, (double)85);
			}
		};
	private static final List<String> TASK_LIST_TABLE_HEADERS =
		Arrays.asList("#", "Name", "Actions");
	private static final Map<Integer, Double> TASK_LIST_COL_WIDTHS =
		new HashMap<Integer, Double>() {
			{
				put(0, INDEX_COL_WIDTH);
				put(4, (double)123);
			}
		};
	private static final List<String> SPAN_LIST_TABLE_HEADERS =
		Arrays.asList("#", "Start", "End", "Duration", "Task", "Actions");
	private static final Map<Integer, Double> SPAN_LIST_COL_WIDTHS =
		new HashMap<Integer, Double>() {
			{
				put(0, INDEX_COL_WIDTH);
				put(1, DATETIME_COL_WIDTH);
				put(2, DATETIME_COL_WIDTH);
				put(3, DURATION_COL_WIDTH);
				put(5, (double)55);
			}
		};
	
	private static final TimeSpentOnTaskProcessor TIME_SPENT_ON_TASK_PROCESSOR =
		new TimeSpentOnTaskProcessor();
	private static final OverallStatProcessor OVERALL_STAT_PROCESSOR = new OverallStatProcessor();
	
	private static final int CHART_SIZE = 312;
	private static final int CHART_HEIGHT = CHART_SIZE;
	private static final int CHART_WIDTH = CHART_SIZE * 2;
	private static final Dimension OVERALL_STATS_DIMENSION = new Dimension(CHART_WIDTH, 155);
	private static final Border OVERALL_STATS_BORDER =
		new LineBorder(Color.BLACK, 1, true); // new BevelBorder(BevelBorder.RAISED);
	
	private static final PieCharter<Task> TIME_SPENT_ON_TASKS_CHARTER =
		new PieCharter<>("Time Spent on Tasks", CHART_HEIGHT, CHART_WIDTH);
	// names
	public static final String SELECTED_PERIOD_ATT_PANE_DATA_TABLE = "selectedPeriodAttPaneDataTable";
	public static final String SELECTED_PERIOD_TASK_STATS_DATA_TABLE = "selectedPeriodTaskStatsDataTable";
	public static final String SELECTED_PERIOD_SPANS_DATA_TABLE = "selectedPeriodSpansDataTable";
	public static final String PERIODS_TABLE_NAME = "periodsDataTable";
	public static final String TASKS_DATA_TABLE = "tasksDataTable";
	
	// </editor-fold>
	// <editor-fold desc="Static methods">
	private static String getFromPrintStreamForMessageOutput(ByteArrayOutputStream stream) {
		String output = stream.toString();
		log.debug("Length of string to output: {}", output.length());
		
		output = output.replace("\t", "    ");
		
		return output;
	}
	
	private static JPanel getOverallStatsPanel(OverallStats results) {
		JPanel overallStatStatPane = new JPanel();
		overallStatStatPane.setLayout(new BoxLayout(overallStatStatPane, BoxLayout.Y_AXIS));
		overallStatStatPane.setBorder(OVERALL_STATS_BORDER);
		
		overallStatStatPane.setSize(OVERALL_STATS_DIMENSION);
		overallStatStatPane.setPreferredSize(OVERALL_STATS_DIMENSION);
		overallStatStatPane.setMinimumSize(OVERALL_STATS_DIMENSION);
		overallStatStatPane.setMaximumSize(OVERALL_STATS_DIMENSION);
		
		JLabel label = new JLabel("Overall stats:");
		Font font = label.getFont();
		font = font.deriveFont(font.getStyle() | Font.BOLD);
		label.setFont(font);
		overallStatStatPane.add(label);
		
		font = font.deriveFont(font.getStyle() & ~Font.BOLD);
		
		label =
			new JLabel(
				"Total time recorded: "
					+ TimeParser.toDurationString(results.getTotalTime()));
		label.setFont(font);
		overallStatStatPane.add(label);
		
		if(results.getNumPeriods() > -1) {
			label = new JLabel("# Work periods: " + results.getNumPeriods());
			label.setFont(font);
			overallStatStatPane.add(label);
		}
		
		if(results.getNumTasksTotal() > -1) {
			label = new JLabel("# Tasks Total: " + results.getNumTasksTotal());
			label.setFont(font);
			overallStatStatPane.add(label);
		}
		
		label = new JLabel("# Tasks Used: " + results.getNumTasksUsed());
		label.setFont(font);
		overallStatStatPane.add(label);
		
		label = new JLabel("All complete: " + (results.isAllComplete() ? "Yes" : "No"));
		label.setFont(font);
		overallStatStatPane.add(label);
		
		label = new JLabel("# Spans Used: " + results.getNumSpans());
		label.setFont(font);
		overallStatStatPane.add(label);
		
		if(results.getNumTasksTotal() > -1) {
			label = new JLabel("# Spans per Work Period: " + results.getNumSpansPerPeriodAverage());
			label.setFont(font);
			overallStatStatPane.add(label);
		}
		
		label =
			new JLabel(
				"Average length of span: "
					+ TimeParser.toDurationString(results.getAverageSpanLength()));
		label.setFont(font);
		overallStatStatPane.add(label);
		
		if(results.getAverageWorkPeriodLength() != null) {
			label =
				new JLabel(
					"Average length of work period: "
						+ TimeParser.toDurationString(
						results.getAverageWorkPeriodLength()));
			label.setFont(font);
			overallStatStatPane.add(label);
		}
		
		return overallStatStatPane;
	}
	
	// </editor-fold>
	// <editor-fold desc="member variables">
	// admin stuff
	private DesktopAppConfiguration config;
	private ManagerIO managerIO;
	private GuiOptions options = null;
	private ByteArrayOutputStream printStream = new ByteArrayOutputStream();
	private ByteArrayOutputStream errorPrintStream = new ByteArrayOutputStream();
	private final String origTitle;
	private final String ABOUT_MESSAGE;
	
	// swing components
	private JFrame mainFrame;
	
	private JPanel mainPanel;
	private JTabbedPane mainTabPane;
	private JPanel selectedPeriodPanel;
	private JPanel periodsPanel;
	private JPanel tasksPanel;
	private JPanel selectedPeriodBannerPanel;
	private JPanel selectedPeriodSpansPanel;
	private JScrollPane periodsScrollPane;
	private JScrollPane tasksScrollPane;
	private JScrollPane selectedPeriodTaskStatsPane;
	private JButton addPeriodButton;
	private JButton addTaskButton;
	private JScrollPane selectedPeriodSpansPane;
	private JButton selectedPeriodAddSpanButton;
	private JScrollPane selectedPeriodAttsPane;
	private JLabel selectedPeriodStartLabel;
	private JLabel selectedPeriodEndLabel;
	private JLabel selectedPeriodDurationLabel;
	private JLabel selectedPeriodCompleteLabel;
	private JButton selectedPeriodEditAttributesButton;
	private JButton selectedPeriodCompleteAllSpansButton;
	private JPanel statsPanel;
	private JTabbedPane statsTabPane;
	private JScrollPane overallStatsPane;
	private JScrollPane selectedPeriodStatsPane;
	
	private JMenuBar mainMenuBar;
	private JCheckBoxMenuItem autoSaveMenuItem;
	private JCheckBoxMenuItem saveOnExitMenuItem;
	private JCheckBoxMenuItem selectNewPeriodMenuItem;
	
	public JFrame getMainFrame() {
		return this.mainFrame;
	}
	
	// </editor-fold>
	// <editor-fold desc="Actions/Handlers">
	private WindowListener windowListener =
		new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				log.info("Window closing event.");
				if(managerIO.isUnSaved(true)) {
					int chosen;
					if(!options.isSaveOnExit()) {
						log.info("Window closing with unsaved changes.");
						chosen =
							JOptionPane.showInternalConfirmDialog(
								mainPanel,
								"You have unsaved changes. Save before closing?",
								"Unsaved changes",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.WARNING_MESSAGE
							);
					} else {
						chosen = JOptionPane.YES_OPTION;
					}
					if(chosen == JOptionPane.YES_OPTION) {
						log.info("User chose to save the data.");
						saveData();
						mainFrame.dispose();
					} else if(chosen == JOptionPane.NO_OPTION) {
						log.info("User chose to not save the data.");
						mainFrame.dispose();
					} else {
						log.info("User cancelled the close action.");
					}
				} else {
					log.info("No changes to worry about.");
					mainFrame.dispose();
				}
			}
		};
	private Action closeAction =
		new AbstractAction("Close") {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
			}
		};
	private Action saveAction =
		new AbstractAction("Save") {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Save action performed.");
				saveData();
			}
		};
	private Action reloadAction =
		new AbstractAction("Reload") {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Reload action performed.");
				reloadData();
			}
		};
	private Action addTaskAction =
		new AbstractAction("Add Task") {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Add Task action hit.");
				TaskEditHelper helper = new TaskEditHelper();
				int result =
					JOptionPane.showInternalConfirmDialog(
						mainPanel,
						helper.getForm(),
						"New Task",
						JOptionPane.OK_CANCEL_OPTION
					);
				log.debug("Got the following new task name: \"{}\"", result);
				if(result != JOptionPane.OK_OPTION) {
					log.info("New task creation canceled.");
					return;
				}
				
				resetStreams();
				ActionConfig config = new ActionConfig(KeeperObjectType.TASK, ADD);
				config.setName(helper.getName());
				String atts = helper.getAttributes().trim();
				if(!atts.isEmpty()) {
					config.setAttributes(atts);
				}
				
				boolean addResult = managerIO.doCrudAction(config, false);
				log.debug("Result of trying to add task: {}", addResult);
				handleResult(addResult);
			}
		};
	
	private class ViewTaskAction extends IndexAction {
		public ViewTaskAction(int index) {
			super("View", index);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			log.info("Viewing task at index {}", this.getIndex());
			resetStreams();
			
			managerIO.doCrudAction(
				new ActionConfig(KeeperObjectType.TASK, VIEW).setIndex(this.getIndex()), false);
			
			if(errorPrintStream.size() != 0) {
				log.warn(
					"Some kind of error happened in trying to view the task at index {}",
					this.getIndex()
				);
				sendError();
			} else {
				JOptionPane.showInternalMessageDialog(
					mainPanel,
					getFromPrintStreamForMessageOutput(printStream),
					"Task View",
					JOptionPane.INFORMATION_MESSAGE
				);
			}
		}
	}
	
	private class EditTaskAction extends IndexAction {
		public EditTaskAction(int index) {
			super("Edit", index);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			log.info("Editing task at index {}", this.getIndex());
			resetStreams();
			
			Task task =
				(Task)
					managerIO
						.getManager()
						.getCrudOperator()
						.getTaskDoer()
						.search()
						.get(this.getIndex() - 1);
			
			TaskEditHelper helper = new TaskEditHelper();
			
			int response =
				JOptionPane.showConfirmDialog(
					mainPanel,
					helper.getForm(task),
					"Task Edit",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE
				);
			if(response == JOptionPane.OK_OPTION) {
				ActionConfig taskChangeConfig = new ActionConfig(KeeperObjectType.TASK, EDIT);
				taskChangeConfig.setName(task.getName().getName());
				
				if(!taskChangeConfig.getName().equals(helper.getName())) {
					taskChangeConfig.setNewName(helper.getName());
				}
				if(helper.getAttributes().trim().isEmpty()) {
					taskChangeConfig.setAttributes("EMPTY");
				} else {
					taskChangeConfig.setAttributes(helper.getAttributes());
				}
				
				boolean result = managerIO.doCrudAction(taskChangeConfig);
				
				log.debug("Result of trying to edit task: {}", result);
				handleResult(result);
			} else {
				log.info("Task editing form was cancelled.");
			}
		}
	}
	
	private class DeleteTaskAction extends IndexAction {
		public DeleteTaskAction(int index) {
			super("Delete", index);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			log.info("Deleting task at index {}", this.getIndex());
			
			int chosen = deleteConfirm("Are you sure you want to delete this task?");
			if(chosen != JOptionPane.YES_OPTION) {
				log.info("User chose to cancel deleting the task.");
				return;
			}
			
			resetStreams();
			
			boolean result =
				managerIO.doCrudAction(
					new ActionConfig(KeeperObjectType.TASK, REMOVE)
						.setIndex(this.getIndex()));
			log.debug("Result of trying to remove task: {}", result);
			handleResult(result);
		}
	}
	
	private Action addPeriodAction =
		new AbstractAction("Add Period") {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Add Period action hit.");
				
				resetStreams();
				ActionConfig config = new ActionConfig(KeeperObjectType.PERIOD, ADD);
				
				if(options.isSelectNewPeriods()) {
					config.setSelect(true);
				}
				
				boolean result = managerIO.doCrudAction(config);
				log.debug("Result of trying to add period: {}", result);
				handleResult(result);
			}
		};
	
	private class SelectPeriodAction extends IndexAction {
		public SelectPeriodAction(int index) {
			super("Select", index);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			log.info("Selecting period at index {}", this.getIndex());
			resetStreams();
			
			managerIO.doCrudAction(
				new ActionConfig(KeeperObjectType.PERIOD, VIEW)
					.setSelect(true)
					.setIndex(this.getIndex()));
			
			if(errorPrintStream.size() != 0) {
				log.warn(
					"Some kind of error happened in trying to view the task at index {}",
					this.getIndex()
				);
				sendError();
			}
			updateUiData();
		}
	}
	
	private class DeletePeriodAction extends IndexAction {
		public DeletePeriodAction(int index) {
			super("Delete", index);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			log.info("Deleting Period at index {}", this.getIndex());
			
			int chosen = deleteConfirm("Are you sure you want to delete this period?");
			if(chosen != JOptionPane.YES_OPTION) {
				log.info("User chose to cancel deleting the period.");
				return;
			}
			
			resetStreams();
			
			boolean result =
				managerIO.doCrudAction(
					new ActionConfig(KeeperObjectType.PERIOD, REMOVE)
						.setIndex(this.getIndex()));
			log.debug("Result of trying to remove period: {}", result);
			handleResult(result);
		}
	}
	
	private Action addSpanAction =
		new AbstractAction("Add Span") {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Add timespan action hit.");
				resetStreams();
				
				SpanEditHelper helper = new SpanEditHelper();
				
				int response =
					JOptionPane.showInternalConfirmDialog(
						mainPanel,
						helper.getForm(managerIO.getManager().getTasks()),
						"Timespan Add",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE
					);
				if(response == JOptionPane.OK_OPTION) {
					ActionConfig taskChangeConfig =
						new ActionConfig(KeeperObjectType.SPAN, ADD);
					
					taskChangeConfig.setName(helper.getTaskName());
					taskChangeConfig.setStart(helper.getStartField());
					taskChangeConfig.setEnd(helper.getEndField());
					
					boolean result = managerIO.doCrudAction(taskChangeConfig);
					
					log.debug("Result of trying to add span: {}", result);
					handleResult(result);
				} else {
					log.info("Span add form was cancelled.");
				}
			}
		};
	
	private Action completeSpansAction =
		new AbstractAction("Complete All") {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Add timespan action hit.");
				resetStreams();
				
				ActionConfig taskChangeConfig = new ActionConfig(KeeperObjectType.SPAN, VIEW);
				
				taskChangeConfig.setSpecialAction("completeSpans");
				
				boolean result = managerIO.doCrudAction(taskChangeConfig);
				
				log.debug("Result of trying to add span: {}", result);
				handleResult(result);
			}
		};
	
	private class EditSpanAction extends IndexAction {
		public EditSpanAction(int index) {
			super("E", index);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			log.info("Editing span from selected period at index {}", this.getIndex());
			resetStreams();
			
			Timespan span =
				(Timespan)
					managerIO
						.getManager()
						.getCrudOperator()
						.getTimespanDoer()
						.search()
						.get(this.getIndex() - 1);
			
			SpanEditHelper helper = new SpanEditHelper();
			
			int response =
				JOptionPane.showInternalConfirmDialog(
					mainPanel,
					helper.getForm(managerIO.getManager().getTasks(), span),
					"Timespan Edit",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE
				);
			if(response == JOptionPane.OK_OPTION) {
				ActionConfig taskChangeConfig = new ActionConfig(KeeperObjectType.SPAN, EDIT);
				
				taskChangeConfig.setIndex(this.getIndex());
				taskChangeConfig.setName(helper.getTaskName());
				taskChangeConfig.setStart(helper.getStartField());
				taskChangeConfig.setEnd(helper.getEndField());
				
				boolean result = managerIO.doCrudAction(taskChangeConfig);
				
				log.debug("Result of trying to edit span: {}", result);
				handleResult(result);
			} else {
				log.info("Span editing form was cancelled.");
			}
		}
	}
	
	private class CompleteSpanAction extends IndexAction {
		public CompleteSpanAction(int index) {
			super("C", index);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			log.info("Completing span from selected period at index {}", this.getIndex());
			resetStreams();
			
			Timespan span =
				(Timespan)
					managerIO
						.getManager()
						.getCrudOperator()
						.getTimespanDoer()
						.search()
						.get(this.getIndex() - 1);
			
			ActionConfig taskChangeConfig = new ActionConfig(KeeperObjectType.SPAN, EDIT);
			
			taskChangeConfig.setIndex(this.getIndex());
			taskChangeConfig.setName(span.getTaskName().getName());
			taskChangeConfig.setEnd("NOW");
			
			boolean result = managerIO.doCrudAction(taskChangeConfig);
			
			log.debug("Result of trying to complete span: {}", result);
			handleResult(result);
		}
	}
	
	private class DeleteSpanAction extends IndexAction {
		public DeleteSpanAction(int index) {
			super("D", index);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			log.info("Deleting span from selected period at index {}", this.getIndex());
			
			int chosen = deleteConfirm("Are you sure you want to delete this span?");
			if(chosen != JOptionPane.YES_OPTION) {
				log.info("User chose to cancel deleting the span.");
				return;
			}
			
			resetStreams();
			boolean result =
				managerIO.doCrudAction(
					new ActionConfig(KeeperObjectType.SPAN, REMOVE)
						.setIndex(this.getIndex()));
			log.debug("Result of trying to remove span: {}", result);
			handleResult(result);
		}
	}
	
	private Action editSelectedPeriodAttributesAction =
		new AbstractAction("Edit Attributes") {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.info("Editing attributes of selected period.");
				resetStreams();
				
				AttributeEditor helper = new AttributeEditor();
				
				int response =
					JOptionPane.showInternalConfirmDialog(
						mainPanel,
						helper.getForm(
							managerIO
								.getManager()
								.getCrudOperator()
								.getSelectedWorkPeriod()
								.getAttributes()),
						"Selected Period attribute Edit",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE
					);
				if(response == JOptionPane.OK_OPTION) {
					ActionConfig attributeChangeConfig =
						new ActionConfig(KeeperObjectType.PERIOD, EDIT);
					
					if(helper.getAttributes().trim().isEmpty()) {
						attributeChangeConfig.setAttributes("EMPTY");
					} else {
						attributeChangeConfig.setAttributes(helper.getAttributes());
					}
					
					log.debug(
						"New attributes of selected period: {}",
						attributeChangeConfig.getAttributes()
					);
					
					boolean result = managerIO.doCrudAction(attributeChangeConfig);
					
					log.debug(
						"Result of trying to edit selected period attributes: {}", result);
					handleResult(result);
				} else {
					log.info("Editing of selected period attribute form was cancelled.");
				}
			}
		};
	
	private class OptionChangedAction extends AbstractAction {
		public OptionChangedAction(String name) {
			super(name);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			updateUiOptions();
		}
	}
	// </editor-fold>
	
	// <editor-fold desc="constructor and public methods">
	{
		this.saveAction.putValue(
			Action.ACCELERATOR_KEY,
			KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)
		);
		this.reloadAction.putValue(
			Action.ACCELERATOR_KEY,
			KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK)
		);
	}
	
	public MainGui(DesktopAppConfiguration config, Image icon, String appTitle)
		throws DataSourceParsingException {
		log.debug("Starting construction of MainGui.");
		//naming wysiwyg components, for testing
		this.mainTabPane.setName("mainTabPane");
		this.selectedPeriodBannerPanel.setName("selectedPeriodBannerPanel");
		this.tasksScrollPane.setName("tasksScrollPane");
		
		this.config = config;
		this.managerIO =
			new ManagerIO(
				DataSource.fromString(this.config.getProperty(ConfigKeys.SAVE_FILE)),
				new Outputter(this.printStream, this.errorPrintStream)
			);
		this.ABOUT_MESSAGE =
			String.format(
				ABOUT_MESSAGE_FORMAT,
				this.config.getProperty(ConfigKeys.APP_VERSION),
				this.config.getProperty(ConfigKeys.LIB_VERSION)
			);
		log.info("Starting GUI.");
		this.origTitle = appTitle;
		
		// setup main frame
		log.debug("Setting up mainFrame.");
		this.mainFrame = new JFrame();
		this.mainFrame.setIconImage(icon);
		this.mainFrame.setContentPane(this.mainPanel);
		this.mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.mainFrame.addWindowListener(this.windowListener);
		log.debug("mainFrame set up");
		
		// setup menu bar
		log.debug("Setting up the Menu Bar.");
		this.mainMenuBar = new JMenuBar();
		// File
		JMenu menu = new JMenu("File");
		menu.setName("fileMenu");
		JMenuItem menuItem = new JMenuItem("Save");
		menuItem.setAction(this.saveAction);
		menu.add(menuItem);
		menuItem = new JMenuItem("Reload data");
		menuItem.setName("reloadDataMenuItem");
		menuItem.setAction(reloadAction);
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem("Close");
		menuItem.setAction(closeAction);
		menu.add(menuItem);
		this.mainMenuBar.add(menu);
		// options
		menu = new JMenu("Options");
		menu.setName("optionsMenu");
		this.autoSaveMenuItem = new JCheckBoxMenuItem();
		this.autoSaveMenuItem.setAction(new OptionChangedAction("Auto save"));
		menu.add(this.autoSaveMenuItem);
		this.saveOnExitMenuItem = new JCheckBoxMenuItem();
		this.saveOnExitMenuItem.setAction(new OptionChangedAction("Save on exit"));
		menu.add(this.saveOnExitMenuItem);
		menu.addSeparator();
		this.selectNewPeriodMenuItem = new JCheckBoxMenuItem();
		this.selectNewPeriodMenuItem.setAction(new OptionChangedAction("Select new periods"));
		menu.add(this.selectNewPeriodMenuItem);
		
		this.mainMenuBar.add(menu);
		// info
		menu = new JMenu("Info");
		menu.setName("infoMenu");
		menuItem = new JMenuItem("About");
		menuItem.setName("aboutMenuItem");
		menuItem.addMouseListener(
			new OpenDialogBoxOnClickListener(
				this.mainPanel,
				ABOUT_MESSAGE,
				"About Task Timekeeper",
				JOptionPane.INFORMATION_MESSAGE,
				icon
			)
		);
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem("Github");
		menuItem.addMouseListener(
			new OpenUrlOnClickListener(
				URI.create(this.config.getProperty(ConfigKeys.GITHUB_REPO_URL))));
		menu.add(menuItem);
		menuItem = new JMenuItem("Donate");
		menuItem.addMouseListener(
			new OpenUrlOnClickListener(
				URI.create(this.config.getProperty(ConfigKeys.DONATE_URL))));
		menu.add(menuItem);
		this.mainMenuBar.add(menu);
		
		this.mainFrame.setJMenuBar(this.mainMenuBar);
		log.debug("Menu bar set up.");
		
		log.debug("Reloading the data (from constructor)");
		this.reloadData();
		log.debug("Done reloading the data (from constructor)");
		
		// wire buttons
		log.debug("Wiring Buttons.");
		this.addTaskButton.setAction(this.addTaskAction);
		this.addPeriodButton.setAction(this.addPeriodAction);
		this.selectedPeriodEditAttributesButton.setAction(this.editSelectedPeriodAttributesAction);
		log.debug("Done Wiring Buttons.");
		
		log.debug("Loading UI Options (From Constructor).");
		this.loadUiOptions();
		log.debug("Done Loading UI Options (From Constructor).");
		
		log.debug("Packing");
		this.mainFrame.pack();
		log.debug("Packed.");
		log.debug("Setting Visible to true");
		this.mainFrame.setVisible(true);
		log.debug("Visibility set to true.");
		
		
		log.info("Opened window");
		log.debug("Constructor finished, Main GUI set up.");
	}
	
	public boolean stillOpen() {
		return this.mainFrame.isVisible();
	}
	
	public void close() {
		this.mainFrame.dispose();
	}
	// </editor-fold>
	
	// <editor-fold desc="Utility methods">
	private void resetStreams() {
		log.info("Resetting print streams.");
		try {
			this.printStream.flush();
			this.printStream.reset();
			this.errorPrintStream.flush();
			this.errorPrintStream.reset();
		} catch(IOException e) {
			log.error("Error flushing stream(s): ", e);
		}
	}
	
	private void sendError() {
		JOptionPane.showInternalMessageDialog(
			mainPanel,
			getFromPrintStreamForMessageOutput(errorPrintStream),
			"Error",
			JOptionPane.ERROR_MESSAGE
		);
	}
	
	private void sendErrorIfNeeded(boolean needed) {
		if(needed) {
			sendError();
		}
	}
	
	private int deleteConfirm(String message) {
		return JOptionPane.showInternalConfirmDialog(
			this.mainPanel,
			message,
			"Deletion Confirmation",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.WARNING_MESSAGE
		);
	}
	
	private void handleResult(boolean result) {
		log.trace("Handling the result ({})", result);
		sendErrorIfNeeded(!result);
		updateUiData();
	}
	
	private void loadUiOptions() {
		log.info("Reading in saved options from file: \"{}\".", this.config.getProperty(ConfigKeys.UI_OPTIONS_FILE));
		try(
			InputStream is =
				new FileInputStream(this.config.getProperty(ConfigKeys.UI_OPTIONS_FILE))
		) {
			this.options = ObjectMapperUtilities.getDefaultMapper().readValue(is, GuiOptions.class);
		} catch(MismatchedInputException e) {
			log.debug("Empty gui options file. Starting with new set of options.");
		} catch(IOException e) {
			log.error("FAILED to read in gui options data: ", e);
			
			// TODO:: breaks due to being too early? fix/ test
			//            JOptionPane.showInternalMessageDialog(
			//                    mainFrame,
			//                    "FAILED to read gui options in. Error: \n" + e.getMessage(),
			//                    "Error",
			//                    JOptionPane.WARNING_MESSAGE);
		}
		
		if(this.options == null) {
			log.info("No options could be read in. Initializing new ones.");
			this.options = new GuiOptions();
		}
		
		this.managerIO.setAutoSave(this.options.isAutoSave());
		this.autoSaveMenuItem.setSelected(this.managerIO.isAutoSave());
		this.saveOnExitMenuItem.setSelected(this.options.isSaveOnExit());
		this.selectNewPeriodMenuItem.setSelected(this.options.isSelectNewPeriods());
	}
	
	private void updateUiOptions() {
		this.options.setAutoSave(autoSaveMenuItem.isSelected());
		this.managerIO.setAutoSave(this.options.isAutoSave());
		this.options.setSaveOnExit(saveOnExitMenuItem.isSelected());
		this.options.setSelectNewPeriods(selectNewPeriodMenuItem.isSelected());
		
		log.trace("Writing out ui options data.");
		try(
			OutputStream os =
				new FileOutputStream(this.config.getProperty(ConfigKeys.UI_OPTIONS_FILE))
		) {
			ObjectMapperUtilities.getDefaultMapper().writeValue(os, this.options);
		} catch(IOException e) {
			log.error("FAILED to write changes to gui options file. Error: ", e);
			
			JOptionPane.showInternalMessageDialog(
				mainFrame,
				"FAILED to write gui options out. Change not saved. Error: \n" + e.getMessage(),
				"Error",
				JOptionPane.WARNING_MESSAGE
			);
		}
		log.trace("Done writing out ui options.");
	}
	// </editor-fold>
	
	// <editor-fold desc="Data Loading methods">
	private void reloadData() {
		log.info("Reloading data from source.");
		
		if(this.managerIO.isUnSaved(true)) {
			log.info("Reloading data with unsaved changes.");
			int chosen =
				JOptionPane.showInternalConfirmDialog(
					mainPanel,
					"You have unsaved changes. Are you sure you want to reload the data?",
					"Unsaved changes",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE
				);
			if(chosen == JOptionPane.YES_OPTION) {
				log.info("User chose to save the data.");
			} else {
				log.info("User chose to not save the data.");
				return;
			}
		} else {
			log.info("No changes to worry about.");
		}
		
		// read in new manager
		// TODO:: handle errors
		// TODO:: setup the manager properly
		this.managerIO.loadManager(true);
		
		this.managerIO
			.getManager()
			.getCrudOperator()
			.setOutputter(new Outputter(this.printStream, this.errorPrintStream));
		this.managerIO.getManager().getCrudOperator().setNewestPeriodAsSelectedQuiet();
		log.info("Done reloading data, updating UI.");
		this.updateUiData();
	}
	
	private void saveData() {
		log.info("Saving data from UI.");
		// TODO:: handle errors
		this.managerIO.saveManager();
		log.info("Data saved. Reloading data.");
		this.reloadData();
	}
	
	private void updateUiData() {
		log.info("Updating UI with current time manager data.");
		if(this.managerIO.isUnSaved(true)) {
			log.debug("Timemanager was changed, updating title.");
			this.mainFrame.setTitle(this.origTitle + " *");
		} else {
			this.mainFrame.setTitle(this.origTitle);
		}
		
		// <editor-fold desc="Selected Period Tab">
		log.info("Populating selected period tab.");
		{
			WorkPeriod selectedPeriod =
				this.managerIO.getManager().getCrudOperator().getSelectedWorkPeriod();
			
			if(selectedPeriod == null) {
				if(this.mainTabPane.getSelectedIndex() == 0) {
					this.mainTabPane.setSelectedIndex(1);
				}
				this.mainTabPane.setEnabledAt(0, false);
			} else {
				this.mainTabPane.setEnabledAt(0, true);
				// period details
				this.selectedPeriodStartLabel.setText(
					TimeParser.toOutputString(selectedPeriod.getStart()));
				this.selectedPeriodEndLabel.setText(
					TimeParser.toOutputString(selectedPeriod.getEnd()));
				this.selectedPeriodDurationLabel.setText(
					TimeParser.toDurationString(selectedPeriod.getTotalTime()));
				this.selectedPeriodCompleteLabel.setText(
					selectedPeriod.isUnCompleted() ? "No" : "Yes");
				{
					List<List<Object>> periodAtts =
						new ArrayList<>(selectedPeriod.getAttributes().size());
					
					for(Map.Entry<String, String> att :
						selectedPeriod.getAttributes().entrySet()) {
						List<Object> attRow = new ArrayList<>(2);
						
						attRow.add(att.getKey());
						attRow.add(att.getValue());
						
						periodAtts.add(attRow);
					}
					
					new TableLayoutHelper(
						SELECTED_PERIOD_ATT_PANE_DATA_TABLE,
						this.selectedPeriodAttsPane,
						periodAtts,
						Arrays.asList("Attribute", "Value")
					);
				}
				
				// task details
				{
					List<List<Object>> taskStats =
						new ArrayList<>(selectedPeriod.getTaskNames().size());
					
					for(Name taskName : selectedPeriod.getTaskNames()) {
						List<Object> taskDetailRow = new ArrayList<>(2);
						
						taskDetailRow.add(taskName.getName());
						taskDetailRow.add(
							TimeParser.toDurationString(
								selectedPeriod.getTotalTimeWith(taskName)));
						
						taskStats.add(taskDetailRow);
					}
					
					new TableLayoutHelper(
						SELECTED_PERIOD_TASK_STATS_DATA_TABLE,
						this.selectedPeriodTaskStatsPane,
						taskStats,
						Arrays.asList("Task Name", "Duration"),
						new HashMap<Integer, Double>() {
							{
								put(1, DURATION_COL_WIDTH);
							}
						}
					);
				}
				
				// span details
				{
					List<List<Object>> spanDetails =
						new ArrayList<>(selectedPeriod.getNumTimespans());
					
					int count = 1;
					for(Timespan span :
						managerIO.getManager().getCrudOperator().getTimespanDoer().search()) {
						List<Object> spanRow = new ArrayList<>(SPAN_LIST_TABLE_HEADERS.size());
						
						JButton edit = new JButton("E");
						edit.setName(SELECTED_PERIOD_SPANS_DATA_TABLE + "Edit" + count);
						edit.setAction(new EditSpanAction(count));
						edit.setToolTipText("Edit");
						
						JButton complete = new JButton("C");
						complete.setName(SELECTED_PERIOD_SPANS_DATA_TABLE + "Complete" + count);
						complete.setAction(new CompleteSpanAction(count));
						complete.setToolTipText("Complete");
						if(span.isComplete() || !span.hasStartTime()) {
							complete.setEnabled(false);
						}
						
						JButton delete = new JButton("D");
						delete.setName(SELECTED_PERIOD_SPANS_DATA_TABLE + "Delete" + count);
						delete.setAction(new DeleteSpanAction(count));
						delete.setToolTipText("Delete");
						
						spanRow.add(count);
						
						spanRow.add(TimeParser.toOutputString(span.getStartTime()));
						spanRow.add(TimeParser.toOutputString(span.getEndTime()));
						spanRow.add(TimeParser.toDurationString(span.getDuration()));
						spanRow.add(span.getTaskName().getName());
						spanRow.add(Arrays.asList(edit, complete, delete));
						
						spanDetails.add(spanRow);
						count++;
					}
					
					new TableLayoutHelper(
						SELECTED_PERIOD_SPANS_DATA_TABLE,
						this.selectedPeriodSpansPane,
						spanDetails,
						SPAN_LIST_TABLE_HEADERS,
						SPAN_LIST_COL_WIDTHS
					);
					this.selectedPeriodAddSpanButton.setAction(this.addSpanAction);
					this.selectedPeriodCompleteAllSpansButton.setAction(this.completeSpansAction);
				}
				this.selectedPeriodCompleteAllSpansButton.setEnabled(
					selectedPeriod.isUnCompleted());
			}
		}
		// </editor-fold>
		
		// <editor-fold desc="Periods tab">
		log.info("Populating periods tab.");
		{
			List<WorkPeriod> periods =
				this.managerIO.getManager().getCrudOperator().getWorkPeriodDoer().search();
			List<List<Object>> periodData = new ArrayList<>(periods.size());
			Map<Integer, Color> rowColors = new HashMap<>();
			
			int curInd = 1;
			for(WorkPeriod period : periods) {
				List<Object> row = new ArrayList<>(PERIOD_LIST_TABLE_HEADERS.size());
				
				JButton select = new JButton("S");
				select.setName(PERIODS_TABLE_NAME + "Select" + curInd);
				select.setAction(new SelectPeriodAction(curInd));
				JButton delete = new JButton("D");
				delete.setName(PERIODS_TABLE_NAME + "Delete" + curInd);
				delete.setAction(new DeletePeriodAction(curInd));
				
				if(this.managerIO.getManager().getCrudOperator().getSelectedWorkPeriod() != null
					&& this.managerIO
					.getManager()
					.getCrudOperator()
					.getSelectedWorkPeriod()
					.equals(period)
				) {
					rowColors.put(curInd, Color.CYAN);
					select.setEnabled(false);
				}
				
				row.add(curInd);
				row.add(TimeParser.toOutputString(period.getStart()));
				row.add(TimeParser.toOutputString(period.getEnd()));
				row.add(TimeParser.toDurationString(period.getTotalTime()));
				row.add((period.isUnCompleted() ? "No" : "Yes"));
				row.add(Arrays.asList(select, delete));
				
				periodData.add(row);
				curInd++;
			}
			
			new TableLayoutHelper(
				PERIODS_TABLE_NAME,
				this.periodsScrollPane,
				periodData,
				PERIOD_LIST_TABLE_HEADERS,
				PERIOD_LIST_COL_WIDTHS,
				rowColors
			);
		}
		// </editor-fold>
		
		// <editor-fold desc="Tasks tab">
		log.info("Populating tasks tab.");
		{
			List<Task> tasks = managerIO.getManager().getCrudOperator().getTaskDoer().search();
			List<List<Object>> periodData = new ArrayList<>(tasks.size());
			
			int curInd = 1;
			for(Task task : tasks) {
				List<Object> row = new ArrayList<>(TASK_LIST_TABLE_HEADERS.size());
				
				JButton viewButton = new JButton("v");
				viewButton.setName(TASKS_DATA_TABLE + "View" + curInd);
				viewButton.setAction(new ViewTaskAction(curInd));
				JButton editButton = new JButton("e");
				editButton.setName(TASKS_DATA_TABLE + "Edit" + curInd);
				editButton.setAction(new EditTaskAction(curInd));
				JButton deleteButton = new JButton("d");
				deleteButton.setName(TASKS_DATA_TABLE + "Delete" + curInd);
				deleteButton.setAction(new DeleteTaskAction(curInd));
				
				row.add(curInd);
				row.add(task.getName().getName());
				row.add(Arrays.asList(viewButton, editButton, deleteButton));
				
				periodData.add(row);
				curInd++;
			}
			
			new TableLayoutHelper(
				TASKS_DATA_TABLE,
				this.tasksScrollPane,
				periodData,
				TASK_LIST_TABLE_HEADERS,
				TASK_LIST_COL_WIDTHS
			);
		}
		// </editor-fold>
		
		// <editor-fold desc="Stats Panel">
		OverallStats overallResults = OVERALL_STAT_PROCESSOR.process(managerIO.getManager());
		// <editor-fold desc="Overall Stats Panel">
		{
			JPanel overallStatsPanel = new JPanel(new WrapLayout());
			
			overallStatsPanel.add(getOverallStatsPanel(overallResults));
			
			PercentStats<Task> results =
				TIME_SPENT_ON_TASK_PROCESSOR.process(this.managerIO.getManager());
			
			overallStatsPanel.add(
				new JLabel(new ImageIcon(TIME_SPENT_ON_TASKS_CHARTER.getChartImage(results))));
			//            overallStatsPanel.add(new JLabel(new
			// ImageIcon(TIME_SPENT_ON_TASKS_CHARTER.getChartImage(results))));
			//            overallStatsPanel.add(new JLabel(new
			// ImageIcon(TIME_SPENT_ON_TASKS_CHARTER.getChartImage(results))));
			
			this.overallStatsPane.setViewportView(overallStatsPanel);
		}
		// </editor-fold>
		// <editor-fold desc="Selected Period Stats Panel">
		if(this.managerIO.getManager().getCrudOperator().getSelectedWorkPeriod() != null) {
			this.statsTabPane.setEnabledAt(1, true);
			OverallStats selectedResults =
				OVERALL_STAT_PROCESSOR.process(
					this.managerIO.getManager(),
					this.managerIO.getManager().getCrudOperator().getSelectedWorkPeriod()
				);
			JPanel selectedPeriodStatsPanel = new JPanel(new WrapLayout());
			
			selectedPeriodStatsPanel.add(getOverallStatsPanel(selectedResults));
			
			PercentStats<Task> results =
				TIME_SPENT_ON_TASK_PROCESSOR.process(
					this.managerIO.getManager(),
					this.managerIO.getManager().getCrudOperator().getSelectedWorkPeriod()
				);
			
			selectedPeriodStatsPanel.add(
				new JLabel(new ImageIcon(TIME_SPENT_ON_TASKS_CHARTER.getChartImage(results))));
			//            overallStatsPanel.add(new JLabel(new
			// ImageIcon(TIME_SPENT_ON_TASKS_CHARTER.getChartImage(results))));
			//            overallStatsPanel.add(new JLabel(new
			// ImageIcon(TIME_SPENT_ON_TASKS_CHARTER.getChartImage(results))));
			
			this.selectedPeriodStatsPane.setViewportView(selectedPeriodStatsPanel);
		} else {
			this.statsTabPane.setEnabledAt(1, false);
		}
		// </editor-fold>
		
		// </editor-fold>
		log.info("Finished updating UI.");
	}
	// </editor-fold>
	
	{
		// GUI initializer generated by IntelliJ IDEA GUI Designer
		// >>> IMPORTANT!! <<<
		// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}
	
	/**
	 * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR
	 * call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainPanel.setMinimumSize(new Dimension(675, 500));
		mainPanel.setPreferredSize(new Dimension(675, 500));
		mainPanel.setBorder(
			BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null));
		mainTabPane = new JTabbedPane();
		mainPanel.add(
			mainTabPane,
			new GridConstraints(
				0,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null,
				new Dimension(200, 200),
				null,
				0,
				false
			)
		);
		selectedPeriodPanel = new JPanel();
		selectedPeriodPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainTabPane.addTab("Selected Period", selectedPeriodPanel);
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		selectedPeriodPanel.add(
			panel1,
			new GridConstraints(
				0,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				new Dimension(-1, 175),
				new Dimension(-1, 175),
				new Dimension(-1, 175),
				0,
				false
			)
		);
		selectedPeriodBannerPanel = new JPanel();
		selectedPeriodBannerPanel.setLayout(
			new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(
			selectedPeriodBannerPanel,
			new GridConstraints(
				0,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
					| GridConstraints.SIZEPOLICY_WANT_GROW,
				null,
				null,
				null,
				0,
				false
			)
		);
		selectedPeriodBannerPanel.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Period Details"));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(4, 4, new Insets(0, 0, 0, 0), -1, -1));
		selectedPeriodBannerPanel.add(
			panel2,
			new GridConstraints(
				0,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null,
				null,
				null,
				0,
				false
			)
		);
		final JLabel label1 = new JLabel();
		label1.setText("Start:");
		panel2.add(
			label1,
			new GridConstraints(
				0,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false
			)
		);
		final Spacer spacer1 = new Spacer();
		panel2.add(
			spacer1,
			new GridConstraints(
				0,
				2,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_WANT_GROW,
				1,
				null,
				null,
				null,
				0,
				false
			)
		);
		selectedPeriodStartLabel = new JLabel();
		selectedPeriodStartLabel.setText("<start datetime>");
		panel2.add(
			selectedPeriodStartLabel,
			new GridConstraints(
				0,
				1,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false
			)
		);
		final JLabel label2 = new JLabel();
		label2.setText("End:");
		panel2.add(
			label2,
			new GridConstraints(
				1,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false
			)
		);
		selectedPeriodEndLabel = new JLabel();
		selectedPeriodEndLabel.setText("<end datetime>");
		panel2.add(
			selectedPeriodEndLabel,
			new GridConstraints(
				1,
				1,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false
			)
		);
		final JLabel label3 = new JLabel();
		label3.setText("Duration:");
		panel2.add(
			label3,
			new GridConstraints(
				2,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false
			)
		);
		selectedPeriodDurationLabel = new JLabel();
		selectedPeriodDurationLabel.setText("<duration>");
		panel2.add(
			selectedPeriodDurationLabel,
			new GridConstraints(
				2,
				1,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false
			)
		);
		final JLabel label4 = new JLabel();
		label4.setText("Complete:");
		panel2.add(
			label4,
			new GridConstraints(
				3,
				0,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false
			)
		);
		selectedPeriodCompleteLabel = new JLabel();
		selectedPeriodCompleteLabel.setText("<complete?>");
		panel2.add(
			selectedPeriodCompleteLabel,
			new GridConstraints(
				3,
				1,
				1,
				1,
				GridConstraints.ANCHOR_WEST,
				GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false
			)
		);
		selectedPeriodEditAttributesButton = new JButton();
		selectedPeriodEditAttributesButton.setText("Edit Attributes");
		panel2.add(
			selectedPeriodEditAttributesButton,
			new GridConstraints(
				3,
				3,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false
			)
		);
		selectedPeriodAttsPane = new JScrollPane();
		selectedPeriodAttsPane.setVerticalScrollBarPolicy(22);
		selectedPeriodBannerPanel.add(
			selectedPeriodAttsPane,
			new GridConstraints(
				1,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
					| GridConstraints.SIZEPOLICY_WANT_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
					| GridConstraints.SIZEPOLICY_WANT_GROW,
				null,
				null,
				null,
				0,
				false
			)
		);
		selectedPeriodTaskStatsPane = new JScrollPane();
		selectedPeriodTaskStatsPane.setVerticalScrollBarPolicy(22);
		panel1.add(
			selectedPeriodTaskStatsPane,
			new GridConstraints(
				0,
				1,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_FIXED,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
					| GridConstraints.SIZEPOLICY_WANT_GROW,
				new Dimension(250, -1),
				new Dimension(250, -1),
				new Dimension(250, -1),
				0,
				false
			)
		);
		selectedPeriodTaskStatsPane.setBorder(
			BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Task Stats"));
		selectedPeriodSpansPanel = new JPanel();
		selectedPeriodSpansPanel.setLayout(
			new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		selectedPeriodPanel.add(
			selectedPeriodSpansPanel,
			new GridConstraints(
				1,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null,
				null,
				null,
				0,
				false
			)
		);
		selectedPeriodSpansPanel.setBorder(
			BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Spans"));
		selectedPeriodSpansPane = new JScrollPane();
		selectedPeriodSpansPane.setVerticalScrollBarPolicy(22);
		selectedPeriodSpansPanel.add(
			selectedPeriodSpansPane,
			new GridConstraints(
				1,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
					| GridConstraints.SIZEPOLICY_WANT_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
					| GridConstraints.SIZEPOLICY_WANT_GROW,
				null,
				null,
				null,
				0,
				false
			)
		);
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		selectedPeriodSpansPanel.add(
			panel3,
			new GridConstraints(
				0,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null,
				null,
				null,
				0,
				false
			)
		);
		selectedPeriodAddSpanButton = new JButton();
		selectedPeriodAddSpanButton.setText("Add Span");
		panel3.add(
			selectedPeriodAddSpanButton,
			new GridConstraints(
				0,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false
			)
		);
		selectedPeriodCompleteAllSpansButton = new JButton();
		selectedPeriodCompleteAllSpansButton.setText("Complete All");
		panel3.add(
			selectedPeriodCompleteAllSpansButton,
			new GridConstraints(
				0,
				1,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false
			)
		);
		periodsPanel = new JPanel();
		periodsPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainTabPane.addTab("Periods", periodsPanel);
		periodsScrollPane = new JScrollPane();
		periodsScrollPane.setVerticalScrollBarPolicy(22);
		periodsPanel.add(
			periodsScrollPane,
			new GridConstraints(
				1,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
					| GridConstraints.SIZEPOLICY_WANT_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
					| GridConstraints.SIZEPOLICY_WANT_GROW,
				null,
				null,
				null,
				0,
				false
			)
		);
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		periodsPanel.add(
			panel4,
			new GridConstraints(
				0,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null,
				null,
				null,
				0,
				false
			)
		);
		addPeriodButton = new JButton();
		addPeriodButton.setText("Add Period");
		panel4.add(
			addPeriodButton,
			new GridConstraints(
				0,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false
			)
		);
		tasksPanel = new JPanel();
		tasksPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainTabPane.addTab("Tasks", tasksPanel);
		tasksScrollPane = new JScrollPane();
		tasksScrollPane.setVerticalScrollBarPolicy(22);
		tasksPanel.add(
			tasksScrollPane,
			new GridConstraints(
				1,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
					| GridConstraints.SIZEPOLICY_WANT_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK
					| GridConstraints.SIZEPOLICY_WANT_GROW,
				null,
				null,
				null,
				0,
				false
			)
		);
		final JPanel panel5 = new JPanel();
		panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		tasksPanel.add(
			panel5,
			new GridConstraints(
				0,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null,
				null,
				null,
				0,
				false
			)
		);
		addTaskButton = new JButton();
		addTaskButton.setText("Add Task");
		panel5.add(
			addTaskButton,
			new GridConstraints(
				0,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_HORIZONTAL,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_FIXED,
				null,
				null,
				null,
				0,
				false
			)
		);
		statsPanel = new JPanel();
		statsPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainTabPane.addTab("Stats", statsPanel);
		statsTabPane = new JTabbedPane();
		statsPanel.add(
			statsTabPane,
			new GridConstraints(
				0,
				0,
				1,
				1,
				GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_BOTH,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null,
				new Dimension(200, 200),
				null,
				0,
				false
			)
		);
		overallStatsPane = new JScrollPane();
		overallStatsPane.setHorizontalScrollBarPolicy(31);
		overallStatsPane.setVerticalScrollBarPolicy(22);
		statsTabPane.addTab("Overall", overallStatsPane);
		selectedPeriodStatsPane = new JScrollPane();
		statsTabPane.addTab("Selected Period", selectedPeriodStatsPane);
	}
	
	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return mainPanel;
	}
}
