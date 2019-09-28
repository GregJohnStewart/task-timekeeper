package com.gjs.taskTimekeeper.desktopApp.runner.gui.forms;

import com.gjs.taskTimekeeper.backend.Task;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.Timespan;
import com.gjs.taskTimekeeper.backend.WorkPeriod;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.backend.crudAction.actionDoer.ActionDoer;
import com.gjs.taskTimekeeper.backend.crudAction.actionDoer.TaskDoer;
import com.gjs.taskTimekeeper.backend.crudAction.actionDoer.TimespanDoer;
import com.gjs.taskTimekeeper.backend.crudAction.actionDoer.WorkPeriodDoer;
import com.gjs.taskTimekeeper.backend.timeParser.TimeParser;
import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.Configuration;
import com.gjs.taskTimekeeper.desktopApp.managerIO.ManagerIO;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.util.Utils;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.util.listener.OpenDialogBoxOnClickListener;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.util.listener.OpenUrlOnClickListener;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.util.table.ButtonsCell;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.util.table.UnEditableTableModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * https://www.jetbrains.com/help/idea/designing-gui-major-steps.html
 */
public class MainGui {
	private static final Logger LOGGER = LoggerFactory.getLogger(MainGui.class);

	private static final String ABOUT_MESSAGE = "Task Timekeeper\n\nVersion: " + Configuration.getProperty(ConfigKeys.APP_VERSION, String.class) +
		                                            "\nUsing Lib version: " + Configuration.getProperty(ConfigKeys.LIB_VERSION, String.class) +
		                                            "\n\nThis program is made for you to easily keep track of time spent on tasks." +
		                                            "\nFor help, please visit the Github for this project." +
		                                            "\nPlease consider donating if you find this program was helpful to you!";

	private static final boolean AUTO_SAVE_DEFAULT = false;
	private static final int INDEX_COL_WIDTH = 35;
	private static final int DATETIME_COL_WIDTH = 130;
	private static final int DURATION_COL_WIDTH = 65;
	private static final DefaultTableCellRenderer CENTER_CELL_RENDERER = new DefaultTableCellRenderer();

	private static final String[] PERIOD_LIST_TABLE_HEADERS = new String[]{"#", "Start", "End", "Duration", "Complete", "Actions"};
	private static final String[] TASK_LIST_TABLE_HEADERS = new String[]{"#", "Name", "Actions"};

	static {
		CENTER_CELL_RENDERER.setHorizontalAlignment(JLabel.CENTER);
	}

	//<editor-fold desc="member variables">
	//admin stuff
	private TimeManager manager;
	private boolean changed = false;
	private ByteArrayOutputStream printStream = new ByteArrayOutputStream();
	private ByteArrayOutputStream errorPrintStream = new ByteArrayOutputStream();
	private final String origTitle;

	//swing components
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
	private JButton selectedPeriodAddAttributeButton;

	private JMenuBar mainMenuBar;
	private JCheckBoxMenuItem autoSaveMenuItem;


	//<editor-fold desc="Actions/Handlers">
	private WindowListener windowListener = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			super.windowClosing(e);
			LOGGER.info("Window closing event.");
			if (changed) {
				LOGGER.info("Window closing with unsaved changes.");
				int chosen = JOptionPane.showInternalConfirmDialog(
					mainPanel,
					"You have unsaved changes. Save before closing?",
					"Unsaved changes",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE
				);
				if (chosen == JOptionPane.YES_OPTION) {
					LOGGER.info("User chose to save the data.");
					saveData();
					mainFrame.dispose();
				} else if (chosen == JOptionPane.NO_OPTION) {
					LOGGER.info("User chose to not save the data.");
					mainFrame.dispose();
				} else {
					LOGGER.info("User cancelled the close action.");
				}
			} else {
				LOGGER.info("No changes to worry about.");
				mainFrame.dispose();
			}
		}
	};
	private Action closeAction = new AbstractAction("Close") {
		@Override
		public void actionPerformed(ActionEvent e) {
			mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
		}
	};
	private Action saveAction = new AbstractAction("Save") {
		@Override
		public void actionPerformed(ActionEvent e) {
			LOGGER.info("Save action performed.");
			saveData();
		}
	};
	private Action reloadAction = new AbstractAction("Reload") {
		@Override
		public void actionPerformed(ActionEvent e) {
			LOGGER.info("Reload action performed.");
			reloadData();
		}
	};
	private Action addTaskAction = new AbstractAction("Add Task") {
		@Override
		public void actionPerformed(ActionEvent e) {
			LOGGER.info("Add Task action hit.");
			String newTaskName = JOptionPane.showInternalInputDialog(
				mainPanel,
				"Enter new task Name",
				"New Task",
				JOptionPane.QUESTION_MESSAGE
			);
			LOGGER.debug("Got the following new task name: \"{}\"", newTaskName);
			if (newTaskName == null) {
				LOGGER.info("New task creation canceled.");
				return;
			}

			resetStreams();
			ActionConfig config = new ActionConfig(KeeperObjectType.TASK, com.gjs.taskTimekeeper.backend.crudAction.Action.ADD);
			config.setName(newTaskName);

			boolean result = ActionDoer.doObjAction(manager, config);
			LOGGER.debug("Result of trying to add task: {}", result);
			wasUpdated(result);

			sendErrorIfNeeded(!result);
		}
	};
	Action editTaskAction = new AbstractAction("Edit") {
		public void actionPerformed(ActionEvent e) {
			LOGGER.info("Editing task in row");
		}
	};
	Action deleteTaskAction = new AbstractAction("Delete") {
		public void actionPerformed(ActionEvent e) {
			int chosen = JOptionPane.showInternalConfirmDialog(
				mainPanel,
				"Are you sure you want to delete this task?",
				"Deletion Confirmation",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE
			);
			if (chosen != JOptionPane.YES_OPTION) {
				LOGGER.info("User chose to cancel deleting the task.");
				return;
			}

			JTable table = (JTable)e.getSource();
			int modelRow = Integer.valueOf( e.getActionCommand() );

			LOGGER.info("Deleting task in row # {}", modelRow);
		}
	};
	private Action addPeriodAction = new AbstractAction("Add Period") {
		@Override
		public void actionPerformed(ActionEvent e) {
			LOGGER.info("Add Period action hit.");

			resetStreams();
			ActionConfig config = new ActionConfig(KeeperObjectType.PERIOD, com.gjs.taskTimekeeper.backend.crudAction.Action.ADD);

			boolean result = ActionDoer.doObjAction(manager, config);
			LOGGER.debug("Result of trying to add task: {}", result);
			wasUpdated(result);

			sendErrorIfNeeded(!result);
		}
	};
	//</editor-fold>
	//</editor-fold>

	//<editor-fold desc="constructor and public methods">
	{
		ActionDoer.setMessageOutputStream(new PrintStream(this.printStream));
		ActionDoer.setMessageErrorStream(new PrintStream(this.errorPrintStream));

		this.saveAction.putValue(Action.ACCELERATOR_KEY,
			KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		this.reloadAction.putValue(Action.ACCELERATOR_KEY,
			KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
	}

	public MainGui(Image icon, String appTitle) {
		LOGGER.info("Starting GUI.");
		this.origTitle = appTitle;

		//setup main frame
		this.mainFrame = new JFrame();
		this.mainFrame.setIconImage(icon);
		this.mainFrame.setContentPane(this.mainPanel);
		this.mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.mainFrame.addWindowListener(this.windowListener);

		//setup menu bar
		this.mainMenuBar = new JMenuBar();
		//File
		JMenu menu = new JMenu("File");
		JMenuItem menuItem = new JMenuItem("Save");
		menuItem.setAction(this.saveAction);
		menu.add(menuItem);
		menuItem = new JMenuItem("Reload data");
		menuItem.setAction(reloadAction);
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem("Close");
		menuItem.setAction(closeAction);
		menu.add(menuItem);
		this.mainMenuBar.add(menu);
		//options
		menu = new JMenu("Options");
		this.autoSaveMenuItem = new JCheckBoxMenuItem("Auto save", AUTO_SAVE_DEFAULT);
		menu.add(autoSaveMenuItem);
		this.autoSaveMenuItem = new JCheckBoxMenuItem("Save on exit", AUTO_SAVE_DEFAULT);
		menu.add(autoSaveMenuItem);
		this.mainMenuBar.add(menu);
		//info
		menu = new JMenu("Info");
		menuItem = new JMenuItem("About");
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
				URI.create(Configuration.getProperty(ConfigKeys.GITHUB_REPO_URL, String.class))
			)
		);
		menu.add(menuItem);
		menuItem = new JMenuItem("Donate");
		menuItem.addMouseListener(
			new OpenUrlOnClickListener(
				URI.create(Configuration.getProperty(ConfigKeys.DONATE_URL, String.class))
			)
		);
		menu.add(menuItem);
		this.mainMenuBar.add(menu);

		this.mainFrame.setJMenuBar(this.mainMenuBar);
		this.reloadData();

		this.mainFrame.pack();
		this.mainFrame.setVisible(true);

		//wire buttons
		this.addTaskButton.setAction(this.addTaskAction);
		this.addPeriodButton.setAction(this.addPeriodAction);

		LOGGER.info("Opened window");
	}

	public boolean stillOpen() {
		return this.mainFrame.isVisible();
	}
	//<editor-fold>

	//<editor-fold desc="Utility methods">
	private void resetStreams() {
		try {
			this.printStream.flush();
			this.printStream.reset();
			this.errorPrintStream.flush();
			this.errorPrintStream.reset();
		} catch (IOException e) {
			LOGGER.error("Error flushing stream(s): ", e);
		}
	}
	private void sendError(){
		JOptionPane.showInternalMessageDialog(
			mainPanel,
			new String(errorPrintStream.toByteArray()),
			"Error",
			JOptionPane.ERROR_MESSAGE
		);
	}
	private void sendErrorIfNeeded(boolean needed){
		if(needed){
			sendError();
		}
	}
	//<editor-fold>

	//<editor-fold desc="Data Loading methods">
	private void reloadData() {
		LOGGER.info("Reloading data from source.");
		//read in new manager
		//TODO:: handle errors
		this.manager = ManagerIO.loadTimeManager();
		this.changed = false;

		ActionDoer.resetDoers();
		ActionDoer.setNewestPeriodAsSelectedQuiet(this.manager);
		LOGGER.info("Done reloading data, updating UI.");
		this.updateUiData();
	}

	private void wasUpdated(boolean wasUpdated) {
		this.changed = this.changed || wasUpdated;
		if (this.autoSaveMenuItem.getState()) {
			this.saveData();
		} else {
			this.updateUiData();
		}
	}

	private void saveData() {
		LOGGER.info("Saving data from UI.");
		//TODO:: handle errors
		ManagerIO.saveTimeManager(this.manager);
		LOGGER.info("Data saved. Reloading data.");
		this.changed = false;
		this.reloadData();
	}

	private void updateUiData() {
		LOGGER.info("Updating UI with current time manager data.");
		if (this.changed) {
			LOGGER.debug("Timemanager was changed, updating title.");
			this.mainFrame.setTitle(this.origTitle + " *");
		} else {
			this.mainFrame.setTitle(this.origTitle);
		}

		//<editor-fold desc="Selected Period Tab">
		LOGGER.info("Populating selected period tab.");
		{
			WorkPeriod selectedPeriod = ActionDoer.getSelectedWorkPeriod();

			if (selectedPeriod == null) {
				if(this.mainTabPane.getSelectedIndex() == 0) {
					this.mainTabPane.setSelectedIndex(1);
				}
				this.mainTabPane.setEnabledAt(0, false);
			} else {
				this.mainTabPane.setEnabledAt(0, true);
				// period details
				this.selectedPeriodStartLabel.setText(TimeParser.toOutputString(selectedPeriod.getStart()));
				this.selectedPeriodEndLabel.setText(TimeParser.toOutputString(selectedPeriod.getEnd()));
				this.selectedPeriodDurationLabel.setText(TimeParser.toDurationString(selectedPeriod.getTotalTime()));
				this.selectedPeriodCompleteLabel.setText(selectedPeriod.isUnCompleted() ? "No" : "Yes");
				{
					Object[][] periodAtts = new Object[selectedPeriod.getAttributes()
						                                   .size()][];

					int count = 0;
					for (Map.Entry<String, String> att : selectedPeriod.getAttributes()
						                                     .entrySet()) {
						periodAtts[count] = new Object[3];

						periodAtts[count][0] = att.getKey();
						periodAtts[count][1] = att.getValue();
						periodAtts[count][2] = new JButton("action1");

						count++;
					}

					JTable attsTable = new JTable(new UnEditableTableModel(periodAtts, new String[]{"Attribute", "Value", "Action"}));
					this.selectedPeriodAttsPane.setViewportView(attsTable);
				}

				//task details
				{
					Object[][] taskStats = new Object[selectedPeriod.getTasks()
						                                  .size()][];

					int count = 0;
					for (Task task : selectedPeriod.getTasks()) {
						taskStats[count] = new Object[2];

						taskStats[count][0] = task.getName();
						taskStats[count][1] = TimeParser.toDurationString(selectedPeriod.getTotalTimeWith(task));

						count++;
					}

					JTable taskStatsTable = new JTable(new UnEditableTableModel(taskStats, new String[]{"Task Name", "Duration"}));
					taskStatsTable.getColumnModel()
						.getColumn(1)
						.setCellRenderer(CENTER_CELL_RENDERER);
					Utils.setColWidth(taskStatsTable, 1, DURATION_COL_WIDTH);
					this.selectedPeriodTaskStatsPane.setViewportView(taskStatsTable);
				}

				//span details
				{
					Object[][] spanDetails = new Object[selectedPeriod.getNumTimespans()][];

					int count = 0;
					for (Timespan span : ((TimespanDoer) ActionDoer.getActionDoer(KeeperObjectType.SPAN)).search(manager, new ActionConfig())) {
						spanDetails[count] = new Object[6];

						spanDetails[count][0] = count + 1;
						spanDetails[count][1] = TimeParser.toOutputString(span.getStartTime());
						spanDetails[count][2] = TimeParser.toOutputString(span.getEndTime());
						spanDetails[count][3] = TimeParser.toDurationString(span.getDuration());
						spanDetails[count][4] = span.getTask()
							                        .getName();
						spanDetails[count][5] = new JButton("button 1");

						count++;
					}
					JTable taskStatsTable = new JTable(new UnEditableTableModel(spanDetails, new String[]{"#", "Start", "End", "Duration", "Task", "Actions"}));
					taskStatsTable.getColumnModel()
						.getColumn(0)
						.setCellRenderer(CENTER_CELL_RENDERER);
					Utils.setColWidth(taskStatsTable, 0, INDEX_COL_WIDTH);
					taskStatsTable.getColumnModel()
						.getColumn(1)
						.setCellRenderer(CENTER_CELL_RENDERER);
					Utils.setColWidth(taskStatsTable, 1, DATETIME_COL_WIDTH);
					taskStatsTable.getColumnModel()
						.getColumn(2)
						.setCellRenderer(CENTER_CELL_RENDERER);
					Utils.setColWidth(taskStatsTable, 2, DATETIME_COL_WIDTH);
					taskStatsTable.getColumnModel()
						.getColumn(3)
						.setCellRenderer(CENTER_CELL_RENDERER);
					Utils.setColWidth(taskStatsTable, 3, DURATION_COL_WIDTH);
					this.selectedPeriodSpansPane.setViewportView(taskStatsTable);
				}
			}
		}
		//</editor-fold>

		//<editor-fold desc="Periods tab">
		LOGGER.info("Populating periods tab.");
		//TODO:: highlight selected period row
		{
			List<WorkPeriod> periods = new WorkPeriodDoer().search(this.manager, new ActionConfig());
			Object[][] periodData = new Object[periods.size()][];

			int curInd = 0;
			for (WorkPeriod period : periods) {
				periodData[curInd] = new Object[PERIOD_LIST_TABLE_HEADERS.length];

				periodData[curInd][0] = curInd + 1;
				periodData[curInd][1] = TimeParser.toOutputString(period.getStart());
				periodData[curInd][2] = TimeParser.toOutputString(period.getEnd());
				periodData[curInd][3] = TimeParser.toDurationString(period.getTotalTime());
				periodData[curInd][4] = (period.isUnCompleted() ? "No" : "Yes");
				periodData[curInd][5] = new JButton("action1");//TODO:: figure out these buttons in a table cell.
				curInd++;
			}

			JTable periodListTable = new JTable(new UnEditableTableModel(periodData, PERIOD_LIST_TABLE_HEADERS));
			periodListTable.getColumnModel()
				.getColumn(0)
				.setCellRenderer(CENTER_CELL_RENDERER);
			Utils.setColWidth(periodListTable, 0, INDEX_COL_WIDTH);
			periodListTable.getColumnModel()
				.getColumn(1)
				.setCellRenderer(CENTER_CELL_RENDERER);
			Utils.setColWidth(periodListTable, 1, DATETIME_COL_WIDTH);
			periodListTable.getColumnModel()
				.getColumn(2)
				.setCellRenderer(CENTER_CELL_RENDERER);
			Utils.setColWidth(periodListTable, 2, DATETIME_COL_WIDTH);
			periodListTable.getColumnModel()
				.getColumn(3)
				.setCellRenderer(CENTER_CELL_RENDERER);
			Utils.setColWidth(periodListTable, 3, DURATION_COL_WIDTH);
			periodListTable.getColumnModel()
				.getColumn(4)
				.setCellRenderer(CENTER_CELL_RENDERER);
			Utils.setColWidth(periodListTable, 4, 85);
			this.periodsScrollPane.setViewportView(periodListTable);
		}
		//</editor-fold>

		//<editor-fold desc="Tasks tab">
		LOGGER.info("Populating tasks tab.");
		//TODO:: rework to use gridlayout to achieve table-like visuals with buttons that function.
		{
			List<Task> tasks = new TaskDoer().search(this.manager, new ActionConfig());
			Object[][] periodData = new Object[tasks.size()][];

			int curInd = 0;
			for (Task task : tasks) {
				periodData[curInd] = new Object[TASK_LIST_TABLE_HEADERS.length];

				JButton editButton = new JButton("e");
				editButton.setAction(editTaskAction);
				JButton deleteButton = new JButton("d");
				deleteButton.setAction(deleteTaskAction);

				periodData[curInd][0] = curInd + 1;
				periodData[curInd][1] = task.getName();
				periodData[curInd][2] = List.of(editButton, deleteButton);//new JButton("action1");//TODO:: figure out these buttons in a table cell.
				curInd++;
			}

			JTable tabListTable = new JTable(new UnEditableTableModel(periodData, TASK_LIST_TABLE_HEADERS));
			tabListTable.getColumnModel()
				.getColumn(0)
				.setCellRenderer(CENTER_CELL_RENDERER);
			Utils.setColWidth(tabListTable, 0, INDEX_COL_WIDTH);

			tabListTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonsCell());


			//ButtonColumn bcTwo = new ButtonColumn(tabListTable, editTaskAction, 2);
			//ButtonColumn bc = new ButtonColumn(tabListTable, deleteTaskAction, 3);
			//JTable table = (JTable)e.getSource();
			//int modelRow = Integer.valueOf( e.getActionCommand() );


			this.tasksScrollPane.setViewportView(tabListTable);
		}
		//</editor-fold>

		//TODO:: rest of this
	}
	//</editor-fold>


	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainPanel.setMinimumSize(new Dimension(675, 500));
		mainPanel.setPreferredSize(new Dimension(675, 500));
		mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null));
		mainTabPane = new JTabbedPane();
		mainPanel.add(mainTabPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
		selectedPeriodPanel = new JPanel();
		selectedPeriodPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainTabPane.addTab("Selected Period", selectedPeriodPanel);
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		selectedPeriodPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 175), new Dimension(-1, 175), new Dimension(-1, 175), 0, false));
		selectedPeriodBannerPanel = new JPanel();
		selectedPeriodBannerPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(selectedPeriodBannerPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		selectedPeriodBannerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Period Details"));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(4, 4, new Insets(0, 0, 0, 0), -1, -1));
		selectedPeriodBannerPanel.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setText("Start:");
		panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		panel2.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		selectedPeriodStartLabel = new JLabel();
		selectedPeriodStartLabel.setText("<start datetime>");
		panel2.add(selectedPeriodStartLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("End:");
		panel2.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		selectedPeriodEndLabel = new JLabel();
		selectedPeriodEndLabel.setText("<end datetime>");
		panel2.add(selectedPeriodEndLabel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setText("Duration:");
		panel2.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		selectedPeriodDurationLabel = new JLabel();
		selectedPeriodDurationLabel.setText("<duration>");
		panel2.add(selectedPeriodDurationLabel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setText("Complete:");
		panel2.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		selectedPeriodCompleteLabel = new JLabel();
		selectedPeriodCompleteLabel.setText("<complete?>");
		panel2.add(selectedPeriodCompleteLabel, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		selectedPeriodAddAttributeButton = new JButton();
		selectedPeriodAddAttributeButton.setText("Add Attribute");
		panel2.add(selectedPeriodAddAttributeButton, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		selectedPeriodAttsPane = new JScrollPane();
		selectedPeriodAttsPane.setVerticalScrollBarPolicy(22);
		selectedPeriodBannerPanel.add(selectedPeriodAttsPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		selectedPeriodTaskStatsPane = new JScrollPane();
		selectedPeriodTaskStatsPane.setVerticalScrollBarPolicy(22);
		panel1.add(selectedPeriodTaskStatsPane, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(250, -1), new Dimension(250, -1), new Dimension(250, -1), 0, false));
		selectedPeriodTaskStatsPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Task Stats"));
		selectedPeriodSpansPanel = new JPanel();
		selectedPeriodSpansPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		selectedPeriodPanel.add(selectedPeriodSpansPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		selectedPeriodSpansPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Spans"));
		selectedPeriodSpansPane = new JScrollPane();
		selectedPeriodSpansPane.setVerticalScrollBarPolicy(22);
		selectedPeriodSpansPanel.add(selectedPeriodSpansPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		selectedPeriodSpansPanel.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		selectedPeriodAddSpanButton = new JButton();
		selectedPeriodAddSpanButton.setText("Add Span");
		panel3.add(selectedPeriodAddSpanButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		periodsPanel = new JPanel();
		periodsPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainTabPane.addTab("Periods", periodsPanel);
		periodsScrollPane = new JScrollPane();
		periodsScrollPane.setVerticalScrollBarPolicy(22);
		periodsPanel.add(periodsScrollPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		periodsPanel.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		addPeriodButton = new JButton();
		addPeriodButton.setText("Add Period");
		panel4.add(addPeriodButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		tasksPanel = new JPanel();
		tasksPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainTabPane.addTab("Tasks", tasksPanel);
		tasksScrollPane = new JScrollPane();
		tasksScrollPane.setVerticalScrollBarPolicy(22);
		tasksPanel.add(tasksScrollPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JPanel panel5 = new JPanel();
		panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		tasksPanel.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		addTaskButton = new JButton();
		addTaskButton.setText("Add Task");
		panel5.add(addTaskButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return mainPanel;
	}

}
